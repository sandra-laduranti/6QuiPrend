package graphique;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import utils.MonLogClient;

public class Main6QuiPrend{

	public static void main(String[] args) {
		/**
         * Set look and feel of app
         */
        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, you can set the GUI to another look and feel.
        }
        
// PASSAGE D'OBJET PAR FLUX
//        try {
//        	FileOutputStream  fout=new FileOutputStream(args.getClass()+"."+args.getClass());
//        	ObjectOutputStream out = new ObjectOutputStream(fout);
//        	User u = new User("thomas","mdp","mdp@mdp.com");
//        	System.out.println(u.toString());
//        	out.writeObject(u);
//        	
//        	FileInputStream fin = new FileInputStream(args.getClass()+"."+args.getClass());
//        	ObjectInputStream in = new ObjectInputStream(fin);
//			User un = (User) in.readObject();
//			System.out.println(un.toString());
//        	System.out.println(u.getUserNickname());
//        	
//		} catch (ClassNotFoundException | IOException e1) {
//			e1.printStackTrace();
//		}
        /**
         * Create GUI and components on Event-Dispatch-Thread
         */
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
            	final FenetrePrincipale f = new FenetrePrincipale(null);
            	f.addWindowListener(new WindowListener() {
        			
        			@Override
        			public void windowClosing(WindowEvent e) {
        				new MonLogClient().add("Fermeture de l'application !\n\n");
        				
        				System.out.println("\n************************************************************************************************************************************************************\n"
        							   	   + "**************************************************** Fermeture du 6 Qui Prend ... **************************************************************************\n"
        								   + "****************************************************                              **************************************************************************\n"
        								   + "****************************************************         A Bientot            **************************************************************************\n"
        								   + "************************************************************************************************************************************************************");
        			}
        			
        			@Override
        			public void windowOpened(WindowEvent e) { }
        			
        			@Override
        			public void windowIconified(WindowEvent e) { }
        			
        			@Override
        			public void windowDeiconified(WindowEvent e) { }
        			
        			@Override
        			public void windowDeactivated(WindowEvent e) { }
        			
        			@Override
        			public void windowClosed(WindowEvent e) { }
        			
        			@Override
        			public void windowActivated(WindowEvent e) { }
        		});
            }
        });
		
		
	}


}
