package communication;

import metier.Partie;
import metier.User;

public class MethodesAImplementerClient {

	// La classe Client contient une instance de FenetrePrincipale fp, et un user (qui sera initialisé lors de l'authentification);
	//	Client c = null;
	//	try {
	//		c = new Client( new URI( "ws://localhost:12345" ));
	//	} catch (URISyntaxException e1) {
	//		e1.printStackTrace();
	//	}
	//	final FenetrePrincipale f = new FenetrePrincipale(c);
	
	
	// La classe Serveur contient la liste des parties;
	
	
	// TODO dans Client.java : le user doit informer le serveur qu'il vient de créer une partie.
	// TODO dans Serveur.java : l'ajoute à sa liste des parties (récupère donc ce qu'il y a sur le flux, construit un new Partie, et add dans sa liste).
	public void creationPartie(String nom,int nbMaxJoueur,boolean modePro, User user){
		//socket.write(Flag.CREATION_PARTIE: nom,nbMaxJoueur,modePro, user)
	}
	
	
	// TODO DANS Client.java : lorsque le user choisit de rejoindre une partie, il doit demander au serveur si il peux la rejoindre
	// et attend vrai ou faux.
	// TODO dans Serveur.java, si ce user peut rejoindre la partie (voir avec Patrick/Nourdine pour la verif), 
	// il l'ajoute dans la partie, et retourne vrai, sinon faux.
	public void rejoindrePartie(Partie idPartie, User user){
		//socket.write(Flag.REJOINDRE_PARTIE : idPartie, user)
		// return bool serait niquel, sinon faut voir dans l'écoute (voir en bas)
	}
	
	// TODO dans Client.java : envoie au serveur la valeur d'une carte, avec un Flag contenant l'id de la partie
	// TODO dans Serveur.java : reçoit la carte, et la file à la partie en cours (partie sait quelle carte correspond à quel joueur)
	public void sendCarteChoisie(int idPartie, int carteValue){
		//socket.write(Flag.SEND_CARTE: idPartie : carteValue)
	}
	
	// TODO dans Client.java : envoie au serveur la valeur d'une ligne, avec un Flag contenant l'id de la partie
	// TODO dans Serveur.java : reçoit la ligne, et la file à la partie en cours (partie sait quelle carte correspond à quel joueur)
	public void sendLigneChoisie(int idPartie, int ligneValue){
		//socket.write(Flag.SEND_LIGNE : idPartie : ligneValue)
	}
	
	// TODO dans Client.java : l'affichage demande au serveur de renvoyer la liste des parties (et attend)
	// TODO dans Serveur.java : reçoit la demande, et renvoie avec le même Flag la liste des parties (avec uniquement les valeurs
	// utiles pour l'affichage)
	public void rafraichirListeParties(){
		// socket.write(Flag.REFRESH_LIST_PARTIES);
	}
	
	
	public void ecoute(){
		// Ecoute ce que le serveur dit, reçoit, la plupart du temps, un Flag suivi de données.
		// 
		// switch(Flag reçu){
		//
		// case Flag.REFRESH_LIST_PARTIES:
		//			donner la liste des parties à l'affichage
		// case Flag.REJOINDRE_PARTIE :
		//			donne un boolean a l'affichage pour l'autoriser à rejoindre la partie
		// case Flag.PARTIE_COMMENCE
		// 			préviens l'affichage que la partie commence
		// case Flag.SEND_CARTE
		//			fp.setTropTardCarte(false);
		//			demande a l'affichage de choisir une carte
		// case Flag.TROP_TARD_POUR_CARTE
		//			fp.setTropTardCarte(true);
		//			dit à l'affichage qu'il est trop tard pour envoyer une carte
		// case Flag.SEND_LIGNE
		//			fp.setTropTardLigne(false);
		//			demande à l'affichage de choisir une carte
		// case Flag.TROP_TARD_POUR_LIGNE
		//			fp.setTropTardLigne(true);
		//			dit a l'affichage qu'il est trop tard pour envoyer une carte
		// case Flag.REFRESH_BEEF:
		//			donne a l'affichage sa nouvelle tete de boeuf
		// case Flag.REFRESH_LIGNES:
		//			donne a l'affichage le nouveau contenu des lignes
		// case Flag.CARTE_ADVERSAIRES:
		//			donne a l'affichage la carte jouée par chaque adversaire
		// case Flag.GAGNANT:
		//			donne a l'affichage le ou les gagnants
		// case Flag.PERDANT:
		//			donne a l'affichage le perdant.
		
	}
	
	
}
