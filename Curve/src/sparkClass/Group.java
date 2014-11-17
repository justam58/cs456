package sparkClass;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import able.Drawable;
import able.Interactable;
import able.Layout;
import able.Selectable;
import spark.data.SA;
import spark.data.SO;
import spark.data.SOReflect;
import spark.data.SParented;

public class Group extends SOReflect implements Drawable, Selectable, Interactable, Layout{
	
	// Group{ contents:[ ... ], sx:1.0, sy:1.0, rotate:0.0, tx:0.0, ty:0.0 } 
	public ArrayList<Drawable> contents = new ArrayList<Drawable>(); // SArray of Drawable objects
	public double sx = 1;
	public double sy = 1;
	public double rotate; // in degrees counter clockwise.
	public double tx;
	public double ty;
	
	// Group{ contents:[...], width:10.0, height:20.0 }
	public double width;
	public double height;
	
	private double centerX;
	private double centerY;
	private double currentX;
	private double currentY;
	private double sliderRotation;
	public boolean isSlider = false;
	
	public double newTy;
	public double newTx;
	
	public void moveTo(double x, double y, double r) {
		currentX = x;
		currentY = y;
		sliderRotation = r;
	}
	
	@Override
	public void setStyle(SO style){
		SA contentsArray = style.getArray("contents");
		for(int i = 0; i < contentsArray.size(); i++){
			SO shapeObj = contentsArray.getSO(i);
			Drawable shape = (Drawable)shapeObj;
			shape.setStyle(shapeObj);
			contents.add(shape);
		}
		
		Point2D center = getCenter();
		centerX = center.getX();
		centerY = center.getY();
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		if(!isSlider){
			for(int i = 0; i < contents.size(); i++){
				AffineTransform atf = g2d.getTransform();
				g2d.translate(tx, ty);
				g2d.rotate(-Math.toRadians(rotate));
				g2d.scale(sx, sy);
	            contents.get(i).paint(g);
				g2d.setTransform(atf);
			}
		}
		else{
			for(int i = 0; i < contents.size(); i++){
				AffineTransform atf = g2d.getTransform();
				g2d.translate(currentX, currentY);
				g2d.rotate(-Math.toRadians(sliderRotation));
				g2d.translate(-centerX, -centerY);
	            contents.get(i).paint(g);
				g2d.setTransform(atf);
			}
		}
	}

	@Override
	public ArrayList<Integer> select(double x, double y, int myIndex, AffineTransform transform) {
		for(int i = 0; i < contents.size(); i++){
			// recursively call select on its contents
			Drawable content = contents.get(i);
			if(content instanceof Selectable){
				// Be sure to correctly account for the transformation by transforming the selection point by the inverse of the transformation on this group and by pass
				// That will bring the selection point into the coordinate system of the group contents
				AffineTransform atf = new AffineTransform(transform);
				if(!isSlider){
					atf.scale(1/sx, 1/sy);
					atf.rotate(Math.toRadians(rotate));
					atf.translate(-tx, -ty);
				}
				else{
					atf.translate(centerX, centerY);
					atf.rotate(Math.toRadians(sliderRotation));
					atf.translate(-currentX, -currentY);
					atf.translate(-newTx, -newTy);
				}
				Selectable shape = (Selectable)content;
				ArrayList<Integer> selectPath = shape.select(x,y,i,atf);
				// If any of the contents are selected 
				// then this should take the selection path from the selected child and add this group's index onto the front and return the more complete path.
				// This will recursively build a path to the selected object.
				if(selectPath != null){
					selectPath.add(0, myIndex);
					return selectPath;
				}
			}
		}
		return null;
	}

	@Override
	public ArrayList<Point2D> controls() {
		// This object has no controls of its own.
		return null;
	}

	@Override
	public boolean mouseDown(double x, double y, AffineTransform myTransform) {
		AffineTransform atf = new AffineTransform(myTransform);
		atf.scale(1/sx, 1/sy);
		atf.rotate(Math.toRadians(rotate));
		atf.translate(-tx, -ty);
		boolean handled = false;
		for(int i = contents.size()-1; i >= 0; i--){ // back to front order
			// recursively call select on its contents
			Interactable shape = (Interactable)contents.get(i);
			boolean shapeHandled = shape.mouseDown(x,y,atf);
			if(!handled && shapeHandled){
				handled = true;
			}
		}
		return handled;
	}

	@Override
	public boolean mouseMove(double x, double y, AffineTransform myTransform) {
		AffineTransform atf = new AffineTransform(myTransform);
		atf.scale(1/sx, 1/sy);
		atf.rotate(Math.toRadians(rotate));
		atf.translate(-tx, -ty);
		for(int i = contents.size()-1; i >= 0; i--){ // back to front order
			// recursively call select on its contents
			Interactable content = (Interactable)contents.get(i);
			boolean handeled = content.mouseMove(x, y, atf);
			if(handeled){
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean mouseUp(double x, double y, AffineTransform myTransform) {
		AffineTransform atf = new AffineTransform(myTransform);
		atf.scale(1/sx, 1/sy);
		atf.rotate(Math.toRadians(rotate));
		atf.translate(-tx, -ty);
		for(int i = contents.size()-1; i >= 0; i--){ // back to front order
			// recursively call select on its contents
			Interactable content = (Interactable)contents.get(i);
			boolean handeled = content.mouseUp(x, y, atf);
			if(handeled){
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean key(char key) {
		return false;
	}

	@Override
	public Root getPanel() {
		SParented parent = myParent(); 
		while(!(parent instanceof Interactable)){
			parent = parent.myParent();
		}
		Interactable InteractableParent = (Interactable)parent;
		return InteractableParent.getPanel();
	}

	@Override
	public double getMinWidth() {
		return width;
	}

	@Override
	public double getDesiredWidth() {
		return width;
	}

	@Override
	public double getMaxWidth() {
		return Double.MAX_VALUE;
	}

	@Override
	public void setHBounds(double left, double right) {
		// TODO ??
	}

	@Override
	public double getMinHeight() {
		return height;
	}

	@Override
	public double getDesiredHeight() {
		return height;
	}

	@Override
	public double getMaxHeight() {
		return Double.MAX_VALUE;
	}

	@Override
	public void setVBounds(double top, double bottom) {
		// TODO ??
	}

	@Override
	public Point2D getCenter() {
		double totalX = 0;
		double totalY = 0;
		int size = contents.size();
		for(int i = 0; i < size; i++){
            Point2D p = contents.get(i).getCenter();
        	totalX += p.getX();
        	totalY += p.getY();
		}
		return new Point2D.Double(totalX/size, totalY/size);
	}

}
