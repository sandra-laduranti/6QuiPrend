package utils;

import java.util.ArrayList;

import metier.Carte;
import metier.Partie;
import metier.User;

import org.json.JSONObject;

public class JSONDecode {

	public static ArrayList<Partie> decodeListPartie(JSONObject jsonList) {
		String flag = jsonList.getString("nomFlag");
		org.json.JSONArray arr = jsonList.getJSONArray("arr");
		ArrayList<Partie> liste = new ArrayList<Partie>();
		
		for (int i = 0; i < arr.length(); i++) {
			int id = arr.getJSONObject(i).getInt("id");
			String nom = arr.getJSONObject(i).getString("nom");
			int nbJoueur = arr.getJSONObject(i).getInt("nbJoueur");
			Boolean isPromode = arr.getJSONObject(i).getBoolean("isPromode");
			String users = arr.getJSONObject(i).getString("users");
			String delims = "[:]";
			String[] tokens = users.split(delims);
			ArrayList<User> usersList = new ArrayList<User>();

			for (String nickName : tokens) {
				usersList.add(new User(nickName, "", ""));
			}

			liste.add(new Partie(id, nom, nbJoueur, isPromode, usersList));
		}

		return null;
	}

	public static ArrayList<Carte> decodeListCarte(JSONObject jsonList) {
		String flag = jsonList.getString("nomFlag");
		ArrayList<Carte> liste = new ArrayList<Carte>();
		org.json.JSONArray arr = jsonList.getJSONArray("arr");

		for (int i = 0; i < arr.length(); i++) {
			liste.add(new Carte(arr.getJSONObject(i).getInt("val")));
		}

		return null;
	}
}
