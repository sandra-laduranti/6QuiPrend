package utils;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public class PanneauBordure extends JPanel{

    private JPanel panel;
	
    // Le contour
    private int strokeSize = 1;
    private boolean _highQuality = true;
    private Dimension _arcs = new Dimension(50, 40);
    private Color _shadowColor = Color.BLACK;
    private boolean shadowed = true;
    private int _shadowGap = 5;
    private int _shadowOffset = 1;
    private int _shadowAlpha = 120;
    private Color _backgroundColor = EcranGauche.BACKGROUND_CENTER;
    private BufferedImage image = null;


	public PanneauBordure(Component ... liste_compo) {
		this.setLayout(new BorderLayout(20,20));
        this.setPreferredSize(new Dimension(190,this.getPreferredSize().height));
        
		JPanel flow_panel = new JPanel();
		flow_panel.setBorder(new EmptyBorder(25,10,30,10));
        panel = new JPanel(new GridLayout(liste_compo.length, 1, 5, 5));   
        panel.setOpaque(false);
        
        for(int i = 0; i<liste_compo.length ; i++ ){
        	panel.add(liste_compo[i]);
        }
        
        flow_panel.add(panel);
        flow_panel.setOpaque(false);
        this.add(flow_panel, BorderLayout.CENTER);
        
	    this.setBackground(EcranGauche.BACKGROUND_HAUT_BAS);
	}
	
	public void removeComposant(Component ... liste_compo){
		for(Component c : liste_compo)
			this.remove(c);
	}
	
	public void addComposant(Component c){
		this.add(c);
	}
	
	public void changeVisibilite(Component c, boolean bool){
		c.setVisible(bool);
	}
	
	public void addComposantEnBas(Component c){
		this.add(c, BorderLayout.SOUTH);
	}

	@Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        int width = getWidth();
        int height = getHeight();
        Color shadowColorA = new Color(_shadowColor.getRed(), _shadowColor.getGreen(), _shadowColor.getBlue(), _shadowAlpha);
        Graphics2D graphics = (Graphics2D) g;

        if(_highQuality)
        {
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }

        if(shadowed)
        {
            graphics.setColor(shadowColorA);
            graphics.fillRoundRect(_shadowOffset, _shadowOffset, width - strokeSize - _shadowOffset,
                    height - strokeSize - _shadowOffset, _arcs.width, _arcs.height);
        }
        else
        {
            _shadowGap = 1;
        }

        RoundRectangle2D.Float rr = new RoundRectangle2D.Float(0, 0, (width - _shadowGap), (height - _shadowGap), _arcs.width, _arcs.height);

        Shape clipShape = graphics.getClip();

        if(image == null)
        {
            graphics.setColor(_backgroundColor);
            graphics.fill(rr);
        }
        else
        {
            RoundRectangle2D.Float rr2 =  new RoundRectangle2D.Float(0, 0, (width - strokeSize - _shadowGap), (height - strokeSize - _shadowGap), _arcs.width, _arcs.height);

            graphics.setClip(rr2);
            graphics.drawImage(this.image, 0, 0, null);
            graphics.setClip(clipShape);
        }

        graphics.setColor(getForeground());
        graphics.setStroke(new BasicStroke(strokeSize));
        graphics.draw(rr);
        graphics.setStroke(new BasicStroke());
    }
}

