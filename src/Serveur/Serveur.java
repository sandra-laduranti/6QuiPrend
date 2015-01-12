package Serveur;



import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class Serveur {
	
	/* attributs classe */
	private static int NB_MAX_CLIENTS = 500;
	private static int NB_THREAD_POOL = 10;
	private static ServerSocket serveur;
	private static Socket socket;
	private List<Socket> clients;

	/* Singleton */
	/* Instance unique non préinitialisée */
	private static Serveur INSTANCE = null;

	/* Constructeur privé */
	private Serveur() {
		clients = new LinkedList <Socket>();
	}

	/* Point d'accès pour l'instance unique du singleton */
	public static Serveur getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new Serveur();
		}
		return INSTANCE;
	}
	
	public void handleServer(){
		try {
			serveur = new ServerSocket(55555);
			System.out.println("> Lancement du serveur...");
			
			while(true) {
				socket = serveur.accept();
				if (clients.size() < NB_MAX_CLIENTS) {
					clients.add(socket);
					new Lecture(socket, clients).start();
				}
				else {
					System.out.println("> Trop de joueurs connectés, merci de reessayer ultérieurement");
					socket.close();
				}
			}
		} catch (IOException e) {
			System.err.println("> Erreur de lancement du serveur...");
		}
	}
	
}
