package com.group.cll.ws;
import java.io.IOException;
import java.net.URI;

import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

@ClientEndpoint
public class WebSocketClient {

	private Session session;
	
	@OnOpen
	public void onOpen(Session session){
	}
	
	@OnClose
	public void onClose(){
	}
	
	@OnMessage
	public void onMessage(String message, Session session){
		System.out.println(session);
		System.out.println(message);
	}	
	
	@OnError
	public void onError(Throwable thr){
	}

	public WebSocketClient() {
		super();
	}
	
	public WebSocketClient(String str) {
		super();
	}
	
	public void play(String sessionId) {
		try {
			WebSocketContainer container = ContainerProvider.getWebSocketContainer();	// 获得WebSocketContainer
			this.session = container.connectToServer(WebSocketClient.class, new URI( "wss://homelaohuji.com/fxCasino/fxLB?gameType=5902" ));	// 该方法会阻塞
			System.out.println(this.session);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}
	
	public void sendMessage(String message){
		System.out.println(this.session);
		try {
			this.session.getBasicRemote().sendText(message);
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				this.session.getBasicRemote().flushBatch();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}