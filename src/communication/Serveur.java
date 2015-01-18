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
import java.util.Map.Entry;
import java.util.logging.Level;

import log.MonLogServer;
import metier.Carte;
import metier.Partie;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;

import utils.JSONDecode;
import utils.JSONEncode;

public class Serveur extends WebSocketServer {

	private HashMap<Integer, Partie> parties;
	public 	HashMap<Integer, Partie> getParties () { return parties; } //Test
 	private HashMap<String, WebSocket> players;
 	public 	HashMap<String, WebSocket> getPlayers () { return players; }

	/** Constructeurs privés */
	public Serveur(int port) throws UnknownHostException {
		super(new InetSocketAddress(port));
		parties = new HashMap<Integer, Partie>();
		players = new HashMap<String, WebSocket>();
	}

	/** Instance unique non préinitialisée */
	private static Serveur INSTANCE = null;

	/**
	 * Point d'accès pour l'instance unique du singleton
	 * 
	 * @throws UnknownHostException
	 */
	public static Serveur getInstance(int port) throws UnknownHostException {
		if (INSTANCE == null) {
			synchronized (Serveur.class) {
				if (INSTANCE == null) {
					INSTANCE = new Serveur(port);
					new MonLogServer().add("Instance du Serveur créée", Level.INFO);
				}
			}
		}
		return INSTANCE;
	}

	/* Send un message à l'user concerné pour simple affichage*/
	public void sendMessage(String nickName, String message){
		String mess = JSONEncode.encodeMessage(message);
		if(players.get(nickName) != null){
			players.get(nickName).send(mess);
		}
	}
	
	/* send un message a une liste de joueur */
	public void sendMessageListPlayers(List<String> nickNames, String message ,boolean withFlag){
		for(String joueur:nickNames){
			if (false == withFlag){
				sendMessage(joueur, message);
			}
			else{
				players.get(joueur).send(message);
			}
		}
	}
	
	public void selectRowToUser(String nickName, int id){
		JSONObject flag = new JSONObject();
		
		flag.put("nomFlag", Flag.SEND_LIGNE);
		flag.put("idParty", id);
		players.get(nickName).send(flag.toString());
		new MonLogServer().add("Le joueur " + nickName + " doit selectionner une ligne pour la partie " + id, Level.INFO);
	}
	
	public void selectRowToParty(String message){
		JSONObject obj = new JSONObject(message);
		int idParty = obj.getInt("idParty");
		int row = obj.getInt("row");
		
		Partie part = parties.get(idParty);
		
		if(null != part)
		{
			part.setChoosenRow(row);
			synchronized(part) {
				part.notify();
			}
			new MonLogServer().add("La ligne "+ row + " de la partie " + idParty + " a été selectionnée par un joueur", Level.INFO);
		}
	}

	/* Send la liste des cartes de l'user pour qu'il puisse choisir les cartes à jouer */
	public void sendCardToUser(String nickName, ArrayList<Integer> cards, int idPartie){
		String mess = JSONEncode.encodeSendCards(cards, idPartie);
		players.get(nickName).send(mess);
		new MonLogServer().add("La main du joueur " + nickName + " de la partie " + idPartie + " vient de lui être envoyée: " + mess, Level.INFO);
	}
	
	/* set dans la partie la carte choisie par le joueur pour le tour */
	public void sendCardToPartie(String message){
		JSONObject obj = new JSONObject(message);
		int idParty = obj.getInt("idParty");
		int value = obj.getInt("value");
		
		Partie part = parties.get(idParty);
		
		//TODO: ajouter le notify pour reveiller le thread
		if(null != part)
		{
			part.addSelectedCard(new Carte(value));
			List<Carte> list = part.getSelectedCardByPlayer();
			synchronized(list) {
				new MonLogServer().add("Carte de valeur " + value + " envoyée à la partie " + idParty + ", notify", Level.INFO);
				list.notify();
			}
		}
	}
	
	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		System.out.println(conn.getRemoteSocketAddress().getAddress()
				.getHostAddress()
				+ " connect to the game!");
		new MonLogServer().add(conn.getRemoteSocketAddress().getAddress()
				.getHostAddress()
				+ " connect to the game!", Level.INFO);
	}

	
	// Recherche dans la liste de toutes les Parties si le joueur y est présent
	public Partie getWichParty(String joueur){
		for(Entry<Integer, Partie> entry : parties.entrySet()) {
			Partie part = entry.getValue();
			List<String> joueurs = part.getListUser();
			for(String nickName: joueurs){
				if(joueur.equals(nickName)){
					return part;
				}
			}
		}
		return null;
	}
	
	
	// récupère dans la map le nickName correspondant à la socket fermée
	// supprime l'user de la liste des participants de la partie et previent les autres
	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		String res = "";
		
		MonLogServer monlog = new MonLogServer();
		for(Entry<String, WebSocket> entry:players.entrySet()){
			if (entry.getValue().equals(conn)){
				res = entry.getKey();
			}
		}
		
		monlog.add("Fermeture de socket détectée pour" + conn +" : " +res +" : " + reason, Level.WARNING);
		
		if ("" != res) {
			Partie party = getWichParty(res);
			if(party != null){
				party.removePlayer(res);
				players.remove(res);
				if (party.getListUser().size() < 2){
					System.out.println("partie annulée!");
					sendMessageListPlayers(party.getListUser(), "plus assez de joueurs, partie annulée", false);
					monlog.add("Nombre de joueur inférieur à 2, partie " + party.getId() + " annulée", Level.WARNING);
					parties.remove(party.getId());
				}
				else{
					sendMessageListPlayers(party.getListUser(), res + "has left the game", false);
					monlog.add(res + "has left the game", Level.INFO);
				}
			}	
		}
		
		System.out.println(res + " has left the game!");
	}

	public void createPartie(String message) {
		Partie party = JSONDecode.decodeCreatePartie(message);
		int idParty;
		if (parties.isEmpty()) {
			idParty = 1;
		} else {
			idParty = parties.size() + 1;
		}
		party.setId(idParty);
		parties.put(idParty, party);
		party.setServeur(this);
		party.start();
		new MonLogServer().add("Nouvelle Partie créée, id numéro: " + idParty+ " détail: " + message, Level.INFO);
		System.out.println("New party: " + party.getName() + " create");
	}

	public void joinPartie(WebSocket conn, String message) {
		String[] messPart = JSONDecode.decodeJoinParty(message);

		MonLogServer monLog = new MonLogServer();
		
		if (null != messPart) {
			Partie party = parties.get(Integer.parseInt(messPart[1]));
			
			if (null != party) {
				if (party.addPlayer(messPart[0]) == false) {
					conn.send(JSONEncode.encodeMessage("Partie déjà pleine"));
					monLog.add(messPart[0] + "a tenté de joindre la partie numéro "+ party.getId() +" cette dernière est déjà pleine, echec." , Level.INFO);
				}
				else{
					conn.send(JSONEncode.encodeMessage("Vous avez rejoins la partie numéro: " + messPart[1]));
					monLog.add(messPart[0] + "a rejoins la partie numéro "+ party.getId() , Level.INFO);
					synchronized(party){
						party.notify();
					}
				}
			}
			else{
				conn.send(JSONEncode.encodeMessage("La partie que vous essayez de rejoindre n'existe pas"));
				monLog.add(messPart[0] + "a tenté de joindre la partie numéro "+ party.getId() +" cette dernière n'existe pas, echec." , Level.INFO);
			}
		}
	}

	public void refreshListParties(String message){
		JSONObject obj = new JSONObject(message);
		String nickName = obj.getString("nickName");
		
		ArrayList<Partie> partiesEnCours = new ArrayList<Partie>();
		if(parties.isEmpty()){
			players.get(nickName).send(JSONEncode.refreshListPartie(null));
		}
		else{
		for(Entry<Integer, Partie> entry : parties.entrySet()) {
			Partie part = entry.getValue();
			if (part.isInGame() == false){
				partiesEnCours.add(part);
			}
		}
		}
		players.get(nickName).send(JSONEncode.refreshListPartie(partiesEnCours));
	}

	/* on parse le message afin de récuperer le flag */
	/* puis switch en fonction du flag */
	@Override
	public void onMessage(WebSocket conn, String message) {
		System.out.println("json: " + message);
		String flag = JSONDecode.getFlag(message);

		System.out.println("flag" + flag);
		
		new MonLogServer().add("communication reçue par le Server avec le flag: " + flag + "detail du message; " + message, Level.INFO);

		switch (flag) {
		case Flag.ON_CONNECT:
			players.put(JSONDecode
					.decodeConnect(message), conn);
			break;
		case Flag.REJOINDRE_PARTIE:
			joinPartie(conn,message);
			break;
		case Flag.CREATION_PARTIE:
			createPartie(message);
			break;
		case Flag.SEND_CARTE:
			sendCardToPartie(message);
			break;
		case Flag.SEND_LIGNE:
			selectRowToParty(message);
			break;
		case Flag.QUIT_PARTIE:
			break;
		case Flag.REFRESH_LIST_PARTIES:
			refreshListParties(message);
			break;
		case "getList":
			System.out.println("getList");
			break;
		default:
			System.out.println("Error: ce flag n'existe pas.");
		}
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		ex.printStackTrace();

		if (conn != null) {
			new MonLogServer().add("some errors like port binding failed may not be assignable to a specific websocket ", Level.SEVERE);

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
	public void sendToAll(String text) {
		Collection<WebSocket> con = connections();
		synchronized (con) {
			for (WebSocket c : con) {
				c.send(text);
			}
			new MonLogServer().add("Send to all client message: " + text, Level.INFO);
		}
	}

	public static void main(String[] args) throws InterruptedException,
			IOException {
		WebSocketImpl.DEBUG = true;
		int port = 12345;
		try {
			port = Integer.parseInt(args[0]);
		} catch (Exception ex) {
		}
		Serveur s = Serveur.getInstance(port);
		s.start();
		System.out.println("Le Serveur vient de démarrer sur le port: " + s.getPort());
		new MonLogServer().add("Le Serveur vient de démarrer sur le port: " + s.getPort(), Level.INFO);

		BufferedReader sysin = new BufferedReader(new InputStreamReader(
				System.in));
		while (true) {
			String in = sysin.readLine();
			System.out.println("in " + in);
			//s.sendToAll(in);

			if (in.equals("exit")) {
				new MonLogServer().add("Exit du serveur ", Level.WARNING);
				s.stop();
				break;
			} else if (in.equals("restart")) {
				new MonLogServer().add("Restart du serveur ", Level.WARNING);
				s.stop();
				s.start();
				break;
			}
		}
	}
}
