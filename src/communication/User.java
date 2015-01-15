package communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

public class User extends WebSocketClient implements Comparable<User>{

	private transient int userId;
	
	private String userNickname;
	
	private String userEmail;
	
	private String userPassword;
	
	private int currentBeef;

	public User( URI serverUri , Draft draft ) {
		super( serverUri, draft );
	}

	public User( URI serverURI ) {
		super( serverURI );
	}
	
	public int getUserId() {
		return this.userId;
	}
	
	public String getUserNickname() {
		return this.userNickname;
	}
	
	public String getUserEmail() {
		return this.userEmail;
	}
	
	public int getCurrentBeef() {
		return this.currentBeef;
	}
	
	public void setCurrentBeef(int currentBeef) {
		this.currentBeef = currentBeef;
	}


	public void setUser(int userId, String userNickname){
		this.userId = userId;
		this.userNickname =userNickname;
		this.currentBeef = 0;
	}
	
	public int compareTo(User user) {
		if(this.getCurrentBeef()>user.getCurrentBeef()){
			return 1;
		} else if(this.getCurrentBeef()<user.getCurrentBeef()){
			return -1;
		} else {
			return 0;
		}
	}
	
	@Override
	public void onOpen( ServerHandshake handshakedata ) {
		System.out.println( "opened connection" );
		// if you plan to refuse connection based on ip or httpfields overload: onWebsocketHandshakeReceivedAsClient
	}

	@Override
	public void onMessage( String message ) {
		String delims = "[:]";
		String[] tokens = message.split(delims);
		
		switch (tokens[0]) {
        case "getCarte":
            System.out.println("getCarte");
            break;
        case "jesaispasencorequelflag":
            System.out.println("tata");
            break;
        default:
            System.out.println("Error: ce flag n'existe pas.");
        }
		//System.out.println( "received: " + message );
	}


	@Override
	public void onClose( int code, String reason, boolean remote ) {
		// The codecodes are documented in class org.java_websocket.framing.CloseFrame
		System.out.println( "Connection closed by " + ( remote ? "remote peer" : "us" ) );
	}

	@Override
	public void onError( Exception ex ) {
		ex.printStackTrace();
		// if the error is fatal then onClose will be called additionally
	}
	
	
	//pour envoyer json => json.tostring()
	public void sendWithFlag(String Text, String flag){
		//this.send(flag + ":" + Text);
		this.send(Text);
	}

	public static void main( String[] args ) throws URISyntaxException, IOException {
		

		User usr = new User( new URI( "ws://localhost:12345" )); 
		usr.connect();
		
		BufferedReader sysin = new BufferedReader( new InputStreamReader( System.in ) );
		while(true){
			String in = sysin.readLine();
			usr.sendWithFlag(in,"titi");
		}
	}

}