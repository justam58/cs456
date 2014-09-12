package drawable;

import java.awt.Graphics;

import spark.data.SArray;

public class Group implements Drawable {
	
	// Group{ contents:[ ... ], sx:1.0, sy:1.0, rotate:0.0, tx:0.0, ty:0.0 } 
	private SArray contents; // SArray of Drawable objects
	private double sx = 1;
	private double sy = 1;
	private double rotate = 0; // in degrees counter clockwise.
	private double tx = 0;
	private double ty = 0;

	@Override
	public void paint(Graphics g) {
		// TODO Auto-generated method stub
		
	}

}
