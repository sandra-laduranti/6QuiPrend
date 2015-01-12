package metier;

import java.util.ArrayList;
import java.util.List;

public class Player extends User {

	private int nbHead;
	private List<Carte> listCardPlayer;
	
	public Player(User user){
		super(user);
		nbHead=0;
		listCardPlayer=new ArrayList<Carte>();
	}
	
	public int getNbHead(){
		return nbHead;
	}
	
	public List<Carte> getListCard(){
		return listCardPlayer;
	}
	
	public void addCardPlayer(List<Carte> listC){
		listCardPlayer=listC;
	}
}
