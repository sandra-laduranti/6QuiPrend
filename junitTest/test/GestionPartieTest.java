package test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import metier.Carte;
import metier.GestionPartie;


public class GestionPartieTest {

	@Test
	public void testPlacementCard(){
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
}
