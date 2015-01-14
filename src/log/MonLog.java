package log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public abstract class MonLog {
		
	File fichier;   // package car les classes filles n'utilisent pas le même fichier (constructeurs différents)
	private BufferedWriter writer;
	
	public void add(String texte){
		
		String texte_avec_date_heure = new Date().toString()+" : "+texte;
		FileWriter fw = null;
		try {
			if(fichier!=null){
				fw = new FileWriter(fichier.getAbsoluteFile(),true);
				writer = new BufferedWriter(fw);
				writer.write(texte_avec_date_heure+"\n");
			}
		} catch (IOException e) {
			new MonLogClient().add(e.getMessage());
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
