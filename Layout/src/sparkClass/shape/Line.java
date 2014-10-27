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
import spark.data.SO;
import spark.data.SOReflect;
import sparkClass.Root;

public class Line extends SOReflect implements Drawable, Selectable, Interactable {
	
	// Line{ x1:10, y1:5, x2: 20, y2:40, thickness:3, color:{r:0,g:0,b:0} }
	public double x1;
	public double y1;
	public double x2;
	public double y2;
	public double thickness;
	public Color color = Color.black;
	
	private static final int HIT_BOX_SIZE = 3;

	@Override
	public void setStyle(SO style){
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
		Graphics2D g2d = (Graphics2D)g;
		g.setColor(color);
		g2d.setStroke(new BasicStroke((int)thickness));
		g.drawLine((int)x1, (int)y1, (int)x2, (int)y2);
	}

	@Override
	public ArrayList<Integer> select(double x, double y, int myIndex, AffineTransform transform) {
		Point2D ptSrc = new Point2D.Double(x,y);
		Point2D ptDst = transform.transform(ptSrc, null);
		x = ptDst.getX();
		y = ptDst.getY();
		
		int HIT_BOX_SIZE_X = (int) (HIT_BOX_SIZE / transform.getScaleX());
		int HIT_BOX_SIZE_Y = (int) (HIT_BOX_SIZE / transform.getScaleY());
		
		// This will select if the selection point is within three pixels of the line on either side.
		// It returns an array containing only the myIndex parameter if selected, null otherwise.
		ArrayList<Integer> result = null;
		
		int boxX = (int) (x - HIT_BOX_SIZE_X / 2);
		int boxY = (int) (y - HIT_BOX_SIZE_Y / 2);
		
		int width = HIT_BOX_SIZE_X;
		int height = HIT_BOX_SIZE_Y;

		Line2D line = new Line2D.Double(x1, y1, x2, y2);
		if (line.intersects(boxX, boxY, width, height)) {
			result = new ArrayList<Integer>();
			result.add(myIndex);
		}		
		return result;
	}

	@Override
	public ArrayList<Point2D> controls() {
		// returns its two end points.
		ArrayList<Point2D> result = new ArrayList<Point2D>();
		result.add(new Point2D.Double(x1,y1));
		result.add(new Point2D.Double(x2,y2));
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
	
}
