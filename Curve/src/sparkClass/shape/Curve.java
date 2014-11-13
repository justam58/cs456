package sparkClass.shape;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
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
	
	private Point2D[] points;
	
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
		// TODO Auto-generated method stub
		return null;
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
            points = new Point2D[pointsArray.size()];
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
		}
	}
	@Override
	public void paint(Graphics g) {
		if(points != null){
			Graphics2D g2d = (Graphics2D)g;
			if(border == null){
				g.setColor(Color.black);
			}
			else{
				g.setColor(border);
			}
			
			g2d.setStroke(new BasicStroke((int)thickness));
			QuadCurve2D curve = new QuadCurve2D.Float();
			curve.setCurve(points, 0);
			g2d.draw(curve);
			
			if(fill != null){
				g.setColor(fill);
				g2d.fill(curve);
			}
			
			int pointSizeX = 4;
			int pointSizeY = 4;
			for(int j = 0; j < points.length; j++){
				Point2D p = points[j];
				g.setColor(Color.white);
				g.fillRect((int)p.getX()-pointSizeX, (int)p.getY()-pointSizeY, pointSizeX*2, pointSizeY*2);
				((Graphics2D) g).setStroke(new BasicStroke(1));
				g.setColor(Color.black);
				g.drawRect((int)p.getX()-pointSizeX, (int)p.getY()-pointSizeY, pointSizeX*2, pointSizeY*2);
			}
		}
	}
}
