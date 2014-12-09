package sparkClass.shape;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import spark.data.SA;
import spark.data.SO;
import spark.data.SOReflect;
import sparkClass.Root;
import able.Drawable;
import able.Interactable;
import able.Selectable;

public class Curve extends SOReflect implements Drawable, Selectable, Interactable{
	// Polygon{ points: [ {x:0,y:0}, . . .], thickness:1, border:{r:100,g:0,b:0}, fill:{r:255,g:255,b:255} }
	public int[] xPoints;
	public int[] yPoints;
	public double thickness;
	public Color border; // If there is no border color then no border is drawn.
	public Color fill; // If there is no fill color then the rectangle is not filled.
	
	private Point2D.Double[] points; // control points
	private boolean isfilled = false;
	private Path2D path;
	public Point2D.Double P, Q, R, S; // arch points
	
	private static final int HIT_BOX_SIZE = 3;

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
	public ArrayList<Integer> select(double x, double y, int myIndex, AffineTransform transform) {
		Point2D ptSrc = new Point2D.Double(x,y);
		Point2D ptDst = transform.transform(ptSrc, null);
		x = ptDst.getX();
		y = ptDst.getY();
		
		int HIT_BOX_SIZE_X = (int) (HIT_BOX_SIZE / transform.getScaleX());
		int HIT_BOX_SIZE_Y = (int) (HIT_BOX_SIZE / transform.getScaleY());
		
		ArrayList<Integer> result = null;
		// If the curve is filled, then it is selected if the selection point is inside the curve
		if(fill != null){
			if(path.contains(x,y)){
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
			for (int i = 0; i < points.length-1; i++) {
			  	makeArch(points, points.length, i); 
				Path2D path = new Path2D.Double();
				path.moveTo(P.x, P.y);
			  	path.curveTo(Q.x, Q.y, R.x, R.y, S.x, S.y);
				if (path.intersects(boxX, boxY, width, height)) {
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
	public void setStyle(SO style) {
		SA pointsArray = style.getArray("points");
        if(pointsArray != null) {
            xPoints = new int[pointsArray.size()];
            yPoints = new int[pointsArray.size()];
            points = new Point2D.Double[pointsArray.size()];
            for (int i = 0; i < pointsArray.size(); i++) {
                SO pointObj = pointsArray.getSO(i);
                xPoints[i] = (int) pointObj.getDouble("x");
                yPoints[i] = (int) pointObj.getDouble("y");
                points[i] = new Point2D.Double(xPoints[i],yPoints[i]);
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
		
		int size = xPoints.length;
		if(xPoints[0] != xPoints[size-1] || yPoints[0] != yPoints[size-1]){
			fill = null;
			isfilled = false;
		}
		else{
			isfilled = true;
		}
		
		path = new Path2D.Double();
		path.moveTo(points[0].x, points[0].y);
		for (int i = 0; i < points.length-1; i++) {
		  	makeArch(points, points.length, i); 
		  	path.curveTo(Q.x, Q.y, R.x, R.y, S.x, S.y);
		}
	}
	
	@Override
	public void paint(Graphics g) {
		if(points != null){
			Graphics2D g2 = (Graphics2D)g;
			g.setColor(border == null? Color.black : border);
			g2.setStroke(new BasicStroke((int)thickness));
			g2.draw(path);
			if(fill != null){
				g.setColor(fill);
				g2.fill(path);
			}	
			
//			// Show Control Points
//			g2.setPaint(Color.blue);
//			int dSize = 8;
//			for (int i = 0; i < points.length; i++) {
//				Point2D.Double P = points[i];
//				g2.fillOval((int)P.getX()-dSize, (int)P.getY()-dSize, 2*dSize, 2*dSize);
//			}	
		}
	}
	
	private void makeArch(Point2D.Double[] controlPoints, int numCP, int p){
		P = new Point2D.Double(controlPoints[p].x, controlPoints[p].y); 
		S = new Point2D.Double(controlPoints[p+1].x, controlPoints[p+1].y);
		Q = new Point2D.Double();
		R = new Point2D.Double();
		if (p == 0){
			if(isfilled){
				Q.x = controlPoints[p].x + (-controlPoints[numCP-2].x + controlPoints[p+1].x)/6;
				Q.y = controlPoints[p].y + (-controlPoints[numCP-2].y + controlPoints[p+1].y)/6;
			}
			else{
				Q = P;
			}
		}
		else { 
			Q.x = controlPoints[p].x + (-controlPoints[p-1].x + controlPoints[p+1].x)/6;
			Q.y = controlPoints[p].y + (-controlPoints[p-1].y + controlPoints[p+1].y)/6;
		}
		
		if (p == numCP - 2){
			if(isfilled){
				R.x = controlPoints[p+1].x - (-controlPoints[p].x + controlPoints[1].x)/6;
				R.y = controlPoints[p+1].y - (-controlPoints[p].y + controlPoints[1].y)/6;
			}
			else{
				R = S;
			}
		}
		else { 
			R.x = controlPoints[p+1].x - (-controlPoints[p].x + controlPoints[p+2].x)/6;
			R.y = controlPoints[p+1].y - (-controlPoints[p].y + controlPoints[p+2].y)/6;
		}
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
