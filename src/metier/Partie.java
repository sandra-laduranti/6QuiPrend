package metier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Partie {

	private List<Carte> listCard; 		
	private HashMap<Integer, Player> compte;


	public Partie(int nbJoueurs){
		this.listCard=new ArrayList<Carte>();

		for (int i = 1; i < 105; i++) {
			this.listCard.add(new Carte(i));
		}

		for(int i=0; i<nbJoueurs; i++){
			this.compte.put(0,new Player(new User(null,null,null)));
		}

	}

	public void addPlayer(User user, Player player){
		compte.put(user.getUserId(), player);
	}

	public List<Carte> getListCard(){
		return this.listCard;
	}

}
