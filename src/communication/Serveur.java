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
	
	/** Constructeurs privés */
	public Serveur( int port ) throws UnknownHostException {
		super( new InetSocketAddress( port ) );
		parties = new ArrayList<Partie>();
	}
	
	/** Instance unique non préinitialisée */
	private static Serveur INSTANCE = null;
 
	/** Point d'accès pour l'instance unique du singleton 
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
		ArrayList<Carte> test= new ArrayList<Carte>();
		test.add(new Carte(1));
		test.add(new Carte(2));
		test.add(new Carte(3));
		test.add(new Carte(4));
		
		ArrayList<User> users = new ArrayList<User>();
		users.add(new User("titi","tutu","tata"));
		users.add(new User("titi2","tutu2","tata2"));
		users.add(new User("titi3","tutu3","tata3"));
		ArrayList<Partie> testPartie = new ArrayList<Partie>();
		testPartie.add(new Partie(0,"toto1",3,true,users));
		testPartie.add(new Partie(1,"toto2",3,true,users));
		JSONObject jsonObject = JSONEncode.encodeListPartie(testPartie);
		JSONDecode.decodeListPartie(jsonObject);
		//System.out.println("getValue: "+ ((Carte) obj).getValue());

	}

	
	/* on parse le message afin de récuperer tout ce qui se trouve avant : */
	/* puis switch en fonction du flag */
	@Override
	public void onMessage( WebSocket conn, String message ) {
		String delims = "[:]";
		String[] tokens = message.split(delims);
		
		switch (tokens[0]) {
        case "newP":												//créer Partie
        	//parties.add(new Partie(tokens[1],tokens[2],tokens[3],tokens[4]));
            System.out.println("newP");
            break;
        case "joinP":												//rejoindre Partie
            System.out.println("joinP");
            break;
        case "quitP":												//quitter Partie
            System.out.println("quitP");
            break;
        case "getList":												//récupérer Liste Parties non lancées
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
