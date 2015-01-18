package log;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * 
 * @author Julien M
 *
 */
public class MonLogServer extends MonLog {
		
	
	public MonLogServer(){
		
		File dossier = new File("Log"+File.separator+"server");
		try {
			if( dossier.exists() && dossier.isDirectory() ){
		
				super.fichier = new File("Log"+File.separator+"server"+File.separator+"server_log.txt");
				try {
					fichier.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			} else {
				dossier.mkdir();
			}
		} catch (SecurityException e) {
			new MonLogServer().add(e.getMessage(), Level.SEVERE);
		}
	}
	
}
