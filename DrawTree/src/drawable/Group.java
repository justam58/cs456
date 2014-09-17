package drawable;

import java.awt.Graphics;
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
	
	public Transformation t;
	
	@Override
	public void setStyle(SO style, Transformation t){
		sx *= t.sx;
		sy *= t.sy;
		rotate += t.rotate;
		tx += t.tx;
		ty += t.ty;
		Transformation newT = new Transformation(sx, sy, tx, ty, rotate);
		SA contentsArray = style.getArray("contents");
		for(int i = 0; i < contentsArray.size(); i++){
			SO shapeObj = contentsArray.getSO(i);
			Drawable shape = (Drawable)shapeObj;
			shape.setStyle(shapeObj, newT);
			contents.add(shape);
		}
	}

	@Override
	public void paint(Graphics g) {
//		Graphics2D g2d = (Graphics2D)g;
		for(int i = 0; i < contents.size(); i++){
			Drawable d = contents.get(i);
//			Point2D center = new Point2D.Double(0,0);
//			if(d instanceof Shape){
//				center = ((Shape)d).getCenter();	
//			}
//			System.out.println(center.toString());
//			AffineTransform atf = g2d.getTransform();
//			g2d.translate(tx, ty);
//			g2d.translate(center.getX(), center.getY());
//			g2d.rotate(-Math.toRadians(rotate));
//			g2d.scale(sx, sy);
//			g2d.translate(-center.getX(), -center.getY());	
			
			d.paint(g);
//			g2d.setTransform(atf);
		}
	}

}
