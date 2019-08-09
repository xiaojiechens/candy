package com.group.cll.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

	public static String getSuccessMessage(String message){
		JSONObject successMessage = new JSONObject();
		successMessage.put("success", true);
		successMessage.put("msg", message);
		return successMessage.toString();
	}
	
	public static String getFailMessage(String message){
		JSONObject failMessage = new JSONObject();
		failMessage.put("success", false);
		failMessage.put("msg", message);
		return failMessage.toString();
	}
	
	@RequestMapping(path = { "/getIndexes" })
	public String getIndexes() throws IOException {
		JSONObject result = new JSONObject();
		
		List<Account> accounts = candyService.calculateIndexs();
		result.put("topics", accounts);
		
		return result.toString();
	}
	
	/**
	    *    打开登录页面
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
	public String openGameWindowUrlPage(Account account) throws IOException, InterruptedException {
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
			return getFailMessage("请先打开游戏窗口!");
		}
		return null;
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
			Map<String, String> cookies = readCookies(account.getAccountNo()+"@@"+account.getDomain());

			// 包含sessionId, 则调用模拟器开始游戏
			if(cookies.containsKey("SESSION_ID")) {
				sessionId = cookies.get("SESSION_ID");

				Simulator simulator = new Simulator();
				
				if(simulator.play(account, sessionId)){
					account.getSimulators().put(simulator.toString(), simulator);
					result.put("threadName", simulator.toString());
					result.put("simulatorNum", account.getSimulators().size());
					return result.toString();
				}
			} else {
				return getFailMessage("请先获取session id!");
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return getFailMessage("开始游戏发生错误");
	}
	
	/**
	 * 开始游戏
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@RequestMapping(path = { "/stopGame" })
	public String stopGame(String domain, String accountNo, String threadName) throws IOException, InterruptedException {
		Account account = Constant.accounts.get(accountNo+"@@"+domain);
		try{
			Simulator simulator = account.getSimulators().get(threadName);
			account.getSimulators().remove(threadName);
			simulator.forceLogout();
		} catch (Exception e){
		}
		return getSuccessMessage("已关闭");
	}
	
	@RequestMapping(path = { "/getMessage" })
	public String getMessage(String domain, String accountNo, String threadName) throws IOException, InterruptedException {
		Account account = Constant.accounts.get(accountNo+"@@"+domain);
		
		JSONObject result = new JSONObject();
        Simulator simulator = account.getSimulators().get(threadName);

        if(simulator != null){
			if(simulator.messages == null){
				simulator.messages = new ArrayList<>();
				simulator.messages.add("启动");
			}

			result.put("threadMessages", JSONArray.fromObject(simulator.messages));
			simulator.messages.clear();
		}

		return result.toString();
	}
	
	@RequestMapping(path = { "/openEliminateCntPage" })
	public void openEliminateCntPage(String domain, String accountNo, String eliminateCntWagersIdAndNum) throws IOException {

		String accountNoAndDomain = accountNo+"@@"+domain;
		Account account = Constant.accounts.get(accountNoAndDomain);
		
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
	public void openLucyWagerIdPage(String domain, String accountNo, String lucyWagerId) throws IOException {
		
		String accountNoAndDomain = accountNo+"@@"+domain;
		Account account = Constant.accounts.get(accountNoAndDomain);
		
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
		Constant.webDrivers.get(accountNoAndDomain).findElement(By.id("29_str2")).sendKeys(lucyWagerId);
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
		
		if(Constant.webDrivers.get(accountNoAndDomain) != null) {
			Set<Cookie> cookies = Constant.webDrivers.get(accountNoAndDomain).manage().getCookies();
			
			Map<String, String> cookieMap = new HashMap<>();
			
			for(Cookie cookie : cookies) {
				System.out.println(cookie.getName()+"------------"+cookie.getValue());
				cookieMap.put(cookie.getName(), cookie.getValue());
			}
			return cookieMap;
		} else {
			return new HashMap<>();
		}
	}
}
