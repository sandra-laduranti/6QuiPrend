package graphique;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
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
import metierDAO.UserDAO;
import utils.EcranGauche;
import utils.PanneauBordure;
import communication.User;


public class FenetrePrincipale extends JFrame implements ActionListener, WindowListener{

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
	
	/// Items pour les tests
	private JMenu menu_test;
	private JMenuItem item_choix_carte;
	private JMenuItem item_choix_ligne;
	private JMenuItem item_gagnant;
	private JMenuItem item_perdant;
	private JMenuItem item_tete_boeuf;
	private JMenuItem item_cartes_pioches;
	
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
	
	public Object sync;
	
	// La fenetre d'attente
	private JFrame frame_wait;
	private WaitLayerUI layerUI;
	
	private boolean boolClickMain = false; // Permet d'ignorer ou non le click d'une carte
	private boolean boolClickLigne = false;
	private boolean canJoinParty;

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
	    item_exit.addActionListener(this);
	    
	    menuBar.add(menu);
	    
	    ///////// Menu pour les tests
	    item_choix_carte = new JMenuItem("Choix carte"); 
		item_choix_ligne = new JMenuItem("Choix ligne");
		item_tete_boeuf = new JMenuItem("Tetes de boeufs");
		item_gagnant = new JMenuItem("Gagnant");
		item_perdant = new JMenuItem("Perdant");
		item_cartes_pioches = new JMenuItem("Cartes piochées");
		
	    menu_test = new JMenu("Tests");
	    menu_test.add(item_choix_carte);
	    menu_test.add(item_choix_ligne);
	    menu_test.add(item_tete_boeuf);
	    menu_test.add(item_cartes_pioches);
	    menu_test.add(item_gagnant);
	    menu_test.add(item_perdant);
	    menuBar.add(menu_test);
	    
	    item_choix_carte.addActionListener(this);
	    item_choix_ligne.addActionListener(this);
	    item_tete_boeuf.addActionListener(this);
	    item_cartes_pioches.addActionListener(this);
	    item_gagnant.addActionListener(this);
	    item_perdant.addActionListener(this);
	    ///////// Fin menu pour tests
	    
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

	
	/// Changement d'interfaces
	private void modifierInterfaceAfterConnexion(){
		
		ecrangauche.paintComponentWithoutImage();
		
		JPanel container_infos = this.initContainerInfo();
	    
	    // On vide le panneau de droite et on met la boite d'infos
	    panneau = context.getPanneauBordure();
	    panneau.removeAll();
	    panneau.setLayout(new BorderLayout());
	    panneau.add(container_infos, BorderLayout.NORTH);
	    
	    /// TODO: FAUSSES PARTIES EN ATTENDANT
	    final ArrayList<Partie> parties = new ArrayList<Partie>();
		Partie p = new Partie("Nom de partie", 5, false, getNomUser());
		p.addPlayer("Thomas");
		p.addPlayer("Hugues");
		p.addPlayer("Jean-Hubert");
		p.addPlayer("Dimitrikovska");
		parties.add(p);
	    
	    JPanel container_rafraichir = new JPanel();
	    container_rafraichir.setOpaque(false);
	    container_rafraichir.setBorder(new EmptyBorder(130,0,0,0));
		    JButton bouton_rafraichir = new JButton(texte_rafraichir);
		    bouton_rafraichir.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					try{
//						user.sendTefreshPart
					} catch (NullPointerException exc){
						/// test
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
						user.sendCreationPartie(fenetrecreation.getNamePartie(),fenetrecreation.getNbMaxJoueurs(),fenetrecreation.getProMode(), context.getNomUser());
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
			container_bouton.setBorder(new EmptyBorder(20,0,0,0));
    			JButton bouton_rejoindre = new JButton("Rejoindre");
    			
    			bouton_rejoindre.setName(parties.get(i).getId()+"::"+parties.get(i).getNom()+"::"+parties.get(i).getNbJoueursMax());
    			for(String user : parties.get(i).getListUser()){
    				bouton_rejoindre.setName(bouton_rejoindre.getName()+"::"+user);
    			}
    			
    			bouton_rejoindre.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						String[] description = ((JButton)e.getSource()).getName().split("::");
						
						try{
							user.sendJoinParty(nomUser, Integer.parseInt(description[0]));
						} catch (NumberFormatException nmbexc){
							log_client.add(nmbexc.getMessage());
						}
						
						synchronized (sync) {
							try {
								sync.wait();
							} catch (InterruptedException e1) {
								log_client.add(e1.getMessage());
							}
						}
							
						// Ici, on attend que le serveur donne une réponse (il fera un setCanJoinParty, puis notify)
						if(canJoinParty==true){
							canJoinParty=false; // Pour une réutilisation
							try{
								String nomPartie = description[1];
								int nbMax = Integer.parseInt(description[2]);
								String[] participants = Arrays.copyOfRange(description, 3, description.length);
								afficheSalonAttente(nomPartie, nbMax, participants);
	
							} catch (NumberFormatException nf){
								log_client.add(nf.getMessage());
							}
						} else {
							
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
	
/////////////////////////////////////////////////::
//////// LA PARTIE
	/**
	 * Commence la partie
	 */
	public void startPartie(){
		// AFFICHER MANCHE 1
		ecrangauche.removeAll();
		ecrangauche.setLayout(new GridLayout(6,1));
		
		panneau.removeAll();
		panneau.add(this.containerInfoDuringPartie(0));
		
//////////// POUR LES TESTS, ON CREE DE FAUSSES CARTES
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
	
	/**
	 * Raffraichis l'affichage des lignes de cartes
	 * @param lignes_cartes
	 */
	public void refreshLignes(List<ArrayList<Carte>> lignes_cartes) {
		
		for(int i=0; i<lignes_cartes.size(); i++){
			
			JPanel ligne = new JPanel();
			ligne.setBorder(new LineBorder(Color.BLACK, 2));
			ligne.setLayout(new GridLayout(0,5,20,0));
			ligne.setBackground(new Color(0,0,50,40));
			for(int j=0; j< lignes_cartes.get(i).size(); j++){
				JLabel panel_carte = new JLabel();
				if(j==0){
					panel_carte.setBorder(new EmptyBorder(0,10,0,20));
				}
				Carte c = lignes_cartes.get(i).get(j);
				panel_carte.setIcon(new ImageIcon(c.getImageIcon().getImage().getScaledInstance(c.getImageIcon().getIconWidth()+8, 
															c.getImageIcon().getIconHeight()-15, java.awt.Image.SCALE_SMOOTH)));
				
				ligne.add(panel_carte);
				ligne.setName(i+1+"");
			}
			JPanel tasvide = new JPanel();
			tasvide.setOpaque(false);

			ligne.addMouseListener(new MouseListener() {
				
				@Override
				public void mouseClicked(final MouseEvent e) {
					if(boolClickLigne){
						boolClickLigne=false; // Pour n'autoriser qu'un seul clik
						System.out.println( ((JPanel)e.getSource()).getName());
						
						((JPanel) e.getSource()).setBorder(new LineBorder(Color.RED,3));
						((JPanel) e.getSource()).setBackground(new Color(0,0,50,40));
						((JPanel) e.getSource()).repaint();
						context.revalidate();
			    	    context.repaint();
						ActionListener taskPerformer = new ActionListener() {
						      public void actionPerformed(ActionEvent evt) {
	  /////						  TODO:  Ligne à envoyer au serveur
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
		
			JPanel container_ligne = new JPanel(new GridLayout(0,2,25,15));
			container_ligne.setOpaque(false);
			container_ligne.add(ligne);
			container_ligne.add(tasvide);
			ecrangauche.add(container_ligne);
			
			context.revalidate();
	  	  	context.repaint();
		
		}
	}
	
	/**
	 * Methode permettant d'envoyer une carte à la partie
	 */
	public void soumettreCarte(){  // Elle autorise l'envoie d'une carte à la partie (voir lignes ~ 650)
		boolClickMain=true; 
		JOptionPane.showMessageDialog(this,
                "Vous devez choisir une carte",
                "Carte", 
                JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * Methode permettant d'envoyer une ligne à la partie
	 */
	public void soumettreLigne(){   // Elle autorise l'envoie d'une ligne à la partie (voir lignes ~ 560)
		boolClickLigne=true;
		JOptionPane.showMessageDialog(this,
                "Vous devez choisir une ligne",
                "Ligne", 
                JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Methode prenant en paramètre les cartes que la partie attribue au joueur
	 * et les affiches
	 * @param cartes
	 */
	public void distribMain(List<Carte> cartes) {

		JPanel container_main = new JPanel(new GridLayout(0,1,0,0));
		container_main.setOpaque(false);
		container_main.setBorder(new EmptyBorder(0,3,5,0));
		
		JPanel main = new JPanel();
		main.setLayout(new GridLayout(0,11));
		main.setOpaque(false);
		
		JPanel container_text = new JPanel(new BorderLayout());
		container_text.setOpaque(false);
		JLabel text = new JLabel("<html><h2><font color='black'><b>Votre main :</b></font></h2></html>");
		text.setBorder(new LineBorder(Color.BLACK,1));
		text.setOpaque(false);
		container_text.add(text, BorderLayout.WEST);
		main.add(container_text);

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
					if(boolClickMain){
						boolClickMain=false; // Pour n'autoriser qu'un seul click
///						ENVOYER LE CLICK
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
	

	public void afficheCartePiochees(List<Carte> cartes){
		Component c = this.getComponentByName(ecrangauche, "pioche");
		if(c!=null) ecrangauche.remove(c);
		
		ecrangauche.setLayout(new BorderLayout());
		
		JPanel container_pioche = new JPanel(new BorderLayout()); // Maximum 10 joueurs, donc 5*2 cartes à afficher
		container_pioche.setName("pioche");
		container_pioche.setOpaque(false);
		container_pioche.setPreferredSize(new Dimension(700, 700));
		container_pioche.setBorder(new EmptyBorder(10,300,200,100));
		JPanel container_cartes = new JPanel(new GridLayout(5, 2, 8 ,20));
		container_cartes.setOpaque(false);
		for(Carte carte : cartes){
			JLabel dispCarte = new JLabel();
			dispCarte.setIcon(new ImageIcon(carte.getImageIcon().getImage().getScaledInstance(carte.getImageIcon().getIconWidth()-10, 
					carte.getImageIcon().getIconHeight()-40, java.awt.Image.SCALE_SMOOTH)));
			dispCarte.setOpaque(false);
			container_cartes.add(dispCarte);
		}
		container_pioche.add(container_cartes, BorderLayout.BEFORE_FIRST_LINE);
		ecrangauche.add(container_pioche,BorderLayout.AFTER_LINE_ENDS);
	}
	
	/**
	 * affiche un popup avec le/les gagnant(s)
	 * @param liste_nom
	 */
	public void afficheGagnant(String ... liste_nom){
		if(liste_nom.length>1){
			String noms_concat = "";
			for(String nom : liste_nom){
				noms_concat+= "-  "+nom+"\n";
			}
			JOptionPane.showMessageDialog(this,
	                "Les gagnants sont : \n"+noms_concat,
	                "Bravo", 
	                JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(this,
	                "Le gagnant est "+liste_nom[0]+" !!",
	                "Bravo", 
	                JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	/**
	 * affiche un popup avec le perdant
	 * @param nom
	 */
	public void affichePerdant(String nom){
		if(nom.equals(this.getNomUser())){
			JOptionPane.showMessageDialog(this,
	                "Vous avez perdu !!\n Vous êtes vraiment null ...",
	                "Pfff, j'vous dit pas bravo ...", 
	                JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(this,
	                "Le perdant est "+nom+". \nVous avez le droit de le huer \net de vous moquer de lui !",
	                "Vous avez de la chance ...", 
	                JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	public void finishPartie(){
		this.modifierInterfaceAfterConnexion();
	}

	
	
	///// Méthodes utiles
	private Component getComponentByName(JPanel container, String name) {
		for(Component comp : container.getComponents()){
			if(comp!=null && comp.getName()!=null && comp.getName().equals(name)){
				return comp;
			}
		}
		return null;
	}
	
	public void setCanJoinParty(boolean can){
		this.canJoinParty=can;
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
	
	private void initWaitLayer() {  // Le popup d'attente
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
	
	private JPanel initContainerInfo() {
		JPanel container_infos = new JPanel();
		container_infos.setBorder(new EmptyBorder(20,15,0,20));
	    container_infos.setOpaque(false);
	    
	        JPanel infos = new JPanel(new BorderLayout(10,15));
		    infos.setOpaque(false);
		    
	        JLabel texte;
	        int nbgagne = UserDAO.getWin(this.getIdUser());
	        int nbperd = UserDAO.getWin(this.getIdUser());
	        if(nbgagne== -1) nbgagne = 0;
	        if(nbperd== -1) nbperd = 0;
	        texte = new JLabel("<html><font color='black'><u>Compte</u> : "+
	        		this.getNomUser()+"<br><br><br>Nombre de parties gagnées: "+nbgagne+"<br><br>"
	        				+ "Nombre de parties perdues : "+nbperd+"<br><br></font></html>");
	    
	        infos.add(texte,BorderLayout.NORTH); // texte en blanc
		    infos.add(bouton_deconnexion,BorderLayout.SOUTH);
	    container_infos.add(infos);
	    return container_infos;
	}
	
	private JPanel containerInfoDuringPartie(int nbTetesBoeuf) {
		JPanel container_infos = new JPanel();
		container_infos.setBorder(new EmptyBorder(20,15,0,20));
	    container_infos.setOpaque(false);
	    
	        JPanel infos = new JPanel(new BorderLayout(10,15));
		    infos.setOpaque(false);
		    
	        JLabel texte;
	        texte = new JLabel("<html><font color='black'><u>Compte</u> : "+this.getNomUser()+"<br><br>Nombre de têtes de boeuf <br>actuel : <h1><b><font color='red'>"+nbTetesBoeuf+"</b></font></h1></font></html>");
	    
	        infos.add(texte,BorderLayout.NORTH); // texte en blanc
	    container_infos.add(infos);
	    return container_infos;
	}
	
	
	/////////// LES ACTIONS DES BOUTONS (Listeners) //////////////
	@Override
	public void actionPerformed(ActionEvent e) {

		if(e.getActionCommand().equals(item_connexion.getText()) || e.getActionCommand().equals(bouton_connexion.getText())){
			//Pour les tests, commenter les 3 lignes suivantes et laisser décommenté la 4 eme ligne
			FenetreConnexion fenetreconnexion = new FenetreConnexion(context);
			fenetreconnexion.setVisible(true);
			if(fenetreconnexion.isSucceeded()){
//			if(true){
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
			
		////// ACTIONS DES BOUTONS D'ITEMS POUR TESTS	
		} else if (e.getActionCommand().equals(item_choix_carte.getText())){
			this.soumettreCarte();
		} else if (e.getActionCommand().equals(item_choix_ligne.getText())){
			this.soumettreLigne();
		} else if (e.getActionCommand().equals(item_gagnant.getText())){
			this.afficheGagnant("Julien","Patrick", "Nourdine", "Sandra", "Damien");
		} else if (e.getActionCommand().equals(item_perdant.getText())){
			this.affichePerdant("Joueur");
		} else if (e.getActionCommand().equals(item_tete_boeuf.getText())){	
			panneau.remove(0);
			panneau.add(this.containerInfoDuringPartie(15));
		
		} else if (e.getActionCommand().equals(item_cartes_pioches.getText())){
			
			List<Carte> cartes = new ArrayList<Carte>();
			int hasard;
			for(int i = 0; i<10; i++){
				hasard=(int) (Math.random()*104)+1;
				cartes.add(new Carte(hasard));
			}
			this.afficheCartePiochees(cartes);
			
		
		} else if (e.getActionCommand().equals(item_exit.getText())){
			int choix = JOptionPane.showConfirmDialog(null, "Êtes-vous sûr de vouloir quitter ?", "Quitter", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    		if(choix == JOptionPane.OK_OPTION){
    			// TODO user.quittePartie()
    			log_client.add("Fermeture de l'application");
    			System.exit(0);
    		}
			
		}
			
		this.revalidate();
		this.repaint();
	}

	@Override
	public void windowClosing(WindowEvent e) {
		log_client.add("Fermeture de la fenetre");
	}
	
	@Override
	public void windowOpened(WindowEvent e) {}

	@Override
	public void windowClosed(WindowEvent e) { }

	@Override
	public void windowIconified(WindowEvent e){ }

	@Override
	public void windowDeiconified(WindowEvent e) {}

	@Override
	public void windowActivated(WindowEvent e) {}

	@Override
	public void windowDeactivated(WindowEvent e) { }
	
	
}