package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class Lecture extends Thread {
	
	private Socket socket;
	private InputStreamReader inputStreamReader;
	private BufferedReader bufferedReader;
	private InputStream inputStream;
	
	public Lecture(Socket socket) {
		this.socket = socket;
	}
	
	@Override
	public void run() {
		try {
			String message;
			this.inputStream = this.socket.getInputStream();
			this.inputStreamReader = new InputStreamReader(this.inputStream);
			this.bufferedReader = new BufferedReader(this.inputStreamReader);
			
			while(true) {
				message = bufferedReader.readLine();
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