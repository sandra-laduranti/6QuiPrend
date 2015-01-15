package metier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Partie extends Thread implements Serializable{

	private static transient final long serialVersionUID = 1L;
	private transient List<Carte> listCard;
	private static int id=0;
	// HashMap contient la clé du joueur ainsi que la liste de ses cartes actuels
	private transient HashMap<User, List<Carte>> comptes;
	private int nbJoueursMax;
	private boolean isProMode;
	private transient List<List<Carte>> rows;
	private String nom;
	private transient boolean isPlayerReach66=false;
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

	// Utilisé uniquement par la socket pour afficher les informations utiles à l'affichage (tout n'étant pas nécessaire)
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

	@Override
	public void run() {
		super.run();
	}

	public void startGame(){
		isInGame = true;
		int cptRound = 1;
		while(!isPlayerReach66){
			initializeRound(isPlayerReach66);
			currentBeefAllPlayers();
			System.out.println("Fin de la manche "+cptRound);
			cptRound++;
		}
		//TODO : Revoir l'algo dans le cas ou y'a un egalite
		List<User> listWinnerAndLoser = GestionPartie.getWinnerAndLoser(getListUser());
		for(User user : getListUser()){
			if(user.equals(listWinnerAndLoser.get(0))){
				System.out.println(user.getUserNickname()+" (Winner) -> "+user.getCurrentBeef()+ " tete de boeufs");
			} else if (user.equals(listWinnerAndLoser.get(1))){
				System.out.println(user.getUserNickname()+" (Loser) -> "+user.getCurrentBeef()+ " tete de boeufs");
			} else {
				System.out.println(user.getUserNickname()+" -> "+user.getCurrentBeef()+" tete de boeufs");

			}
		}
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
		while(cptTurn<10){
			selectedCardByPlayer=new ArrayList<Carte>();

			//Affiche le plateau
			showGameArea();
			System.out.println("Tour numéro : "+(cptTurn+1));
			// Faire en sorte que chaque joueur selectionne une carte chacun a leur tour
			for(int i = 0; i<comptes.size(); i++){
				//Méthode qui propose a chaque joueur de choisir sa carte, retourne une carte
				int valueCard;
				Scanner sc=new Scanner(System.in);
				int j=0;
				//Affiche la liste des cartes du joueur
				System.out.print(getListUser().get(i).getUserNickname()+" : [ ");
				while(j<=comptes.get(getListUser().get(i)).size()-1){
					System.out.print(comptes.get(getListUser().get(i)).get(j).getValue()+"  ");
					j++;
				}
				System.out.println("]");


				System.out.println("Au tour de " + getListUser().get(i).getUserNickname()+" : ");
				valueCard = GestionPartie.selectValueCardToPlay();
				boolean saisieCard = false;
				while(!saisieCard){
					if(valueCard != -1){
						if(GestionPartie.getCardFromHand(comptes.get(getListUser().get(i)),valueCard) != null){ 
							System.out.println("Vous avez saisie la valeur "+valueCard);
							saisieCard = true;
						} else {
							System.err.println("Cette carte n'est pas dans votre main");
							System.err.println("Recommencez");
							valueCard = GestionPartie.selectValueCardToPlay();
						}
					} else {
						System.out.println("Mauvaise saisie, recommencez");
						valueCard = GestionPartie.selectValueCardToPlay();
					}
				}
				selectedCard = GestionPartie.getCardFromHand(comptes.get(getListUser().get(i)), valueCard);
				comptes.get(getListUser().get(i)).remove(selectedCard);
				selectedCardByPlayer.add(selectedCard);

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
			List<Carte> sortedCardsSelection = new ArrayList<Carte>();
			for(Carte carte : selectedCardByPlayer){
				sortedCardsSelection.add(carte);
			}
			Collections.sort(sortedCardsSelection);
			// Pour chaque carte de la liste selectedCardByPlayer on regarde si on peut la jouer

			List<Carte> fourLastCardRows = GestionPartie.getLastCardRows(rows);
			Carte cardToPlace;
			for(int i=0; i<sortedCardsSelection.size();i++){
				cardToPlace = sortedCardsSelection.get(i);
				int selectRow;
				//Si la carte est plus petite que les dernières cartes de chaque rangée
				//Le joueur prend alors la ligne
				int indexCardChoosen = selectedCardByPlayer.indexOf(cardToPlace);

				if(GestionPartie.isPlusPetit(cardToPlace, fourLastCardRows)){
					User userGetRow  = getListUser().get(indexCardChoosen);
					System.out.println("Votre carte ne peut pas être placé");
					System.out.println(userGetRow.getUserNickname()+" : Vous devez choisir la rangé a prendre entre 1 et 4");
					int selectRowCollect = GestionPartie.getRowToCollect();

					
					while(!(selectRowCollect>0 && selectRowCollect<5)){
						System.out.println("Saisie entre 1 et 4 !!!!");
						selectRowCollect = GestionPartie.getRowToCollect();
					}
					int nbBeef = GestionPartie.countBeef(rows.get(selectRowCollect-1));
					System.out.println("Vous avez saisie la rangée : "+selectRowCollect+" qui contient "+nbBeef+" tete de boeufs");
					attributeBeef(selectRowCollect-1, cardToPlace, selectedCardByPlayer, userGetRow);
					fourLastCardRows = GestionPartie.getLastCardRows(rows);
				} else if (rows.get(GestionPartie.selectRow(cardToPlace, fourLastCardRows)).size()==5){
					User userGetRow  = getListUser().get(indexCardChoosen);
					// Si le joueur place la 6eme carte alors il prend la rangée
					selectRow = GestionPartie.selectRow(cardToPlace, fourLastCardRows);
					System.out.println("Vous avez placé la 6ème carte de la rangée "+selectRow);
					attributeBeef(selectRow, cardToPlace, selectedCardByPlayer, userGetRow);
					fourLastCardRows = GestionPartie.getLastCardRows(rows);
				} else {
					selectRow  = GestionPartie.selectRow(cardToPlace, fourLastCardRows);
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

	public String getNom() {
		return nom;
	}

	public List<List<Carte>> getRows() {
		return rows;
	}

	public HashMap<User, List<Carte>> getPlayers(){
		return this.comptes;
	}

	public int getIdPartie(){
		return id;
	}

	public boolean isInGame(){
		return isInGame;
	}

	private void attributeBeef(int indexRow, Carte card, List<Carte> selectedCardByPlayer, User userGetRow){
		int nbBeef = GestionPartie.countBeef(rows.get(indexRow));

		userGetRow.setCurrentBeef(userGetRow.getCurrentBeef()+nbBeef);

		System.out.println(userGetRow.getUserNickname()+ " a maintenant "+userGetRow.getCurrentBeef()+ " tete de boeufs");

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

	private void currentBeefAllPlayers(){
		for(User user : getListUser()){
			System.out.println(user.getUserNickname()+" -> "+user.getCurrentBeef()+" tete de boeufs");
		}
	}

}
