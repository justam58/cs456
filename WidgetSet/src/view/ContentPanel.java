package view;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import javax.swing.JPanel;

import widget.Root;

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
		repaint();
	}
	
	public void clean(){
		this.root = null;
		this.removeMouseListener(mouseListener);
		this.removeMouseMotionListener(mouseMotionListner);
		this.removeKeyListener(keyListener);
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
			Point2D p = new Point2D.Double(e.getX(),e.getY());
			root.mouseDown(p.getX(), p.getY(), new AffineTransform());
			requestFocusInWindow();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
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
			if(painted){
				Point2D p = new Point2D.Double(e.getX(),e.getY());
				root.mouseMove(p.getX(), p.getY(), new AffineTransform());
			}
		}
    	
    };
    
    private KeyListener keyListener = new KeyListener(){

		@Override
		public void keyTyped(KeyEvent e) {
			// do nothing
		}

		@Override
		public void keyPressed(KeyEvent e) {
			root.key(e.getKeyChar());
		}

		@Override
		public void keyReleased(KeyEvent e) {
			// do nothing
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
