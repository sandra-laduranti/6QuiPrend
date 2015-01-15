package test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import metier.Carte;
import metier.GestionPartie;


public class GestionPartieTest {

	@Test
	public void checkInitializeDeck(){
		List<Carte> deck1 = GestionPartie.initializeDeck(5, true);
		List<Carte> deck2 = GestionPartie.initializeDeck(5, false);
		
		assertEquals(54, deck1.size());
		assertEquals(104, deck2.size());
	}
	
	@Test
	public void checkInitializeRowCard(){
		List<Carte> deck = GestionPartie.initializeDeck(4, false);
		List<Carte> player1Cards = GestionPartie.disturb(deck);
		for (Carte carte : player1Cards) {
			deck.remove(carte);
		}
		List<Carte> player2Cards = GestionPartie.disturb(deck);
		for (Carte carte : player2Cards) {
			deck.remove(carte);
		}
		List<Carte> player3Cards = GestionPartie.disturb(deck);
		for (Carte carte : player3Cards) {
			deck.remove(carte);
		}
		List<Carte> player4Cards = GestionPartie.disturb(deck);
		for (Carte carte : player4Cards) {
			deck.remove(carte);
		}
		
		List<List<Carte>> rows = new ArrayList<List<Carte>>();
		for(int i = 0; i<4; i++){
			rows.add(new ArrayList<Carte>());
		}
		GestionPartie.iniatializeRowsFirstCard(rows, deck);
		
		for(int i = 0; i<4; i++){
			assertEquals(deck.get(i), rows.get(i).get(0));
		}
	}
	
	@Test
	public void checkPlacementCard(){
		Carte card = new Carte(8);
		Carte card2 = new Carte(24);
		Carte card3 = new Carte(69);
		Carte card4 = new Carte(81);

		List<Carte> cards = new ArrayList<Carte>();
		cards.add(card);
		cards.add(card2);
		cards.add(card3);
		cards.add(card4);

		Carte cardToPlace = new Carte(29);
		int indexRow = GestionPartie.selectRow(cardToPlace, cards);
		assertEquals(1, indexRow);
	}
	
	@Test
	public void checkDisturbCard(){
		List<Carte> deck = GestionPartie.initializeDeck(4, false);
		List<Carte> player1Cards = GestionPartie.disturb(deck);
		List<Carte> player2Cards = GestionPartie.disturb(deck);
		List<Carte> player3Cards = GestionPartie.disturb(deck);
		List<Carte> player4Cards = GestionPartie.disturb(deck);
		
		for(Carte card : player1Cards){
			assertFalse(player2Cards.contains(card));
			assertFalse(player3Cards.contains(card));
			assertFalse(player4Cards.contains(card));
		}
		
		for(Carte card : player2Cards){
			assertFalse(player3Cards.contains(card));
			assertFalse(player4Cards.contains(card));
			assertFalse(player1Cards.contains(card));
		}
		
		for(Carte card : player3Cards){
			assertFalse(player2Cards.contains(card));
			assertFalse(player1Cards.contains(card));
			assertFalse(player4Cards.contains(card));
		}
		
		for(Carte card : player4Cards){
			assertFalse(player2Cards.contains(card));
			assertFalse(player3Cards.contains(card));
			assertFalse(player1Cards.contains(card));
		}
	}
	
	@Test
	public void checkChooseCard(){
		List<Carte> deck = GestionPartie.initializeDeck(4, false);
		List<Carte> player1Cards = GestionPartie.disturb(deck);
		Carte chooseCard =  GestionPartie.getCardFromHand(player1Cards, 2);
		
		assertEquals(chooseCard, player1Cards.get(2));
	}

	@Test
	public void checkGetLastCardRows(){
		List<Carte> deck = GestionPartie.initializeDeck(4, false);
		List<List<Carte>> rows = new ArrayList<List<Carte>>();
		for(int i = 0; i<4; i++){
			rows.add(new ArrayList<Carte>());
		}
		GestionPartie.iniatializeRowsFirstCard(rows, deck);
		List<Carte> lastCardRows = 	GestionPartie.getLastCardRows(rows);
		for(int i = 0; i<lastCardRows.size(); i++){
			assertEquals(deck.get(i), lastCardRows.get(i));
		}
		
		rows.get(2).add(deck.get(15));
		lastCardRows = 	GestionPartie.getLastCardRows(rows);
		assertEquals(deck.get(0), lastCardRows.get(0));
		assertEquals(deck.get(1), lastCardRows.get(1));
		assertEquals(deck.get(15), lastCardRows.get(2));
		assertEquals(deck.get(3), lastCardRows.get(3));
	}
}
