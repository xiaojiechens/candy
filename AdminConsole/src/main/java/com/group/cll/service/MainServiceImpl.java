package com.group.cll.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Service;

import com.group.cll.constant.Constant;
import com.group.cll.model.Account;
import com.group.cll.model.Activity;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
public class MainServiceImpl {
	
	public List<Account> calculateIndexs() throws IOException{
		
		List<Account> topics = new ArrayList<>();
		for(Map.Entry<String, Account> accountMap : Constant.accounts.entrySet()) {
			
			Account returnAccount = new Account();
			Account account = accountMap.getValue();
			
			returnAccount.setAccountNo(account.getAccountNo());
			returnAccount.setDomain(account.getDomain());
			returnAccount.setWebSiteName(account.getWebSiteName());
			returnAccount.setActivities(account.getActivities());
			returnAccount.setAmount(account.getAmount());
			returnAccount.setBalance(account.getBalance());
			returnAccount.setBetNumber(account.getBetNumber());
			returnAccount.setBombIndex(account.getBombIndex());
			returnAccount.setEliminateRecords(account.getEliminateRecords());
			returnAccount.setExchangeRecords(account.getExchangeRecords());
			returnAccount.setGameWindowUrl(account.getGameWindowUrl());
			returnAccount.setLastFiveBombScore(account.getLastFiveBombScore());
			returnAccount.setLeastEliminateCnt(account.getLeastEliminateCnt());
			returnAccount.setLuckNum1(account.getLuckNum1());
			returnAccount.setLuckNum2(account.getLuckNum2());
			returnAccount.setLuckNum3(account.getLuckNum3());
			returnAccount.setLuckNum4(account.getLuckNum4());
			returnAccount.setPassword(account.getPassword());
			returnAccount.setPeriodSeconds(account.getPeriodSeconds());
			returnAccount.setSimulatorNums(account.getSimulatorNums());
			returnAccount.setLucyWagerRecords(account.getLucyWagerRecords());
			
			// 爆分指标
			returnAccount.setBombIndex(coutBombIndex(account.getExchangeRecords()));
			
			// 近五次爆分指标
			returnAccount.setLastFiveBombScore(getLastFiveBombScore(account.getExchangeRecords()));
			
			topics.add(returnAccount);
		}
		Collections.sort(topics);
		return topics;
	}
	
	/**
	 * 可参与的活动
	 * @param account
	 * @return
	 * @throws IOException
	 */
	public Map<String, Activity> getAvalibleActivity(Account account) throws IOException{
		
		Map<String, Activity> availableActivities = new TreeMap<String, Activity>();
		
		for(Map.Entry<String, Activity> activity: account.getActivities().entrySet()) {
			if(activity.getKey().equals("eliminateCnt") || activity.getKey().equals("lucyWagerId") ) {
				continue;
			}
			
			if(matchRule(activity.getValue().getRuleParams(), account)) {
				availableActivities.put(activity.getKey(), activity.getValue());
			}
		}
		return availableActivities;
	}
	
	
	/**
	 * 查找最优的lia
	 * @param ruleName
	 * @param candyIndex
	 * @return
	 */
	public boolean matchRule(JSONObject ruleParams, Account account){
		JSONObject jsonCandyIndex = JSONObject.fromObject(account);
		
		for(Object key: ruleParams.keySet()){
			if(!(jsonCandyIndex.getDouble(key.toString()) >= ruleParams.getDouble(key.toString()))) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 查找最优的连消次数
	 * @param ruleName
	 * @param candyIndex
	 * @return
	 */
	public String findEliminateCnt(JSONObject ruleParams, JSONArray eliminateCntRecords){
		
//		{"pid_num": 8172, "wagers_id": "395872629447", "eliminate_cnt": 8, "stick_num": 6, "lines": [[{"ElementID": 1, "Grids": "1,2,3,5,7,8,9,10,13,14", "GridNum": 10, "Payoff": 30}], [{"ElementID": 2, "Grids": "5,8,9,12", "GridNum": 4, "Payoff": 4}], [{"ElementID": 3, "Grids": "6,8,9,10,12,14", "GridNum": 6, "Payoff": 20}], [{"ElementID": 1, "Grids": "10,12,13,14", "GridNum": 4, "Payoff": 2}], [{"ElementID": 2, "Grids": "6,7,10,11,15", "GridNum": 5, "Payoff": 5}], [{"ElementID": 5, "Grids": "8,9,10,13", "GridNum": 4, "Payoff": 20}], [{"ElementID": 4, "Grids": "10,13,14,15", "GridNum": 4, "Payoff": 10}], [{"ElementID": 3, "Grids": "8,12,13,14", "GridNum": 4, "Payoff": 5}], []]}
//		{"pid_num": 8172, "wagers_id": "395872629447", "eliminate_cnt": 8, "stick_num": 6, "lines": [[{"ElementID": 1, "Grids": "1,2,3,5,7,8,9,10,13,14", "GridNum": 10, "Payoff": 30}], [{"ElementID": 2, "Grids": "5,8,9,12", "GridNum": 4, "Payoff": 4}], [{"ElementID": 3, "Grids": "6,8,9,10,12,14", "GridNum": 6, "Payoff": 20}], [{"ElementID": 1, "Grids": "10,12,13,14", "GridNum": 4, "Payoff": 2}], [{"ElementID": 2, "Grids": "6,7,10,11,15", "GridNum": 5, "Payoff": 5}], [{"ElementID": 5, "Grids": "8,9,10,13", "GridNum": 4, "Payoff": 20}], [{"ElementID": 4, "Grids": "10,13,14,15", "GridNum": 4, "Payoff": 10}], [{"ElementID": 3, "Grids": "8,12,13,14", "GridNum": 4, "Payoff": 5}], []]}
		String maxEliminateCntId = eliminateCntRecords.getJSONObject(0).getString("wagers_id");
		int maxEliminateCnt = eliminateCntRecords.getJSONObject(0).getInt("eliminate_cnt");
		for(Object recordObject: eliminateCntRecords){
			JSONObject record = (JSONObject)recordObject;
			
			if(record.getInt("eliminate_cnt") > maxEliminateCnt) {
				maxEliminateCntId = record.getString("wagers_id");
				maxEliminateCnt = record.getInt("eliminate_cnt");
			}
		}
		
		return maxEliminateCntId+ "/" + maxEliminateCnt;
	}
	
	/**
	 * 查找最优的幸运主单
	 * @param ruleParams
	 * @param account
	 * @return
	 */
	public String findLucyWagerId(JSONObject ruleParams, JSONArray wagersRecords){
		
		JSONArray luckWagerIds = ruleParams.getJSONArray("luckWagerIds");
//		{"pid_num": 8172, "wagers_id": "395871676888", "eliminate_cnt": 0, "stick_num": 11, "lines": [[{"ElementID": 6, "GridNum": 1, "Grids": "8", "Payoff": 0, "BrickNum": [34]}], []]}
//		{"pid_num": 8172, "wagers_id": "395871676888", "eliminate_cnt": 0, "stick_num": 11, "lines": [[{"ElementID": 6, "GridNum": 1, "Grids": "8", "Payoff": 0, "BrickNum": [34]}], []]}
		for(Object luckWagerIdObject: luckWagerIds){
			String luckWagerId = (String)luckWagerIdObject;
			
			for(Object recordObject: wagersRecords){
				JSONObject record = (JSONObject)recordObject;
				if(record.getString("wagers_id").endsWith(luckWagerId)) {
					return record.getString("wagers_id");
				}
			}
		}
		return null;
	}
	
	/**
	 * 累加json数组中，指定字段的值。每个字段分别累加。
	 * @param array json数据
	 * @param fieldNames 指定的字段
	 * @return
	 */
	public Map<String, Double> sumFields(JSONArray array, String ...fieldNames) {
		Map<String, Double> sums = new HashMap<>();
		for(String fieldName : fieldNames) {
			sums.put(fieldName, 0.0);
		}
		
		for(Object object : array) {
			JSONObject jsonObject = (JSONObject)object;
			
			for(String fieldName : fieldNames) {
				sums.put(fieldName, new BigDecimal(String.valueOf(sums.get(fieldName))).add(new BigDecimal(String.valueOf(jsonObject.getDouble(fieldName)))).doubleValue());
			}
		}
		return sums;
	}
	
	/**
	 * 统计爆分指标
	 * @param array json数据
	 * @param fieldNames 指定的字段
	 * @return
	 */
	public String coutBombIndex(JSONArray array) {
		
		// 爆(50)-高(30)-平(20)-亏(10)-烂(>0)-惨(=0)
		int[] bombs = new int[6];
		
		if(array != null && array.size() > 0) {
			for(Object o : array) {
				JSONObject exchangeRecord = (JSONObject)o;
				double amount = exchangeRecord.getDouble("amount");
				
				if(amount >= 50) {
					bombs[0] ++;
				} else if(amount >= 30) {
					bombs[1] ++;
				} else if(amount >= 20) {
					bombs[2] ++;
				} else if(amount >= 10) {
					bombs[3] ++;
				} else if(amount > 0) {
					bombs[4] ++;
				} else {
					bombs[5] ++;
				}
			}
		}
		return array == null ? null : "爆("+bombs[0]+")-高("+bombs[1]+")-平("+bombs[2]+")-亏("+bombs[3]+")-烂("+bombs[4]+")-惨("+bombs[5]+")";
	}
	
	/**
	 * 获取近五次爆分数值
	 * @param array
	 * @return
	 */
	public String getLastFiveBombScore(JSONArray array) {
		String lastFiveBombScore = "";
		
		if(array != null && array.size() > 0) {
			for(int i = (array.size() > 5 ? array.size() - 5 : 0) ; i < array.size() ; i++) {
				lastFiveBombScore = lastFiveBombScore +"-"+ (array.getJSONObject(i)).getDouble("amount");
			}
		}
		return lastFiveBombScore.substring(lastFiveBombScore.length() > 0 ? 1 : 0);
	}
}
