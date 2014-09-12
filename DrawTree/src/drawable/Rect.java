package drawable;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class Rect implements Drawable {
	
	// Rect{ left:0, top:100, width:10, height:10, thickness:2, border:{r:0,g:0,b:0}, fill:{r:0,g:0,b:128} } 
	private Point topLeft;
	private int width;
	private int height;
	private int thickness;
	private Color border; // If there is no border color then no border is drawn.
	private Color fill; // If there is no fill color then the rectangle is not filled. 

	@Override
	public void paint(Graphics g) {
		// TODO Auto-generated method stub
		
	}

}
