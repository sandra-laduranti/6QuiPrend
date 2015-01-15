package communication;

import org.java_websocket.WebSocket;

public class PlayerConnect {
	private WebSocket conn;
	private String nickName;
	
	public PlayerConnect(WebSocket conn, String nickName){
		this.conn = conn;
		this.nickName = nickName;
	}
	
	public WebSocket getWebSocket(){
		return conn;
	}
	
	public String getNickName(){
		return nickName;
	}
	
}
