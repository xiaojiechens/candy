package com.group.cll.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Test;

import com.group.cll.ws.WSClientImpl;

public class WSClientImplTest {

	@Test
	public void test() throws InterruptedException, URISyntaxException, IOException {
		
		WSClientImpl chatclient = new WSClientImpl( new URI( "wss://homelaohuji.com/fxCasino/fxLB?gameType=5902" ) ) {
			@Override
			public void onMessage( String message ) {
				System.out.println( "<<<<: " + message );
			}
		};
		chatclient.connectBlocking();
		BufferedReader reader = new BufferedReader( new InputStreamReader( System.in ) );
		while ( true ) {
			String line = reader.readLine();
			if( line.equals( "close" ) ) {
				chatclient.close();
			} else {
				chatclient.sendMessage( line );
			}
		}
		
	}
}
