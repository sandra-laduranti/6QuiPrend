package metier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

public class Partie implements Serializable{

	/**
	 * 
	 */
	private static transient final long serialVersionUID = 1L;
	private transient List<Carte> listCard;
	private static int id=0;
	// HashMap contient la clé du joueur ainsi que la liste de ses cartes actuels
	private transient HashMap<User, List<Carte>> comptes;
	private transient int nbJoueursMax;
	private transient boolean isProMode;
	private transient List<List<Carte>> rows;
	private transient String nom;
	private boolean isPlayerReach66=false;
	private transient boolean isInGame;

	public Partie(String nom, int nbJoueurs, boolean isProMode, User user){
		this.listCard=new ArrayList<Carte>();
		this.nbJoueursMax = nbJoueurs;
		this.isProMode = isProMode;
		this.nom = nom;
		id++;
		isInGame = false;
		comptes = new HashMap<User, List<Carte>>();
		comptes.put(user, new ArrayList<Carte>());
	}

	public Partie(int id, String nom, int nbJoueurs, boolean isProMode, List<User> users){
		this.listCard=new ArrayList<Carte>();
		this.nbJoueursMax = nbJoueurs;
		this.isProMode = isProMode;
		this.nom = nom;
		this.id = id;
		isInGame = false;
		comptes = new HashMap<User, List<Carte>>();
		for(User user : users){
			comptes.put(user, new ArrayList<Carte>());
		}
	}



	public void startGame(){
		isInGame = true;
		while(!isPlayerReach66){
			initializeRound(isPlayerReach66);
		}
		//TODO : Revoir l'algo dans le cas ou y'a un egalite
		List<User> listWinnerAndLoser = GestionPartie.getWinnerAndLoser(getListUser());
		System.out.println("Le gagnant est : "+listWinnerAndLoser.get(0).getUserNickname());
		System.out.println("Le perdant est : "+listWinnerAndLoser.get(1).getUserNickname());
		//TODO : UserDao.addPartieGagnant(user) set le user gagnant, same pour le loser 
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

		}

		// On récupére les 4 premières cartes et on les ajoute a chacune des rangées 
		GestionPartie.iniatializeRowsFirstCard(rows, listCard);

		//Représente le déroulement d'une manche
		List<Carte> selectedCardByPlayer = null;
		int cptTurn = 0;
		Carte selectedCard = null;
		while(cptTurn<10||!isPlayerReach66){
			selectedCardByPlayer=new ArrayList<Carte>();

			//Affiche le plateau
			showGameArea();

			// Faire en sorte que chaque joueur selectionne une carte chacun a leur tour
			for(int i = 0; i<comptes.size(); i++){
				//Méthode qui propose a chaque joueur de choisir sa carte, retourne une carte
				int valueCard=0;
				Scanner sc=new Scanner(System.in);
				int j=0;
				//Affiche la liste des cartes du joueur
				System.out.print(getListUser().get(i).getUserNickname()+" : [ ");
				while(j<=comptes.get(getListUser().get(i)).size()-1){
					System.out.print(comptes.get(getListUser().get(i)).get(j).getValue()+"  ");
					j++;
				}
				System.out.println("]");


				try{
					System.out.println("Au tour de " + getListUser().get(i).getUserNickname()+" : ");
					String ch="";
					boolean cardValid = false;
					while((ch=sc.nextLine()) == null){
						System.err.println("Recommence la saisie");

					}
					valueCard = Integer.parseInt(ch);
					//sc.close();
				}catch(NumberFormatException | IllegalStateException e){
					e.printStackTrace();
					System.err.println("Saisissez un nombre");
				}

				if(GestionPartie.chooseCardFromHand(comptes.get(getListUser().get(i)),valueCard) != null){ 
					selectedCard = GestionPartie.chooseCardFromHand(comptes.get(getListUser().get(i)), valueCard);
					comptes.get(getListUser().get(i)).remove(selectedCard);
					selectedCardByPlayer.add(selectedCard);
				} else {
					System.err.println("Aucune carte selectionne");
				}

				/*
				 * TODO : Ajout du while pour l'affichage des cartes qui ne sont pas encore jouées
				 */
				int k=0;
				System.out.print("[ ");
				while(k<comptes.get(getListUser().get(i)).size()){
					System.out.print(comptes.get(getListUser().get(i)).get(k).getValue()+" ");
					k++;
				}
				System.out.println(" ]");

				//On l'ajoute dans une liste de carte qui représente l'ensemble des cartes selectionne par les joueurs
			}
			List<Carte> sortedCardsSelection = selectedCardByPlayer;
			Collections.sort(sortedCardsSelection);
			// Pour chaque carte de la liste selectedCardByPlayer on regarde si on peut la jouer

			List<Carte> fourLastCardRows = lastCardsRows();
			Carte cardToPlace;
			for(int i=0; i<sortedCardsSelection.size();i++){
				cardToPlace = sortedCardsSelection.get(i);
				int selectRow;
				//Si la carte est plus petite que les dernières cartes de chaque rangée
				//Le joueur prend alors la ligne
				if(GestionPartie.isPlusPetit(selectedCardByPlayer.get(i), fourLastCardRows)){
					int selectRowCollect = GestionPartie.getRowToCollect();
					attributeBeef(selectRowCollect, cardToPlace, selectedCardByPlayer);
				} else if ((selectRow = rows.get(GestionPartie.selectRow(cardToPlace, fourLastCardRows)).size())==5){
					// Si le joueur place la 6eme carte alors il prend la rangée
					attributeBeef(selectRow, cardToPlace, selectedCardByPlayer);
				} else {
					selectRow  = GestionPartie.selectRow(cardToPlace, fourLastCardRows);
					rows.get(selectRow).add(cardToPlace);
					fourLastCardRows = lastCardsRows();
				}
			}
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

	public void addPlayer(User user){
		if(getListUser().size() < getNbJoueursMax()){
			comptes.put(user, new ArrayList<Carte>());
		}else{
			System.out.println("Bonjour "+user.getUserNickname()+", le nombre de joueur maximum est atteint ! veuillez revenir l'année prochaine !");
		}
	}

	public void removePlayer(User user){
		getListUser().remove(user);
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

	public int getId(){
		return id;
	}

	public boolean isInGame(){
		return isInGame;
	}

	private void attributeBeef(int indexRow, Carte card, List<Carte> selectedCardByPlayer){
		int indexCardChoosen = selectedCardByPlayer.indexOf(card);
		int nbBeef = GestionPartie.countBeef(rows.get(indexRow));

		User userGetRow  = getListUser().get(indexCardChoosen);
		userGetRow.setCurrentBeef(userGetRow.getCurrentBeef()+nbBeef);

		if(userGetRow.getCurrentBeef()>=66){
			isPlayerReach66 = true;
		}

		//Enleve la ligne selectRow et ajoute la carte du joueur
		rows.get(indexRow).clear();
		rows.get(indexRow).add(card);
	}

	public boolean isProMode(){
		return isProMode;
	}

	private void showGameArea(){
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
	}

}
