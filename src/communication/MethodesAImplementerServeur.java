package communication;

import java.util.List;

import metier.Carte;
import metier.Partie;
import metier.User;

public class MethodesAImplementerServeur {
	
	// La classe Serveur contient la liste des parties;
	List<Partie> parties;
	
	// La classe Client contient une instance de FenetrePrincipale fp;
	
	
	//	envoie au client concerné la liste des parties
	public void refreshListParties(List<Partie> parties){ 
		// socket.write(Flag.REFRESH_LIST_PARTIES:parties)
	}
	
	// envoie au client concerné la réponse à sa demande pour rejoindre une partie
	public void rejoindrePartie(boolean reponse){
		// socket.write(Flag.REJOINDRE:reponse)
	}
	
	// dès que Serveur reçoit un Flag.REJOINDRE_PARTIE et que sa réponse est positive, il envoie à tous les participants
	// de sa partie le nom du joueur (pour pouvoir l'afficher)
	public void sendNewParticipant(String nom_user){
		// socket.write(Flag.SEND_PARTICIPANTS: nom_user);
	}
	
	// demande a tous les joueurs de la partie d'envoyer une carte
	public void sendCarte(){
		// socket.write(Flag.SEND_CARTE);
	}
	
	// demande a tous les joueurs de la partie d'envoyer une ligne
	public void sendLigne(){
		// socket.write(Flag.SEND_LIGNE);
	}
	
	// dit au joueur concerné que c'est trop tard pour envoyer une carte
	public void sendTroTardCarte(){
		// socket.write(Flag.TROP_TARD_POUR_CARTE);
	}
	
	// dit au joueur concerné que c'est trop tard pour envoyer une ligne
	public void sendTropTardLigne(){
		// socket.write(Flag.TROP_TARD_POUR_LIGNE);
	}
	
	// donne au client concerné les tetes de boeufs à ajouter pour son user
	public void refreshBeef(int nbBeef){
		// socket.write(Flag.REFRESH_BEEF: nbBeef);
	}
	
	// donne au client concerné le nouveau contenu des lignes
	public void refreshLignes(List<List<Carte>> lignes){
		// socket.write(Flag.REFRESH_LIGNES: lignes);
	}
	
	// affiche aux clients de la partie le ou les gagnants
	public void afficheGagnant(String ... nomGagnant){
		// socket.write(Flag.GAGNANT:nomGagnant[0],nomGagnant[1],...);
	}
	
	// affiche aux clients de la partie le perdant
	public void affichePerdants(String nomPerdant){
		// socket.write(Flag.GAGNANT:nomPerdant);
	}
			
	
	public void ecoute(){
		
		// Ecoute ce que le client dit. Reçoit, la plupart du temps, un Flag suivi de données.
		// 
		// switch(Flag reçu){
		//
		// case Flag.CREATION_PARTIE:
		//		- ajoute à sa liste de partie la partie reçu avec ce flag (faire new Partie avec les paramètres reçus)
		
		// case Flag.REJOINDRE_PARTIE :
		//		- verifie si il reste une place
		//		- si oui, ajoute le user à la partie et effectue this.rejoindrePartie(true)
		//		- si non, this.rejoindrePartie(false);
		
		// case Flag.SEND_CARTE
		//		- retrouver la partie graçe au paramètre reçu idPartie
		//		- faire new Carte avec la valeur reçue
		//		- ajouter à la partie la carte (il sait quelle carte correspond à quel joueur)
		
		
		// case Flag.SEND_LIGNE
		//		- retrouver la partie graçe au paramètre reçu idPartie
		//		- ajouter à la partie le int reçu
		
		
		// case Flag.REFRESH_LISTE_PARTIES:
		//		- this.refreshListeParties(parties);
		
	}
}
