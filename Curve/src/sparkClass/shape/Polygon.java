package sparkClass.shape;

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
import sparkClass.Root;

public class Polygon extends SOReflect implements Drawable, Selectable, Interactable {
	
	// Polygon{ points: [ {x:0,y:0}, . . .], thickness:1, border:{r:100,g:0,b:0}, fill:{r:255,g:255,b:255} }
	public int[] xPoints;
	public int[] yPoints;
	public double thickness;
	public Color border; // If there is no border color then no border is drawn.
	public Color fill; // If there is no fill color then the rectangle is not filled.
	
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

	@Override
	public ArrayList<Integer> select(double x, double y, int myIndex, AffineTransform transform) {
		Point2D ptSrc = new Point2D.Double(x,y);
		Point2D ptDst = transform.transform(ptSrc, null);
		x = ptDst.getX();
		y = ptDst.getY();
		
		int HIT_BOX_SIZE_X = (int) (HIT_BOX_SIZE / transform.getScaleX());
		int HIT_BOX_SIZE_Y = (int) (HIT_BOX_SIZE / transform.getScaleY());
		
		ArrayList<Integer> result = null;
		// If the polygon is filled, then it is selected if the selection point is inside the polygon
		if(fill != null){
			java.awt.Polygon polygon = new java.awt.Polygon(xPoints,yPoints,xPoints.length);
			if(polygon.contains(x,y)){
				result = new ArrayList<Integer>();
				result.add(myIndex);
			}
		}
		else // If it is not filled then the selection point must be within 3 pixels of one of the edges.
		{
			int boxX = (int) (x - HIT_BOX_SIZE_X / 2);
			int boxY = (int) (y - HIT_BOX_SIZE_Y / 2);
			
			int width = HIT_BOX_SIZE_X;
			int height = HIT_BOX_SIZE_Y;
			for(int i = 0; i < xPoints.length; i++){
				int j = i+1;
				if(i == (xPoints.length-1)){
					j = 0;
				}
				Line2D line = new Line2D.Double(xPoints[i], yPoints[i], xPoints[j], yPoints[j]);
				if (line.intersects(boxX, boxY, width, height)) {
					result = new ArrayList<Integer>();
					result.add(myIndex);
					break;
				}
			}
		}
		return result;
	}

	@Override
	public ArrayList<Point2D> controls() {
		ArrayList<Point2D> result = new ArrayList<Point2D>();
        for (int i = 0; i < xPoints.length; i++) {
        	result.add(new Point2D.Double(xPoints[i],yPoints[i]));
        }
		return result;
	}

	@Override
	public boolean mouseDown(double x, double y, AffineTransform myTransform) {
		return false;
	}

	@Override
	public boolean mouseMove(double x, double y, AffineTransform myTransform) {
		return false;
	}

	@Override
	public boolean mouseUp(double x, double y, AffineTransform myTransform) {
		return false;
	}

	@Override
	public boolean key(char key) {
		return false;
	}

	@Override
	public Root getPanel() {
		return null;
	}

	@Override
	public Point2D getCenter() {
		double totalX = 0;
		double totalY = 0;
		int size = xPoints.length;
        for (int i = 0; i < size; i++) {
        	totalX += xPoints[i];
        	totalY += yPoints[i];
        }
		return new Point2D.Double(totalX/size, totalY/size);
	}

}
