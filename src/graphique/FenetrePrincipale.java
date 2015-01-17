package graphique;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URI;
import java.net.URISyntaxException;
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
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import log.MonLog;
import log.MonLogClient;
import metier.Carte;
import metier.Partie;
import utils.EcranGauche;
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
						context.startPartie();
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
//			FenetreConnexion fenetreconnexion = new FenetreConnexion(context);
//			fenetreconnexion.setVisible(true);
//			if(fenetreconnexion.isSucceeded()){
			if(true){
				synchronized (sync) {
					sync.notify();
				}
		    	is_connected=true; // Flag
		    	
				// Réorganisation des menus
				this.menu.remove(item_connexion); // Si la connexion à réussie, on l'enlève du menu
				this.menu.add(item_deconnexion,0); // et on ajoute le bouton deconnexion
				
				log_client.add("Connecté !");
				//// A DECOMMENTER
				this.setNomUser("Julien");
				this.setIdUser(5);
				
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
				synchronized (sync) {
					sync.notify();
				}
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
		
		JPanel container_infos = this.initContainerInfo();
	    
	    // On vide le panneau de droite et on met la boite d'infos
	    panneau = context.getPanneauBordure();
	    panneau.removeAll();
	    panneau.setLayout(new BorderLayout());
	    panneau.add(container_infos, BorderLayout.NORTH);
	    
	    
	    final ArrayList<Partie> parties = new ArrayList<Partie>();
		Partie p = new Partie("Nom de partie", 5, false, context.user);
		try {
		User u2 = new User (new URI( "ws://localhost:12345" ));
		u2.setUser(34, "Thomas");
		p.addPlayer(u2);
		User u3 = new User (new URI( "ws://localhost:12345" ));
		u3.setUser(37, "HUgues");
		p.addPlayer(u3);
		User u4 = new User (new URI( "ws://localhost:12345" ));
		u4.setUser(22, "JP");
		p.addPlayer(u4);
		User u5 = new User (new URI( "ws://localhost:12345" ));
		u5.setUser(29, "Herve");
		p.addPlayer(u5);
		parties.add(p);
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    
	    
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
						context.afficherToutesLesParties(parties);
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
		
		context.afficherToutesLesParties(parties);
		
	}
	
	public void afficherToutesLesParties(List<Partie> parties){
		
		ecrangauche.removeAll();
    	ecrangauche.setLayout(new BorderLayout());
    	JPanel container_all_parties = new JPanel();
    	container_all_parties.setLayout(new FlowLayout(FlowLayout.CENTER,10,10));
    	container_all_parties.setBorder(new EmptyBorder(20,20,50,20));
    	container_all_parties.setOpaque(false);
    	
    	List<String> users;
    	for(int x=0; x<12;x++)
    	for(int i=0; i<parties.size(); i++){ // On créer un carré par parties
    		users = parties.get(i).getListUser();
    		
			JPanel container_one_partie = new JPanel(new BorderLayout(0,0));
			TitledBorder bordure = BorderFactory.createTitledBorder("<html>"+parties.get(i).getNom()+"<br>(Max : "+parties.get(i).getNbJoueursMax()+" pers.)</html>");
			container_one_partie.setBorder(bordure);
			JPanel infos = new JPanel(new GridLayout(users.size(),1));
				
			for (int j=0; j<users.size(); j++){
				JLabel joueur = new JLabel(" - "+users.get(j));
				infos.add(joueur);
			}
			
			
			JPanel container_bouton = new JPanel(); // Panel permettant de garder une taille raisonnable au bouton
    			JButton bouton_rejoindre = new JButton("Rejoindre");
    			
    			bouton_rejoindre.setName(parties.get(i).getNom()+"::"+parties.get(i).getNbJoueursMax());
    			for(String user : parties.get(i).getListUser()){
    				bouton_rejoindre.setName(bouton_rejoindre.getName()+"::"+user);
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
								//System.out.println("Débloqué par un notify");
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
		panneau.removeAll();
		panneau.add(this.initContainerInfo());
		bouton_deconnexion.setVisible(false);
		
	
		ecrangauche.setLayout(new BorderLayout());
		JPanel description_partie = new JPanel(new GridLayout(10,1));
		description_partie.setOpaque(false);
		description_partie.setBorder(new EmptyBorder(20,50,0,0));
		
		JPanel nom_partie = new JPanel();
		nom_partie.setOpaque(false);
		JLabel nom_texte;
		if(nomsParticipants.length == nbMax){
			nom_texte = new JLabel("<html><font color='black'><b><h1>Que la partie ... commence !</h1></b></font></html>");
			log_client.add("La partie "+titre_partie+" commence !");
			System.out.println("La partie "+titre_partie+" commence !");
		} else {
			nom_texte = new JLabel("<html><font color='black'><b><h2>Partie : "+titre_partie+
					" (en attente de joueurs - Max :"+nbMax+")</h2></b></font></html>");
		}
		nom_partie.add(nom_texte);
		description_partie.add(nom_partie);
		
		for(String nom : nomsParticipants){
			JLabel joueur = new JLabel("==> "+nom);
			joueur.setOpaque(false);
			description_partie.add(joueur);
		}
		
		ecrangauche.add(description_partie,BorderLayout.CENTER);
		
		// Pour donner un effet d'initialisation de la partie, pour que l'affichage ne soit pas instantanné et que
		// le joueur puisse lire le nouvel adversaire
		if(nomsParticipants.length == nbMax){
			final Timer timer = new Timer(3000, new ActionListener() {
			    public void actionPerformed(ActionEvent evt) {
			    	startPartie();
			    	System.out.println("start");
			        ((Timer)evt.getSource()).stop();
			    }
			});
			timer.start();
		}
	
	}
	
	// Une partie commence !
	public void startPartie(){
		// AFFICHER MANCHE 1
		ecrangauche.removeAll();
		ecrangauche.setLayout(new GridLayout(6,1));
		
		List<ArrayList<Carte>> lignes_cartes = new ArrayList<ArrayList<Carte>>();
		for(int i = 0; i<4; i++){
			lignes_cartes.add(new ArrayList<Carte>());
			for(int j = 0; j<5; j++){
				lignes_cartes.get(i).add(new Carte((int) (Math.random()*104)+1));
			}
		}
		this.refreshLignes(lignes_cartes);
		
		List<Carte> cartes = new ArrayList<Carte>();
		int hasard;
		for(int i = 0; i<10; i++){
			hasard=(int) (Math.random()*104)+1;
			cartes.add(new Carte(hasard));
		}
		this.distribMain(cartes);

		context.revalidate();
		context.repaint();
	}
	
	
	private void refreshLignes(List<ArrayList<Carte>> lignes_cartes) {
		
		for(int i=0; i<lignes_cartes.size(); i++){
			
			JPanel ligne = new JPanel();
			ligne.setBorder(new LineBorder(Color.BLACK, 2));
			ligne.setLayout(new GridLayout(0,5,20,0));
			ligne.setBackground(new Color(0,0,50,40));
			for(int j=0; j< lignes_cartes.get(i).size(); j++){
				JLabel bouton = new JLabel();
				if(j==0){
					bouton.setBorder(new EmptyBorder(0,10,0,0));
				}
				Carte c = lignes_cartes.get(i).get(j);
				bouton.setIcon(new ImageIcon(c.getImageIcon().getImage().getScaledInstance(c.getImageIcon().getIconWidth()+8, 
															c.getImageIcon().getIconHeight()-15, java.awt.Image.SCALE_SMOOTH)));
				
				ligne.add(bouton);
				ligne.setName(i+1+"");
			}
			JPanel tasvide = new JPanel();
			tasvide.setOpaque(false);
			JPanel container_ligne = new JPanel(new GridLayout(0,2,20,15));
			container_ligne.setBorder(new EmptyBorder(0,1,0,0));
			container_ligne.setOpaque(false);
			container_ligne.add(ligne);
			container_ligne.add(tasvide);
			
			ecrangauche.add(container_ligne);
			
			ligne.addMouseListener(new MouseListener() {
				
				@Override
				public void mouseClicked(final MouseEvent e) {
//					if(ligne.)
//					((JPanel) e.getSource()).setEnabled(false);
					System.out.println( ((JPanel)e.getSource()).getName());
					((JPanel) e.getSource()).setBorder(new LineBorder(Color.RED,3));
					ActionListener taskPerformer = new ActionListener() {
					      public void actionPerformed(ActionEvent evt) {
  /////						  Ligne à envoyer
					    	  context.revalidate();
					    	  context.repaint();
					    	  ((JPanel) e.getSource()).setBackground(new Color(0,0,50,40));
					    	  ((JPanel) e.getSource()).setBorder(new LineBorder(Color.BLACK, 2));
					    	  ((Timer)evt.getSource()).stop();
					    	  context.revalidate();
					    	  context.repaint();
					      }
				    };
					new Timer(1000, taskPerformer).start();
				}
				@Override
				public void mouseReleased(MouseEvent e) { }
				@Override
				public void mousePressed(MouseEvent e) { }
				@Override
				public void mouseExited(MouseEvent e) { }
				@Override
				public void mouseEntered(MouseEvent e) { }
				
			});
		}
		
		context.revalidate();
  	  	context.repaint();
		
	}
	
	public int soumettreCarte(){
		return 0;  
	}
	
//	public int soumettreLigne

	private void distribMain(List<Carte> cartes) {

		JPanel container_main = new JPanel(new GridLayout(0,1,0,0));
		container_main.setOpaque(false);
		container_main.setBorder(new EmptyBorder(0,150,5,0));
		
		JPanel main = new JPanel();
		main.setLayout(new GridLayout(0,10));
		main.setOpaque(false);
		for(Carte oneCard : cartes){
			
			int width = oneCard.getImageIcon().getIconWidth();
			int heigth = oneCard.getImageIcon().getIconHeight();
			JButton bouton_main = new JButton();
			bouton_main.setIcon(new ImageIcon(oneCard.getImageIcon().getImage().getScaledInstance(width+8, heigth-15, 
																								java.awt.Image.SCALE_SMOOTH)));
			bouton_main.setName(oneCard.getBeefHead()+"");
			bouton_main.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(final ActionEvent e) {
					((JButton) e.getSource()).setBorder(new LineBorder(Color.RED,3));
					
					System.out.println(((JButton) e.getSource()).getName());
					ActionListener taskPerformer = new ActionListener() {
					      public void actionPerformed(ActionEvent evt) {
					    	  // user.sendCarte( Integer.parseInt(((JButton) e.getSource()).getName()) );
					    	  ((JButton) e.getSource()).setEnabled(false);
					    	  ((JButton) e.getSource()).setBorder(new EmptyBorder(0, 0, 0, 0));
					      }
				    };
					new Timer(1000, taskPerformer).start();
				}
			});
			main.add(bouton_main);
		}
		container_main.add(main);
		
		JPanel espace = new JPanel();
		espace.setOpaque(false);
		ecrangauche.add(espace);
		ecrangauche.add(container_main);
	}

	///// Méthodes utiles
	
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
		this.user.setUser(idUser,nomUser);
	}
	
	private JPanel initContainerInfo() {
		JPanel container_infos = new JPanel();
		container_infos.setBorder(new EmptyBorder(20,15,0,20));
	    container_infos.setOpaque(false);
	    
	        JPanel infos = new JPanel(new BorderLayout(10,15));
		    infos.setOpaque(false);
		    
	        JLabel texte;
	        texte = new JLabel("<html><font color='black'><u>Compte</u> : "+
	        		this.getNomUser()+"<br><br><br>Nombre de parties gagnées: "+0/*user.getNb_parties_gagnees()*/+"<br><br>Nombre de parties perdues : "+0/*user.getNb_parties_perdues()*/+"<br><br></font></html>");
	    
	        infos.add(texte,BorderLayout.NORTH); // texte en blanc
	        infos.add(bouton_deconnexion).setVisible(true);
		    infos.add(bouton_deconnexion,BorderLayout.SOUTH);
	    container_infos.add(infos);
	    return container_infos;
	}
	
	
}