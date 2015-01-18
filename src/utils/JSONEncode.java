package utils;

import java.util.ArrayList;
import java.util.List;

import metier.Carte;
import metier.Partie;

import org.json.JSONArray;
import org.json.JSONObject;

import communication.Flag;
import communication.User;

public class JSONEncode {

		public static String encodeCarte(String nickName, int value, int idParty){
			JSONObject flag = new JSONObject();
			
			flag.put("nomFlag", Flag.SEND_CARTE);
			flag.put("nickName", nickName);
			flag.put("value", value);
			flag.put("idParty", idParty);
			
			return flag.toString();
		}
		
		/*Créer un string contenant toutes les parties et les joueurs inscrits */
		public static String refreshListPartie(ArrayList<Partie> parties){
			JSONObject flag = new JSONObject();
			JSONArray  arr = new JSONArray();
			
			if (parties.isEmpty()){
				return ("isEmpty");
			}
			flag.put("nomFlag", Flag.REFRESH_LIST_PARTIES);
			for(Partie partie: parties){
				JSONObject tmp = new JSONObject();
				StringBuffer usersBuff = new StringBuffer("");
				tmp.put("id", partie.getId());
				tmp.put("nom", partie.getNom());
				tmp.put("nbJoueur", partie.getNbJoueursMax());
				tmp.put("isPromode", partie.isProMode() );
				List<String> joueurs = partie.getListUser();
				for(String nickName: joueurs){
					usersBuff.append(nickName+":");
				}
				tmp.put("usersNickName",usersBuff);
				arr.put(tmp);
			}
			flag.put("arr", arr);
			return flag.toString();
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
		
		public static String encodeCreatePartie(String nom, int nbJoueurs, boolean isPromode, String userName){
			JSONObject flag = new JSONObject();

		      flag.put("nomFlag", Flag.CREATION_PARTIE);
		      flag.put("nom", nom);
		      flag.put("nbJoueurs", nbJoueurs);
		      flag.put("isPromode", isPromode);
		      flag.put("userName", userName);
	
		      return flag.toString();
		}
		
		public static String encodeConnect(String nickName){
			JSONObject flag = new JSONObject();
			
			flag.put("nomFlag", Flag.ON_CONNECT);
			flag.put("nickName", nickName);
			
			return flag.toString();
			
		}
		
		public static String encodeSendCards(ArrayList<Integer> cards, int idPartie){
			JSONObject flag = new JSONObject();
			JSONArray cardsArr = new JSONArray();			
			
			flag.put("nomFlag", Flag.SEND_CARTE);
			flag.put("idPartie", idPartie);
			for(Integer c: cards){
				JSONObject tmp = new JSONObject();
				cardsArr.put(tmp.put("value",c));
			}
			flag.put("arr",cardsArr);
			return flag.toString();
		}
		
		
		
		public static String encodeJoinParty(String nickName, int idParty){
			JSONObject flag = new JSONObject();
			
			flag.put("nomFlag", Flag.REJOINDRE_PARTIE);
			flag.put("nickName", nickName);
			flag.put("idParty", idParty);
			
			return flag.toString();
		}

		public static String encodeMessage(String message){
			JSONObject flag = new JSONObject();
			
			flag.put("nomFlag", Flag.MESSAGE);
			flag.put("message", message);
			
			return flag.toString();
		}
		
		public static String encodeSendRow(String userNickname, int id, int row){
			JSONObject flag = new JSONObject();
			
			flag.put("nomFlag", Flag.SEND_LIGNE);
			flag.put("nickName", userNickname);
			flag.put("idParty", id);
			flag.put("row", row);
			
			return flag.toString();
		}

}
