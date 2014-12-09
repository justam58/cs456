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
import spark.data.SV;
import view.ContentPanel;

public class Select extends Group implements Drawable, Selectable {
	
	public ArrayList<Integer> selectedPath = null;
	
	@Override
	public void setStyle(SO style){
		SA contentsArray = style.getArray("contents");
		for(int i = 0; i < contentsArray.size(); i++){
			SO shapeObj = contentsArray.getSO(i);
			Drawable shape = (Drawable)shapeObj;
			shape.setStyle(shapeObj);
			contents.add(shape);
		}
		
		SA selectedArray = style.getArray("selected");
		if(selectedArray != null){
			selectedPath = new ArrayList<Integer>();
			for(int i = 0; i < selectedArray.size(); i++){
				SV integer = selectedArray.get(i);
				selectedPath.add((int)integer.getDouble());
			}
		}
	}
	
	@Override
	public void paint(Graphics g) {
		// first draw all of its contents using the paint() method from group 
		Graphics2D g2d = (Graphics2D)g;
		for(int i = 0; i < contents.size(); i++){
			Drawable shape = contents.get(i);
			AffineTransform atf = g2d.getTransform();
			g2d.translate(tx, ty);
			g2d.rotate(-Math.toRadians(rotate));
			g2d.scale(sx, sy);
			shape.paint(g);
			g2d.setTransform(atf);
		}
		// then check to see if it has a "selected" path. 
		// If it does then it should find that object using the path 
		if(selectedPath != null){
			Selectable currentShape = (Selectable)this;
			for(int i = 0; i < selectedPath.size(); i++){
				int childIndex = selectedPath.get(i);
				if(!(currentShape instanceof Group) && !(currentShape instanceof Select)){
					selectedPath = null;
					return;
				}
				Group group = (Group)currentShape;
				Drawable shape = group.contents.get(childIndex);
				if(shape instanceof Polygon){
					selectedPath = null;
					return;
				}
				currentShape = (Selectable)shape;
			}
			ArrayList<Point2D> controlPoints = currentShape.controls();
			// draw the control points.
			AffineTransform atf = g2d.getTransform();
			g2d.translate(tx, ty);
			g2d.rotate(-Math.toRadians(rotate));
			g2d.scale(sx, sy);
			drawControls(g2d, controlPoints);
			g2d.setTransform(atf);
		}
	}

	@Override
	public ArrayList<Integer> select(double x, double y, int myIndex, AffineTransform transform) {
		for(int i = contents.size()-1; i >=0 ; i--){
			Drawable content = contents.get(i);
			if(content instanceof Selectable){
				AffineTransform atf = new AffineTransform(transform);
				atf.scale(1/sx, 1/sy);
				atf.rotate(Math.toRadians(rotate));
				atf.translate(-tx, -ty);
				Selectable shape = (Selectable)content;
				ArrayList<Integer> selectPath = shape.select(x,y,i,atf);
				if(selectPath != null){
					selectedPath = selectPath;
					ContentPanel.getInstance().repaint();
					return selectPath;
				}
			}
		}
		selectedPath = null;
		ContentPanel.getInstance().repaint();
		return null;
	}
	
	private void drawControls(Graphics2D g, ArrayList<Point2D> controlPoints){
		int pointSizeX = (int)(4/sx);
		int pointSizeY = (int)(4/sy);
		for(int j = 0; j < controlPoints.size(); j++){
			Point2D p = controlPoints.get(j);
			g.setColor(Color.white);
			g.fillRect((int)p.getX()-pointSizeX, (int)p.getY()-pointSizeY, pointSizeX*2, pointSizeY*2);
			g.setStroke(new BasicStroke(1));
			g.setColor(Color.black);
			g.drawRect((int)p.getX()-pointSizeX, (int)p.getY()-pointSizeY, pointSizeX*2, pointSizeY*2);
		}
	}

}
