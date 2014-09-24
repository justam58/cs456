package drawable;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import spark.data.SO;
import spark.data.SOReflect;

public class Line extends SOReflect implements Drawable, Selectable  {
	
	// Line{ x1:10, y1:5, x2: 20, y2:40, thickness:3, color:{r:0,g:0,b:0} }
	public double x1;
	public double y1;
	public double x2;
	public double y2;
	public double thickness;
	public Color color = Color.black;

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
		// TODO 
		// This will select if the selection point is within three pixels of the line on either side.
		// It returns an array containing only the myIndex parameter if selected, null otherwise.
		return null;
	}

	@Override
	public ArrayList<Point2D> controls() {
		// returns its two end points.
		ArrayList<Point2D> result = new ArrayList<Point2D>();
		result.add(new Point2D.Double(x1,y1));
		result.add(new Point2D.Double(x2,y2));
		return result;
	}
}
