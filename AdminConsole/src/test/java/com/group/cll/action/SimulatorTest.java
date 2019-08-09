package com.group.cll.action;

import com.group.cll.model.Account;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SimulatorTest {

	@Test
	public void test() throws IOException {
		Simulator simulator = new Simulator();
		
		Account account = new Account();
		account.setPeriodSeconds(3000);
		account.setLuckNum1("88888");
		account.setLuckNum2("8888");
		account.setLuckNum3("888");
		account.setLuckNum4("88");

		simulator.play(account,"55652017f4e6e8bfd969a58d4f6afdb675aa6d51");
		
		BufferedReader reader = new BufferedReader( new InputStreamReader( System.in ) );
		while ( true ) {
			String line = reader.readLine();
			if( line.equals( "close" ) ) {
				simulator.onClose();
			} else {
				simulator.sendMessage( line );
			}
		}
	}
}
