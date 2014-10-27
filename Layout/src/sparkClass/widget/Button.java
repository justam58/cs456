package sparkClass.widget;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import listener.ActiveListener;
import spark.data.SA;
import spark.data.SO;
import spark.data.SOReflect;
import spark.data.SParented;
import sparkClass.Root;
import sparkClass.shape.Text;
import able.Drawable;
import able.Interactable;
import able.Layout;
import able.Selectable;

public class Button extends SOReflect implements Drawable, Interactable, Layout {
	
	// Button{ label:"my label", contents:[...],state:"idle", idle:{r:0,g:0,b:0}, hover:{r:100,g:100,b:100}, active:{r:255,g:255,b:0}, model:[...], value:10 } 
	public String label;
	public ArrayList<Drawable> contents = new ArrayList<Drawable>(); // SArray of Drawable objects
	public ArrayList<String> models = new ArrayList<String>(); 
	public String state;
	public Color idle;
	public Color hover;
	public Color active;
	public double value;
	
	private ArrayList<ActiveListener> listeners = new ArrayList<ActiveListener>();
	private Root root = null;
	
	private void updateState(boolean clicked, boolean hovered){
		for(int i = 0; i < listeners.size(); i++){
			ActiveListener listener = listeners.get(i);
			if(clicked && state.equals("idle")){
				listener.stateChanged(active);
			}
			else if(clicked && state.equals("active")){
				listener.stateChanged(idle);
			}
			else if(!clicked && hovered){ // hover
				listener.stateChanged(hover);
			}
			else if(!clicked && !hovered){ //idle or active
				if(state.equals("idle")){
					listener.stateChanged(idle);
				}
				else{
					listener.stateChanged(active);
				}
			}
		}
		if(clicked && state.equals("idle")){
			state = "active";
		}
		root.repaint();
	}
	
	@Override
	public boolean mouseDown(double x, double y, AffineTransform myTransform) {
		for(int i = contents.size()-1; i >= 0; i--){
			if(contents.get(i) instanceof Selectable){
				Selectable content = (Selectable)contents.get(i);
				ArrayList<Integer> selectPath = content.select(x, y, 0, myTransform);
				if(selectPath != null){
					updateState(true, false);
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean mouseMove(double x, double y, AffineTransform myTransform) {
		for(int i = contents.size()-1; i >= 0; i--){
			if(contents.get(i) instanceof Selectable){
				Selectable shape = (Selectable)contents.get(i);
				ArrayList<Integer> selectPath = shape.select(x, y, 0, myTransform);
				if(selectPath != null){
					updateState(false, true);
					return true;
				}
			}
		}
		updateState(false, false);
		return false;
	}
	
	@Override
	public boolean mouseUp(double x, double y, AffineTransform myTransform) {
		for(int i = contents.size()-1; i >= 0; i--){
			if(contents.get(i) instanceof Selectable){
				Selectable shape = (Selectable)contents.get(i);
				ArrayList<Integer> selectPath = shape.select(x, y, 0, myTransform);
				if(selectPath != null){
					updateState(false, true);
					if(state.equals("active") && models.size() > 0){
						root.model = root.model.update(models, root.model, 0, String.valueOf(value));
					}
					state = "idle";
					return true;
				}
			}
		}
		state = "idle";
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
//		SA contentsArray = style.getArray("contents");
//		for(int i = 0; i < contentsArray.size(); i++){
//			SO shapeObj = contentsArray.getSO(i);
//			Drawable shape = (Drawable)shapeObj;
//			shape.setStyle(shapeObj);
//			contents.add(shape);
//		}
		
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
		
		SA modelsObj = style.getArray("model");
		if(modelsObj != null){
			for(int i = 0; i < modelsObj.size(); i++){
				models.add(modelsObj.get(i).toString().replace("\"", ""));
			}
		}
		
//		for(int j = 0; j < contents.size(); j++){
//			SOReflect shape = (SOReflect)contents.get(j);
//			String classVal = shape.getString("class");
//			if(classVal != null && classVal.equals("active")){
//				ActiveListener activeShape = (ActiveListener)shape; 
//				listeners.add(activeShape);
//				if(state.equals("active")){
//					activeShape.stateChanged(active);
//				}
//				else{
//					activeShape.stateChanged(idle);
//				}
//			}
//			if(classVal != null && classVal.equals("label")){
//				Text activeShape = (Text)shape;
//				activeShape.changeLabel(label);
//			}
//		}
		
		root = getPanel();
	}
	
	@Override
	public void paint(Graphics g) {
		for(int i = 0; i < contents.size(); i++){
			contents.get(i).paint(g);
		}
	}

	@Override
	public double getMinWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getDesiredWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getMaxWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setHBounds(double left, double right) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double getMinHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getDesiredHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getMaxHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setVBounds(double top, double bottom) {
		// TODO Auto-generated method stub
		
	}

}
