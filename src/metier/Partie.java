package metier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONObject;

import communication.Flag;
import communication.Serveur;
import metier.Carte;

public class Partie extends Thread implements Serializable{

	private static transient final long serialVersionUID = 1L;
	private transient List<Carte> listCard;
	private int id;
	// HashMap contient la clé du joueur ainsi que la liste de ses cartes actuels
	private transient HashMap<String, List<Carte>> comptes;
	private HashMap<String, Integer> map;
	private int nbJoueursMax;
	private boolean isProMode;
	private transient List<List<Carte>> rows;
	private List<Carte> selectedCardByPlayer = null;
	private String nom;
	private transient boolean isPlayerReach66=false;
	private transient boolean isInGame;
	private Serveur serveur;
	private int choosenRow;

	public Partie(String nom, int nbJoueurs, boolean isProMode, String userNickname){
		this.listCard=new ArrayList<Carte>();
		this.nbJoueursMax = nbJoueurs;
		this.isProMode = isProMode;
		this.nom = nom;
		isInGame = false;
		comptes = new HashMap<String, List<Carte>>();
		map = new HashMap<String, Integer>();
		comptes.put(userNickname, new ArrayList<Carte>());
		map.put(userNickname, 0);
	}


	// Utilisé uniquement par la socket pour afficher les informations utiles à l'affichage (tout n'étant pas nécessaire)
	public Partie(int id, String nom, int nbJoueurs, boolean isProMode, List<String> userNicknames){
		this.listCard=new ArrayList<Carte>();
		this.nbJoueursMax = nbJoueurs;
		this.isProMode = isProMode;
		this.nom = nom;
		this.id = id;
		isInGame = false;
		comptes = new HashMap<String, List<Carte>>();
		for(String userNickname : userNicknames){
			comptes.put(userNickname, new ArrayList<Carte>());
		}
	}

	public void setChoosenRow(int row){
		choosenRow = row;
	}
	
	public void setServeur(Serveur serveur){
		this.serveur = serveur;
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	
	@Override
	public void run() {
		try {
			synchronized (this) {
				while(getListUser().size()<nbJoueursMax){
					serveur.sendMessageListPlayers(getListUser(),"Partie en attente de joueurs...",false);
					System.out.println("En attente de joueur...");
					this.wait();
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.out.println("Nombre de joueurs max non atteint");
		}
		JSONObject flag = new JSONObject();
		serveur.sendMessageListPlayers(getListUser(), flag.put("nomFlag", Flag.PARTIE_COMMENCE).toString(), true);
		System.out.println("La partie commence ! Bon jeu");
		startGame();
	}

	private void startGame(){
		isInGame = true;
		int cptRound = 1;
		while(!isPlayerReach66){
			initializeRound();
			currentBeefAllPlayers();
			System.out.println("Fin de la manche "+cptRound);
			cptRound++;
		}
		//TODO : Revoir l'algo dans le cas ou y'a un egalite
		//TODO: ajouter dans client methode "youlose" "youwin"
		List<String> listWinnerAndLoser = GestionPartie.getWinnerAndLoser(getListUser());
		for(String user : getListUser()){
			if(user.equals(listWinnerAndLoser.get(0))){
				System.out.println(user+" (Winner) -> "+user+ " tete de boeufs");
			} else if (user.equals(listWinnerAndLoser.get(1))){
				System.out.println(user+" (Loser) -> "+user+ " tete de boeufs");
			} else {
				System.out.println(user+" -> "+user+" tete de boeufs");

			}
		}
		//TODO : UserDao.addPartieGagnant(user) set le user gagnant, same pour le loser 
	}


	/*
	 * TODO  : Ajout des println et d'un Scanner pour la saisie sur console
	 */
	private void initializeRound(){
		this.listCard = GestionPartie.initializeDeck(nbJoueursMax, isProMode);
		initializeRows();
		String user = null;
		// On distribue les cartes pour chaque joueur
		List<Carte> playerCards=new ArrayList<Carte>();
		for(int i = 0; i<nbJoueursMax; i++){				// Ajout des system.out.println()
			playerCards =  GestionPartie.disturb(listCard); //disturb == distribué chez celui qui a écrit ça :^p
			System.out.println(getListUser().get(i));
			user=getListUser().get(i);
			comptes.put(user, playerCards);

		}

		// On récupére les 4 premières cartes et on les ajoute a chacune des rangées 
		GestionPartie.iniatializeRowsFirstCard(rows, listCard);

		//Représente le déroulement d'une manche
		int cptTurn = 0;
		Carte selectedCard = null;
		while(cptTurn<10){
			selectedCardByPlayer=new ArrayList<Carte>();

			//Affiche le plateau
			showGameArea();
			serveur.sendMessageListPlayers(getListUser(), showGameArea(), false);
			System.out.println("Tour numéro : "+(cptTurn+1));
			serveur.sendMessageListPlayers(getListUser(), "Tour numéro : "+(cptTurn+1)+"\n" , false);
			// Faire en sorte que chaque joueur selectionne une carte chacun a leur tour
			for(int i = 0; i<comptes.size(); i++){
				//Méthode qui propose a chaque joueur de choisir sa carte, retourne une carte
				int valueCard;
				
				//TODO: demander à tous les joueurs de donner une carte
				int j=0;
				//Affiche la liste des cartes du joueur
				System.out.print(getListUser().get(i)+" : [ ");
				ArrayList<Integer> arrPlayerCards = new ArrayList<Integer>();
				while(j<=comptes.get(getListUser().get(i)).size()-1){
					System.out.print(comptes.get(getListUser().get(i)).get(j).getValue()+"  ");
					arrPlayerCards.add(comptes.get(getListUser().get(i)).get(j).getValue());
					j++;
				}
				System.out.println("] ");
				
				serveur.sendCardToUser(getListUser().get(i), arrPlayerCards, id);
				synchronized(selectedCardByPlayer){
					try {
						selectedCardByPlayer.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				if (i+1 < getListUser().size()) {
					System.out.println("Au tour de " + getListUser().get(i+1)+" : ");
				}
				//valueCard = GestionPartie.selectValueCardToPlay();
				valueCard = selectedCardByPlayer.get(selectedCardByPlayer.size() - 1).getValue();
				boolean saisieCard = false;
				int cptEssaie = 0;
				while(!saisieCard && cptEssaie<2){
					if(valueCard != -1 ){
						if(GestionPartie.getCardFromHand(comptes.get(getListUser().get(i)),valueCard) != null){
							//TODO: send à la place des syso
							System.out.println("Vous avez saisie la valeur "+valueCard);
							saisieCard = true;
						} else {
							System.err.println("Cette carte n'est pas dans votre main");
							System.err.println("Recommencez");
							valueCard = GestionPartie.selectValueCardToPlay();
							cptEssaie++;
						}
					} else {
						System.out.println("Mauvaise saisie, recommencez");
						valueCard = GestionPartie.selectValueCardToPlay();
						cptEssaie++;
					}
				}
				if(cptEssaie == 2){
					selectedCard = GestionPartie.chooseRDMCardForPlayer(comptes.get(getListUser().get(i)));
					System.out.println("Une carte a été choisie pour vous "+selectedCard.getValue());
					cptEssaie = 0;
				} else {
					selectedCard = GestionPartie.getCardFromHand(comptes.get(getListUser().get(i)), valueCard);
				}
				comptes.get(getListUser().get(i)).remove(selectedCard);
				addSelectedCard(selectedCard);

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
					String userGetRow  = getListUser().get(indexCardChoosen);
					System.out.println("Votre carte ne peut pas être placé");
					System.out.println(userGetRow+" : Vous devez choisir la rangé a prendre entre 1 et 4");
					serveur.selectRowToUser( getListUser().get(i), id );
					synchronized(this) {
						this.wait();
					}
					int selectRowCollect = GestionPartie.getRowToCollect();
					int cptEssaie = 0;
					while(!(selectRowCollect>0 && selectRowCollect<5) && cptEssaie<2){
						System.out.println("Saisie entre 1 et 4 !!!!");
						selectRowCollect = GestionPartie.getRowToCollect();
						cptEssaie++;
					}
					if(cptEssaie ==3 ){
						selectRowCollect = GestionPartie.getRDMRowForPlayer(rows);
					}
					int nbBeef = GestionPartie.countBeef(rows.get(selectRowCollect-1));
					System.out.println("Vous avez saisie la rangée : "+selectRowCollect+" qui contient "+nbBeef+" tete de boeufs");
					attributeBeef(selectRowCollect-1, cardToPlace, selectedCardByPlayer, userGetRow);
					fourLastCardRows = GestionPartie.getLastCardRows(rows);
				} else if (rows.get(GestionPartie.selectRow(cardToPlace, fourLastCardRows)).size()==5){
					String userGetRow  = getListUser().get(indexCardChoosen);
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

	public List<String> getListUser(){
		Set<String> users = comptes.keySet();
		List<String> listUserName = new ArrayList<String>();
		for(String name : users){
			listUserName.add(name);
		}

		return listUserName;
	}

	public int getNbJoueursMax() {
		return nbJoueursMax;
	}

	public boolean addPlayer(String userNickname){
		if(getListUser().size() < getNbJoueursMax()){
			comptes.put(userNickname, new ArrayList<Carte>());
			map.put(userNickname, 0);
			comptes.put(userNickname, new ArrayList<Carte>());
			return true;
		}else{
			return false;
		}
	}

	//TODO: probleme User ne devrait plus apparaitre et ne pas être manipulé. 
	public void removePlayer(String nickName){
		getListUser().remove(nickName);
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

	public HashMap<String, List<Carte>> getPComptes(){
		return this.comptes;
	}

	public HashMap<String, Integer> getMap(){
		return this.map;
	}

	public int getIdPartie(){
		return id;
	}

	public boolean isInGame(){
		return isInGame;
	}

	private void attributeBeef(int indexRow, Carte card, List<Carte> selectedCardByPlayer, String userGetRow){
		int nbBeef = GestionPartie.countBeef(rows.get(indexRow));
		int nbMapbeef = 0;
		String name="";
		//		System.out.println("*********** Test de la map : "+map.values().size());
		for (Entry<String, Integer> entry : map.entrySet()) {
			name=entry.getKey();
			if(name.equals(userGetRow)){
				nbMapbeef=entry.getValue()+nbBeef;
				entry.setValue(nbMapbeef);
				System.out.println(userGetRow+ " a maintenant :  "+entry.getValue()+ " tete de boeufs");
			}
		}

		if(nbMapbeef>=66){
			isPlayerReach66 = true;
		}

		//Enleve la ligne selectRow et ajoute la carte du joueur
		rows.get(indexRow).clear();
		rows.get(indexRow).add(card);
	}

	public boolean isProMode(){
		return isProMode;
	}

	private String showGameArea(){
		StringBuffer bf = new StringBuffer("");
		bf.append("\n*****************************************\n");
		System.out.println("\n*****************************************");
		for(int r=0; r<rows.size();r++){
			listCard = rows.get(r);
			bf.append("          -----------------------------\n");
			System.out.println("          -----------------------------");
			bf.append("ligne "+(r+1)+" :");
			System.out.print("ligne "+(r+1)+" : ");
			for(Carte carte : listCard){
				bf.append("| "+carte.getValue()+" |");
				System.out.print("| "+carte.getValue()+" | ");
			}
			System.out.println();
			bf.append("\n          -----------------------------\n");
			System.out.println("          -----------------------------");

		}
		bf.append("*****************************************\n\n");
		System.out.println("*****************************************\n");
		return bf.toString();
	}

	private void currentBeefAllPlayers(){
		for (Entry<String, Integer> entry : map.entrySet()) {
			System.out.println(entry.getKey() + " -> " + entry.getValue()) ;
		}
	}
	
	public List<Carte> getSelectedCardByPlayer() {
		return selectedCardByPlayer;
	}
	
	public boolean addSelectedCard(Carte card){
		return selectedCardByPlayer.add(card);
		
	}
}

