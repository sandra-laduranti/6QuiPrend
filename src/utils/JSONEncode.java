package utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import metier.Carte;



import metier.Partie;
import metier.User;

import org.json.JSONObject;
import org.json.JSONArray;

public class JSONEncode {

		public static JSONObject encodeCarte(){
			return null;
		}
		
		public static JSONObject encodeListPartie(ArrayList<Partie> liste){
			JSONObject flag = new JSONObject();
			JSONArray  arr = new JSONArray();
			
			
			/* TODO: rajouter promod? */
			flag.put("nomFlag", "flagtrululu");
			for(Partie partie : liste){
				JSONObject tmp = new JSONObject();
				StringBuffer usersBuff = new StringBuffer("");
				tmp.put("id", partie.getId());
				tmp.put("nom", partie.getNom());
				tmp.put("nbJoueur", partie.getNbJoueursMax());
				tmp.put("isPromode", partie.isProMode() );
				List<User> users = partie.getListUser();
				JSONArray arrUser = new JSONArray();
				for(User user : users){
					usersBuff.append(":" + user.getUserNickname());
				}
				tmp.put("users", usersBuff.toString());
				arr.put(tmp);
			}
			flag.append("arr", arr);
			
			return flag;
		}
		
		
		public static JSONObject encodeListCarte(ArrayList<Carte> liste){
			JSONObject flag = new JSONObject();
			JSONArray  arr = new JSONArray();
			
		      flag.put("nomFlag", "flag");
		      for(Carte carte : liste){
		    	  JSONObject tmp = new JSONObject();
		    	  tmp.put("val", carte.getValue());
		    	  arr.put(tmp);
		      }
		      flag.put("arr", arr);
	
		      return flag;
		}
		

}
