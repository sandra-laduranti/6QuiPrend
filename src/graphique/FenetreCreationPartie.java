package graphique;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import log.MonLogClient;

/**
 * 
 * @author Julien M
 *
 */
public class FenetreCreationPartie extends JDialog {

	private static final long serialVersionUID = 1L;
	private JTextField nomPartie;
    private JComboBox<Integer> nbMaxJoueurs;
    private JRadioButton normal;
    private JRadioButton pro;
    private JButton btnCreation;
    private JButton btnCancel;
    private boolean succeeded = false;

    /**
     * Créer une fenetre de remplissage de formulaire pour la création d'une partie
     * @param context
     */
    public FenetreCreationPartie(final FenetrePrincipale context) {

    	super(context, "Nouvelle partie", true);
    	new MonLogClient().add("Tentative de création d'une nouvelle partie",Level.INFO);
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints cs = new GridBagConstraints();
        cs.fill = GridBagConstraints.HORIZONTAL;
        
        // Nom de la partie
        cs.gridx = 0;
        cs.gridy = 0;
        cs.gridwidth = 1;
        panel.add(new JLabel("Nom de la partie : "), cs);
        
        nomPartie = new JTextField(15);
        cs.gridx = 1;
        cs.gridy = 0;
        cs.gridwidth = 2;
        panel.add(nomPartie, cs);
        
        
        cs.gridx = 0;
        cs.gridy = 1;
        cs.gridwidth = 1;
        panel.add(new JLabel("Nombre max de joueurs : "), cs);

        nbMaxJoueurs = new JComboBox<Integer>();
        for(int i=2; i<11; i++){
        	nbMaxJoueurs.addItem(i);
        }
        cs.gridx = 1;
        cs.gridy = 1;
        cs.gridwidth = 2;
        panel.add(nbMaxJoueurs, cs);
        panel.setBorder(new LineBorder(Color.GRAY));
        
        
        cs.gridx = 0;
        cs.gridy = 2;
        cs.gridwidth = 1;
        panel.add(new JLabel("Mode de jeu : "), cs);

        normal = new JRadioButton("Normal");
        pro = new JRadioButton("Pro");
        ButtonGroup bg = new ButtonGroup();
        bg.add(normal);
        bg.add(pro);
        normal.setSelected(true);
        JPanel radio = new JPanel(new GridLayout(0,3));
        cs.gridx = 1;
        cs.gridy = 2;
        cs.gridwidth = 2;
        radio.add(normal);
        radio.add(pro);
        panel.add(radio,cs);


        btnCreation = new JButton("Créer");
        btnCreation.addActionListener(new ActionListener() {
        	
            public void actionPerformed(ActionEvent e) {
            	
            	if(nomPartie.getText().isEmpty()){
            		JOptionPane.showMessageDialog(context,
                            "Vous devez lui donner un nom",
                            "Erreur", 
                            JOptionPane.ERROR_MESSAGE);
            		succeeded = false;
            	} else { 
                    JOptionPane.showMessageDialog(context,
                            "Création de la partie "+nomPartie.getText()+" réussie !",
                            "Connexion réussie",
                            JOptionPane.INFORMATION_MESSAGE);
                    succeeded = true;
                    dispose();
            	}
            }
        });
        btnCancel = new JButton("Annuler");
        btnCancel.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        JPanel bp = new JPanel();
        bp.add(btnCreation);
        bp.add(btnCancel);

        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(bp, BorderLayout.PAGE_END);

        pack();
        setResizable(false);
        setLocationRelativeTo(context);
        
    }

    public String getNamePartie() {
        return nomPartie.getText().trim();
    }

    public int getNbMaxJoueurs() {
    	return (int) nbMaxJoueurs.getSelectedItem(); // Ici, on est sur que nbMaxJoueurs est un int
    }
    
    public boolean getProMode() {
        if(normal.isSelected()){ return false; }
        return true;
    }

    public boolean isSucceeded() {
        return succeeded;
    }
    
}