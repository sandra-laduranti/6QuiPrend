package communication;

import org.java_websocket.WebSocket;

public class PlayerConnect {
	private WebSocket conn;
	private int id;
	private String nickName;
	
	public PlayerConnect(WebSocket conn, int id){
		this.conn = conn;
		this.id = id;
	}
	
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
	
	public int getId(){
		return id;
	}
	
}
