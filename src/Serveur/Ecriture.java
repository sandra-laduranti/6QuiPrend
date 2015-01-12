package Serveur;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;

public class Ecriture extends Thread {
	
	private Socket socket;
	private List <Socket> clients;
	private OutputStream outputStream;
	private OutputStreamWriter outputStreamWriter;
	private BufferedWriter bufferedWriter;
	
	public Ecriture(Socket socket, List <Socket> clients) {
		this.socket = socket;
		this.clients = clients;
	}

	public void envoyerClient(String texte) {
		if (!this.socket.isClosed()) {
			for (int i = 0; i < clients.size(); i++) {
				if(clients.get(i) != this.socket) {
					try {
						this.outputStream = clients.get(i).getOutputStream();
						this.outputStreamWriter = new OutputStreamWriter(outputStream);
						this.bufferedWriter = new BufferedWriter(outputStreamWriter);
						
						bufferedWriter.write(texte);
						bufferedWriter.newLine();
						bufferedWriter.flush();
					}
					catch (IOException e) {
						System.err.println("> Erreur d'envoi au(x) client(s)...");
					}
				}	
			}
		}
	}

}
