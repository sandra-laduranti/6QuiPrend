package log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import metier.Partie;

public class MonLogPartie extends MonLog {
	
	public MonLogPartie(Partie partie){
		
		Date date = new Date();
		
		// Crée ou récupère un fichier, avec comme nom, par exemple, log_partie_id35_13-02-2015_16h53min.txt
		super.fichier = new File("Log"+File.separator+"parties"+File.separator+"log_partie_"+
				"id"+partie.getId()+
				new SimpleDateFormat("dd-MM-yyyy", Locale.FRANCE).format(date)+"_"+
				new SimpleDateFormat("HH", Locale.FRANCE).format(date)+"h"+
				new SimpleDateFormat("mm", Locale.FRANCE).format(date)+"min.txt");
		try {
			if( !fichier.exists() ){
				fichier.createNewFile();
			}
		} catch (IOException | SecurityException e) {
			new MonLogClient().add(e.getMessage());
		}
	}
	

}
