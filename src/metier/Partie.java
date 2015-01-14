package metier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

import metier.Carte;
import metier.GestionPartie;
import metier.User;

public class Partie {

	private List<Carte> listCard;
	private int id=0;
	// HashMap contient la clé du joueur ainsi que la liste de ses cartes actuels
	private HashMap<User, List<Carte>> comptes;
	private int nbJoueursMax;
	private boolean isProMode;
	private List<List<Carte>> rows;
	private String nom;
	private boolean isPlayerReach66=false;

	public Partie(String nom, int nbJoueurs, boolean isProMode, User user){
		this.listCard=new ArrayList<Carte>();
		this.nbJoueursMax = nbJoueurs;
		this.isProMode = isProMode;
		this.nom = nom;
		this.id++;
		comptes = new HashMap<User, List<Carte>>();
		comptes.put(user, new ArrayList<Carte>());
	}

	
	
	public void startGame(){
		while(!isPlayerReach66){
			initializeRound(isPlayerReach66);
		}
	}
	
	/*
	 * TODO  : Ajout des println et d'un Scanner pour la saisie sur console
	 */
	private void initializeRound(boolean isPlayerReach66){
		this.listCard = GestionPartie.initializeDeck(nbJoueursMax, isProMode);
		initializeRows();
		User user = null;
		// On distribue les cartes pour chaque joueur
		List<Carte> playerCards=new ArrayList<Carte>();
		for(int i = 0; i<nbJoueursMax; i++){				// Ajout des system.out.println()
			playerCards =  GestionPartie.disturb(listCard);
			user=getListUser().get(i);
			comptes.put(user, playerCards);
			/*
			 * TODO : Ajout du while permettant de voir la liste des cartes distribuées
			 */
			int j=0;
			System.out.print(getListUser().get(i).getUserNickname()+" : [ ");
			while(j<=comptes.get(user).size()-1){
				System.out.print(comptes.get(user).get(j).getValue()+"  ");
				j++;
			}
			System.out.println("]");	
		}
		comptes.get(null);
		// On récupére les 4 premières cartes et on les ajoute a chacune des rangées 
		GestionPartie.iniatializeRowsFirstCard(rows, listCard);
		
		for (Entry<User, List<Carte>> entry : comptes.entrySet()) {
	        entry.getValue().addAll(playerCards);
	    }
		//Représente le déroulement d'une manche
		List<Carte> selectedCardByPlayer = null;
		int cptTurn = 0;
		Carte selectedCard = null;
		while(cptTurn<10||!isPlayerReach66){
			selectedCardByPlayer=new ArrayList<Carte>();
			// Faire en sorte que chaque joueur selectionne une carte chacun a leur tour
			for(int i = 0; i<comptes.size(); i++){
				//Méthode qui propose a chaque joueur de choisir sa carte, retourne une carte
				int a=0;
				try{
					Scanner sc=new Scanner(System.in);
					String ch="";
					ch=sc.nextLine();
					a = Integer.parseInt(ch);
				}catch(NumberFormatException e){
					e.printStackTrace();
				}
				
				if(!GestionPartie.chooseCardFromHand(comptes.get(user),a).equals(new Carte(0))){ 
					selectedCard = GestionPartie.chooseCardFromHand(comptes.get(user), a);
					comptes.get(user).remove(selectedCard);
					System.out.println("\n*****************************************");
					for(int r=0; r<rows.size();r++){
						listCard = rows.get(r);
						System.out.println("          -----------------------------");
						System.out.print("ligne "+(r+1)+" : ");
						for(Carte carte : listCard){
							System.out.print("| "+carte.getValue()+" | ");
						}
						System.out.println();
						System.out.println("          -----------------------------");
						
					}
					System.out.println("*****************************************\n");
				}else{
					isPlayerReach66=true;
				}
			/*
			 * TODO : Ajout du while pour l'affichage des cartes qui ne sont pas encore jouées
			 */
				int k=0;
				System.out.print("[ ");
				while(k<comptes.get(user).size()){
					System.out.print(comptes.get(user).get(k).getValue()+" ");
					k++;
				}
				System.out.print(" ]");
				List<Carte> listCard = new ArrayList<Carte>();
				//On l'ajoute dans une liste de carte qui représente l'ensemble des cartes selectionne par les joueurs
				selectedCardByPlayer.add(selectedCard);
				
			}
			List<Carte> sortedCardsSelection = selectedCardByPlayer;
			Collections.sort(sortedCardsSelection);
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
					
					//User user  = getListUser().get(indexCardChoosen); 	// Faut car l'indexOff peut dépasser le nombre de joueur 
					
					for (Entry<User, List<Carte>> entry : comptes.entrySet()) {
						for(Carte carte : entry.getValue()){
					        if (selectedCard.equals(carte)) {
					        	user = entry.getKey();
					        }
					    }
					}

					user.setCurrentBeef(user.getCurrentBeef()+nbBeef);
					if(user.getCurrentBeef()>=66){
						isPlayerReach66 = true;
					}

					//Enleve la ligne selectRow et ajoute la carte du joueur
					rows.get(selectRow).clear();
					rows.get(selectRow).add(cardToPlace);
					//rows.get(selectRow);
				} else {
					int selectRow = GestionPartie.selectRow(cardToPlace, fourLastCardRows);
					rows.get(selectRow).add(cardToPlace);
					fourLastCardRows = lastCardsRows();
				}
			}
			System.out.println("\nCompteur : "+cptTurn);
			cptTurn++;
			System.out.println("Compteur2 : "+cptTurn);
		}
		System.out.println("\nScore de "+user.getUserNickname()+" : "+user.getCurrentBeef()+" tête(s) de boeufs");
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
		if(getListUser().size() < getNbJoueursMax()){
			comptes.put(user, playerCards);
		}else{
			System.out.println("Bonjour "+user.getUserNickname()+", le nombre de joueur maximum est atteint ! veuillez revenir l'année prochaine !");
		}
	}

	public List<Carte> getListCard(){
		return this.listCard;
	}

	private void initializeRows(){
		rows = new ArrayList<List<Carte>>();
		for(int i = 0; i<4; i++){
			rows.add(new ArrayList<Carte>());
		}
	}
	
	private List<Carte> lastCardsRows(){
		List<Carte> lastCardsRows = new ArrayList<Carte>();
		for(int i = 0; i<rows.size(); i++){
			List<Carte> listCardRow = rows.get(i);
			lastCardsRows.add(listCardRow.get(listCardRow.size()-1));
		}
		return lastCardsRows;
	}

	public String getNom() {
		return nom;
	}
	
	public List<List<Carte>> getRows() {
		return rows;
	}
	
	public HashMap<User, List<Carte>> getPlayers(){
		return this.comptes;
	}
	
	public int getIdParty(){
		return this.id;
	}
	
}
