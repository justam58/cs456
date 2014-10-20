package view;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import javax.swing.JPanel;

import model.Root;

@SuppressWarnings("serial")
public class ContentPanel extends JPanel {
	
	// singleton
    private static ContentPanel instance = new ContentPanel();

    public static ContentPanel getInstance() {
        return instance;
    }
	
	public Root root = null;
	
	private boolean painted = false;
	
	public ContentPanel (){
		super();
	}
	
	public void setRoot(Root root){
		this.root = root;
		this.addMouseListener(mouseListener);
		this.addMouseMotionListener(mouseMotionListner);
		this.addKeyListener(keyListener);
		this.addFocusListener(focusListener);
		repaint();
	}
	
	public void clean(){
		this.root = null;
		this.removeMouseListener(mouseListener);
		this.removeMouseMotionListener(mouseMotionListner);
		this.removeKeyListener(keyListener);
		this.removeFocusListener(focusListener);
		painted = false;
		repaint();
	}
	
    private MouseListener mouseListener = new MouseListener() {
    	
		@Override
		public void mouseClicked(MouseEvent e) {
			// do nothing
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// do nothing
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// do nothing
		}

		@Override
		public void mousePressed(MouseEvent e) {
//			System.out.println("mousePressed (" + e.getX() + ", " + e.getY() + ")");
			Point2D p = new Point2D.Double(e.getX(),e.getY());
			root.mouseDown(p.getX(), p.getY(), new AffineTransform());
		}

		@Override
		public void mouseReleased(MouseEvent e) {
//			System.out.println("mouseReleased (" + e.getX() + ", " + e.getY() + ")");
			Point2D p = new Point2D.Double(e.getX(),e.getY());
			root.mouseUp(p.getX(), p.getY(), new AffineTransform());
		}
    };
    
    private MouseMotionListener mouseMotionListner = new MouseMotionListener(){

		@Override
		public void mouseDragged(MouseEvent e) {
			Point2D p = new Point2D.Double(e.getX(),e.getY());
			root.mouseMove(p.getX(), p.getY(), new AffineTransform());
		}

		@Override
		public void mouseMoved(MouseEvent e) {
//			System.out.println("mouseMoved (" + e.getX() + ", " + e.getY() + ")");
			if(painted){
				Point2D p = new Point2D.Double(e.getX(),e.getY());
				root.mouseMove(p.getX(), p.getY(), new AffineTransform());
			}
		}
    	
    };
    
    private KeyListener keyListener = new KeyListener(){

		@Override
		public void keyTyped(KeyEvent e) {
			System.out.println("keyTyped");
		}

		@Override
		public void keyPressed(KeyEvent e) {
			System.out.println("keyPressed");
		}

		@Override
		public void keyReleased(KeyEvent e) {
			System.out.println("keyReleased");
		}
    	
    };
    
    private FocusListener focusListener = new FocusListener(){

		@Override
		public void focusGained(FocusEvent e) {
			System.out.println("focusGained");
		}

		@Override
		public void focusLost(FocusEvent e) {
			System.out.println("focusLost");
		}

    };
	
	@Override
	public void paint(Graphics g){
		g.setColor(Color.white);
		g.fillRect(0, 0, getWidth(), getHeight());
		if(root != null){
			root.paint(g);
			painted = true;
		}
	}
}
