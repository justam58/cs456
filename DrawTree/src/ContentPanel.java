import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import drawable.Drawable;


@SuppressWarnings("serial")
public class ContentPanel extends JPanel {
	
	public Drawable root;
	
	public void setRoot(Drawable root){
		this.root = root;
		repaint();
	}
	
	public void clean(){
		root = null;
		repaint();
	}
	
	@Override
	public void paint(Graphics g){
		g.setColor(Color.white);
		g.fillRect(0, 0, getWidth(), getHeight());
		if(root != null){
			root.paint(g);
		}
	}
}
