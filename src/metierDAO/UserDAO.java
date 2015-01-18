package metierDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import javax.swing.JOptionPane;

import log.MonLogClient;
import utils.Md5;

/**
 * @(#) CompteDAO.java
 */

public class UserDAO {
	
	private static final Connection CONNECTION = DatabaseConnection.getInstance();
	
	/**
	 * Vérifie que les informations rentrée sont bonnes
	 * @param login
	 * @param pass
	 * @return le compte associé, ou null
	 */
	public static int verifieAuthentification(String login, String pass) {
		PreparedStatement statement;
		String requete;
		String passwd = Md5.encodeMd5(pass);
		try{
			if(CONNECTION!=null){
				requete = "SELECT * FROM "+DatabaseUtils.TABLE_USER+" WHERE nickname = ? AND password = ?";
				
				statement = CONNECTION.prepareStatement(requete);
				statement.setString(1, login);		// Rempli le premier "?" avec une valeur
				statement.setString(2, passwd);
				ResultSet result = statement.executeQuery();
				
				// Une fois que l'on a récupéré l'id du compte
				if(result!= null && result.first()==true){
					new MonLogClient().add("Récupération du compte depuis la base de données (après authentification)",Level.FINE);
					return result.getInt("id"); 				
				} else { // aucun user existe avec ce login/mdp
                    JOptionPane.showMessageDialog(null,
                            "Login ou mot de passe invalide !",
                            "Connexion refusée",
                            JOptionPane.ERROR_MESSAGE);
                }
			}
				
		} catch (SQLException e) {
		    return -1;
		}
		return -1;
	}

	public static int createUser(String nickname, String email, String password) {
		PreparedStatement statement;
		String requete;
		String encodedPasswd = Md5.encodeMd5(password);
		try{
			if(CONNECTION!=null){
				requete = "INSERT INTO "+DatabaseUtils.TABLE_USER+" (nickname, email, password, nbWin, nbLose) VALUES (?, ?, ?, 0, 0)";

				statement = DatabaseConnection.getInstance().prepareStatement(requete);
				statement.setString(1, nickname);		// Rempli le premier "?" avec une valeur
				statement.setString(2, email);
				statement.setString(3, encodedPasswd);
				statement.executeUpdate();
				new MonLogClient().add("Création du nouveau compte pour "+ nickname,Level.INFO);

				requete = "SELECT id FROM "+DatabaseUtils.TABLE_USER+" WHERE nickname = ?";
				statement = DatabaseConnection.getInstance().prepareStatement(requete);
				statement.setString(1, nickname);
				ResultSet result = statement.executeQuery();
				
				if(result!=null && result.first()==true){
					return result.getInt("id");
				}
			}
		} catch (SQLException e) {
		    return -1;
		}
		return -1;
	}
	
	/**
	 * Retourne vrai si un compte possède déjà ce login
	 * @param login
	 * @return
	 */
	public static boolean existLogin(String login) {
		PreparedStatement statement;
		String requete;
		try{
			if(CONNECTION!=null){
				requete = "SELECT * FROM "+DatabaseUtils.TABLE_USER+" WHERE nickname = ?";
				
				statement = CONNECTION.prepareStatement(requete);
				statement.setString(1, login);
				ResultSet result = statement.executeQuery();
				
				if(result!= null && result.first()==true){ // Si y'a un résultat
					new MonLogClient().add("Le login est bien unique",Level.FINE);
					return true;
				}
			}
		} catch(SQLException e){
			return false;
		}
		return false;
	}
	
	
	public static int getWin(int id){
		PreparedStatement statement;
		String requete;
		try{
			if(CONNECTION!=null){
				requete = "SELECT nbwin FROM "+DatabaseUtils.TABLE_USER+" WHERE id = ?";
				
				statement = CONNECTION.prepareStatement(requete);
				statement.setInt(1, id);
				ResultSet result = statement.executeQuery();
				
				if(result!= null && result.first()==true) // Si y'a un résultat
					return result.getInt("nbwin");
			}
		} catch(SQLException e){
			return -1;
		}
		return -1;
	}
	
	public static int getLose(int id){
		PreparedStatement statement;
		String requete;
		try{
			if(CONNECTION!=null){
				requete = "SELECT nblose FROM "+DatabaseUtils.TABLE_USER+" WHERE id = ?";
				
				statement = CONNECTION.prepareStatement(requete);
				statement.setInt(1, id);
				ResultSet result = statement.executeQuery();
				
				if(result!= null && result.first()==true) // Si y'a un résultat
					return result.getInt("nblose");
			}
		} catch(SQLException e){
			return -1;
		}
		return -1;
	}
	
	public static void updateStats(int id, String type){
		PreparedStatement statement;
		String requete;
		
		if(type == "win"){
			try{
				if(CONNECTION!=null){
					requete = "INSERT INTO "+DatabaseUtils.TABLE_USER+" (nbwin) VALUES (?) WHERE id=?";
					statement = DatabaseConnection.getInstance().prepareStatement(requete);
					statement.setInt(1, getWin(id)+1);
					statement.setInt(2, id);
					statement.executeQuery();
				}
			} catch(SQLException e){
				//TODO add log
			}
		}	
		else{
			try{
				if(CONNECTION!=null){
					requete = "INSERT INTO "+DatabaseUtils.TABLE_USER+" (nblose) VALUES (?) WHERE id=?";
					statement = DatabaseConnection.getInstance().prepareStatement(requete);
					statement.setInt(1, getLose(id)+1);
					statement.setInt(2, id);
					statement.executeQuery();
				}
			} catch(SQLException e){
				//TODO add log
			}
		}
	}
}
