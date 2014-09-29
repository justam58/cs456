package view;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import javax.swing.JPanel;

import drawable.Drawable;
import drawable.Selectable;


@SuppressWarnings("serial")
public class ContentPanel extends JPanel {
	
	// singleton
    private static ContentPanel instance = new ContentPanel();

    public static ContentPanel getInstance() {
        return instance;
    }
	
	public Drawable root;
	
	public ContentPanel (){
		super();
	}
	
	public void setRoot(Drawable root){
		this.root = root;
		this.addMouseListener(mouseListener);
		repaint();
	}
	
	public void clean(){
		root = null;
		this.removeMouseListener(mouseListener);
		repaint();
	}
	
    private MouseListener mouseListener = new MouseListener() {
    	
		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
//			System.out.println("Select (" + e.getX() + ", " + e.getY() + ")");
			Point2D p = new Point2D.Double(e.getX(),e.getY());
			((Selectable) root).select(p.getX(), p.getY(), 0, new AffineTransform());
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
    };
	
	@Override
	public void paint(Graphics g){
		g.setColor(Color.white);
		g.fillRect(0, 0, getWidth(), getHeight());
		if(root != null){
			root.paint(g);
		}
	}
}
