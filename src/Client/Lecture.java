package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class Lecture extends Thread {
	
	private Socket socket;
	private InputStreamReader isr;
	private BufferedReader br;
	private InputStream is;
	
	public Lecture(Socket socket) {
		this.socket = socket;
	}
	
	@Override
	public void run() {
		try {
			String message;
			this.is = this.socket.getInputStream();
			this.isr = new InputStreamReader(this.is);
			this.br = new BufferedReader(this.isr);
			
			while(true) {
				message = br.readLine();
				if (message == null) { // Deconnexion du serveur
					socket.close();
					System.out.println("> Deconnexion du serveur...");
					break;
				}
				else {
					System.out.println("Recu > " + message);
				}
			}
		} catch (IOException e) {
			System.err.println("> Erreur de lecture du flux...");
		}
	}

}