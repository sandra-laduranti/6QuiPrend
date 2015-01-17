package graphique;

import java.net.URI;
import java.net.URISyntaxException;

import metier.Partie;
import communication.User;


public class MainPartie {

	public static void main(String[] args) throws URISyntaxException{

		User user = new User(new URI("ws://localhost:12345"));
		user.setUser(1, "Patrick");

		Partie game = new Partie("DANT Game", 2, false, user.getUserNickname());
		game.start();

		User user2 = new User(new URI("ws://localhost:12345"));
		user.setUser(2, "Nourdine");
		
		try {
			synchronized (game) {
				Thread.sleep(5000);
				System.out.println("End of sleep");
				game.addPlayer(user2.getUserNickname());
				System.out.println(game.getListUser().size());
				game.notify();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
