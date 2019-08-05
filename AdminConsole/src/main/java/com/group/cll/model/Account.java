package com.group.cll.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.group.cll.action.Simulator;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Account implements Comparable<Account>{

	/**账号*/
	private String accountNo;
	
	/**密码*/
	private String password;

	/**网站域名*/
	private String domain;
	
	/**网站网址*/
	private String webSiteName;
	
	/**游戏网址*/
	private String gameWindowUrl;

	/**幸运号码1*/
	private String luckNum1;

	/**幸运号码2*/
	private String luckNum2;

	/**幸运号码3*/
	private String luckNum3;

	/**幸运号码4*/
	private String luckNum4;

	/**最少连消次数*/
	private int leastEliminateCnt;

	/**发包时间*/
	private int periodSeconds;

	/**挂机数量*/
	private int simulatorNums;

	/**网站活动*/
	private Map<String, Activity> activities;

	// --外挂执行过程数据------

	/**账户余额*/
	private double balance;

	/**换分总额*/
	private double amount;

	/**打码总数*/
	private int betNumber;

	/**爆分指标*/
	private String bombIndex;

	/**近五次爆分*/
	private String lastFiveBombScore;

	/**换分记录*/
	private JSONArray exchangeRecords;

	/**连消记录*/
	private List<String> eliminateWagerIds;
	
	/**连消记录*/
	private JSONArray eliminateRecords;
	
	/**幸运注单记录*/
	private List<String> lucyWagerIds;
	
	/**幸运注单记录*/
	private JSONArray lucyWagerRecords;
	
	private Map<String, Simulator> simulators = new HashMap<String, Simulator>();

	public Map<String, Simulator> getSimulators() {
		return simulators;
	}

	public void setSimulators(Map<String, Simulator> simulators) {
		this.simulators = simulators;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
		int preIndex = domain.indexOf("://") + 3;
		this.webSiteName = domain.substring(preIndex, domain.indexOf(":", preIndex));
	}

	public String getWebSiteName() {
		if(webSiteName != null) {
			return webSiteName;
		} else {
			int preIndex = domain.indexOf("://") + 3;
			return domain.substring(preIndex, domain.indexOf(":", preIndex));
		}
	}

	public void setWebSiteName(String webSiteName) {
		this.webSiteName = webSiteName;
	}

	public String getGameWindowUrl() {
		return gameWindowUrl;
	}

	public void setGameWindowUrl(String gameWindowUrl) {
		this.gameWindowUrl = gameWindowUrl;
	}

	public String getLuckNum1() {
		return luckNum1;
	}

	public void setLuckNum1(String luckNum1) {
		this.luckNum1 = luckNum1;
	}

	public String getLuckNum2() {
		return luckNum2;
	}

	public void setLuckNum2(String luckNum2) {
		this.luckNum2 = luckNum2;
	}

	public String getLuckNum3() {
		return luckNum3;
	}

	public void setLuckNum3(String luckNum3) {
		this.luckNum3 = luckNum3;
	}

	public String getLuckNum4() {
		return luckNum4;
	}

	public void setLuckNum4(String luckNum4) {
		this.luckNum4 = luckNum4;
	}

	public int getLeastEliminateCnt() {
		return leastEliminateCnt;
	}

	public void setLeastEliminateCnt(int leastEliminateCnt) {
		this.leastEliminateCnt = leastEliminateCnt;
	}

	public int getPeriodSeconds() {
		return periodSeconds;
	}

	public void setPeriodSeconds(int periodSeconds) {
		this.periodSeconds = periodSeconds;
	}

	public int getSimulatorNums() {
		return simulatorNums;
	}

	public void setSimulatorNums(int simulatorNums) {
		this.simulatorNums = simulatorNums;
	}

	public Map<String, Activity> getActivities() {
		return activities;
	}

	public void setActivities(Map<String, Activity> activities) {
		this.activities = activities;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public int getBetNumber() {
		return betNumber;
	}

	public void setBetNumber(int betNumber) {
		this.betNumber = betNumber;
	}

	public String getBombIndex() {
		return bombIndex;
	}

	public void setBombIndex(String bombIndex) {
		this.bombIndex = bombIndex;
	}

	public String getLastFiveBombScore() {
		return lastFiveBombScore;
	}

	public void setLastFiveBombScore(String lastFiveBombScore) {
		this.lastFiveBombScore = lastFiveBombScore;
	}

	public JSONArray getExchangeRecords() {
		return exchangeRecords;
	}

	public void setExchangeRecords(JSONArray exchangeRecords) {
		this.exchangeRecords = exchangeRecords;
	}

	public JSONArray getEliminateRecords() {
		return eliminateRecords;
	}

	public void setEliminateRecords(JSONArray eliminateRecords) {
		this.eliminateRecords = eliminateRecords;
	}

	public List<String> getEliminateWagerIds() {
		return eliminateWagerIds;
	}

	public void setEliminateWagerIds(List<String> eliminateWagerIds) {
		this.eliminateWagerIds = eliminateWagerIds;
	}

	public List<String> getLucyWagerIds() {
		return lucyWagerIds;
	}

	public void setLucyWagerIds(List<String> lucyWagerIds) {
		this.lucyWagerIds = lucyWagerIds;
	}

	public JSONArray getLucyWagerRecords() {
		return lucyWagerRecords;
	}

	public void setLucyWagerRecords(JSONArray lucyWagerRecords) {
		this.lucyWagerRecords = lucyWagerRecords;
	}

	public int compareTo(Account account) {
		 return this.accountNo.compareTo(account.getAccountNo());
	}
	
	public String getJSONObjectString() {
		
		JSONObject accountObject = new JSONObject();
		accountObject.put("accountNo", this.getAccountNo());
		accountObject.put("domain", this.getDomain());
		accountObject.put("password", this.getPassword());
		accountObject.put("password", this.getPassword());
		accountObject.put("gameWindowUrl", this.getGameWindowUrl());
		accountObject.put("luckNum1", this.getLuckNum1());
		accountObject.put("luckNum2", this.getLuckNum2());
		accountObject.put("luckNum3", this.getLuckNum3());
		accountObject.put("luckNum4", this.getLuckNum4());
		accountObject.put("leastEliminateCnt", this.getLeastEliminateCnt());
		accountObject.put("periodSeconds", this.getPeriodSeconds());
		accountObject.put("simulatorNums", this.getSimulatorNums());
		
		return accountObject.toString();
	}
}
