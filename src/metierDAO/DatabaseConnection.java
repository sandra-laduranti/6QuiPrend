package metierDAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JOptionPane;

public class DatabaseConnection {
	  
	private static String url = "jdbc:mysql://localhost:3306/"; // ici 3306 correspond au port que MYSQL Installer m'a désigné (ça peut être un autre pour vous)
	private static String user = "root";
	private static String passwd = "root"; // Ca c'est les miens
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
			        System.out.println("Connexion à la base réussie !");
			    } catch (SQLException e) {
			    	// LOG !
			    	JOptionPane.showMessageDialog(null, e.getMessage(), "ERREUR DE CONNEXION A LA BASE ! ", JOptionPane.ERROR_MESSAGE);
			    }
	    	}
		}
	  }      
	  return connect;
	}    
}
