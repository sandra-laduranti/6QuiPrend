package Client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Ecriture extends Thread {
	
	private Socket socket;
	private InputStreamReader inputStreamReader;
	private BufferedReader bufferedReader;
	private OutputStream outputStream;
	private OutputStreamWriter outputStreamWriter;
	private BufferedWriter bufferedWriter;
	
	public Ecriture(Socket socket) {
		this.socket = socket;
	}
	
	@Override
	public void run() {
		if (!this.socket.isClosed()) {
			this.inputStreamReader = new InputStreamReader(System.in);
			this.bufferedReader = new BufferedReader(inputStreamReader);
	
			try {
				this.outputStream = this.socket.getOutputStream();
				this.outputStreamWriter = new OutputStreamWriter(outputStream);
				this.bufferedWriter = new BufferedWriter(outputStreamWriter);
			} catch (IOException e1) {
				System.err.println("> Erreur d'ouverture du flux...");
			}
	
			try {
				String texte;
				while ((texte = bufferedReader.readLine()) != null) {
					bufferedWriter.write(texte);
					bufferedWriter.newLine();
					bufferedWriter.flush();
				}
			}
			catch(IOException e) {
				System.err.println("> Erreur de lecture...");
			}
		}
	}

}