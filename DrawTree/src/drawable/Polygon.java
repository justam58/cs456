package drawable;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

public class Polygon implements Drawable {
	
	// Polygon{ points: [ {x:0,y:0}, . . .], thickness:1, border:{r:100,g:0,b:0}, fill:{r:255,g:255,b:255} }
	private ArrayList<Point> points;
	private int thickness;
	private Color border; // If there is no border color then no border is drawn.
	private Color fill; // If there is no fill color then the rectangle is not filled. 

	@Override
	public void paint(Graphics g) {
		// TODO Auto-generated method stub
		
	}

}
