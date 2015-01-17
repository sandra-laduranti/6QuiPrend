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
	
	public static ArrayList<Partie> decodeListPartie(String List) {
		ArrayList<Partie> liste = new ArrayList<Partie>();
		JSONObject testObj = new JSONObject(List);
		JSONArray arr = testObj.getJSONArray("arr");

		for (int i = 0; i < arr.getJSONArray(0).length(); i++) {
			JSONObject objTMP =  arr.getJSONArray(0).getJSONObject(i);
			int id = objTMP.getInt("id");
			String nom = objTMP.getString("nom");
			int nbJoueur = objTMP.getInt("nbJoueur");
			Boolean isPromode = objTMP.getBoolean("isPromode");
			String users = objTMP.getString("users");
			String delims = "[:]";
			String[] tokens = users.split(delims);
			ArrayList<User> usersList = new ArrayList<User>();

			for (String nickName : tokens) {
				// TODO usersList.add(new User(nickName, "", ""));
			}
			liste.add(new Partie(id, nom, nbJoueur, isPromode, usersList));
		}
		
		return liste;
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
	
	public static Partie decodeCreatePartie(String message){
	
		JSONObject messageObj = new JSONObject(message);
		String nom = messageObj.getString("nom");
		int nbJoueurs = messageObj.getInt("nbJoueurs");
		boolean isPromode = messageObj.getBoolean("isPromode");
		String userName = messageObj.getString("userName");

		//return new Partie(nom, nbJoueurs, isPromode, userName);
		return null;
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
}
