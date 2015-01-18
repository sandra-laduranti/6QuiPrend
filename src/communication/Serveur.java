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
				}
			}
		}
		return INSTANCE;
	}

	/* Send un message à l'user concerné pour simple affichage*/
	public void sendMessage(String nickName, String message){
		String mess = JSONEncode.encodeMessage(message);
		players.get(nickName).send(mess);
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
		}
	}

	/* Send la liste des cartes de l'user pour qu'il puisse choisir les cartes à jouer */
	public void sendCardToUser(String nickName, ArrayList<Integer> cards, int idPartie){
		String mess = JSONEncode.encodeSendCards(cards, idPartie);
		players.get(nickName).send(mess);
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
				list.notify();
			}
		}
	}
	
	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		System.out.println(conn.getRemoteSocketAddress().getAddress()
				.getHostAddress()
				+ " entered the room!");
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		this.sendToAll( conn + " has left the game!");
		// playerConnect.remove(); //TODO: remove du client dans la liste
		System.out.println(conn + " has left the game!");
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
		System.out.println("New party: " + party.getName() + " create");
	}

	public void joinPartie(WebSocket conn, String message) {
		String[] messPart = JSONDecode.decodeJoinParty(message);

		if (null != messPart) {
			Partie party = parties.get(Integer.parseInt(messPart[1]));
			
			if (null != party) {
				if (party.addPlayer(messPart[0]) == false) {
					conn.send(JSONEncode.encodeMessage("Partie déjà pleine")); // TODO: voir avec Julien pour message à renvoyer au client
				}
				else{
					conn.send(JSONEncode.encodeMessage("Vous avez rejoins la partie numéro: " + messPart[1]));
					synchronized(party){
						party.notify();
					}
				}
			}
			else{
				conn.send(JSONEncode.encodeMessage("La partie que vous essayez de rejoindre n'existe pas"));
			}
		}
	}

	public void removePartie(String message) {

	}

	/* on parse le message afin de récuperer le flag */
	/* puis switch en fonction du flag */
	@Override
	public void onMessage(WebSocket conn, String message) {
		System.out.println("json: " + message);
		String flag = JSONDecode.getFlag(message);

		System.out.println("flag" + flag);

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
			System.out.println("refresh");
			break;
		case "getList":
			System.out.println("getList");
			break;
		default:
			System.out.println("Error: ce flag n'existe pas.");
		}
		// this.sendToAll( message );
		// System.out.println( conn + ": " + message + " test trallalaal");
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		ex.printStackTrace();
		if (conn != null) {
			// some errors like port binding failed may not be assignable to a
			// specific websocket
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
		System.out.println("ChatServer started on port: " + s.getPort());

		BufferedReader sysin = new BufferedReader(new InputStreamReader(
				System.in));
		while (true) {
			String in = sysin.readLine();
			System.out.println("in " + in);
			//s.sendToAll(in);

			if (in.equals("exit")) {
				s.stop();
				break;
			} else if (in.equals("restart")) {
				s.stop();
				s.start();
				break;
			}
		}
	}
}
