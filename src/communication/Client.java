package communication;

import java.io.IOException;
import java.net.Socket;

public class Client {
	
	private static Socket socket;
	
	public static void main(String[] args) {
		boolean connexion = false;
		int attente = 2000;
		
		do {
			try {
				socket = new Socket("127.0.0.1", 55555);
				
				System.out.println("> Connexion etablie avec succes...");
				connexion = true;
				
			} catch (IOException e) {
				try {
					Thread.sleep(attente);
					System.out.println("> Impossible de se connecter au serveur (" + attente/1000 + " secondes)...");
					attente = attente * 2;
				} catch (InterruptedException e1) {
					System.err.println("> Erreur de sleep...");
				}
			}
		} while(!connexion);
	}
	
}