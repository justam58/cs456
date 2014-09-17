package drawable;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import spark.data.SO;
import spark.data.SOReflect;

public class Text extends SOReflect implements Drawable,Shape {
	
	// Text{ text:"Draw This", x:10,y:100, font:"Times", size:10 }
	public String text;
	public int x;
	public int y;
	public String font; // TODO this should also support the names "serif" and "sans-serif" to select the corresponding standard fonts.
	public int size;
	
	public Transformation t;
	
	@Override
	public void setStyle(SO style, Transformation t) {
		x = (int)style.getDouble("x");
		y = (int)style.getDouble("y");
		size = (int)style.getDouble("size");
		text = style.getString("text");
		font = style.getString("font");
		
		this.t = t;
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		AffineTransform atf = g2d.getTransform();
		g2d.translate(t.tx, t.ty);
		g2d.translate(getCenter().getX(), getCenter().getY());
		g2d.rotate(-Math.toRadians(t.rotate));
		g2d.scale(t.sx, t.sy);
		g2d.translate(-getCenter().getX(), -getCenter().getY());
		
		g.setColor(Color.black);
		g.setFont(new Font(font,Font.PLAIN,size));
		g.drawChars(text.toCharArray(), 0, text.length(), x, y);
		
		g2d.setTransform(atf);
	}

	@Override
	public Point2D getCenter() {
		// TODO What is the center?
		return new Point2D.Double(x,y);
	}

}
