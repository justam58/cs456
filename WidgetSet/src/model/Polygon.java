package model;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import able.Drawable;
import spark.data.SA;
import spark.data.SO;
import spark.data.SOReflect;

public class Polygon extends SOReflect implements Drawable {
	
	// Polygon{ points: [ {x:0,y:0}, . . .], thickness:1, border:{r:100,g:0,b:0}, fill:{r:255,g:255,b:255} }
	public int[] xPoints;
	public int[] yPoints;
	public double thickness;
	public Color border; // If there is no border color then no border is drawn.
	public Color fill; // If there is no fill color then the rectangle is not filled.
	
	@Override
	public void setStyle(SO style) {
		SA pointsArray = style.getArray("points");
        if(pointsArray != null) {
            xPoints = new int[pointsArray.size()];
            yPoints = new int[pointsArray.size()];
            for (int i = 0; i < pointsArray.size(); i++) {
                SO pointObj = pointsArray.getSO(i);
                xPoints[i] = (int) pointObj.getDouble("x");
                yPoints[i] = (int) pointObj.getDouble("y");
            }
        }
		
		SO borderObj = style.getObj("border");
		if(borderObj != null){
			int r = (int)borderObj.getDouble("r");
			int g = (int)borderObj.getDouble("g");
			int b = (int)borderObj.getDouble("b");
			border = new Color(r, g, b);
		}
		
		SO fillObj = style.getObj("fill");
		if(fillObj != null){
			int r = (int)fillObj.getDouble("r");
			int g = (int)fillObj.getDouble("g");
			int b = (int)fillObj.getDouble("b");
			fill = new Color(r, g, b);
		}
	}

	@Override
	public void paint(Graphics g) {
		if(xPoints != null && yPoints != null){
			if(fill != null){
				g.setColor(fill);
				g.fillPolygon(xPoints, yPoints, xPoints.length);
			}
			if(border != null){
				g.setColor(border);
	            Graphics2D g2d = (Graphics2D)g;
				g2d.setStroke(new BasicStroke((int)thickness));
				g.drawPolygon(xPoints, yPoints, xPoints.length);
			}
	
	        if(border == null && fill == null){
	            g.setColor(Color.black);
	            Graphics2D g2d = (Graphics2D)g;
	            g2d.setStroke(new BasicStroke((int)thickness));
	            g.drawPolygon(xPoints, yPoints, xPoints.length);
	        }
		}
	}

}
