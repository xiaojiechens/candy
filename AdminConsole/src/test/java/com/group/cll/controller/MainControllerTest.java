package com.group.cll.controller;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;
import org.springframework.web.bind.annotation.RequestMapping;

import com.group.cll.action.Simulator;
import com.group.cll.constant.Constant;
import com.group.cll.model.Account;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class MainControllerTest {

	@Test
	public void testGetIndexes() {
		fail("Not yet implemented");
	}

	@Test
	public void testLoginWebPage() {
		fail("Not yet implemented");
	}

	@Test
	public void testOpenGameWindowUrlPage() {
		fail("Not yet implemented");
	}

	@Test
	public void testStartGame() throws IOException, InterruptedException {
		
		Account account = new Account();
		account.setPeriodSeconds(3000);
		account.setLuckNum1("88888");
		account.setLuckNum2("8888");
		account.setLuckNum3("888");
		account.setLuckNum4("88");
		account.setSimulatorNums(3);
		
		System.out.println(startGame(account));
		
		BufferedReader reader = new BufferedReader( new InputStreamReader( System.in ) );
		while ( true ) {
		}
	}

	public String startGame(Account account) throws IOException, InterruptedException {
		String sessionId = "55652017f4e6e8bfd969a58d4f6afdb675aa6d51";
		JSONObject result = new JSONObject();
		try {
			
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
	
	@Test
	public void testStopGame() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetMessage() {
		fail("Not yet implemented");
	}

	@Test
	public void testOpenEliminateCntPage() {
		fail("Not yet implemented");
	}

	@Test
	public void testOpenLucyWagerIdPage() {
		fail("Not yet implemented");
	}

	@Test
	public void testOpenDailySignPage() {
		fail("Not yet implemented");
	}

}
