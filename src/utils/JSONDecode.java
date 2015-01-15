package utils;

import java.util.ArrayList;

import metier.Carte;
import metier.Partie;
import metier.User;

import org.json.JSONArray;
import org.json.JSONObject;

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
			System.out.println("id :" + id);
			String nom = objTMP.getString("nom");
			System.out.println("nom :" + nom);
			int nbJoueur = objTMP.getInt("nbJoueur");
			System.out.println("nbJoueur :" + nbJoueur);
			Boolean isPromode = objTMP.getBoolean("isPromode");
			String users = objTMP.getString("users");
			String delims = "[:]";
			String[] tokens = users.split(delims);
			ArrayList<User> usersList = new ArrayList<User>();

			for (String nickName : tokens) {
				usersList.add(new User(nickName, "", ""));
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
	
	public static String[] decodeCreatePartie(String message){
		
		
		return null;
	}
}
