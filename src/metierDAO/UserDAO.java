package metierDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import metier.User;

/**
 * @(#) CompteDAO.java
 */

public class UserDAO {
	
	private final Connection connect = DatabaseConnection.getInstance();
	
	public boolean creationNouveauCompte( User e )
	{
		return false;
	}
	
	public boolean modificationCompteParId( int id_Compte, User e )
	{
		return false;
	}
	
	public boolean suppressionCompteParId( int id_Compte, User e )
	{
		return false;
	}
	
	
	/**
	 * Vérifie que les informations rentrée sont bonnes
	 * @param login
	 * @param pass
	 * @return Compte_Id, ou null si compte inexistant
	 */
	public User verifieAuthentification(String login, String pass) {
		PreparedStatement statement;
		String requete;
		try{
			
			requete = "SELECT * FROM "+DatabaseUtils.TABLE_COMPTE+" WHERE Compte_Login = ? AND Compte_Mdp = ?";

			statement = this.connect.prepareStatement(requete);
			statement.setString(1, login);		// Rempli le premier "?" avec une valeur
			statement.setString(2, pass);
			ResultSet result = statement.executeQuery();
			
			// Une fois que l'on a récupéré l'id du compte
			if(result!= null && result.first()==true){
				
				User user = new User(result.getInt("id"), result.getString("nickname"), result.getString("email"), result.getString("password")); // Ici remplir le constructeur avec les champs récupérés (exemple: result.getString("Compte_Stats"),...)
				return user;
			}
				
		} catch (SQLException e) {
			e.printStackTrace();
		    return null;
		}
		return null;
	}
	
	
}
