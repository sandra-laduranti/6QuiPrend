package utils;

import java.util.ArrayList;

import metier.Carte;
import metier.Partie;

import org.json.JSONArray;
import org.json.JSONObject;

import communication.Flag;
import communication.User;

public class JSONDecode {

	
	public static String getFlag(String message){
		JSONObject testObj = new JSONObject(message);
		
		return(testObj.getString("nomFlag"));
	}
	
	
	
	//Creer une liste de copies de parties destin�e � l'affichage de l'ui
		public static ArrayList<Partie> decodeRefreshListePartie(String json) {
			if (json.equals("isEmpty")){
				return null;
			}
			ArrayList<Partie> parties = new ArrayList<Partie>();
			JSONObject testObj = new JSONObject(json);
			JSONArray arr = testObj.getJSONArray("arr");

			
			for (int i = 0; i < arr.getJSONArray(0).length(); i++) {
				JSONObject objTMP =  arr.getJSONArray(0).getJSONObject(i);
				int id = objTMP.getInt("id");
				String nom = objTMP.getString("nom");
				int nbJoueur = objTMP.getInt("nbJoueur");
				Boolean isPromode = objTMP.getBoolean("isPromode");
				String users = objTMP.getString("usersNickName");
				String delims = "[:]";
				String[] usersNickName = users.split(delims);
				
				parties.add(new Partie(id,nom,nbJoueur,isPromode,usersNickName));
			}
			
			return parties;
		}


	public static ArrayList<Carte> decodeListCarte(JSONObject jsonList) {
		String flag = jsonList.getString("nomFlag");
		ArrayList<Carte> liste = new ArrayList<Carte>();
		org.json.JSONArray arr = jsonList.getJSONArray("arr");

		for (int i = 0; i < arr.length(); i++) {
			liste.add(new Carte(arr.getJSONObject(i).getInt("val")));
		}

		return liste;
	}
	
	public static ArrayList<Integer> decodeSendCards(String message){
		JSONObject messageObj = new JSONObject(message);
		ArrayList<Integer> cards = new ArrayList<Integer>();

		JSONArray arr = messageObj.getJSONArray("arr");
		cards.add(messageObj.getInt("idPartie"));
		for (int i = 0; i < arr.length(); i++) {
			cards.add(arr.getJSONObject(i).getInt("value"));
		}

		return cards;
	}
	
	
	
	public static Partie decodeCreatePartie(String message){
	
		JSONObject messageObj = new JSONObject(message);
		String nom = messageObj.getString("nom");
		int nbJoueurs = messageObj.getInt("nbJoueurs");
		boolean isPromode = messageObj.getBoolean("isPromode");
		String userName = messageObj.getString("userName");

		return new Partie(nom, nbJoueurs, isPromode, userName);
	}
	
	public static String decodeConnect(String message){
		JSONObject messageObj = new JSONObject(message);
		String name = messageObj.getString("nickName");
		
		return name;
	}
	
	public static int decodeQuitParty(String message){
		JSONObject messageObj = new JSONObject(message);
		int id = messageObj.getInt("idPartie");
		
		return id;
	}
	
	public static String[] decodeJoinParty(String message){
		JSONObject messageObj = new JSONObject(message);
		
		String[] messParam = new String[2];
		messParam[0] = messageObj.getString("nickName");
		messParam[1] = messageObj.getInt("idParty")+"";
		
		return messParam;
	}
	
	public static String decodeMessage(String message){
		JSONObject messageObj = new JSONObject(message);
		
		return messageObj.getString("message");
	}
}
