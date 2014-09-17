package drawable;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import spark.data.SA;
import spark.data.SO;
import spark.data.SOReflect;

public class Polyline extends SOReflect implements Drawable,Shape {
	
	// Polyline{ points:[ {x:10,y:10}, ... ], thickness:1, color:{r:100,g:0,b:0} } 
	public int[] xPoints;
	public int[] yPoints;
	public int thickness;
	public Color color;
	
	public Transformation t;
	
	@Override
	public void setStyle(SO style, Transformation t) {
		SA pointsArray = style.getArray("points");
		xPoints = new int[pointsArray.size()];
		yPoints = new int[pointsArray.size()];
		for(int i = 0; i < pointsArray.size(); i++){
			SO pointObj = pointsArray.getSO(i);
			xPoints[i] = (int)pointObj.getDouble("x");
			yPoints[i] = (int)pointObj.getDouble("y");
		}
		
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
		g.drawPolyline(xPoints, yPoints, xPoints.length);
		
		g2d.setTransform(atf);
	}

	@Override
	public Point2D getCenter() {
		double xTotal = 0;
		double yTotal = 0;
		int size = xPoints.length;
		for(int i = 0; i < 3; i++){
			xTotal += xPoints[i];
			yTotal += yPoints[i];
		}
		return new java.awt.geom.Point2D.Double(xTotal/size, yTotal/size);
	}

}
