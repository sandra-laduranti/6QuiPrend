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

	public Partie(String nom, int nbJoueurs, boolean isProMode, User user){
		this.listCard=new ArrayList<Carte>();
		this.nbJoueursMax = nbJoueurs;
		this.isProMode = isProMode;
		comptes.put(user, null);
		initializeGame();
	}

	private void initializeGame(){
		initializeDeck();
		initializeRows();
		// On distribue les cartes pour chaque joueur
		for(int i = 0; i<nbJoueursMax; i++){
			List<Carte> playerCards =  GestionPartie.disturb(listCard);
			comptes.get(i).addAll(playerCards);
		}

		// On récupére les 4 premières cartes et on les ajoute a chacune des rangées 
		initRowsCard();


		//Représente le déroulement d'une manche
		List<Carte> selectedCardByPlayer = new ArrayList<Carte>();
		boolean isPlayerReach66 = false;
		while(!isPlayerReach66){
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
			sortedCardsSelection.sort(new Comparator<Carte>() {

				@Override
				public int compare(Carte card1, Carte card2) {
					if(card1.getValue()>card2.getValue()){
						return -1;
					} else if(card1.getValue()<card2.getValue()){
						return 1;
					} else {
						return 0;
					}
				}
			});
			
			// Pour chaque carte de la liste selectedCardByPlayer on regarde si on peut la jouer
			List<Carte> fourLastCardRows = lastCardsRows();
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
					
					//rows.get(selectRow);
				} else {
					int selectRow = GestionPartie.selectRow(cardToPlace, fourLastCardRows);
					rows.get(selectRow).add(cardToPlace);
					fourLastCardRows = lastCardsRows();
				}
			}
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

	public void addPlayer(User user, List<Carte> playerCards){
		comptes.put(user, playerCards);
	}

	public List<Carte> getListCard(){
		return this.listCard;
	}

	private void initializeRows(){
		for(int i = 0; i<4; i++){
			rows.add(new ArrayList<Carte>());
		}
	}

	private void initializeDeck(){
		if(isProMode){
			for(int i = 1; i<(10*nbJoueursMax+4); i++){
				this.listCard.add(new Carte(i));
			}
		} else {
			for(int i = 1; i<105; i++){
				this.listCard.add(new Carte(i));
			}
		}
	}

	private void initRowsCard(){
		for(int i = 0; i<4; i++){
			rows.get(i).add(listCard.get(i));
		}
	}
	
	private List<Carte> lastCardsRows(){
		List<Carte> lastCardsRows = new ArrayList<Carte>();
		for(int i = 0; i<rows.size(); i++){
			List<Carte> listCardRow = rows.get(i);
			lastCardsRows.add(listCardRow.get(rows.size()-1));
		}
		return lastCardsRows;
	}

}
