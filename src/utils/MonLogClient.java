package utils;

import java.io.File;
import java.io.IOException;

public class MonLogClient extends MonLog {
		
	
	public MonLogClient(){
		
		super.fichier = new File("Log"+File.separator+"client"+File.separator+"client_log.txt");
		try {
			fichier.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
