package communication;

import graphique.FenetrePrincipale;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import log.MonLogClient;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import utils.JSONEncode;

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
	
	
	@Override
	public void onOpen( ServerHandshake handshakedata ) {
		System.out.println( "opened connection" );
		send(JSONEncode.encodeConnect(userId).toString());
		// if you plan to refuse connection based on ip or httpfields overload: onWebsocketHandshakeReceivedAsClient
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
	public void onMessage( String message ) {
		JSONObject obj = new JSONObject();
		String delims = "[:]";
		String[] tokens = message.split(delims);
		
		switch (tokens[0]) {
		case "getNickName":
			obj.put("monFlag", Flag.GET_NICKNAME);
			obj.put("id", userId);
			this.send(obj.toString());
			break;
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
	
	//julien  mdp

	public static void main( String[] args ) throws URISyntaxException, IOException {
		
		
////////////// POUR LES TESTS SUR LE GRAPHIQUES, LAISSER DECOMMENTE LES LIGNES SUIVANTES ///////////////////////		
		/**
         * Set look and feel of app
         */
        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, you can set the GUI to another look and feel.
        }
        
		Object sync = new Object();
		FenetrePrincipale fenetre = new FenetrePrincipale(sync);
		
		synchronized (sync) {
			try {
				sync.wait();
			} catch (InterruptedException e) {
				new MonLogClient().add(e.getMessage());
				System.out.println(e.getMessage());
			}
		}
		User user = null;
		try {
			user = new User( new URI( "ws://localhost:12345" ));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		user.connect();
		fenetre.setUser(user);
		int id = fenetre.getIdUser();
		
		BufferedReader sysin = new BufferedReader( new InputStreamReader( System.in ) );
		while(true){
			String in = null;
			try {
				in = sysin.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			user.sendWithFlag(in,"titi");
		}
	}

}