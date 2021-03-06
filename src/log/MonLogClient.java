package log;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * 
 * @author Julien M
 *
 */
public class MonLogClient extends MonLog {
		
	
	public MonLogClient(){
		
		File dossier = new File("Log"+File.separator+"client");
		try {
			if( dossier.exists() && dossier.isDirectory() ){
		
				super.fichier = new File("Log"+File.separator+"client"+File.separator+"client_log.txt");
				try {
					fichier.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			} else {
				dossier.mkdir();
			}
		} catch (SecurityException e) {
			new MonLogClient().add(e.getMessage(), Level.SEVERE);
		}
	}
	
}
