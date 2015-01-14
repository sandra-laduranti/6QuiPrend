package metier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Partie {

	private List<Carte> listCard;
	// HashMap contient la clé du joueur ainsi que la liste de ses cartes actuels
	private HashMap<User, List<Carte>> comptes;
	private int nbJoueursMax;
	private boolean isProMode;
	private List<List<Carte>> rows;
	private String nom;

	public Partie(String nom, int nbJoueurs, boolean isProMode, User user){
		this.listCard=new ArrayList<Carte>();
		this.nbJoueursMax = nbJoueurs;
		this.isProMode = isProMode;
		this.nom = nom;
		comptes = new HashMap<User, List<Carte>>();
		comptes.put(user, new ArrayList<Carte>());
	}

	public void startGame(){
		boolean isPlayerReach66 = false;
		while(!isPlayerReach66){
			initializeRound(isPlayerReach66);
		}
	}

	private void initializeRound(boolean isPlayerReach66){
		this.listCard = GestionPartie.initializeDeck(nbJoueursMax, isProMode);
		initializeRows();
		// On distribue les cartes pour chaque joueur
		for(int i = 0; i<nbJoueursMax; i++){
			List<Carte> playerCards =  GestionPartie.disturb(listCard);
			comptes.get(i).addAll(playerCards);
		}

		// On récupére les 4 premières cartes et on les ajoute a chacune des rangées 
		GestionPartie.iniatializeRowsFirstCard(rows, listCard);


		//Représente le déroulement d'une manche
		List<Carte> selectedCardByPlayer = new ArrayList<Carte>();
		int cptTurn = 0;
		while(!isPlayerReach66 || cptTurn <10){
			
			Carte selectedCard;
			// Faire en sorte que chaque joueur selectionne une carte chacun a leur tour
			for(int i = 0; i<comptes.size(); i++){
				//Méthode qui propose a chaque joueur de choisir sa carte, retourne une carte
				//				selectedCard = GestionPartie.chooseCardFromHand(comptes.get(i), idSelectedCard);
				//				comptes.get(i).remove(selectedCard);
				//On l'ajoute dans une liste de carte qui représente l'ensemble des cartes selectionne par les joueurs
				//				selectedCardByPlayer.add(selectedCard);

			}
			List<Carte> sortedCardsSelection = selectedCardByPlayer;
			// Trier la liste
			Collections.sort(sortedCardsSelection);

			// Pour chaque carte de la liste selectedCardByPlayer on regarde si on peut la jouer
			List<Carte> fourLastCardRows = GestionPartie.getLastCardRows(rows);
			Carte cardToPlace;
			for(int i=0; i<sortedCardsSelection.size();i++){
				cardToPlace = sortedCardsSelection.get(i);
				if(GestionPartie.isPlusPetit(selectedCardByPlayer.get(i), fourLastCardRows)){
					//Méthode qui dis au joueur de choisir sa rangée
					int selectRow = 0;
					int indexCardChoosen = selectedCardByPlayer.indexOf(cardToPlace);
					int nbBeef = GestionPartie.countBeef(rows.get(selectRow));

					User user  = getListUser().get(indexCardChoosen);
					user.setCurrentBeef(user.getCurrentBeef()+nbBeef);
					if(user.getCurrentBeef()>=66){
						isPlayerReach66 = true;
					}

					//Enleve la ligne selectRow et ajoute la carte du joueur
					rows.get(selectRow).clear();
					rows.get(selectRow).add(cardToPlace);
				} else {
					int selectRow = GestionPartie.selectRow(cardToPlace, fourLastCardRows);
					rows.get(selectRow).add(cardToPlace);
					fourLastCardRows = GestionPartie.getLastCardRows(rows);
				}
			}
			cptTurn++;
		}
	}

	public List<User> getListUser(){
		Set<User> users = comptes.keySet();
		List<User> listUser = new ArrayList<User>();
		for(User user : users){
			listUser.add(user);
		}

		return listUser;
	}

	public int getNbJoueursMax() {
		return nbJoueursMax;
	}

	public void addPlayer(User user){
		comptes.put(user, new ArrayList<Carte>());
	}

	public List<Carte> getListCard(){
		return this.listCard;
	}

	private void initializeRows(){
		for(int i = 0; i<4; i++){
			rows.add(new ArrayList<Carte>());
		}
	}

	public String getNom() {
		return nom;
	}

	public List<List<Carte>> getRows() {
		return rows;
	}
}
