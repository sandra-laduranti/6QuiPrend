package communication;

import graphique.FenetrePrincipale;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import log.MonLogClient;
import log.MonLogServer;
import metier.Partie;
import metierDAO.UserDAO;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import utils.JSONDecode;
import utils.JSONEncode;

public class User extends WebSocketClient implements Comparable<User> {

	private transient int userId;

	private String userNickname;

	private String userEmail;

	private String userPassword;

	private int currentBeef;

	private boolean isConsole = false;
	private static FenetrePrincipale fenetre;
	private static Object sync;

	public User(URI serverUri, Draft draft) {
		super(serverUri, draft);
	}

	public User(URI serverURI) {
		super(serverURI);
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
		System.out.println("opened connection");
		send(JSONEncode.encodeConnect(userNickname));
		new MonLogServer().add("Connection ouverte", Level.INFO);
	}

	public void setConsole(boolean isConsole) {
		this.isConsole = isConsole;
	}

	public int getUserId() {
		return this.userId;
	}

	public String getUserNickname() {
		return this.userNickname;
	}

	public String getUserEmail() {
		return this.userEmail;
	}

	public int getCurrentBeef() {
		return this.currentBeef;
	}

	public void setCurrentBeef(int currentBeef) {
		this.currentBeef = currentBeef;
	}

	public void setUser(int userId, String userNickname) {
		this.userId = userId;
		this.userNickname = userNickname;
		this.currentBeef = 0;
	}

	public int compareTo(User user) {
		if (this.getCurrentBeef() > user.getCurrentBeef()) {
			return 1;
		} else if (this.getCurrentBeef() < user.getCurrentBeef()) {
			return -1;
		} else {
			return 0;
		}
	}

	public void refreshListPartie() {
		JSONObject flag = new JSONObject();
		flag.put("nomFlag", Flag.REFRESH_LIST_PARTIES);
		flag.put("nickName", userNickname);
		send(flag.toString());
	}

	// voir comment faire pour que l'ui recup l'info? Synchronize? appeler une
	// méthode de l'ui?
	public void recupListPartie(String message) {
		ArrayList<Partie> parties = JSONDecode.decodeRefreshListePartie(message);
		fenetre.setListe_parties_affichees(parties);
		synchronized (sync) {
			sync.notify();
		}
	}

	// donne la liste des cartes et send celle choisie
	// le nombre d'essai est limité à 5 pour ne pas faire attendre indéfiniment
	// les autres joueurs
	// ! premier element de la liste est l'id de la partie !
	public void chooseCard(ArrayList<Integer> cards) {
		int cardValue = 0;
		int idParty = cards.get(0);
		cards.remove(0);
		Scanner sysin = new Scanner(System.in);

		System.out.print("[ ");
		for (Integer c : cards) {
			System.out.print("(" + c + ")");
		}
		System.out.println(" ]");

		for (int i = 0; i < 5; i++) {
			if (isConsole == true) {
				cardValue = sysin.nextInt();
			} else {
				// ICI DOIT RECUP LA VALEUR EN MODE UI
			}
			if (cards.contains(cardValue)) {
				System.out.println("Vous avez choisi la carte" + cardValue);
				send(JSONEncode.encodeCarte(userNickname, cardValue, idParty));
				return;
			} else {
				System.out
						.println("Erreur cette Carte n'existe pas dans votre main");
			}
		}
		cardValue = Collections.max(cards);
		System.out
				.println("Vous vous êtes trompé de carte trop de fois! Nous choisissons la carte "
						+ cardValue + " à votre place :D ");
		send(JSONEncode.encodeCarte(userNickname, cardValue, idParty));
	}

	public void chooseLine(int idPartie) {
		Scanner sysin = new Scanner(System.in);
		int row = 1;
		System.out
				.println("Votre carte ne peut être placée \n Choisissez une rangée à prendre entre 1 et 4");

		while (true) {
			if (isConsole == true) {
				row = sysin.nextInt();
			} else {
				// SI MODE UI RECUP ICI VALEUR
			}
			if (row < 1 || row > 4) {
				System.out
						.println("la ligne que vous avez choisi n'existe pas! Merci de rentrer une valeur entre 1 et 4");
			} else {
				send(JSONEncode.encodeSendRow(userNickname, idPartie, row));
				return;
			}
		}
	}

	@Override
	public void onMessage(String message) {
		System.out.println("json: " + message);
		String flag = JSONDecode.getFlag(message);

		switch (flag) {
		case Flag.SEND_CARTE:
			chooseCard(JSONDecode.decodeSendCards(message));
			break;
		case Flag.SEND_LIGNE:
			JSONObject json = new JSONObject(message.trim());
			chooseLine(json.getInt("idParty"));
			break;
		case Flag.MESSAGE:
			System.out.println(JSONDecode.decodeMessage(message));
			break;
		case Flag.PARTIE_COMMENCE:
			System.out.println("La partie commence! Bon jeu :)");
			break;
		case Flag.REFRESH_LIST_PARTIES:
			recupListPartie(message);
			break;
		default:
			System.out.println("Error: ce flag n'existe pas.");
		}
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		// The codecodes are documented in class
		// org.java_websocket.framing.CloseFrame
		System.out.println("Connection closed by "
				+ (remote ? "remote peer" : "us"));
	}

	@Override
	public void onError(Exception ex) {
		ex.printStackTrace();
		// if the error is fatal then onClose will be called additionally
	}

	/**
	 * Envoie au serveur la partie qui vient d'être crée, par le joueur nomUser
	 * 
	 * @param namePartie
	 * @param nbMaxJoueurs
	 * @param proMode
	 * @param nomUser
	 */
	public void sendCreationPartie(String namePartie, int nbMaxJoueurs,
			boolean proMode, String nomUser) {
		send(JSONEncode.encodeCreatePartie(namePartie, nbMaxJoueurs, proMode,
				nomUser));
	}

	/**
	 * Demande au serveur si il peut rejoindre la partie
	 * 
	 * @param nomUser
	 * @param parseInt
	 */
	public void sendJoinParty(String nomUser, int idParty) {
		send(JSONEncode.encodeJoinParty(nomUser, idParty));
	}

	
	//Pour decommenter simplement rajouter // devant le /etoile de la ligne en dessous
	/*
	  public static void main(String[] args) throws URISyntaxException,
	  IOException {
	  
	  // Ce bloc sert uniquement à avoir un affichage des éléments plus "jolie" 
		  try { 
				for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
					if ("Nimbus".equals(info.getName())) {
						  UIManager.setLookAndFeel(info.getClassName()); 
						  break; 
					} 
				} 
				
			} catch (Exception e) { 
				new MonLogClient().add("If Nimbus is not available, you can set the GUI to another look and feel.",Level.SEVERE); 
			}
				  
				  
			sync = new Object(); 
			fenetre = new FenetrePrincipale(sync);
			  
			synchronized (sync) { 
				try { 
					sync.wait(); 
				} catch (InterruptedException e){
					new MonLogClient().add(e.getMessage(),Level.SEVERE);
				}
			}

			try{
				User usr = new User(new URI("ws://localhost:12345"));
				usr.userId = fenetre.getIdUser();
				usr.userNickname = fenetre.getNomUser();
				fenetre.setUser(usr);
				usr.connect();
				new MonLogClient().add("Bienvenue "+usr.userNickname+" !",Level.FINE);
			} catch (URISyntaxException e) {
				new MonLogClient().add(e.getMessage(),Level.SEVERE);
			}

	  
	  } //*/
	 

	//Pour commenter simplement => retirer le // sur ligne en dessous
	 ///*
	public static void main(String[] args) throws URISyntaxException,
			IOException {
		// ici authentification;
		// fenetre.getId
		// fenetre .getNickName
		// uri Fanfan: 192.168.1.29
		User usr = new User(new URI("ws://localhost:12345"));

		Random random = new Random(System.nanoTime());
		usr.userId = random.nextInt();
		usr.userNickname = "toto" + usr.userId;
		usr.connect();

		System.out.println("bienvenue " + usr.userNickname);

		BufferedReader sysin = new BufferedReader(new InputStreamReader(
				System.in));
		while (true) {
			String in = sysin.readLine();
			if (in.equals("a")) {
				System.out.println("send create");
				usr.send(JSONEncode.encodeCreatePartie(usr.userNickname
						+ "Party", 3, true, usr.userNickname));
			}
			if (in.equals("b")) {
				System.out.println("send join");
				usr.send(JSONEncode.encodeJoinParty(usr.userNickname, 1));
			}
			if (in.equals("c")) {
				usr.send(JSONEncode.encodeCreatePartie(usr.userNickname
						+ "Party", 2, true, usr.userNickname));
			}
			if (in.equals("d")) {
				System.out.println("send join");
				usr.send(JSONEncode.encodeJoinParty(usr.userNickname, 2));
			}
		}
	}
	// */

}
