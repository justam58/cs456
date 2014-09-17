package drawable;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import spark.data.SA;
import spark.data.SO;
import spark.data.SOReflect;

public class Polyline extends SOReflect implements Drawable{
	
	// Polyline{ points:[ {x:10,y:10}, ... ], thickness:1, color:{r:100,g:0,b:0} } 
	public int[] xPoints;
	public int[] yPoints;
	public double thickness;
	public Color color = Color.black;

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
		
		SO colorObj = style.getObj("color");
        if(colorObj != null){
            int r = (int)colorObj.getDouble("r");
            int g = (int)colorObj.getDouble("g");
            int b = (int)colorObj.getDouble("b");
            color = new Color(r, g, b);
        }
	}

	@Override
	public void paint(Graphics g) {
		if(xPoints != null && yPoints != null){
			Graphics2D g2d = (Graphics2D)g;
			g.setColor(color);
			g2d.setStroke(new BasicStroke((int)thickness));
			g.drawPolyline(xPoints, yPoints, xPoints.length);
		}
	}
}
