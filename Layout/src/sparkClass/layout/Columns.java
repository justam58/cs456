package sparkClass.layout;

import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import able.Drawable;
import able.Interactable;
import able.Layout;
import spark.data.SA;
import spark.data.SO;
import spark.data.SOReflect;
import spark.data.SParented;
import sparkClass.Root;

public class Columns extends SOReflect implements Layout, Drawable, Interactable {
	
	// Columns{ contents:[...], nColumns:12, gutter:10 }
	public ArrayList<Drawable> contents = new ArrayList<Drawable>();
	public double nColumns;
	public double gutter;
	
	private double minWidth;
	private double maxWidth;
	private double desiredWidth;

	@Override
	public double getMinWidth() {
		return minWidth;
	}

	@Override
	public double getDesiredWidth() {
		return desiredWidth;
	}

	@Override
	public double getMaxWidth() {
		return maxWidth;
	}

	@Override
	public void setHBounds(double left, double right) {
		// TODO Auto-generated method stub

	}

	@Override
	public double getMinHeight() {
		double result = 0;
		for(int i = 0; i < contents.size(); i++){
			Layout layout = (Layout)contents.get(i);
			result += layout.getMinHeight();
		}
		return result;
	}

	@Override
	public double getDesiredHeight() {
		double result = 0;
		for(int i = 0; i < contents.size(); i++){
			Layout layout = (Layout)contents.get(i);
			result += layout.getDesiredHeight();
		}
		return result;
	}

	@Override
	public double getMaxHeight() {
		double result = 0;
		for(int i = 0; i < contents.size(); i++){
			Layout layout = (Layout)contents.get(i);
			result += layout.getMaxHeight();
		}
		return result;
	}

	@Override
	public void setVBounds(double top, double bottom) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean mouseDown(double x, double y, AffineTransform myTransform) {
		boolean handled = false;
		for(int i = contents.size()-1; i >= 0; i--){ // back to front order
			Interactable shape = (Interactable)contents.get(i);
			boolean shapeHandled = shape.mouseDown(x,y,myTransform);
			if(!handled && shapeHandled){
				handled = true;
			}
		}
		return handled;
	}

	@Override
	public boolean mouseMove(double x, double y, AffineTransform myTransform) {
		for(int i = contents.size()-1; i >= 0; i--){ // back to front order
			Interactable content = (Interactable)contents.get(i);
			boolean handeled = content.mouseMove(x, y, myTransform);
			if(handeled){
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean mouseUp(double x, double y, AffineTransform myTransform) {
		for(int i = contents.size()-1; i >= 0; i--){ // back to front order
			Interactable content = (Interactable)contents.get(i);
			boolean handeled = content.mouseUp(x, y, myTransform);
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
	public void setStyle(SO style) {
		SA contentsArray = style.getArray("contents");
		double maxMinColumnWidth = -1;
		double maxMaxColumnWidth = -1;
		double maxDesiredColumnWidth = -1;
		for(int i = 0; i < contentsArray.size(); i++){
			SO shapeObj = contentsArray.getSO(i);
			Drawable shape = (Drawable)shapeObj;
			shape.setStyle(shapeObj);
			contents.add(shape);
			
			SOReflect shapeSOR = (SOReflect)shape;
			String classVal = shapeSOR.getString("columnSpan");
			double columnSpan = 1;
			if(classVal != null && classVal.equals("columnSpan")){
				columnSpan = Double.valueOf(classVal);
			}
			
			Layout layout = (Layout)shape;
			double minColumnWidth = computeColumnWidth(layout.getMinWidth(),columnSpan);
			if(maxMinColumnWidth == -1 || minColumnWidth > maxMinColumnWidth){
				maxMinColumnWidth = minColumnWidth;
			}
			double maxColumnWidth = computeColumnWidth(layout.getMaxWidth(),columnSpan);
			if(maxMaxColumnWidth == -1 || maxColumnWidth > maxMaxColumnWidth){
				maxMaxColumnWidth = maxColumnWidth;
			}
			double desiredColumnWidth = computeColumnWidth(layout.getDesiredWidth(),columnSpan);
			if(maxDesiredColumnWidth == -1 || desiredColumnWidth > maxDesiredColumnWidth){
				maxDesiredColumnWidth = desiredColumnWidth;
			}
		}
		
		minWidth = computeWidth(maxMinColumnWidth);
		maxWidth = computeWidth(maxMaxColumnWidth);
		desiredWidth = computeWidth(maxDesiredColumnWidth);
	}

	@Override
	public void paint(Graphics g) {
		for(int i = 0; i < contents.size(); i++){
            contents.get(i).paint(g);
		}
	}
	
	private double computeColumnWidth(double width, double columnSpan){
		return (width-((columnSpan-1)*gutter))/columnSpan;
	}
	
	private double computeWidth(double maxChildWith){
		return (maxChildWith*nColumns)+(gutter*(nColumns-1));
	}

}
