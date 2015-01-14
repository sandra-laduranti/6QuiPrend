package metierDAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import log.MonLogClient;

public class DatabaseConnection {
	  
	private static String url = "jdbc:mysql://37.59.110.237:3306/"; // ici 3306 correspond au port que MYSQL Installer m'a désigné (ça peut être un autre pour vous)
	private static String user = "remote";
	private static String passwd = "dantoncul"; // Ca c'est les miens
	private volatile static Connection connect;
	
	// constructeur privé car c'est un singleton
	private DatabaseConnection(){ }

	/**
	 * 
	 * @return
	 */
	public static Connection getInstance(){
	  if(connect == null){
	    synchronized (DatabaseConnection.class) {
	    	if(connect == null){
		    	try {
		    		url += DatabaseUtils.BASE;
			        connect = DriverManager.getConnection(url, user, passwd);
			        new MonLogClient().add("Connection à la base réussie");
			    } catch (SQLException e) {
			    	new MonLogClient().add("Problème d'accès à la base distante"+e.getMessage());
			    	JOptionPane.showMessageDialog(null, "Message reçu : "+e.getMessage(), "Erreur de connexion à la base distante", JOptionPane.ERROR_MESSAGE);
			    }
	    	}
		}
	  }      
	  return connect;
	}    
}
