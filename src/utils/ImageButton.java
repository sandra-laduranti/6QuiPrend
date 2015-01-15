package utils;

import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class ImageButton extends JButton{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ImageIcon image;
	
	public ImageButton(ImageIcon image){
		super(image);
		this.image = image;
	}
	
	@Override
    public void paintComponent(Graphics g) {
		if (g!=null && image!=null){
				super.paintComponent(g);
//	            g.drawImage(image.getImage(), 0,0, null);
    	}
        
    }
	
}
