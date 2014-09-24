package drawable;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import spark.data.SA;
import spark.data.SO;
import spark.data.SOReflect;

public class Polyline extends SOReflect implements Drawable, Selectable {
	
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

	@Override
	public ArrayList<Integer> select(double x, double y, int myIndex, AffineTransform transform) {
		// TODO
		// This is selected if the selection point is within 3 pixels of any of the line segments
		return null;
	}

	@Override
	public ArrayList<Point2D> controls() {
		// returns its points as the control points.
		ArrayList<Point2D> result = new ArrayList<Point2D>();
        for (int i = 0; i < xPoints.length; i++) {
        	result.add(new Point2D.Double(xPoints[i],yPoints[i]));
        }
		return result;
	}
}
