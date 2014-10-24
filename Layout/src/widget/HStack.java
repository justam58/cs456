package widget;

import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import able.Drawable;
import able.Interactable;
import spark.data.SA;
import spark.data.SO;
import spark.data.SOReflect;
import spark.data.SParented;
import view.Layout;

public class HStack extends SOReflect implements Layout, Drawable, Interactable {
	
	// HStack{ contents:[...] } 
	public ArrayList<Drawable> contents = new ArrayList<Drawable>();

	@Override
	public double getMinWidth() {
		double result = 0;
		for(int i = 0; i < contents.size(); i++){
			Layout layout = (Layout)contents.get(i);
			result += layout.getMinWidth();
		}
		return result;
	}

	@Override
	public double getDesiredWidth() {
		double result = 0;
		for(int i = 0; i < contents.size(); i++){
			Layout layout = (Layout)contents.get(i);
			result += layout.getDesiredWidth();
		}
		return result;
	}

	@Override
	public double getMaxWidth() {
		double result = 0;
		for(int i = 0; i < contents.size(); i++){
			Layout layout = (Layout)contents.get(i);
			result += layout.getMaxWidth();
		}
		return result;
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
			result += layout.getMaxHeight();
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
		for(int i = 0; i < contentsArray.size(); i++){
			SO shapeObj = contentsArray.getSO(i);
			Drawable shape = (Drawable)shapeObj;
			shape.setStyle(shapeObj);
			contents.add(shape);
		}
	}

	@Override
	public void paint(Graphics g) {
		for(int i = 0; i < contents.size(); i++){
            contents.get(i).paint(g);
		}
	}

}
