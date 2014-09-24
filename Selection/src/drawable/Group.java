package drawable;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import spark.data.SA;
import spark.data.SO;
import spark.data.SOReflect;

public class Group extends SOReflect implements Drawable, Selectable  {
	
	// Group{ contents:[ ... ], sx:1.0, sy:1.0, rotate:0.0, tx:0.0, ty:0.0 } 
	public ArrayList<Drawable> contents = new ArrayList<Drawable>(); // SArray of Drawable objects
	public double sx = 1;
	public double sy = 1;
	public double rotate; // in degrees counter clockwise.
	public double tx;
	public double ty;
	
	@Override
	public void setStyle(SO style){
		SA contentsArray = style.getArray("contents");
		for(int i = 0; i < contentsArray.size(); i++){
			SO shapeObj = contentsArray.getSO(i);
			Drawable shape = (Drawable)shapeObj;
			shape.setStyle(shapeObj);
			contents.add(shape);
		}
	}

	@Override
	public void paint(Graphics g) {
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
		// if the object or its contents are selected then it returns a path to the selected object. 
		// The x, y coordinates are in the coordinates of your panel, not in the transformed coordinates. 
		// If the object or its contents are not selected, then NULL is returned.
		for(int i = 0; i < contents.size(); i++){
			// recursively call select on its contents
			Drawable content = contents.get(i);
			if(content instanceof Selectable){
				// Be sure to correctly account for the transformation by transforming the selection point by the inverse of the transformation on this group and by pass
				// That will bring the selection point into the coordinate system of the group contents
				AffineTransform atf = new AffineTransform();
				atf.scale(1/sx, 1/sy);
				atf.rotate(Math.toRadians(rotate));
				atf.translate(-tx, -ty);
				
				Selectable shape = (Selectable)content;
				ArrayList<Integer> selectPath = shape.select(x,y,i,atf);
				// If any of the contents are selected then this should take the selection path from the selected child and add this group's index onto the front and return the more complete path.
				if(selectPath != null){
					selectPath.add(0, myIndex);
					return selectPath;
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

}
