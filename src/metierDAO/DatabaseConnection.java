package metierDAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;

import javax.swing.JOptionPane;

import log.MonLogClient;

/**
 * 
 * @author Julien M
 *
 */
public class DatabaseConnection {
	  
	private static String url = "jdbc:mysql://37.59.110.237:3306/";
	private static String user = "remote";
	private static String passwd = "dantoncul"; // Identifiants de la base sur le serveur de Damien
	private volatile static Connection connect;
	
	// constructeur privé car c'est un singleton
	private DatabaseConnection(){ }

	/**
	 * Retourne une instance de la connexion (Design Pattern Singleton), avec la technique du double checking+volatile, donc
	 * thread-safe.
	 * @return
	 */
	public static Connection getInstance(){
	  if(connect == null){
	    synchronized (DatabaseConnection.class) {
	    	if(connect == null){
		    	try {
		    		url += DatabaseUtils.BASE;
			        connect = DriverManager.getConnection(url, user, passwd);
			        new MonLogClient().add("Connection à la base réussie",Level.FINE);
			    } catch (SQLException e) {
			    	new MonLogClient().add("Problème d'accès à la base distante"+e.getMessage(),Level.SEVERE);
			    	JOptionPane.showMessageDialog(null, "Message reçu : "+e.getMessage(), "Erreur de connexion à la base distante", JOptionPane.ERROR_MESSAGE);
			    }
	    	}
		}
	  }      
	  return connect;
	}    
}
