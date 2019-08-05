package com.group.cll.service;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.group.cll.constant.Constant;
import com.group.cll.model.Account;
import com.group.cll.model.Activity;
import com.group.cll.task.FileLoader;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class CandyServiceImplTest {

	@Autowired
	private MainServiceImpl candyServiceImpl;
	
	@Autowired
	private FileLoader fileLoader;
	
	@Test
	public void testCalculateIndexs() throws IOException {
		fileLoader.loadFiles();
		
		System.out.println("---"+JSONArray.fromObject(candyServiceImpl.calculateIndexs()).toString());
	}
	
	@Test
	public void testGetAvalibleActivity() throws IOException {
		
		Account candyIndex = new Account();
		
		Map<String, Activity> activities = candyServiceImpl.getAvalibleActivity(candyIndex);
		
		for(Map.Entry<String, Activity> activity : activities.entrySet()) {
			System.out.println(activity.getValue().getName());
		}
	}
	
	@Test
	public void testMatchRule() throws IOException {
		JSONObject ruleParams = JSONObject.fromObject("{\"playTime\":4000}");
		Account candyIndex = new Account();
		
		assertTrue(candyServiceImpl.matchRule(ruleParams, candyIndex));
	}
	
	@Test
	public void testSumFields() throws IOException {
		
		fileLoader.loadFiles();
		
		System.out.println(Constant.accounts.get(0).getExchangeRecords());
		
		System.out.println("---"+JSONObject.fromObject(candyServiceImpl.sumFields(Constant.accounts.get(0).getExchangeRecords(), 
				"pid_num", "balance", "amount", "balance_total", "bet_number")));
		
	}

}
