package metier;

import java.util.ArrayList;
import java.util.List;

public class GestionPartie {

	/**
	 * Permet de connaitre la rang�e en fonction de la carte a place
	 * et des derniers cartes de chaque rang�e
	 * @param carte carteAPlace a place dans les rang�es
	 * @param cartes list compos� des derni�res carte de chaque rang�e
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

	public static int countBeef(List<Carte> cards){
		int nbBeef = 0;
		for (Carte card : cards) {
			nbBeef = nbBeef + card.getBeefHead();
		}
		return nbBeef;
	}
	
	public static void disturb(List<Carte> deck, Player player){
		int idCard;
		int deckSize = deck.size()-1;
		for(int i = 0; i<10; i++){
			idCard = (int) (Math.random()*deckSize);
			Carte card = deck.get(idCard);
			player.getListCard().add(card);
			deck.remove(card);
			deckSize = deck.size()-1;
		}
	}
	
	

}