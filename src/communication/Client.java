package communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import metier.User;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

public class Client extends WebSocketClient {
	
	private User user;

	public Client( URI serverUri , Draft draft ) {
		super( serverUri, draft );
	}

	public Client( URI serverURI ) {
		super( serverURI );
	}

	public void setUser(User user){
		this.user = user;
	}
	
	public User getUser(){
		return user;
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
		Client c = new Client( new URI( "ws://localhost:12345" )); 
		c.connect();
		
		BufferedReader sysin = new BufferedReader( new InputStreamReader( System.in ) );
		while(true){
			String in = sysin.readLine();
			c.sendWithFlag(in,"titi");
		}
	}

}