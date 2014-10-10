package view;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import javax.swing.JPanel;

import model.Root;
import able.*;

@SuppressWarnings("serial")
public class ContentPanel extends JPanel {
	
	// singleton
    private static ContentPanel instance = new ContentPanel();

    public static ContentPanel getInstance() {
        return instance;
    }
	
	public Root root = new Root();
	
	public ContentPanel (){
		super();
	}
	
	public void setRoot(Drawable root){
		this.root.setRoot(root);;
		this.addMouseListener(mouseListener);
		repaint();
	}
	
	public void clean(){
		this.root.clean();;
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
			root.mouseDown(p.getX(), p.getY(), new AffineTransform());
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
		root.paint(g);
	}
}
