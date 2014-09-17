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

public class Polygon extends SOReflect implements Drawable,Shape {
	
	// Polygon{ points: [ {x:0,y:0}, . . .], thickness:1, border:{r:100,g:0,b:0}, fill:{r:255,g:255,b:255} }
	public int[] xPoints;
	public int[] yPoints;
	public int thickness;
	public Color border; // If there is no border color then no border is drawn.
	public Color fill; // If there is no fill color then the rectangle is not filled. 
	
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
		
		SO borderObj = style.getObj("border");
		if(borderObj != null){
			int r = (int)borderObj.getDouble("r");
			int g = (int)borderObj.getDouble("g");
			int b = (int)borderObj.getDouble("b");
			border = new Color(r, g, b);
			thickness = (int)style.getDouble("thickness");
		}
		
		SO fillObj = style.getObj("fill");
		if(fillObj != null){
			int r = (int)fillObj.getDouble("r");
			int g = (int)fillObj.getDouble("g");
			int b = (int)fillObj.getDouble("b");
			fill = new Color(r, g, b);
		}
		
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
		
		if(fill != null){
			g.setColor(fill);
			g.fillPolygon(xPoints, yPoints, xPoints.length);
		}
		if(border != null){
			g.setColor(border);
			g2d.setStroke(new BasicStroke(thickness));
			g.drawPolygon(xPoints, yPoints, xPoints.length);
		}
		
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
