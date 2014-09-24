package drawable;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

public class Select extends Group implements Drawable, Selectable {
	
	public ArrayList<Integer> selected = new ArrayList<Integer>();
	
	@Override
	public void paint(Graphics g) {
		
		// TODO
		// first draw all of its contents using the paint() method from group 
		// then check to see if it has a "selected" path. 
		// If it does then it should find that object using the path
		// retrieve its control points using the controls() method 
		// draw the control points. 
		// Note that this must correctly handle the transformations of any nested groups.
		
		Graphics2D g2d = (Graphics2D)g;
		for(int i = 0; i < contents.size(); i++){
			AffineTransform atf = g2d.getTransform();
			g2d.translate(tx, ty);
			g2d.rotate(-Math.toRadians(rotate));
			g2d.scale(sx, sy);
            contents.get(i).paint(g);
			g2d.setTransform(atf);
		}
	}

	@Override
	public ArrayList<Integer> select(double x, double y, int myIndex, AffineTransform transform) {
		// TODO 
		// behave as a Group (see below) but should also store the path returned by its child selection in its "selected" attribute
		// If its selected attribute changes
		// it should also call repaint() to make certain the control points are correctly updated on the screen using the paint method.
		return null;
	}

}
