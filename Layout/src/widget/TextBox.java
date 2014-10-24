package widget;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import listener.ActiveListener;
import listener.ModelListener;
import spark.data.SA;
import spark.data.SO;
import spark.data.SOReflect;
import spark.data.SParented;
import view.Layout;
import able.Drawable;
import able.Interactable;
import able.Selectable;

public class TextBox extends SOReflect implements Interactable, Drawable, ModelListener, Layout {
	
	// TextBox{ state:"idle", contents:[...], idle:{r:0,g:0,b:0}, hover:{r:100,g:100,b:100}, active:{r:255,g:255,b:0}, model:[...], desiredChars:10 }
	public String label;
	public ArrayList<Drawable> contents = new ArrayList<Drawable>(); // SArray of Drawable objects
	public ArrayList<String> models = new ArrayList<String>(); 
	public String state;
	public Color idle;
	public Color hover;
	public Color active;
	public boolean edit;
	public double cursor = -1;
	
	public double desiredChars;
	
	private Text textContent;
	private ArrayList<ActiveListener> listeners = new ArrayList<ActiveListener>();
	private Root root = null;
	
	private void updateState(boolean clicked, boolean hovered){
		for(int i = 0; i < listeners.size(); i++){
			ActiveListener listener = listeners.get(i);
			if(clicked){
				listener.stateChanged(active);
			}
			else if(!clicked && !state.equals("active") && hovered){ // hover
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
	public void setStyle(SO style) {
		SA contentsArray = style.getArray("contents");
		for(int i = 0; i < contentsArray.size(); i++){
			SO shapeObj = contentsArray.getSO(i);
			Drawable shape = (Drawable)shapeObj;
			shape.setStyle(shapeObj);
			contents.add(shape);
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
		
		SA modelsObj = style.getArray("model");
		if(modelsObj != null){
			for(int i = 0; i < modelsObj.size(); i++){
				models.add(modelsObj.get(i).toString().replace("\"", ""));
			}
		}
		
		root = getPanel();
		root.model.addListener(models, root.model, 0, this);
				
		for(int j = 0; j < contents.size(); j++){
			SOReflect shape = (SOReflect)contents.get(j);
			String classVal = shape.getString("class");
			if(classVal != null && classVal.equals("active")){
				ActiveListener listener = (ActiveListener)shape;
				listeners.add(listener);
				if(state.equals("idle")){
					listener.stateChanged(idle);
				}
			}
			if(classVal != null && classVal.equals("content")){
				textContent = (Text)shape;
				textContent.edit = this.edit;
				textContent.cursor = this.cursor;
				textContent.models = this.models;
				textContent.text = root.model.getValue(models, root.model, 0);
			}
		}
		
	}

	@Override
	public void paint(Graphics g) {
		for(int i = 0; i < contents.size(); i++){
			contents.get(i).paint(g);
		}
	}

	@Override
	public boolean mouseDown(double x, double y, AffineTransform myTransform) {
		for(int i = contents.size()-1; i >= 0; i--){
			if(contents.get(i) instanceof Selectable){
				Selectable content = (Selectable)contents.get(i);
				ArrayList<Integer> selectPath = content.select(x, y, 0, myTransform);
				if(selectPath != null){
					updateState(true, false);
					if(edit){
						textContent.editing(x);
					}
					return true;
				}
			}
		}
		state = "idle";
		updateState(false, false);
		textContent.cursor = -1;
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
	public void modelChanged(String newValue) {
		if(textContent.text != newValue){
			textContent.text = newValue;
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
