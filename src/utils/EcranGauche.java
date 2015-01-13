package utils;

import graphique.FenetreAccueil;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Paint;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import metier.Partie;
import metier.User;

@SuppressWarnings("serial")
public class EcranGauche extends JPanel {
	
	private FenetreAccueil context;
	private Image image;
	private Graphics g;
    public static final Color BACKGROUND_HAUT_BAS      = new Color(36,163,199);
    public static final Color BACKGROUND_CENTER    = new Color(234,236,236);
	
    public EcranGauche(FenetreAccueil context, LayoutManager layout) {
    	this.context = context;
        this.setLayout(layout);
    }
    
	@Override
    public void paintComponent(Graphics g) {
		if (g!=null){
	    	Graphics2D graphics = (Graphics2D) g.create();        
	        int midY = 100;
	        Paint topPaint = new GradientPaint(0, 0, BACKGROUND_HAUT_BAS,0, midY, BACKGROUND_CENTER);
	        graphics.setPaint(topPaint);
	        graphics.fillRect(0, 0, getWidth(), midY);        
	        Paint bottomPaint = new GradientPaint(0, midY + 1, BACKGROUND_CENTER,0, getHeight(), BACKGROUND_HAUT_BAS);
	        graphics.setPaint(bottomPaint);
	        graphics.fillRect(0, midY, getWidth(), getHeight());
	        
	        if (image!=null){
	            g.drawImage(image, (int) (getWidth()/3), (int) (getHeight()/4), g.getClipBounds().width/3, g.getClipBounds().height/2, null);
	    	}
	    	this.setOpaque(false);
	        
    	}
        
    }

	public void paintComponentWithoutImage(){
    	this.image=null;
    	this.paintComponent(g);
    }
    
    public void setImage(Image image){
    	this.image=image;
    }
    
    public void afficheParties(List<Partie> parties){
    	this.setLayout(new BorderLayout());
    	JPanel container_all_parties = new JPanel();
    	container_all_parties.setBorder(new EmptyBorder(0,10,50,50));
    	container_all_parties.setOpaque(false);
    	
    	List<User> users;
    	for(int x=0;x<9; x++){
	    	for(int i=0; i<parties.size(); i++){
	    		users = parties.get(i).getListUser();
	    		
	    		
	    			JPanel container_one_partie = new JPanel(new BorderLayout());
	    			JPanel infos = new JPanel(new GridLayout(users.size(),1));
	    				
	    			for (int j=0; i<users.size(); i++){
						JLabel joueur = new JLabel(users.get(j).getUserNickname());
						infos.add(joueur);
	    			}
	    			JButton bouton_rejoindre = new JButton("Rejoindre");
	    			bouton_rejoindre.setName(context.getUser().getUserNickname());
	    			container_one_partie.add(infos,BorderLayout.CENTER);
	    			container_one_partie.add(bouton_rejoindre,BorderLayout.SOUTH);
	    		
	    			container_all_parties.add(container_one_partie);	
	    	}
    	}
    	this.add(container_all_parties, BorderLayout.CENTER);
    	
    }

   
}