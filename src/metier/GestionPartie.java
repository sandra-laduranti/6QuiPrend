package metier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GestionPartie {

	/**
	 * Permet de connaitre la rangée en fonction de la carte a place
	 * et des derniers cartes de chaque rangée
	 * @param carte carteAPlace a place dans les rangées
	 * @param cartes list composé des dernières carte de chaque rangée
	 * @return la ligne a laquelle la carte doit etre place
	 */
	public static int selectRow(Carte cardToPlace, List<Carte> cards){
		List<Carte> newListCard = possibleRow(cardToPlace, cards);
		Carte cardRow;
		if(newListCard.size()==1){
			cardRow = newListCard.get(0);
		} else {
			cardRow = lastCardRow(cardToPlace, newListCard);
		}

		int indexCard = 0;
		for(Carte card : cards){
			if(card.equals(cardRow)){
				indexCard = cards.indexOf(card);
			}
		}
		
		return indexCard;
	}

	public static boolean isPlusPetit(Carte cardToPlace, List<Carte> cards){
		boolean isPetit = true;
		for (Carte carte : cards) {
			if(carte.getValue()<cardToPlace.getValue()){
				isPetit = false;
			}
		}
		return isPetit;
	}

	private static List<Carte> possibleRow(Carte carteToPlace, List<Carte> cards){
		List<Carte> listCards = new ArrayList<Carte>();
		for (Carte card : cards) {
			if(card.getValue()<carteToPlace.getValue()){
				listCards.add(card);
			}
		}
		return listCards;
	}

	/**
	 * 
	 * @param carteToPlace
	 * @param newCards
	 * @return permet de connaitre 
	 */
	private static Carte lastCardRow(Carte carteToPlace, List<Carte> newCards){
		Carte cardRow = newCards.get(0);
		int valTmp = carteToPlace.getValue() - cardRow.getValue();
		int posTmp = 0;

		for(int i = 1; i<newCards.size(); i++){
			if(valTmp > carteToPlace.getValue() - newCards.get(i).getValue()){
				valTmp = carteToPlace.getValue() - newCards.get(i).getValue();
				posTmp = i;
			}
		}

		return newCards.get(posTmp);
	}

	/**
	 * Compte le nombre de tete de boeuf cumulé par le joueur
	 * @param cards
	 * @return
	 */
	public static int countBeef(List<Carte> cards){
		int nbBeef = 0;
		for (Carte card : cards) {
			nbBeef = nbBeef + card.getBeefHead();
		}
		return nbBeef;
	}
	
	/**
	 * Distribue les cartes à un joueur
	 * @param deck
	 * @param player
	 */
	public static List<Carte> disturb(List<Carte> deck){
		int idCard;
		int deckSize = deck.size()-1;
		List<Carte> playerCards = new ArrayList<Carte>();
		for(int i = 0; i<10; i++){
			idCard = (int) (Math.random()*deckSize);
			Carte card = deck.get(idCard);
			playerCards.add(card);
			deck.remove(card);
			deckSize = deck.size()-1;
		}
		return playerCards;
	}

	/**
	 * Retourne la carte joué par le joueur
	 * @param cards la main du joueur
	 * @param cardValue 
	 * @return
	 */
	public static Carte chooseCardFromHand(List<Carte> cards, int cardValue){
		for (Carte carte : cards) {
			if(carte.getValue()==cardValue){
				return carte;
			}
		}
		return null;
	}
	
	public static void getWinnerAndLoser(List<User> users){
		
	}
	
	public static List<Carte> initializeDeck(int nbJoueursMax, boolean isProMode){
		List<Carte> listCard = new ArrayList<Carte>();
		if(isProMode){
			for(int i = 1; i<=(10*nbJoueursMax+4); i++){
				listCard.add(new Carte(i));
			}
		} else {
			for(int i = 1; i<105; i++){
				listCard.add(new Carte(i));
			}
		}
		
		return listCard;
	}
	
	public static void iniatializeRowsFirstCard(List<List<Carte>> rows, List<Carte> deck){
		for(int i = 0; i<4; i++){
			rows.get(i).add(deck.get(i));
		}
	}
	
	public static List<Carte> getLastCardRows(List<List<Carte>> rows){
		List<Carte> lastCardsRows = new ArrayList<Carte>();
		for(int i = 0; i<rows.size(); i++){
			List<Carte> listCardRow = rows.get(i);
			lastCardsRows.add(listCardRow.get(listCardRow.size()-1));
		}
		return lastCardsRows;
	}
	
}

