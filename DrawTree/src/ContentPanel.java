import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JPanel;

import drawable.Drawable;


@SuppressWarnings("serial")
public class ContentPanel extends JPanel {
	
	public ArrayList<Drawable> shapes = new ArrayList<Drawable>();
	
	public void add(Drawable shape){
		shapes.add(shape);
		this.repaint();
	}
	
	public void clean(){
		shapes = new ArrayList<Drawable>();
	}
	
	@Override
	public void paint(Graphics g){
		g.setColor(Color.white);
		g.fillRect(0, 0, getWidth(), getHeight());
		for(int i = 0; i < shapes.size(); i++){
			shapes.get(i).paint(g);
		}
	}
}
