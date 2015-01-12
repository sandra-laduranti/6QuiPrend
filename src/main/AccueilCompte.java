package main;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import metier.Compte;
import utils.EcranGauche;
import utils.PanneauBordure;

public class AccueilCompte{
	
	private FenetreAccueil context;
	private PanneauBordure panneau;
	private EcranGauche ecrangauche;
	
	Compte compte;
	int nb_parties_gagnees;
	int nb_parties_perdues;
	
	// Premier formulaire : Liste événements
	private JButton bouton_retour;
	
	public AccueilCompte(FenetreAccueil context, Compte compte) {
		
		this.context= context;
		this.compte = compte;
		
		JPanel container_infos = new JPanel();
		container_infos.setBorder(new EmptyBorder(20,15,0,20));
	    container_infos.setOpaque(false);
	    
	        JPanel infos = new JPanel(new BorderLayout(10,15));
		    infos.setOpaque(false);
		    
	        JLabel texte;
	        texte = new JLabel("<html><font color='black'><u>Compte</u> : "+
	        		compte.getCompte_Login()+"<br><br><br>Nombre de parties gagnées: "+nb_parties_gagnees+"<br><br>Nombre de parties perdues : "+nb_parties_perdues+"<br><br></font></html>");
	    
	        infos.add(texte,BorderLayout.NORTH); // texte en blanc
		    infos.add(context.getBoutonDeconnexion(),BorderLayout.SOUTH);
	    container_infos.add(infos);

	    
	    // On vide le panneau de droite et on met la boite d'infos
	    panneau = context.getPanneauBordure();
	    panneau.removeAll();
	    panneau.add(container_infos, BorderLayout.NORTH);
		
		ecrangauche = context.getEcranGauche();
		ecrangauche.paintComponentWithoutImage();

		bouton_retour = new JButton("Retour au menu principal");
		bouton_retour.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// methode déclenchée si clique
			}
		});
		
		context.revalidate();
		context.repaint();
	}
	
	
	
}
