package com.group.cll.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;

import org.junit.Test;

import com.group.cll.ws.WebSocketClient;

public class WebSocketClientTest {

	@Test
	public void test() throws InterruptedException, URISyntaxException, IOException {
		
		WebSocketClient chatclient = new WebSocketClient("11");
		
		chatclient.play("111");
		
		BufferedReader reader = new BufferedReader( new InputStreamReader( System.in ) );
		while ( true ) {
			String line = reader.readLine();
			if( line.equals( "close" ) ) {
				chatclient.onClose();
			} else {
				chatclient.sendMessage( line );
			}
		}
	}
}
