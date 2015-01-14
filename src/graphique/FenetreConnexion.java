package graphique;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import log.MonLogClient;
import metier.User;
import metierDAO.UserDAO;

public class FenetreConnexion extends JDialog {

	private static final long serialVersionUID = 1L;
	private User user; // Id user
	private JTextField tfUsername;
    private JPasswordField pfPassword;
    private JButton btnLogin;
    private JButton btnCancel;
    private boolean succeeded = false;

    public FenetreConnexion(final FenetrePrincipale context) {

    	super(context, "Login", true);
    	new MonLogClient().add("Tentative de connexion");
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints cs = new GridBagConstraints();
        cs.fill = GridBagConstraints.HORIZONTAL;
        
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

            	user = UserDAO.verifieAuthentification(getUsername(), getPassword());
                if (user!=null) {
                	context.setUser(user);
                    JOptionPane.showMessageDialog(FenetreConnexion.this,
                            "Salut " + getUsername() + " !",
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
        bp.add(btnLogin);
        bp.add(btnCancel);

        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(bp, BorderLayout.PAGE_END);

        pack();
        setResizable(false);
        setLocationRelativeTo(context);
        
    }

    private String getUsername() {
        return tfUsername.getText().trim();
    }

    private String getPassword() {
        return new String(pfPassword.getPassword());
    }

    public boolean isSucceeded() {
        return succeeded;
    }
    
}