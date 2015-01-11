package main;

import java.awt.Color;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

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
        /**
         * Create GUI and components on Event-Dispatch-Thread
         */
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
            	FenetreAccueil f = new FenetreAccueil();
            	f.addWindowListener(new WindowListener() {
        			
        			@Override
        			public void windowClosing(WindowEvent e) {
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
