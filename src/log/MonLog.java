package log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;

/**
 * 
 * @author Julien M
 *
 */
public abstract class MonLog {
		
	File fichier;   // package car les classes filles n'utilisent pas le même fichier (constructeurs différents)
	private BufferedWriter writer;
	
	/**
	 * Ajoute un texte dans un fichier (fichier différent suivant le type réel de l'objet log instancié)
	 * @param texte
	 * @param level
	 */
	public void add(String texte, Level level){
		
		String texte_avec_date_heure = new Date().toString()+" : ["+level.getName()+"] "+texte;
		FileWriter fw = null;
		try {
			if(fichier!=null){
				fw = new FileWriter(fichier.getAbsoluteFile(),true);
				writer = new BufferedWriter(fw);
				writer.write(texte_avec_date_heure+"\n");
			}
		} catch (IOException e) {
			new MonLogClient().add(e.getMessage(), Level.SEVERE);
		} finally {
				try {
					if(writer != null) writer.close();
					if( fw !=null) fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		
	}
}
