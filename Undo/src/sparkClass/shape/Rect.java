package sparkClass.shape;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import listener.ActiveListener;
import able.Dragable;
import able.Drawable;
import able.Interactable;
import able.Selectable;
import spark.data.SO;
import spark.data.SOReflect;
import sparkClass.Root;

public class Rect extends SOReflect implements Drawable, Selectable, Interactable, Dragable, ActiveListener {
	
	// Rect{ left:0, top:100, width:10, height:10, thickness:2, border:{r:0,g:0,b:0}, fill:{r:0,g:0,b:128} } 
	public double left;
	public double top;
	public double width;
	public double height;
	public double thickness;
	public Color border; // If there is no border color then no border is drawn.
	public Color fill; // If there is no fill color then the rectangle is not filled. 
	
	private static final int HIT_BOX_SIZE = 3;
	
	public Rect() {
		super();
	}

	public Rect(double left, double top, double width, double height) {
		super();
		this.left = left;
		this.top = top;
		this.width = width;
		this.height = height;
	}

	@Override
	public void setStyle(SO style){
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
		if(fill != null){
			g.setColor(fill);
			g.fillRect((int)left, (int)top, (int)width, (int)height);
		}
		if(border != null)
			g.setColor(border);{
            Graphics2D g2d = (Graphics2D)g;
			g2d.setStroke(new BasicStroke((int)thickness));
			g.drawRect((int)left, (int)top, (int)width, (int)height);
		}

        if(border == null && fill == null){
            g.setColor(Color.black);
            Graphics2D g2d = (Graphics2D)g;
            g2d.setStroke(new BasicStroke((int)thickness));
            g.drawRect((int)left, (int)top, (int)width, (int)height);
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
		// If the rect is filled, then it is selected if the selection point is inside the rectangle
		if(fill != null){
			if(y > top && x > left && y < (top+height) && x < (left+width)){
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
			ArrayList<Point2D> controls = controls();
			for(int i = 0; i < controls.size(); i++){
				int j = i + 1;
				if(j == controls.size()){
					j = 0;
				}
				double x1 = controls.get(i).getX();
				double y1 = controls.get(i).getY();
				double x2 = controls.get(j).getX();
				double y2 = controls.get(j).getY();
				Line2D line = new Line2D.Double(x1, y1, x2, y2);
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
		// returns its four corners.
		ArrayList<Point2D> result = new ArrayList<Point2D>();
		result.add(new Point2D.Double(left,top)); // top left point
		result.add(new Point2D.Double(left+width,top)); // top right point
		result.add(new Point2D.Double(left+width,top+height)); // down right point
		result.add(new Point2D.Double(left,top+height)); // down left point
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
	public void stateChanged(Color c) {
		fill = c;
	}
	
	@Override
	public double move(double dx, double dy, double max, double min) {
		if(dy != 0){
			top += dy;
			top = top > max ? max : top;
			top = top < min ? min : top;
			return top;
		}
		left += dx;
		left = left > max ? max : left;
		left = left < min ? min : left;
		return left;
	}
	
	@Override
	public double getSliderHeight() {
		return height;
	}
	
	@Override
	public double getSliderWidth() {
		return width;
	}
	
	@Override
	public double moveTo(double x, double y, double max, double min) {
		if(x != -1){
			left = x;
			left = left > max ? max : left;
			left = left < min ? min : left;
			return left;
		}
		if(y != -1){
			top = y;
			top = top > max ? max : top;
			top = top < min ? min : top;
			return top;
		}
		return -1;
	}
	
	@Override
	public double getCurrentX() {
		return left;
	}

	@Override
	public double getCurrentY() {
		return top;
	}

	@Override
	public Point2D getCenter() {
		ArrayList<Point2D> controls = controls();
		double totalX = 0;
		double totalY = 0;
		int size = controls.size();
        for (int i = 0; i < size; i++) {
        	totalX += controls.get(i).getX();
        	totalY += controls.get(i).getY();
        }
		return new Point2D.Double(totalX/size, totalY/size);
	}

}
