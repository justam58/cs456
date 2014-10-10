package model;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import able.Drawable;
import able.Interactable;
import able.Selectable;
import spark.data.SA;
import spark.data.SO;
import spark.data.SOReflect;

public class Polyline extends SOReflect implements Drawable, Selectable, Interactable {
	
	// Polyline{ points:[ {x:10,y:10}, ... ], thickness:1, color:{r:100,g:0,b:0} } 
	public int[] xPoints;
	public int[] yPoints;
	public double thickness;
	public Color color = Color.black;
	
	private static final int HIT_BOX_SIZE = 3;

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
		Point2D ptSrc = new Point2D.Double(x,y);
		Point2D ptDst = transform.transform(ptSrc, null);
		x = ptDst.getX();
		y = ptDst.getY();
		
		int HIT_BOX_SIZE_X = (int) (HIT_BOX_SIZE / transform.getScaleX());
		int HIT_BOX_SIZE_Y = (int) (HIT_BOX_SIZE / transform.getScaleY());
		
		// This is selected if the selection point is within 3 pixels of any of the line segments
		ArrayList<Integer> result = null;
		
		int boxX = (int) (x - HIT_BOX_SIZE_X / 2);
		int boxY = (int) (y - HIT_BOX_SIZE_Y / 2);
		
		int width = HIT_BOX_SIZE_X;
		int height = HIT_BOX_SIZE_Y;
		for(int i = 0; i < xPoints.length-1; i++){
			Line2D line = new Line2D.Double(xPoints[i], yPoints[i], xPoints[i+1], yPoints[i+1]);
			if (line.intersects(boxX, boxY, width, height)) {
				result = new ArrayList<Integer>();
				result.add(myIndex);
				break;
			}
		}
		return result;
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

	@Override
	public boolean mouseDown(double x, double y, AffineTransform myTransform) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMove(double x, double y, AffineTransform myTransform) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseUp(double x, double y, AffineTransform myTransform) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean key(char key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Root getPanel() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
