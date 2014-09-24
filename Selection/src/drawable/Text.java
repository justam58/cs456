package drawable;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import spark.data.SO;
import spark.data.SOReflect;

public class Text extends SOReflect implements Drawable, Selectable  {
	
	// Text{ text:"Draw This", x:10,y:100, font:"Times", size:10 }
	public String text = "";
	public double x;
	public double y;
	public String font = ""; // this should also support the names "serif" and "sans-serif" to select the corresponding standard fonts.
	public double size;

	@Override
	public void setStyle(SO style) {

	}

	@Override
	public void paint(Graphics g) {
		g.setColor(Color.black);
		g.setFont(new Font(font,Font.PLAIN,(int)size));
		g.drawChars(text.toCharArray(), 0, text.length(), (int)x, (int)y);
	}

	@Override
	public ArrayList<Integer> select(double x, double y, int myIndex, AffineTransform transform) {
		// TODO
		// This is selected if the selection point is anywhere in the bounding box of the drawn length of the string and the full ascent of the font.
		return null;
	}

	@Override
	public ArrayList<Point2D> controls() {
		// TODO 
		// returns only the left end of its baseline as the control point.
		return null;
	}
}
