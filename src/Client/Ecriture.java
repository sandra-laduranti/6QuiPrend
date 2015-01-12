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
	private InputStreamReader isr;
	private BufferedReader br;
	private OutputStream os;
	private OutputStreamWriter osw;
	private BufferedWriter bw;
	
	public Ecriture(Socket socket) {
		this.socket = socket;
	}
	
	@Override
	public void run() {
		if (!this.socket.isClosed()) {
			this.isr = new InputStreamReader(System.in);
			this.br = new BufferedReader(isr);
	
			try {
				this.os = this.socket.getOutputStream();
				this.osw = new OutputStreamWriter(os);
				this.bw = new BufferedWriter(osw);
			} catch (IOException e1) {
				System.err.println("> Erreur d'ouverture du flux...");
			}
	
			try {
				String texte;
				while ((texte = br.readLine()) != null) {
					bw.write(texte);
					bw.newLine();
					bw.flush();
				}
			}
			catch(IOException e) {
				System.err.println("> Erreur de lecture...");
			}
		}
	}

}