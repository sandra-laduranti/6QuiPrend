package utils;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Paint;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class EcranGauche extends JPanel {
	private Image image;
	private Graphics g;
    public static final Color BACKGROUND_HAUT_BAS      = new Color(36,163,199);
    public static final Color BACKGROUND_CENTER    = new Color(234,236,236);
	
	public EcranGauche(Image image){
		this.image=image;
	}
	
    public EcranGauche(Image image, LayoutManager layout) {
       this.setLayout(layout);
       this.image=image;
       this.setOpaque(false);
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
	            g.drawImage(image, (int) (getWidth()/4), (int) (getHeight()/4), g.getClipBounds().width/2, g.getClipBounds().height/2, null);
	    	}
	    	this.setOpaque(false);
	        
    	}
        
    }

	public void paintComponentWithoutImage(){
    	this.image=null;
    	this.paintComponent(g);
    }
    
    public void changeImage(Image image){
    	this.image=image;
    	this.paintComponent(g);
    }

   
}