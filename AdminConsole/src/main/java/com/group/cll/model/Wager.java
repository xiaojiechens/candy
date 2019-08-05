package com.group.cll.model;

public class Wager {
	
	/**主键*/
	private int pidNum;
	
	/**注单号*/
	private String wagersId;
	
	/**注单余额*/
	private double balance;
	
	/**注单金额*/
	private double amount;
	
	/**总余额*/
	private double balanceTotal;
	
	/**注单次数*/
	private int betNumber;
	
	public int getPidNum() {
		return pidNum;
	}
	public void setPidNum(int pidNum) {
		this.pidNum = pidNum;
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
	public double getBalanceTotal() {
		return balanceTotal;
	}
	public void setBalanceTotal(double balanceTotal) {
		this.balanceTotal = balanceTotal;
	}
	public int getBetNumber() {
		return betNumber;
	}
	public void setBetNumber(int betNumber) {
		this.betNumber = betNumber;
	}
	public String getWagersId() {
		return wagersId;
	}
	public void setWagersId(String wagersId) {
		this.wagersId = wagersId;
	}
	
}
