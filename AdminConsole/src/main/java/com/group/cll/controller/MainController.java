package com.group.cll.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group.cll.action.Simulator;
import com.group.cll.constant.Constant;
import com.group.cll.model.Account;
import com.group.cll.model.Activity;
import com.group.cll.service.MainServiceImpl;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@RestController
public class MainController {
	
	@Autowired
	private MainServiceImpl candyService;
	
	@RequestMapping(path = { "/getIndexes" })
	public String getIndexes() throws IOException {
		JSONObject result = new JSONObject();
		
		List<Account> accounts = candyService.calculateIndexs();
		result.put("topics", accounts);
		
		return result.toString();
	}
	
	/**
	    *    打开登录页面
	 * @param domain
	 * @param accountNo
	 * @throws IOException
	 */
	@RequestMapping(path = { "/loginWebPage" })
	public void loginWebPage(Account account) throws IOException {
		
		String accountNoAndDomain = account.getAccountNo()+"@@"+account.getDomain();
		if(Constant.webDrivers.get(accountNoAndDomain) == null) {
			Constant.webDrivers.put(accountNoAndDomain, new ChromeDriver());
		}
		try {
			Constant.webDrivers.get(accountNoAndDomain).get(account.getDomain());
		} catch (Exception e) {
			Constant.webDrivers.put(accountNoAndDomain, new ChromeDriver());
			Constant.webDrivers.get(accountNoAndDomain).get(account.getDomain());
		}
		
		account = Constant.accounts.get(accountNoAndDomain);
		
		try {
			Constant.webDrivers.get(accountNoAndDomain).findElement(By.id("login_account")).sendKeys(account.getAccountNo());
			Constant.webDrivers.get(accountNoAndDomain).findElement(By.id("login_password")).sendKeys(account.getPassword());
		} catch(Exception e) {
		}
	}

	@RequestMapping(path = { "/openGameWindowUrlPage" })
	public void openGameWindowUrlPage(Account account) throws IOException, InterruptedException {
		String accountNoAndDomain = account.getAccountNo()+"@@"+account.getDomain();
		account = Constant.accounts.get(account.getAccountNo()+"@@"+account.getDomain());
		try {
			JavascriptExecutor js = (JavascriptExecutor) Constant.webDrivers.get(accountNoAndDomain);
			ArrayList<String> tabs = new ArrayList<String>(Constant.webDrivers.get(accountNoAndDomain).getWindowHandles());
			
			// 第一次打开游戏网页
			try {
				js.executeScript("window.open()");
			} catch(org.openqa.selenium.NoSuchWindowException e) {
				Constant.webDrivers.get(accountNoAndDomain).switchTo().window(tabs.get(tabs.size() - 1));
				openGameWindowUrlPage(account);
			}
			
			tabs = new ArrayList<String>(Constant.webDrivers.get(accountNoAndDomain).getWindowHandles());
			
			Constant.webDrivers.get(accountNoAndDomain).switchTo().window(tabs.get(tabs.size()-1));
			
			Constant.webDrivers.get(accountNoAndDomain).get(account.getGameWindowUrl());
			
			// 第二次打开游戏网页
			try {
				js.executeScript("window.open()");
			} catch(org.openqa.selenium.NoSuchWindowException e) {
				tabs = new ArrayList<String>(Constant.webDrivers.get(accountNoAndDomain).getWindowHandles());
				Constant.webDrivers.get(accountNoAndDomain).switchTo().window(tabs.get(tabs.size()-1));
				openGameWindowUrlPage(account);
			}
			
			tabs = new ArrayList<String>(Constant.webDrivers.get(accountNoAndDomain).getWindowHandles());
			
			Constant.webDrivers.get(accountNoAndDomain).switchTo().window(tabs.get(tabs.size()-1));
			
			Constant.webDrivers.get(accountNoAndDomain).get(account.getGameWindowUrl());
		} catch(Exception e) {
		}
	}

	/**
	 * 开始游戏
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@RequestMapping(path = { "/startGame" })
	public String startGame(Account account) throws IOException, InterruptedException {
		String sessionId = null;
		account = Constant.accounts.get(account.getAccountNo()+"@@"+account.getDomain());
		JSONObject result = new JSONObject();
		try {
			if(sessionId == null || sessionId.equals("")) {
				Map<String, String> cookies = readCookies(account.getAccountNo()+"@@"+account.getDomain());
				
				// 包含sessionId, 则调用模拟器开始游戏
				if(cookies.containsKey("SESSION_ID")) {
					sessionId = cookies.get("SESSION_ID");
				}
			}
			
			int threadNum = account.getSimulatorNums() > 0 ? account.getSimulatorNums() : 3;
			
			JSONArray threadNames = new JSONArray();
			
			for(Entry<String,Simulator> simulatorEntry : account.getSimulators().entrySet()) {
				try {
					simulatorEntry.getValue().forceLogout();
				} catch(Exception e) {
				}
			}
			
			for(int i = 0 ; i < threadNum; i++) {
				String threadName = "candy_"+ i;
				Simulator simulator = new Simulator(threadName);
				
				account.getSimulators().put(threadName, simulator);
				simulator.play(account, sessionId);
				threadNames.add(threadName);
			}
			result.put("threadNames", threadNames);
			return result.toString();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 开始游戏
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@RequestMapping(path = { "/stopGame" })
	public void stopGame(Account account) throws IOException, InterruptedException {
		account = Constant.accounts.get(account.getAccountNo()+"@@"+account.getDomain());
		
		for(Entry<String,Simulator> simulatorEntry : account.getSimulators().entrySet()) {
			simulatorEntry.getValue().forceLogout();
		}
	}
	
	@RequestMapping(path = { "/getMessage" })
	public String getMessage(Account account) throws IOException, InterruptedException {
		account = Constant.accounts.get(account.getAccountNo()+"@@"+account.getDomain());
		
		JSONObject result = new JSONObject();
		
		for(Entry<String,Simulator> simulatorEntry : account.getSimulators().entrySet()) {

			if(simulatorEntry.getValue().messages == null){
				simulatorEntry.getValue().messages = new ArrayList<>();
				simulatorEntry.getValue().messages.add("启动");
			}

			result.put(simulatorEntry.getKey(), JSONArray.fromObject(simulatorEntry.getValue().messages));
			simulatorEntry.getValue().messages.clear();
		}
		return result.toString();
	}
	
	@RequestMapping(path = { "/openEliminateCntPage" })
	public void openEliminateCntPage(Account account, String eliminateCntWagersIdAndNum) throws IOException {

		account = Constant.accounts.get(account.getAccountNo()+"@@"+account.getDomain());
		String accountNoAndDomain = account.getAccountNo()+"@@"+account.getDomain();
		
		Activity activity = account.getActivities().get("eliminateCnt");
		
		if(Constant.webDrivers.get(accountNoAndDomain) == null) {
			Constant.webDrivers.put(accountNoAndDomain, new ChromeDriver());
		}
		try {
			Constant.webDrivers.get(activity.getUrl());
		} catch (Exception e) {
			Constant.webDrivers.put(accountNoAndDomain, new ChromeDriver());
			Constant.webDrivers.get(accountNoAndDomain).get(activity.getUrl());
		}
		Constant.webDrivers.get(accountNoAndDomain).findElement(By.id("user_name")).sendKeys(account.getAccountNo());
		Constant.webDrivers.get(accountNoAndDomain).findElement(By.id("name")).sendKeys(eliminateCntWagersIdAndNum.substring(0, eliminateCntWagersIdAndNum.indexOf("/")));
		
	}
	
	@RequestMapping(path = { "/openLucyWagerIdPage" })
	public void openLucyWagerIdPage(Account account, String wagerId) throws IOException {
		
		account = Constant.accounts.get(account.getAccountNo()+"@@"+account.getDomain());
		String accountNoAndDomain = account.getAccountNo()+"@@"+account.getDomain();
		
		Activity activity = account.getActivities().get("eliminateCnt");
		
		if(Constant.webDrivers == null) {
			Constant.webDrivers.put(accountNoAndDomain, new ChromeDriver());
		}
		try {
			Constant.webDrivers.get(activity.getUrl());
		} catch (Exception e) {
			Constant.webDrivers.put(accountNoAndDomain, new ChromeDriver());
			Constant.webDrivers.get(accountNoAndDomain).get(activity.getUrl());
		}
		Constant.webDrivers.get(accountNoAndDomain).findElement(By.id("29_str1")).sendKeys(account.getAccountNo());
		Constant.webDrivers.get(accountNoAndDomain).findElement(By.id("29_str2")).sendKeys(wagerId);
		Constant.webDrivers.get(accountNoAndDomain).findElement(By.id("29_str3")).sendKeys("BBin");
		Constant.webDrivers.get(accountNoAndDomain).findElement(By.id("29_str4")).sendKeys("1");
		
	}
	
	@RequestMapping(path = { "/openDailySignPage"})
	public void openDailySignPage(Account account) throws IOException {
		
		account = Constant.accounts.get(account.getAccountNo()+"@@"+account.getDomain());
		String accountNoAndDomain = account.getAccountNo()+"@@"+account.getDomain();
		
		Activity activity = account.getActivities().get("eliminateCnt");
		
		if(Constant.webDrivers == null) {
			Constant.webDrivers.put(accountNoAndDomain, new ChromeDriver());
		}
		try {
			Constant.webDrivers.get(activity.getUrl());
		} catch (Exception e) {
			Constant.webDrivers.put(accountNoAndDomain, new ChromeDriver());
			Constant.webDrivers.get(accountNoAndDomain).get(activity.getUrl());
		}
		Constant.webDrivers.get(accountNoAndDomain).findElement(By.id("23_str1")).sendKeys(account.getAccountNo());
		Constant.webDrivers.get(accountNoAndDomain).findElement(By.id("29_str3")).sendKeys("4008");
	}
	
	private Map<String, String> readCookies(String accountNoAndDomain) throws IOException {
		
		Set<Cookie> cookies = Constant.webDrivers.get(accountNoAndDomain).manage().getCookies();
		
		Map<String, String> cookieMap = new HashMap<>();
		
		for(Cookie cookie : cookies) {
			System.out.println(cookie.getName()+"------------"+cookie.getValue());
			cookieMap.put(cookie.getName(), cookie.getValue());
		}
		
		return cookieMap;
	}
}
