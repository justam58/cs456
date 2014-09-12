package drawable;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;

public class Text implements Drawable {
	
	// Text{ text:"Draw This", x:10,y:100, font:"Times", size:10 }
	private String text;
	private Point baseline;
	private Font font; // this should also support the names "serif" and "sans-serif" to select the corresponding standard fonts.
	private int size;

	@Override
	public void paint(Graphics g) {
		// TODO Auto-generated method stub
		
	}

}
