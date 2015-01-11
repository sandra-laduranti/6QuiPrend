package authentification;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import metier.Compte;
import metierDAO.CompteDAO;

public class FenetreConnexion extends JDialog {

	private static final long serialVersionUID = 1L;
	private Compte compte; // Id compte
	private JTextField tfUsername;
    private JPasswordField pfPassword;
    private JComboBox<String> liste_user;
    private JButton btnLogin;
    private JButton btnCancel;
    private boolean succeeded;
    private boolean canceled = false;

    public FenetreConnexion(Frame parent) {

    	super(parent, "Login", true);
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints cs = new GridBagConstraints();
        cs.fill = GridBagConstraints.HORIZONTAL;
        
        // Type utilisateur
        cs.gridx = 0;
        cs.gridy = 0;
        cs.gridwidth = 1;
        panel.add(new JLabel("Je suis : "),cs);
        cs.gridx = 1;
        cs.gridy = 0;
        cs.gridwidth = 1;
        panel.add(liste_user,cs);
        
        // Login
        cs.gridx = 0;
        cs.gridy = 2;
        cs.gridwidth = 1;
        panel.add(new JLabel("Login: "), cs);

        tfUsername = new JTextField(20);
        cs.gridx = 1;
        cs.gridy = 2;
        cs.gridwidth = 2;
        panel.add(tfUsername, cs);

        // Password
        cs.gridx = 0;
        cs.gridy = 3;
        cs.gridwidth = 1;
        panel.add(new JLabel("Password: "), cs);

        pfPassword = new JPasswordField(20);
        cs.gridx = 1;
        cs.gridy = 3;
        cs.gridwidth = 2;
        panel.add(pfPassword, cs);
        panel.setBorder(new LineBorder(Color.GRAY));

        btnLogin = new JButton("Connexion");

        btnLogin.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
            	compte = new CompteDAO().verifieAuthentification(getUsername(), getPassword());
                if (compte!=null) {
                    JOptionPane.showMessageDialog(FenetreConnexion.this,
                            "Salut " + getUsername() + " !",
                            "Connexion réussie", 
                            JOptionPane.INFORMATION_MESSAGE);
                    succeeded = true;
                    dispose();
                } else { // Si id_compte = 0, c'est que le compte n'existe pas
                    JOptionPane.showMessageDialog(FenetreConnexion.this,
                            "Login ou mot de passe invalide !",
                            "Connexion refusée",
                            JOptionPane.ERROR_MESSAGE);
                    tfUsername.setText("");
                    pfPassword.setText("");
                    succeeded = false;
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
        bp.add(btnLogin);
        bp.add(btnCancel);

        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(bp, BorderLayout.PAGE_END);

        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
        
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
    
    public String getTypeUser(){
    	return (String) liste_user.getSelectedItem();
    }

    public String getUsername() {
        return tfUsername.getText().trim();
    }

    public String getPassword() {
        return new String(pfPassword.getPassword());
    }

    public boolean isSucceeded() {
        return succeeded;
    }
    
    public boolean isCanceled() {
        return canceled;
    }
    
}