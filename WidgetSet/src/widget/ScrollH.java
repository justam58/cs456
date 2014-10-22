package widget;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import spark.data.SA;
import spark.data.SO;
import spark.data.SOReflect;
import spark.data.SParented;
import able.ColorChangable;
import able.Dragable;
import able.Drawable;
import able.Interactable;
import able.Selectable;

public class ScrollH extends SOReflect implements Interactable, Drawable {
	
	//ScrollV{ state:"idle",contents:[...], idle:{r:0,g:0,b:0}, hover:{r:100,g:100,b:100}, active:{r:255,g:255,b:0}, model:[...], max:1.0, min:0.0, step:0.1}
	public ArrayList<Drawable> contents = new ArrayList<Drawable>();
	public ArrayList<String> models = new ArrayList<String>(); 
	public String state;
	public double min;
	public double max;
	public double step;
	public Color idle;
	public Color hover;
	public Color active;
	
	private boolean dragging = false;
	private Dragable slider;
	private Interactable ranger;
	private double d_dragStart;
	private double sliderMax;
	private double sliderMin;
	private double sliderWidth;
	
	private void updateState(boolean clicked, boolean hovered){
		for(int j = 0; j < contents.size(); j++){
			SOReflect shape = (SOReflect)contents.get(j);
			String classVal = shape.getString("class");
			if(classVal != null && classVal.equals("active")){
				ColorChangable activeShape = (ColorChangable)shape;
				if(clicked && state.equals("idle")){
					activeShape.changeBackgroundColor(active);
				}
				else if(clicked && state.equals("active")){
					activeShape.changeBackgroundColor(idle);
				}
				else if(!clicked && hovered){ // hover
					activeShape.changeBackgroundColor(hover);
				}
				else if(!clicked && !hovered){ //idle or active
					if(state.equals("idle")){
						activeShape.changeBackgroundColor(idle);
					}
					else{
						activeShape.changeBackgroundColor(active);
					}
				}
			}
		}
		if(clicked && state.equals("idle")){
			state = "active";
		}
		getPanel().repaint();
	}
	
	private void setSliderMaxAndMin(){
		if(ranger instanceof Rect){
			Rect rangeRect = (Rect)ranger;
			sliderMin = rangeRect.left;
			sliderMax = rangeRect.left+rangeRect.width-sliderWidth;
		}
		if(ranger instanceof Line){
			Line rangeLine = (Line)ranger;
			double xRight = Math.min(rangeLine.x1, rangeLine.x2);
			double yLeft = Math.max(rangeLine.x1, rangeLine.x2);
			sliderMin = yLeft;
			sliderMax = xRight-sliderWidth;
		}
	}
	
	private double valueToModel(double v){
		return ((v-sliderMin)*(max-min)/(sliderMax-sliderMin))+min;
	}
	
	private double valueFromModel(double v){
		return ((v-min)*(sliderMax-sliderMin)/(max-min))+sliderMin;
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
		
		
		for(int j = 0; j < contents.size(); j++){
			SOReflect shape = (SOReflect)contents.get(j);
			String classVal = shape.getString("class");
			if(classVal != null && classVal.equals("active")){
				ColorChangable activeShape = (ColorChangable)shape;
				if(state.equals("active")){
					activeShape.changeBackgroundColor(active);
				}
				else if(state.equals("idle")){
					activeShape.changeBackgroundColor(idle);
				}
			}
			if(classVal != null && classVal.equals("range")){
				Interactable rangerShape = (Interactable)shape;
				ranger = rangerShape;
			}
			if(classVal != null && classVal.equals("slide")){
				Dragable dragableSlider = (Dragable)shape;
				slider = dragableSlider;
				sliderWidth = slider.getSliderWidth();
				setSliderMaxAndMin();
			}
		}
		
		for(int j = 0; j < contents.size(); j++){
			SOReflect shape = (SOReflect)contents.get(j);
			String classVal = shape.getString("class");
			if(classVal != null && classVal.equals("slide")){
				Root root = getPanel();
				Object value = root.model.getValue(models, root.model, 0);
				if(value != null){
					double modelValue = (Double)(root.model.getValue(models, root.model, 0));
					slider.moveTo(valueFromModel(modelValue), -1);
				}
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
				ArrayList<Integer> selectPath = content.select(x, y, i, myTransform);
				if(selectPath != null){
					updateState(true, false);
					SOReflect shape = (SOReflect)content;
					String classVal = shape.getString("class");
					if(classVal != null && classVal.equals("slide")){
						dragging = true;
						Point2D endp = new Point2D.Double();
						myTransform.transform(new Point2D.Double(x,y), endp);
						d_dragStart = endp.getX();
					}
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean mouseMove(double x, double y, AffineTransform myTransform) {
		if(dragging){
			Point2D endp = new Point2D.Double();
			myTransform.transform(new Point2D.Double(x,y), endp);
			
			double d_delta = (endp.getX() - d_dragStart);
			d_dragStart = endp.getX();
			double newModelValue = valueToModel(slider.move(d_delta, 0, sliderMax, sliderMin));
			Root root = getPanel();
			root.model = root.model.update(models, root.model, 0, String.valueOf(newModelValue));
			state = "idle";
			updateState(true, false);
			return true;
		}
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
		dragging = false;
		for(int i = contents.size()-1; i >= 0; i--){
			if(contents.get(i) instanceof Selectable){
				Selectable content = (Selectable)contents.get(i);
				ArrayList<Integer> selectPath = content.select(x, y, 0, myTransform);
				if(selectPath != null){
					updateState(false, true);
					SOReflect shape = (SOReflect)content;
					String classVal = shape.getString("class");
					Root root = getPanel();
					if(classVal != null && classVal.equals("up") && models.size() > 0){
						String value = (String)root.model.getValue(models, root.model, 0);
						Double newValue = Double.valueOf(value)+step;
						if(newValue > max){
							newValue = max;
						}
						root.model = root.model.update(models, root.model, 0, newValue.toString());
						slider.moveTo(valueFromModel(newValue), -1);
					}
					else if(classVal != null && classVal.equals("down") && models.size() > 0){
						String value = (String)root.model.getValue(models, root.model, 0);
						Double newValue = Double.valueOf(value)-step;
						if(newValue < min){
							newValue = min;
						}
						root.model = root.model.update(models, root.model, 0, newValue.toString());
						slider.moveTo(valueFromModel(newValue), -1);
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

}
