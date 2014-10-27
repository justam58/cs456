package sparkClass;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import able.Drawable;
import able.Selectable;
import view.ContentPanel;

public class Select extends Group implements Drawable, Selectable {
	
	public ArrayList<ArrayList<Integer>> selected = new ArrayList<ArrayList<Integer>>();

	@Override
	public void paint(Graphics g) {
		// first draw all of its contents using the paint() method from group 
		Graphics2D g2d = (Graphics2D)g;
		for(int i = 0; i < contents.size(); i++){
			Drawable shape = contents.get(i);
			AffineTransform atf = g2d.getTransform();
			g2d.translate(tx, ty);
			g2d.rotate(-Math.toRadians(rotate));
			g2d.scale(sx, sy);
			shape.paint(g);
    		// then check to see if it has a "selected" path. 
			ArrayList<Integer> selectPath = selected.get(i);
			// If it does then it should find that object using the path 
			if(selectPath != null){
				// retrieve its control points using the controls() method 
				Selectable currentShape = (Selectable)shape;
				int currentIndex = 0;
				while(currentShape instanceof Group)
				{
					Group group = (Group)currentShape;
					currentShape = (Selectable) group.contents.get(currentIndex);
					currentIndex++;
				}
				ArrayList<Point2D> controlPoints = currentShape.controls();
				// draw the control points.
				drawControls(g2d, controlPoints);
			}
			g2d.setTransform(atf);
			// Note that this must correctly handle the transformations of any nested groups.
		}
	}

	@Override
	public ArrayList<Integer> select(double x, double y, int myIndex, AffineTransform transform) {
		// behave as a Group (see below) 
		// if the object or its contents are selected then it returns a path to the selected object. 
		// The x, y coordinates are in the coordinates of your panel, not in the transformed coordinates. 
		// If the object or its contents are not selected, then NULL is returned.
		for(int i = 0; i < contents.size(); i++){
			// recursively call select on its contents
			Drawable content = contents.get(i);
			if(content instanceof Selectable){
				// Be sure to correctly account for the transformation by transforming the selection point by the inverse of the transformation on this group and by pass
				// That will bring the selection point into the coordinate system of the group contents
				AffineTransform atf = new AffineTransform(transform);
				atf.scale(1/sx, 1/sy);
				atf.rotate(Math.toRadians(rotate));
				atf.translate(-tx, -ty);
				Selectable shape = (Selectable)content;
				ArrayList<Integer> selectPath = shape.select(x,y,i,atf);
				// If any of the contents are selected 
				// then this should take the selection path from the selected child and add this group's index onto the front and return the more complete path.
				// but should also store the path returned by its child selection in its "selected" attribute
				// If its selected attribute changes
				// it should also call repaint() to make certain the control points are correctly updated on the screen using the paint method.
				
				if(!equalsTo(selected.get(i),(selectPath))){
					selected.set(i, selectPath);
					ContentPanel.getInstance().repaint();
				}
				if(selectPath != null){
					selectPath.add(0, myIndex);
//					return selectPath;
				}
			}
		}
		return null;
	}

	@Override
	public ArrayList<Point2D> controls() {
		// This object has no controls of its own.
		return null;
	}
	
	private boolean equalsTo(ArrayList<Integer> p1, ArrayList<Integer> p2){
		if(p1 == null && p2 == null){
			return true;
		}
		if(p1 == null || p2 == null){
			return false;
		}
		if(p1.size() != p2.size()){
			return false;
		}
		for(int i = 0; i < p1.size(); i++){
			if(p1.get(i) != p2.get(i)){
				return false;
			}
		}
		return true;
	}
	
	private void drawControls(Graphics2D g, ArrayList<Point2D> controlPoints){
		AffineTransform atf = g.getTransform();
		int pointSizeX = (int)(4/atf.getScaleX());
		int pointSizeY = (int)(4/atf.getScaleY());
		for(int j = 0; j < controlPoints.size(); j++){
			Point2D p = controlPoints.get(j);
			g.setColor(Color.white);
			g.fillRect((int)p.getX()-pointSizeX, (int)p.getY()-pointSizeY, pointSizeX*2, pointSizeY*2);
			g.setStroke(new BasicStroke(1));
			g.setColor(Color.black);
			g.drawRect((int)p.getX()-pointSizeX, (int)p.getY()-pointSizeY, pointSizeX*2, pointSizeY*2);
		}
	}
}
