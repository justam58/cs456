package drawable;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import spark.data.SA;
import spark.data.SO;
import spark.data.SOReflect;

public class Group extends SOReflect implements Drawable {
	
	// Group{ contents:[ ... ], sx:1.0, sy:1.0, rotate:0.0, tx:0.0, ty:0.0 } 
	public ArrayList<Drawable> contents = new ArrayList<Drawable>(); // SArray of Drawable objects
	public double sx = 1;
	public double sy = 1;
	public double rotate = 0; // in degrees counter clockwise.
	public double tx = 0;
	public double ty = 0;
	
	@Override
	public void setStyle(SO style){
		sx = style.getDouble("sx");
		sy = style.getDouble("sy");
		rotate = style.getDouble("rotate");
		tx = style.getDouble("tx");
		ty = style.getDouble("ty");
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
	public Point2D getCenter() {
		// TODO What?
		return null;
	}

}
