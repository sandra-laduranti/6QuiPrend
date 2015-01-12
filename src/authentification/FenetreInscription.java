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
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import metier.User;

public class FenetreInscription extends JDialog {

	private static final long serialVersionUID = 1L;
	private User User; // Id User
	private JTextField tfUsername;
    private JPasswordField pfPassword;
	private JTextField tfMail;
    private JButton btnLogin;
    private JButton btnCancel;
    private boolean succeeded = false;
    private boolean canceled = false;

    public FenetreInscription(Frame context) {

    	super(context, "Inscription", true);
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

        btnLogin = new JButton("S'inscrire");

        btnLogin.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
//            	En attendant la BDD, on commente et on attribue un User bidon
//            	User = new UserDAO().verifieAuthentification(getUsername(), getPassword());
            	
            	User = null;
            	
                if (User!=null) {
                    JOptionPane.showMessageDialog(FenetreInscription.this,
                            "Salut " + getUsername() + " !",
                            "Connexion réussie", 
                            JOptionPane.INFORMATION_MESSAGE);
                    succeeded = true;
                    dispose();
                } else { // Si User null, c'est qu'aucun User contient ce Login+mdp
                    JOptionPane.showMessageDialog(FenetreInscription.this,
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

    public boolean isSucceeded() {
        return succeeded;
    }
    
    public boolean isCanceled() {
        return canceled;
    }
    
    public User getUser(){
    	return User;
    }
    
}
