package graphique;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayer;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import log.MonLog;
import log.MonLogClient;
import metier.Carte;
import metier.Partie;
import utils.EcranGauche;
import utils.ImageButton;
import utils.PanneauBordure;

import communication.User;


public class FenetrePrincipale extends JFrame implements ActionListener{

	private static final long serialVersionUID = 1L;
	
	private MonLog log_client;
	
	private static final String texte_connexion = "Connexion";
	private static final String texte_deconnexion = "Déconnexion";
	private static final String texte_inscription = "Inscription";
	private static final String texte_quitter = "Quitter";
	
	private static final String texte_rafraichir = "Rafraichir les parties";
	private static final String texte_creerPartie = "Creer une nouvelle partie";
	private FenetrePrincipale context;
	private JMenuBar menuBar;;
	private JMenu menu;
	private JMenuItem item_connexion; // Apparait si non connecté
	private JMenuItem item_deconnexion; // Apparait si connecté
	private JMenuItem item_exit;
	
	// Connexion
	private JButton bouton_connexion;
	private JButton bouton_deconnexion;
	private boolean is_connected;
	
	// Autres boutons
	private JButton bouton_inscription;
	
	//Panneau
	private PanneauBordure panneau;
	private EcranGauche ecrangauche;
	
	private User user;
	private int idUser;
	private String nomUser;
	
	private boolean debloqueur = false; // Permet de différencier si la sortie d'1 wait(timeout) est grace a notify ou temps écoulé (le Client va le modifier lors d'un notify)
	public Object sync;
	
	// La fenetre d'attente
	private JFrame frame_wait;
	private WaitLayerUI layerUI;



	public FenetrePrincipale(Object sync){
		
		this.context = this;  // Pour pouvoir utiliser notre instance de fenetreprincipale partout (methodes statics, listeners, classes ...) lorsque this ne fonctionne pas
	    this.sync = sync;
		
		this.setTitle("6 Qui Prend");
	    
	    URL url_tmp = getClass().getResource("/images/logo 6QuiPrend.png");
		if(url_tmp!=null) this.setIconImage(new ImageIcon(url_tmp).getImage()); // Logo
		this.setLayout(new BorderLayout()); // Layout qui permet d'ajouter soit sur le bord, soit au centre
	    this.setUndecorated(true);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
	    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    this.setLocationRelativeTo(null);
	    
	    //On initialise nos menus     
		menuBar = new JMenuBar();
		menu = new JMenu("Menu");
		item_connexion = new JMenuItem(texte_connexion); // Apparait si non connecté
		item_deconnexion = new JMenuItem(texte_deconnexion); // Apparait si connecté
		item_exit = new JMenuItem(texte_quitter);
	    this.menu.add(item_connexion); // Au début il n'y a que connexion
	    this.menu.addSeparator();
	    this.menu.add(item_exit);
	    
	    item_connexion.addActionListener(this); // l'action du listener est définie plus bas (va différencier les connexions et déconnexions)
	    item_deconnexion.addActionListener(this);
	    item_exit.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
//						int choix = JOptionPane.showConfirmDialog(null, "Êtes-vous sûr de vouloir quitter ?", "Quitter", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
//			    		if(choix == JOptionPane.OK_OPTION){
//			    			context.dispatchEvent(new WindowEvent(context, WindowEvent.WINDOW_CLOSING)); // On ferme l'appli
//			    		}
						context.afficherPartie();
                    }
        });
	    
	    menuBar.add(menu);
	    this.setJMenuBar(menuBar);
	    
	    // On créé nos boutons et ajoutons les listeners
	    // Boutons connexions/déconnexions
	    bouton_connexion = new JButton(texte_connexion);
        bouton_connexion.addActionListener(this); // L'action du listener est définie en bas
	    
	    bouton_deconnexion = new JButton(texte_deconnexion);
	    bouton_deconnexion.addActionListener(this);
	    
	    bouton_inscription = new JButton(texte_inscription);
	    bouton_inscription.addActionListener(this);
	    
	    
        // Partie centre/gauche
        ecrangauche = new EcranGauche(new BorderLayout());
        url_tmp = getClass().getResource("/images/fond 6QuiPrend.png");
        if(url_tmp!=null) ecrangauche.setImage(new ImageIcon(url_tmp).getImage());
        this.add(ecrangauche, BorderLayout.CENTER);

        // On créé un nouveau panneau sur la droite (avec un JPanel vide pour espacer)
        panneau = new PanneauBordure(this, bouton_connexion, bouton_inscription);
        url_tmp = getClass().getResource("/images/fonddroite.jpg");
        if(url_tmp!=null) panneau.setImage(new ImageIcon(url_tmp).getImage());
        this.add(panneau, BorderLayout.EAST);

	    this.setVisible(true);
	    this.revalidate();
	    this.repaint();
	    
	    log_client = new MonLogClient();
	    log_client.add("Application lancée :)");
	    this.initWaitLayer();
	}

	private void initWaitLayer() {
		layerUI = new WaitLayerUI();
		frame_wait = new JFrame();
		
		frame_wait.setIconImage(null);
		frame_wait.setLocationRelativeTo(null);
		JPanel panel = new JPanel() {

            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public Dimension getPreferredSize() {
                return new Dimension(300, 200);
            }
        };
        panel.setOpaque(false);
        panel.setLayout(new BorderLayout());
        panel.add(new JLabel("En attente de réponse du serveur ..."), BorderLayout.CENTER);
        panel.setBorder(new EmptyBorder(0,60,0,0));
        JLayer<JPanel> jlayer = new JLayer<>(panel, layerUI);
        frame_wait.setUndecorated(true);
        frame_wait.add(jlayer);
        frame_wait.pack();
        
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// Si c'est une connexion
		if(e.getActionCommand().equals(item_connexion.getText()) || e.getActionCommand().equals(bouton_connexion.getText())){
			//Pour les tests, commenter les 3 lignes suivantes et laisser décommenté la 4 eme ligne
			FenetreConnexion fenetreconnexion = new FenetreConnexion(context);
			fenetreconnexion.setVisible(true);
			if(fenetreconnexion.isSucceeded()){
//			if(true){
		    	is_connected=true; // Flag
		    	
				// Réorganisation des menus
				this.menu.remove(item_connexion); // Si la connexion à réussie, on l'enlève du menu
				this.menu.add(item_deconnexion,0); // et on ajoute le bouton deconnexion
				
				log_client.add("Connecté !");
				// Ligne suivante à décommenter pour test
				this.modifierInterfaceAfterConnexion();
				
	        } else {
	        	is_connected=false;
	        	log_client.add("Connexion échouée");
	        }
	    
	    // Si c'est une déconnexion
		} else if (e.getActionCommand().equals(item_deconnexion.getText()) || e.getActionCommand().equals(bouton_deconnexion.getText())){
			
			int choix = JOptionPane.showConfirmDialog(null, "Êtes-vous sûr de vouloir vous déconnecter ?", "Deconnexion", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    		if(choix == JOptionPane.OK_OPTION){

    			this.menu.remove(item_deconnexion); // Si la deconnexion à réussie, on l'enlève du menu
    			this.menu.add(item_connexion,0);
    			
    			ecrangauche.removeAll();
    			URL url_tmp = getClass().getResource("/images/fond 6QuiPrend.png");
    	        if(url_tmp!=null) ecrangauche.setImage(new ImageIcon(url_tmp).getImage());
    	        
    			this.remove(panneau);
    			panneau = new PanneauBordure(context, bouton_connexion,bouton_inscription);
    			url_tmp = getClass().getResource("/images/fonddroite.jpg");
    	        if(url_tmp!=null) panneau.setImage(new ImageIcon(url_tmp).getImage());
    	        this.add(panneau, BorderLayout.EAST);
    			context.revalidate(); // A mettre toujours avant repaint
    			context.repaint(); // Mise à jour de la fenetre, a faire souvent lorsque changement
			
				log_client.add("Déconnecté :(");
    		}
		} else if (e.getActionCommand().equals(bouton_inscription.getText())){
			FenetreInscription fenetreinscription = new FenetreInscription(context);
			fenetreinscription.setVisible(true);
			if(fenetreinscription.isSucceeded()){
		    	is_connected=true; // Flag pouvant servir plus tard
		    	
				// Réorganisation des menus
				this.menu.remove(item_connexion); // Si la connexion à réussie, on l'enlève du menu
				this.menu.add(item_deconnexion,0); // et on ajoute le bouton deconnexion
				
				log_client.add("Inscription réussie");
				this.modifierInterfaceAfterConnexion();
	        } else {
	        	is_connected=false;
	        	log_client.add("Inscription échouée");
	        }
		}
		this.revalidate();
		this.repaint();
	}
	
	public boolean isConnected() {
		return is_connected;
	}
	
	public PanneauBordure getPanneauBordure(){
		return panneau;
	}
	
	public EcranGauche getEcranGauche(){
		return ecrangauche;
	}
	
	public JButton getBoutonDeconnexion(){
		return bouton_deconnexion;
	}
	

	/// Changement d'interfaces
	private void modifierInterfaceAfterConnexion(){
		
		ecrangauche.paintComponentWithoutImage();
		
		JPanel container_infos = new JPanel();
		container_infos.setBorder(new EmptyBorder(20,15,0,20));
	    container_infos.setOpaque(false);
	    
	        JPanel infos = new JPanel(new BorderLayout(10,15));
		    infos.setOpaque(false);
		    
	        JLabel texte;
	        texte = new JLabel("<html><font color='black'><u>Compte</u> : "+
	        		context.getName()+"<br><br><br>Nombre de parties gagnées: "+0/*user.getNb_parties_gagnees()*/+"<br><br>Nombre de parties perdues : "+0/*user.getNb_parties_perdues()*/+"<br><br></font></html>");
	    
	        infos.add(texte,BorderLayout.NORTH); // texte en blanc
		    infos.add(context.getBoutonDeconnexion(),BorderLayout.SOUTH);
	    container_infos.add(infos);

	    
	    // On vide le panneau de droite et on met la boite d'infos
	    panneau = context.getPanneauBordure();
	    panneau.removeAll();
	    panneau.setLayout(new BorderLayout());
	    panneau.add(container_infos, BorderLayout.NORTH);
	    
	    
	    JPanel container_rafraichir = new JPanel();
	    container_rafraichir.setOpaque(false);
	    container_rafraichir.setBorder(new EmptyBorder(130,0,0,0));
		    JButton bouton_rafraichir = new JButton(texte_rafraichir);
		    bouton_rafraichir.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					try{
						//context.afficherToutesLesParties(client.recupereListParties());
					} catch (NullPointerException exc){
						/// TODO : TESTs
//						ArrayList<Partie> parties = new ArrayList<Partie>();
//						Partie p = new Partie("test", 5, false, context.getCl);
//						p.addPlayer(new User("test2","",""));
//						p.addPlayer(new User("test3","",""));
//						p.addPlayer(new User("test4","",""));
//						p.addPlayer(new User("test5","",""));
//						parties.add(p);
						context.afficherToutesLesParties(null);
						log_client.add("Le client est à null (modifierInterfaceAfterConnexion, FenetrePrincipale)");
					}
				}
			});
	    container_rafraichir.add(bouton_rafraichir);

	    JPanel container_creer = new JPanel();
	    container_creer.setBorder(new EmptyBorder(0,0,60,0));
	    container_creer.setOpaque(false);
		    JButton bouton_creerPartie = new JButton(texte_creerPartie);
		    bouton_creerPartie.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					
					FenetreCreationPartie fenetrecreation = new FenetreCreationPartie(context);
					fenetrecreation.setVisible(true);
					if(fenetrecreation.isSucceeded()){
						log_client.add("Creation de la partie réussie");
						context.afficheSalonAttente(fenetrecreation.getNamePartie(),fenetrecreation.getNbMaxJoueurs(), context.getNomUser() );
//						client.creationPartie(fenetrecreation.getNamePartie(),fenetrecreation.getNbMaxJoueurs(),fenetrecreation.getProMode());
			        } else {
			        	log_client.add("Création de la partie échouée");
			        } 
					
				}
			});
		container_creer.add(bouton_creerPartie);
		
		panneau.add(container_rafraichir, BorderLayout.CENTER);
		panneau.addComposantEnBas(container_creer);
		
		ArrayList<Partie> parties = new ArrayList<Partie>();
		Partie p = new Partie("test", 5, false, context.user);
		p.addPlayer(context.user);
		p.addPlayer(context.user);
		p.addPlayer(context.user);
		p.addPlayer(context.user);
		parties.add(p);
		context.afficherToutesLesParties(parties);
		
	}
	
	public void afficherToutesLesParties(List<Partie> parties){
		
		ecrangauche.removeAll();
    	ecrangauche.setLayout(new BorderLayout());
    	JPanel container_all_parties = new JPanel();
    	container_all_parties.setLayout(new FlowLayout(FlowLayout.CENTER,10,10));
    	container_all_parties.setBorder(new EmptyBorder(20,20,50,20));
    	container_all_parties.setOpaque(false);
    	
    	List<User> users;
    	for(int x=0; x<12;x++)
    	for(int i=0; i<parties.size(); i++){ // On créer un carré par parties
    		users = parties.get(i).getListUser();
    		
			JPanel container_one_partie = new JPanel(new BorderLayout(0,0));
			TitledBorder bordure = BorderFactory.createTitledBorder("<html>"+parties.get(i).getNom()+"<br>(Max : "+parties.get(i).getNbJoueursMax()+" pers.)</html>");
			container_one_partie.setBorder(bordure);
			JPanel infos = new JPanel(new GridLayout(users.size(),1));
				
			for (int j=0; j<users.size(); j++){
				JLabel joueur = new JLabel(" - "+users.get(j).getUserNickname());
				infos.add(joueur);
			}
			
			
			JPanel container_bouton = new JPanel(); // Panel permettant de garder une taille raisonnable au bouton
    			JButton bouton_rejoindre = new JButton("Rejoindre");
    			
    			bouton_rejoindre.setName(parties.get(i).getNom()+"::"+parties.get(i).getNbJoueursMax());
    			for(User user : parties.get(i).getListUser()){
    				bouton_rejoindre.setName(bouton_rejoindre.getName()+"::"+user.getUserNickname());
    			}
    			
    			bouton_rejoindre.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						try{
//							client.rejoindrePartie(context.getUser().getUserNickname(), ((JButton)e.getSource()).getName());
							
//					        System.out.println("start");
//							layerUI.start();
//							context.revalidate();
//					        context.repaint();
							
//							if(debloqueur){
								System.out.println("Débloqué par un notify");
								String[] description = ((JButton)e.getSource()).getName().split("::");
								
								// Ici, je crée la partie avec les infos enregistrées dans le bouton rejoindre (grace au split)
								try{
									String nomPartie = description[0];
									int nbMax = Integer.parseInt(description[1]);
									String[] participants = Arrays.copyOfRange(description, 2, description.length);
									afficheSalonAttente(nomPartie, nbMax, participants);

								} catch (NumberFormatException nf){
									log_client.add(nf.getMessage());
								}
								
								
//								}
//							} else {
//								System.out.println("Temps écoulé");
//							}

						} catch (NullPointerException exc){
							log_client.add("Le client est null (afficherToutesLesParties, FenetrePrincipale)");
							
							exc.getMessage();
						}
					}
				});
    		container_bouton.add(bouton_rejoindre);
    		
			container_one_partie.add(infos,BorderLayout.NORTH);
			container_one_partie.add(container_bouton,BorderLayout.SOUTH);
			container_all_parties.add(container_one_partie);	
    	}
    	ecrangauche.add(container_all_parties, BorderLayout.CENTER);
    	context.revalidate();
    	context.repaint();
    	
	}
	
	public void afficheSalonAttente(String titre_partie, int nbMax, String ... nomsParticipants){
		ecrangauche.removeAll();
		
		ecrangauche.setLayout(new BorderLayout());
		JPanel description_partie = new JPanel(new GridLayout(10,1));
		description_partie.setOpaque(false);
		description_partie.setBorder(new EmptyBorder(20,50,0,0));
		
		JPanel nom_partie = new JPanel();
		nom_partie.setOpaque(false);
		JLabel nom_texte = new JLabel("<html><font color='black'><b><h2>Partie : "+titre_partie+" (en attente de joueurs - Max :"+nbMax+")</h2></b></font></html>");
		nom_partie.add(nom_texte);
		description_partie.add(nom_partie);
		
		for(String nom : nomsParticipants){
			JLabel joueur = new JLabel("==> "+nom);
			joueur.setOpaque(false);
			description_partie.add(joueur);
		}
		
		ecrangauche.add(description_partie,BorderLayout.CENTER);
		context.revalidate();
		context.repaint();
	
	}
	
	// Une partie commence !
	public void afficherPartie(){
		// AFFICHER MANCHE 1
		ecrangauche.removeAll();
		ecrangauche.setLayout(new GridLayout(6,1));
		JPanel tas1 = new JPanel();
		tas1.setLayout(new GridLayout(0,5));
		tas1.setOpaque(false);
		tas1.add(new ImageButton( new Carte(57).getImageIcon()));
		tas1.add(new ImageButton( new Carte(30).getImageIcon()));
		tas1.add(new ImageButton( new Carte(22).getImageIcon()));
		tas1.add(new ImageButton( new Carte(1).getImageIcon()));
		tas1.add(new ImageButton( new Carte(103).getImageIcon()));
		JPanel tasvide = new JPanel();
		tasvide.setLayout(new GridLayout(0,5));
		tasvide.setOpaque(false);

		JPanel tas2 = new JPanel();
		tas2.setLayout(new GridLayout(0,5));
		tas2.setOpaque(false);
		tas2.add(new ImageButton( new Carte(45).getImageIcon()));
		tas2.add(new ImageButton( new Carte(9).getImageIcon()));
		tas2.add(new ImageButton( new Carte(1).getImageIcon()));
		tas2.add(new ImageButton( new Carte(41).getImageIcon()));
		tas2.add(new ImageButton( new Carte(85).getImageIcon()));
		
		JPanel tas3 = new JPanel();
		tas3.setLayout(new GridLayout(0,5));
		tas3.setOpaque(false);
		tas3.add(new ImageButton( new Carte(57).getImageIcon()));
		tas3.add(new ImageButton( new Carte(30).getImageIcon()));
		tas3.add(new ImageButton( new Carte(22).getImageIcon()));
		tas3.add(new ImageButton( new Carte(1).getImageIcon()));
		tas3.add(new ImageButton( new Carte(103).getImageIcon()));

		JPanel tas4 = new JPanel();
		tas4.setLayout(new GridLayout(0,5));
		tas4.setOpaque(false);
		tas4.add(new ImageButton( new Carte(45).getImageIcon()));
		tas4.add(new ImageButton( new Carte(9).getImageIcon()));
		tas4.add(new ImageButton( new Carte(1).getImageIcon()));
		tas4.add(new ImageButton( new Carte(41).getImageIcon()));
		tas4.add(new ImageButton( new Carte(85).getImageIcon()));
		JPanel container_ligne1 = new JPanel(new GridLayout(0,2,0,0));
		container_ligne1.setOpaque(false);
		container_ligne1.add(tas1);
		container_ligne1.add(tasvide);
		
		JPanel container_ligne2 = new JPanel(new GridLayout(0,2,0,0));
		container_ligne2.setOpaque(false);
		container_ligne2.add(tas2);
		JPanel tasvide2 = new JPanel();
		tasvide2.setOpaque(false);
		container_ligne2.add(tasvide2);
		
		
		JPanel container_ligne3 = new JPanel(new GridLayout(0,2,0,0));
		container_ligne3.setOpaque(false);
		container_ligne3.add(tas3);
		
		JPanel tasvide3 = new JPanel();
		tasvide3.setOpaque(false);
		container_ligne3.add(tasvide3);
		
		
		JPanel container_ligne4 = new JPanel(new GridLayout(0,2,0,0));
		container_ligne4.setOpaque(false);
		container_ligne4.add(tas4);
		
		JPanel tasvide4 = new JPanel();
		tasvide4.setOpaque(false);
		container_ligne4.add(tasvide4);

		ecrangauche.add(container_ligne1);
		ecrangauche.add(container_ligne2);
		ecrangauche.add(container_ligne3);
		ecrangauche.add(container_ligne4);
		
		JPanel container_main = new JPanel(new GridLayout(0,1,0,0));
		container_main.setOpaque(false);
		container_main.setBorder(new EmptyBorder(0,150,5,0));
		JPanel tas5 = new JPanel();
		tas5.setLayout(new GridLayout(0,10));
		tas5.setOpaque(false);
		for(int i=0; i<10;i++){
			int hasard = (int) ((Math.random()*104)+1);
			Carte carte = new Carte(hasard);
			int width = carte.getImageIcon().getIconWidth();
			int heigth = carte.getImageIcon().getIconHeight();
			JButton bouton_main = new JButton();
			bouton_main.setIcon(new ImageIcon(carte.getImageIcon().getImage().getScaledInstance(width+8, heigth-15, 
																								java.awt.Image.SCALE_SMOOTH)));
			bouton_main.setName(hasard+"");
			bouton_main.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					((JButton) e.getSource()).setBorder(new LineBorder(Color.RED,2));
					context.revalidate();
					context.repaint();
					
					
					System.out.println(((JButton) e.getSource()).getName());
					try {
						Thread.sleep(500);
					} catch (InterruptedException e1) {
						log_client.add(e1.getMessage());
					}
					((JButton) e.getSource()).setEnabled(false);
					((JButton) e.getSource()).setBorder(new EmptyBorder(0, 0, 0, 0));
					context.revalidate();
					context.repaint();
				}
			});
			tas5.add(bouton_main);
		}
		container_main.add(tas5);
		
		JPanel espace = new JPanel();
		espace.setOpaque(false);
		ecrangauche.add(espace);
		ecrangauche.add(container_main);

		context.revalidate();
		context.repaint();
	}

	public void isNotify(boolean debloqueur) {
		this.debloqueur = debloqueur;
	}

	
	public void setIdUser(int id) {
		idUser = id;
	}
	
	public int getIdUser() {
		return idUser;
	}
	
	public void setNomUser(String nom) {
		nomUser = nom;
	}
	
	public String getNomUser() {
		return nomUser;
	}
	
	public void setUser(User user){
		this.user = user;
	}
	
	
}