package utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MonLog {
	
	public static final String CLIENT = "CLIENT";
	public static final String PARTIE = "PARTIE";
	
	private File fichier;
	private BufferedWriter writer;
	
	/**
	 * Choisir entre MonLog.CLIENT et MonLog.PARTIE
	 * @param type
	 */
	public MonLog(String type){
		
		if(type.equals(CLIENT)){  // on vérifie ici si le fichier existe déjà ou pas
			
				fichier = new File("Log"+File.separator+"client"+File.separator+"client_log.txt");
				try {
					fichier.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			
		} else if (type.equals(PARTIE)){
				
				Date date = new Date();
				
				fichier = new File("Log"+File.separator+"parties"+File.separator+"log_partie_"
						+new SimpleDateFormat("dd-MM-yyyy", Locale.FRANCE).format(date)+"_"+
						new SimpleDateFormat("HH", Locale.FRANCE).format(date)+"h"+
						new SimpleDateFormat("mm", Locale.FRANCE).format(date)+"min"+
						new SimpleDateFormat("ss", Locale.FRANCE).format(date)+"s.txt");
				try {
					if(fichier.createNewFile()==false){
						new MonLog(MonLog.CLIENT).add("Le fichier "+fichier.getName()+" existe déjà");
					}
				} catch (IOException e) {
					new MonLog(MonLog.CLIENT).add(e.getMessage());
				}
		}
	}
	
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
			new MonLog(MonLog.CLIENT).add(e.getMessage());
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
