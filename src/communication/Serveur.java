package communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import metier.Carte;
import metier.Partie;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import utils.JSONDecode;


public class Serveur extends WebSocketServer{
	
	private HashMap<Partie, Integer> parties;
	private List<PlayerConnect> playerConnect;
	
	/** Constructeurs privés */
	public Serveur( int port ) throws UnknownHostException {
		super( new InetSocketAddress( port ) );
		parties = new HashMap<Partie, Integer>();
		playerConnect = new ArrayList<PlayerConnect>();
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
		//playerConnect.remove(); //TODO: remove du client dans la liste
		System.out.println( conn + " has left the room!" );
	}
	
	public void createPartie(String message){
		Partie party = JSONDecode.decodeCreatePartie(message);
		int idParty = party.getIdPartie(); //TODO: changer en générant l'id dans le serveur et en le set dans partie
		parties.put(party,idParty);
		System.out.println("partie: " + parties.get(party));
	}
	
	public void removePartie(String message){
		
	}

	
	/* on parse le message afin de récuperer le flag*/
	/* puis switch en fonction du flag */
	@Override
	public void onMessage( WebSocket conn, String message ) {
		System.out.println("json: " + message);
		String flag = JSONDecode.getFlag(message);
		
		System.out.println("flag" + flag);
		
				
				switch (flag) {
				case Flag.ON_CONNECT:
					playerConnect.add(new PlayerConnect(conn,JSONDecode.decodeConnect(message)));
					break;
		        case Flag.REJOINDRE_PARTIE:
		            System.out.println("joinP");
		            break;
		        case Flag.CREATION_PARTIE:
		        	createPartie(message);
		            System.out.println("newP");
		            break;
		        case Flag.QUIT_PARTIE:
		        	break;
		        case Flag.REFRESH_LIST_PARTIES:
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
		WebSocketImpl.DEBUG = true;
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
		}
	}
}
