package drawable;

import java.awt.Graphics;
import java.util.ArrayList;

import spark.data.SA;
import spark.data.SO;
import spark.data.SOReflect;

public class Group extends SOReflect implements Drawable {
	
	// Group{ contents:[ ... ], sx:1.0, sy:1.0, rotate:0.0, tx:0.0, ty:0.0 } 
	public ArrayList<Drawable> contents = new ArrayList<Drawable>(); // SArray of Drawable objects
	public double sx;
	public double sy;
	public double rotate; // in degrees counter clockwise.
	public double tx;
	public double ty;
	
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
		// TODO figure out transformations
		for(int i = 0; i < contents.size(); i++){
			contents.get(i).paint(g);;
		}
	}

}
