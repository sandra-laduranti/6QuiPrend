package Serveur;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;

public class Lecture extends Thread {
	
	private Socket socket;
	private Ecriture ecriture;
	private InputStream inputStream;
	private InputStreamReader inputStreamReader;
	private BufferedReader bufferedReader;
	private List <Socket> clients;
	
	public Lecture() { }
	
	public Lecture(Socket socket, List <Socket> clients) {
		this.socket = socket;
		this.clients = clients;
		this.ecriture = new Ecriture(socket, clients);
	}
	
	/*public void setLecture(Socket socket, List <Socket> clients) {
		this.socket = socket;
		this.clients = clients;
		this.ecriture = new Ecriture(socket, clients);
	}*/

	@Override
	public void run() {
		
		try {
			this.wait();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		String message;
		try {
			this.inputStream = this.socket.getInputStream();
			this.inputStreamReader = new InputStreamReader(this.inputStream);
			this.bufferedReader = new BufferedReader(this.inputStreamReader);
			
			while(true) {
				message = this.bufferedReader.readLine();
				if (message == null) { // Deconnexion d'un client
					this.clients.remove(this.socket);
					this.socket.close();
					System.out.println("> Deconnexion d'un client...");
					break;
				}
				else {
					this.ecriture.envoyerClient(message);
					System.out.println("Client > " + message);
				}
			}
		} catch (IOException e) {
			System.err.println("> Erreur de lecture sur le serveur...");
		}
	}

}

