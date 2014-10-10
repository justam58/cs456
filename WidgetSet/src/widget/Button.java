package widget;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import model.Root;
import spark.data.SA;
import spark.data.SO;
import spark.data.SOReflect;
import able.Drawable;
import able.Interactable;
import able.Selectable;

public class Button extends SOReflect implements Drawable, Selectable, Interactable {
	
	// Button{ label:"my label", contents:[...],state:"idle", idle:{r:0,g:0,b:0}, hover:{r:100,g:100,b:100}, active:{r:255,g:255,b:0}, model:[...], value:10 } 
	public String label;
	public ArrayList<Drawable> contents = new ArrayList<Drawable>(); // SArray of Drawable objects
	public ArrayList<ArrayList<Integer>> models = new ArrayList<ArrayList<Integer>>(); 
	public String state;
	public Color idle;
	public Color hover;
	public Color active;
	public double value;
	
	@Override
	public boolean mouseDown(double x, double y, AffineTransform myTransform) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean mouseMove(double x, double y, AffineTransform myTransform) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean mouseUp(double x, double y, AffineTransform myTransform) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean key(char key) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public Root getPanel() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public ArrayList<Integer> select(double x, double y, int myIndex,
			AffineTransform transform) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public ArrayList<Point2D> controls() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void setStyle(SO style) {
		SA contentsArray = style.getArray("contents");
		for(int i = 0; i < contentsArray.size(); i++){
			SO shapeObj = contentsArray.getSO(i);
			Drawable shape = (Drawable)shapeObj;
			shape.setStyle(shapeObj);
			contents.add(shape);
			models.add(null);
		}
		
		SO idleObj = style.getObj("idle");
		if(idleObj != null){
			int r = (int)idleObj.getDouble("r");
			int g = (int)idleObj.getDouble("g");
			int b = (int)idleObj.getDouble("b");
			idle = new Color(r, g, b);
		}
		
		SO hoverObj = style.getObj("hover");
		if(hoverObj != null){
			int r = (int)hoverObj.getDouble("r");
			int g = (int)hoverObj.getDouble("g");
			int b = (int)hoverObj.getDouble("b");
			hover = new Color(r, g, b);
		}
		
		SO activeObj = style.getObj("active");
		if(hoverObj != null){
			int r = (int)activeObj.getDouble("r");
			int g = (int)activeObj.getDouble("g");
			int b = (int)activeObj.getDouble("b");
			active = new Color(r, g, b);
		}
	}
	
	@Override
	public void paint(Graphics g) {
		// TODO Auto-generated method stub
		
	}
	
}
