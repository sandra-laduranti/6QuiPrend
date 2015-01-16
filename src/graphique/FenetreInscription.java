package graphique;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import log.MonLogClient;
import metierDAO.UserDAO;

public class FenetreInscription extends JDialog {

	
	private static final long serialVersionUID = 1L;
	private JTextField tfUsername;
    private JPasswordField pfPassword;
	private JTextField tfMail;
    private JButton btnInscription;
    private JButton btnCancel;
    private boolean succeeded = false;
    private boolean canceled = false;

    public FenetreInscription(final FenetrePrincipale context) {

    	super(context, "Inscription", true);
    	new MonLogClient().add("Tentative d'inscription");
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints cs = new GridBagConstraints();
        cs.fill = GridBagConstraints.HORIZONTAL;
        
        // Inscription
        cs.gridx = 0;
        cs.gridy = 2;
        cs.gridwidth = 1;
        panel.add(new JLabel("Login : "), cs);

        tfUsername = new JTextField(20);
        cs.gridx = 1;
        cs.gridy = 2;
        cs.gridwidth = 2;
        panel.add(tfUsername, cs);

        // Password
        cs.gridx = 0;
        cs.gridy = 3;
        cs.gridwidth = 1;
        panel.add(new JLabel("Password : "), cs);

        pfPassword = new JPasswordField(20);
        cs.gridx = 1;
        cs.gridy = 3;
        cs.gridwidth = 2;
        panel.add(pfPassword, cs);
        panel.setBorder(new LineBorder(Color.GRAY));
        
        // Mail
        cs.gridx = 0;
        cs.gridy = 4;
        cs.gridwidth = 1;
        panel.add(new JLabel("Mail : "), cs);

        tfMail = new JTextField(20);
        cs.gridx = 1;
        cs.gridy = 4;
        cs.gridwidth = 2;
        panel.add(tfMail, cs);

        btnInscription = new JButton("S'inscrire");
        btnInscription.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
            	
            	Pattern VALID_EMAIL_ADDRESS_REGEX = 
            		    Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

            	if(getUsername().isEmpty() || getPassword().isEmpty() || getMail().isEmpty()){
            		JOptionPane.showMessageDialog(FenetreInscription.this,
                            "Tous les champs doivent être remplis",
                            "Erreur", 
                            JOptionPane.ERROR_MESSAGE);
            		succeeded = false;
            	} else if( getUsername().contains(" ") ){
            		JOptionPane.showMessageDialog(FenetreInscription.this,
                            "Votre login contient un ou plusieurs espaces\n"
                            + "Veuillez les retirer.",
                            "Erreur", 
                            JOptionPane.ERROR_MESSAGE);
            		succeeded = false;
            	}else if( !VALID_EMAIL_ADDRESS_REGEX .matcher(getMail()).matches() ){
            		JOptionPane.showMessageDialog(FenetreInscription.this,
                            "Votre adresse mail est incorrecte.",
                            "Erreur", 
                            JOptionPane.ERROR_MESSAGE);
            		succeeded = false;
            	} else { 
            		if( UserDAO.existLogin(getUsername())){
            			JOptionPane.showMessageDialog(FenetreInscription.this,
                            "Login " + getUsername() + " déjà existant !",
                            "Erreur", 
                            JOptionPane.ERROR_MESSAGE);
            			succeeded = false;
            		} else {
            			int id = UserDAO.createUser(getUsername(),getMail(),getPassword());
                        if ( id != -1 ) {
                        	context.setIdUser(id);
                        	context.setNomUser(getUsername());
                            JOptionPane.showMessageDialog(FenetreInscription.this,
                                    "Salut " + getUsername() + " !",
                                    "Inscription réussie",
                                    JOptionPane.INFORMATION_MESSAGE);
                            succeeded = true;
                            dispose();
                        } else {
                        	JOptionPane.showMessageDialog(FenetreInscription.this,
                                    "Problème en base ... Votre compte ne s'est pas crée.",
                                    "Erreur", 
                                    JOptionPane.ERROR_MESSAGE);
                    			succeeded = false;
                        }
            		}
            	}
            }
        });
        
        btnCancel = new JButton("Annuler");
        btnCancel.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
            	canceled=true;
                dispose();
            }
        });
        JPanel bp = new JPanel();
        bp.add(btnInscription);
        bp.add(btnCancel);

        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(bp, BorderLayout.PAGE_END);

        pack();
        setResizable(false);
        setLocationRelativeTo(context);
        
        this.addWindowListener(new WindowListener() {
			
        	@Override
			public void windowClosing(WindowEvent e) {
				canceled=true;
			}
			@Override
			public void windowClosed(WindowEvent e) {
				canceled=true;
			}
			@Override
			public void windowOpened(WindowEvent e) {}
			@Override
			public void windowIconified(WindowEvent e) {}
			@Override
			public void windowDeiconified(WindowEvent e) {}
			@Override
			public void windowDeactivated(WindowEvent e) {}
			@Override
			public void windowActivated(WindowEvent e) {}
		});
    }

    public String getUsername() {
        return tfUsername.getText().trim();
    }

    public String getPassword() {
        return new String(pfPassword.getPassword());
    }
    
    public String getMail() {
        return tfMail.getText().trim();
    }

    public boolean isSucceeded() {
        return succeeded;
    }
    
    public boolean isCanceled() {
        return canceled;
    }

    
}
