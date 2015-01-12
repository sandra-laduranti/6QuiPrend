package metier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Partie {

	private List<Carte> listCard; 		
	private HashMap<Integer, Player> compte;

	public Partie(int nbJoueurs, boolean isProMode){
		this.listCard=new ArrayList<Carte>();
		initializeDeck(nbJoueurs, isProMode);
	}
	
	public void addPlayer(User user, Player player){
		compte.put(user.getUserId(), player);
	}

	public List<Carte> getListCard(){
		return this.listCard;
	}
	
	private void initializeDeck(int nbJoueur, boolean isProMode){
		if(isProMode){
			for(int i = 1; i<(10*nbJoueur+4); i++){
				this.listCard.add(new Carte(i));
			}
		} else {
			for(int i = 1; i<105; i++){
				this.listCard.add(new Carte(i));
			}
		}
	}

}
