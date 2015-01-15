package log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import metier.Partie;

public class MonLogPartie extends MonLog {
	
	public MonLogPartie(Partie partie){
		Date date = new Date();
		
		File dossier = new File("Log"+File.separator+"parties");
		try {
			if( dossier.exists() && dossier.isDirectory() ){
				
				// Cherche si un fichier contenant l'id existe déjà, si oui, on reprend ce fichier pour les ecritures
				for(File file : dossier.listFiles()){
					if(file.getName().contains("id"+partie.getIdPartie())){
						super.fichier = fichier;
						break;
					}
				}
				
				// Si aucun fichier existe, on le crée
				if(super.fichier==null){
						super.fichier = new File("Log"+File.separator+"parties"+File.separator+"log_partie_"+
												"id"+partie.getIdPartie()+"_"+
												new SimpleDateFormat("dd-MM-yyyy", Locale.FRANCE).format(date)+"_"+
												new SimpleDateFormat("HH", Locale.FRANCE).format(date)+"h"+
												new SimpleDateFormat("mm", Locale.FRANCE).format(date)+"min.txt");
				}
				
			} else {
				dossier.mkdir();
			}
		} catch (SecurityException e) {
			new MonLogClient().add(e.getMessage());
		}
	}
	

}
