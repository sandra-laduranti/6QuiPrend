package communication;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import metier.Carte;
import metier.Partie;
import metier.User;
import utils.JSONDecode;
import utils.JSONEncode;




import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;


public class Serveur extends WebSocketServer{
	
	/*TODO: ajout Singleton */
	private List<Partie> parties;
	
	/** Constructeurs priv�s */
	public Serveur( int port ) throws UnknownHostException {
		super( new InetSocketAddress( port ) );
		parties = new ArrayList<Partie>();
	}
	
	/** Instance unique non pr�initialis�e */
	private static Serveur INSTANCE = null;
 
	/** Point d'acc�s pour l'instance unique du singleton 
	 * @throws UnknownHostException */
	public static Serveur getInstance(int port) throws UnknownHostException
	{	
		if (INSTANCE == null)
		{ 	
			synchronized(Serveur.class)
			{
				if (INSTANCE == null)
				{	INSTANCE = new Serveur(port);
				}
			}
		}
		return INSTANCE;
	}

	@Override
	public void onOpen( WebSocket conn, ClientHandshake handshake ) {
		this.sendToAll( "new connection: " + handshake.getResourceDescriptor() );
		System.out.println( conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!" );
	}

	@Override
	public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
		this.sendToAll( conn + " has left the room!" );
		System.out.println( conn + " has left the room!" );
	}
	
	public void addPartie(String[] param){
		//String nom, int nbJoueurs, boolean isProMode, User user
		
		
	}
	
	public static void testJSON() throws IOException{
		ArrayList<Partie> test= new ArrayList<Partie>();
		ArrayList<Partie> test2;
		ArrayList<User> users = new ArrayList<User>();
		ArrayList<User> users2 = new ArrayList<User>();
		users.add(new User("Sarah", "Sarah@youpron.com", "lol"));
		users.add(new User("Robert", "Robert@youpron.com", "lolo"));
		users.add(new User("PouletCitron", "PouletCitron@youpron.com", "lolol"));
		users2.add(new User("Sarah2", "Sarah2@youpron.com", "lol"));
		users2.add(new User("Robert2", "Robert2@youpron.com", "lolo"));
		users2.add(new User("PouletCitron2", "PouletCitron2@youpron.com", "lolol"));
		test.add(new Partie(1, "camion", 2, false, users));
		test.add(new Partie(2, "voiture", 5, false, users2));
		
		JSONObject testpartie = JSONEncode.encodeListPartie(test);
		System.out.println("Encode : " + testpartie);
		//test2=JSONDecode.decodeListPartie(testpartie);
		test2 = JSONDecode.decodeListPartie(testpartie.toString());
		//System.out.println(test2);
	}

	
	/* on parse le message afin de r�cuperer tout ce qui se trouve avant : */
	/* puis switch en fonction du flag */
	@Override
	public void onMessage( WebSocket conn, String message ) {
			//	String delims = "[:]";
			//	String[] tokens = message.split(delims);
				//JSONObject messageObject = message.
				//JSONDecode.getFlag();
				
		String titi = "tutu";
		
		
				/*CREATION_PARTIE = 0;
			public static final int REFRESH_LIST_PARTIES = 1;
			public static final int REJOINDRE_PARTIE = 2;
			public static final int PARTIE_COMMENCE = 3;
			public static final int SEND_CARTE = 4;
			public static final int TROP_TARD_POUR_CARTE = 5;
			public static final int SEND_LIGNE = 6;
			public static final int TROP_TARD_POUR_LIGNE = 7;
			public static final int REFRESH_BEEF = 8;
			public static final int REFRESH_LIGNES = 9;
			public static final int CARTE_ADVERSAIRES = 10;
			public static final int GAGNANT = 11;
			public static final int PERDANT = 12;
			*/
				
				switch (titi) {
		        case Flag.REJOINDRE_PARTIE:
		        	//parties.add(new Partie(tokens[1],tokens[2],tokens[3],tokens[4]));
		            System.out.println("newP");
		            break;
		        case Flag.CREATION_PARTIE:
		        	//parties.add(decodePartie(message));
		            System.out.println("joinP");
		            break;
		        case "quitP":
		            System.out.println("quitP");
		            break;
		        case "getList":
		            System.out.println("getList");
		            break;
		        default:
		            System.out.println("Error: ce flag n'existe pas.");
		        }
				//this.sendToAll( message );
				//System.out.println( conn + ": " + message + " test trallalaal");
	}
			

	@Override
	public void onError( WebSocket conn, Exception ex ) {
		ex.printStackTrace();
		if( conn != null ) {
			// some errors like port binding failed may not be assignable to a specific websocket
		}
	}
	

	/**
	 * Sends <var>text</var> to all currently connected WebSocket clients.
	 * 
	 * @param text
	 *            The String to send across the network.
	 * @throws InterruptedException
	 *             When socket related I/O errors occur.
	 */
	public void sendToAll( String text ) {
		Collection<WebSocket> con = connections();
		synchronized ( con ) {
			for( WebSocket c : con ) {
				c.send( text );
			}
		}
	}
	
	
	public static void main( String[] args ) throws InterruptedException , IOException {
		testJSON();
		/*WebSocketImpl.DEBUG = true;
		int port = 12345; 
		try {
			port = Integer.parseInt( args[ 0 ] );
		} catch ( Exception ex ) {
		}
		Serveur s = Serveur.getInstance( port );
		s.start();
		System.out.println( "ChatServer started on port: " + s.getPort() );

		BufferedReader sysin = new BufferedReader( new InputStreamReader( System.in ) );
		while ( true ) {
			String in = sysin.readLine();
			System.out.println("in "+ in);
			s.sendToAll( in );
			if( in.equals( "exit" ) ) {
				s.stop();
				break;
			} else if( in.equals( "restart" ) ) {
				s.stop();
				s.start();
				break;
			}
		}*/
	}
}
