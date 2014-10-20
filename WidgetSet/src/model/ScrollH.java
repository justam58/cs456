package model;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import spark.data.SA;
import spark.data.SO;
import spark.data.SOReflect;
import spark.data.SParented;
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
	private int slider = -1;
	private int ranger = -1;
	private double d_dragStart;
	
	private void updateState(boolean clicked, boolean hovered){
		for(int j = 0; j < contents.size(); j++){
			SOReflect shape = (SOReflect)contents.get(j);
			String classVal = shape.getString("class");
			if(classVal != null && classVal.equals("active")){
				Interactable activeShape = (Interactable)shape;
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
		getPanel();
	}
	
	private double valueToModel(double v, double sliderHeight){
		Interactable rangerShape = (Interactable)contents.get(ranger);
		double sliderMax = 10;
		double sliderMin = 110;
		if(rangerShape instanceof Rect){
			Rect rangeRect = (Rect)rangerShape;
			sliderMin = rangeRect.top;
			sliderMax = rangeRect.top+rangeRect.height-sliderHeight;
		}
		if(rangerShape instanceof Line){
			Line rangeLine = (Line)rangerShape;
			double yTop = Math.min(rangeLine.y1, rangeLine.y2);
			double yBot = Math.max(rangeLine.y1, rangeLine.y2);
			sliderMin = yTop;
			sliderMax = yBot-sliderHeight;
		}
		return max-((v-sliderMin)*(max-min)/(sliderMax-sliderMin));
	}
	
	private double valueFromModel(double v, double sliderHeight){
		Interactable rangerShape = (Interactable)contents.get(ranger);
		double sliderMax = 10;
		double sliderMin = 110;
		if(rangerShape instanceof Rect){
			Rect rangeRect = (Rect)rangerShape;
			sliderMin = rangeRect.top;
			sliderMax = rangeRect.top+rangeRect.height-sliderHeight;
		}
		if(rangerShape instanceof Line){
			Line rangeLine = (Line)rangerShape;
			double yTop = Math.min(rangeLine.y1, rangeLine.y2);
			double yBot = Math.max(rangeLine.y1, rangeLine.y2);
			sliderMin = yTop;
			sliderMax = yBot-sliderHeight;
		}
		return sliderMax-((v-min)*(sliderMax-sliderMin)/(max-min));
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
				Interactable activeShape = (Interactable)shape;
				if(state.equals("active")){
					activeShape.changeBackgroundColor(active);
				}
				else if(state.equals("idle")){
					activeShape.changeBackgroundColor(idle);
				}
			}
			if(classVal != null && classVal.equals("range")){
				ranger = j;
			}
			if(classVal != null && classVal.equals("slide")){
				slider = j;
			}
		}
		
		for(int j = 0; j < contents.size(); j++){
			SOReflect shape = (SOReflect)contents.get(j);
			String classVal = shape.getString("class");
			if(classVal != null && classVal.equals("slide")){
				Interactable sliderShape = (Interactable)shape;
				Root root = getPanel();
				double modelValue = Double.valueOf(root.model.getValue(models, root.model, 0));
				sliderShape.moveTo(-1, valueFromModel(modelValue,sliderShape.getSliderHeight()));
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
		for(int i = 0; i < contents.size(); i++){
			if(contents.get(i) instanceof Selectable){
				Selectable content = (Selectable)contents.get(i);
				ArrayList<Integer> selectPath = content.select(x, y, i, myTransform);
				if(selectPath != null){
					updateState(true, false);
					SOReflect shape = (SOReflect)content;
					String classVal = shape.getString("class");
//					System.out.println(classVal);
					if(classVal != null && classVal.equals("slide")){
						dragging = true;
						slider = i;
						Point2D endp = new Point2D.Double();
						myTransform.transform(new Point2D.Double(x,y), endp);
						d_dragStart = endp.getY();
					}
					if(classVal != null && !classVal.equals("active") && !classVal.equals("range")){
						return true;
					}
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
			
			double d_delta = (endp.getY() - d_dragStart);
			d_dragStart = endp.getY();
			Interactable sliderShape = (Interactable)contents.get(slider);
			Interactable rangerShape = (Interactable)contents.get(ranger);
			double newValue = sliderShape.move(0, d_delta, rangerShape);
			double newModelValue = valueToModel(newValue,sliderShape.getSliderHeight());
			Root root = getPanel();
			root.model = root.model.update(models, root.model, 0, String.valueOf(newModelValue));
			state = "idle";
			updateState(true, false);
			return true;
		}
		for(int i = 0; i < contents.size(); i++){
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
		Root root = getPanel();
		for(int i = 0; i < contents.size(); i++){
			if(contents.get(i) instanceof Selectable){
				Selectable content = (Selectable)contents.get(i);
				ArrayList<Integer> selectPath = content.select(x, y, 0, myTransform);
				if(selectPath != null){
					updateState(false, true);
					SOReflect shape = (SOReflect)content;
					String classVal = shape.getString("class");
					if(classVal != null && classVal.equals("up") && models.size() > 0){
						String value = root.model.getValue(models, root.model, 0);
						Double newValue = Double.valueOf(value)+step;
						if(newValue > max){
							newValue = max;
						}
						root.model = root.model.update(models, root.model, 0, newValue.toString());
						Interactable sliderShape = (Interactable)contents.get(slider);
						sliderShape.moveTo(-1, valueFromModel(newValue,sliderShape.getSliderHeight()));
					}
					else if(classVal != null && classVal.equals("down") && models.size() > 0){
						String value = root.model.getValue(models, root.model, 0);
						Double newValue = Double.valueOf(value)-step;
						if(newValue < min){
							newValue = min;
						}
						root.model = root.model.update(models, root.model, 0, newValue.toString());
						Interactable sliderShape = (Interactable)contents.get(slider);
						sliderShape.moveTo(-1, valueFromModel(newValue,sliderShape.getSliderHeight()));
					}
				}
			}
		}
		state = "idle";
		root.model.print();
		return true;
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
	public void changeBackgroundColor(Color c) {
		// do nothing
	}

	@Override
	public void changeLabel(String label) {
		// do nothing
	}
	
	@Override
	public double move(double dx, double dy, Interactable range) {
		return 0;
	}
	
	@Override
	public double getSliderHeight() {
		return 0;
	}
	
	@Override
	public void moveTo(double x, double y) {
		// do nothing
	}

}
