package drawable;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import spark.data.SO;
import spark.data.SOReflect;

public class Line extends SOReflect implements Drawable,Shape {
	
	// Line{ x1:10, y1:5, x2: 20, y2:40, thickness:3, color:{r:0,g:0,b:0} }
	public int x1;
	public int y1;
	public int x2;
	public int y2;
	public int thickness;
	public Color color;
	
	public Transformation t;
	
	@Override
	public void setStyle(SO style, Transformation t){
		x1 = (int)style.getDouble("x1");
		x2 = (int)style.getDouble("x2");
		y1 = (int)style.getDouble("y1");
		y2 = (int)style.getDouble("y2");
		
		SO colorObj = style.getObj("color");
		int r = (int)colorObj.getDouble("r");
		int g = (int)colorObj.getDouble("g");
		int b = (int)colorObj.getDouble("b");
		color = new Color(r, g, b);
		thickness = (int)style.getDouble("thickness");
		
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
		
		g.setColor(color);
		g2d.setStroke(new BasicStroke(thickness));
		g.drawLine(x1, y1, x2, y2);
		
		g2d.setTransform(atf);
	}

	@Override
	public Point2D getCenter() {
		double xTotal = x1 + x2;
		double yTotal = y1 + y2;
		return new Point2D.Double(xTotal/2, yTotal/2);
	}

}
