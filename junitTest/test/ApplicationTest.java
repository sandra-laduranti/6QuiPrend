package test;

import static org.junit.Assert.*;

import java.net.URI;
import java.util.HashMap;

import metier.Partie;

import org.java_websocket.WebSocket;
import org.junit.Test;

import utils.JSONEncode;
import communication.Serveur;
import communication.User;

public class ApplicationTest {
	
	@Test
	public void test () {
		try
		{
			int port 	= 12345;
			String url 	= "ws://localhost:" + Integer.toString( port );
			URI uri 	= new URI ( url );
	
			if (null != uri
					&& 1024 < port) {
				
				System.out.println( "Test : instanciating ..." );
				
				Serveur server 	= Serveur.getInstance(port);
				User toto 		= new User ( uri );
				User slip 		= new User ( uri );
				
				if (null != server
						&& null != toto
						&& null != slip) {
					
					System.out.println( "Test : setting up users name and id" );
					
					toto.setUser( 1, "toto" );
					slip.setUser( 2, "slip" );
						
					System.out.println( "Test : Launching server" );
					server.start();
					
					System.out.println( "Test : Users connects ...");
					toto.connect();
					slip.connect();
					
					synchronized(this) {
						this.wait(1000);
					}
					
					System.out.println( "Test : toto sends createParty" );
					toto.send(
							JSONEncode.encodeCreatePartie(
									toto.getUserNickname() + "Party",
									2,
									true,
									toto.getUserNickname()
							)
					);
					
					System.out.println( "Test : slip joins toto Party" );
					slip.send(JSONEncode.encodeJoinParty(
							slip.getUserNickname(),
							1));
					
					synchronized(this) {
						this.wait(1000);
					}
					
					HashMap <Integer, Partie> serverMap = server.getParties();
					HashMap<String, WebSocket> playersMap = server.getPlayers();
					
					System.out.println( serverMap );
					System.out.println( playersMap );

					assertEquals ( 1, serverMap.size() );
					assertEquals ( 2, playersMap.size() );
				}
			}
		} catch ( Throwable e ) {
			StringBuilder builder = new StringBuilder ();
			if(null != builder) {
				builder.append( "Exception catched" );
				if(null != e)
				{
					builder.append( "Message : " + e.getMessage() );
					builder.append( "StackTrace : " + e.getStackTrace() );
				}
				fail( builder.toString() );
			}
		}
	}
}
