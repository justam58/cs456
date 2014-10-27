package sparkClass.widget;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import listener.ActiveListener;
import listener.ModelListener;
import spark.data.SA;
import spark.data.SO;
import spark.data.SOReflect;
import sparkClass.Group;
import sparkClass.Root;
import sparkClass.shape.Line;
import sparkClass.shape.Rect;
import able.Dragable;
import able.Drawable;
import able.Interactable;
import able.Layout;
import able.Selectable;

public class ScrollV extends Group implements Interactable, Drawable, ModelListener, Layout {
	
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
	private double sliderHeight;
	private ArrayList<ActiveListener> listeners = new ArrayList<ActiveListener>();
	private Root root = null;
	
	private double top;
	private double bottom;
	private double left;
	private double right;
	
	private void updateState(boolean clicked, boolean hovered){
		for(int i = 0; i < listeners.size();i++){
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
	
	private void setSliderMaxAndMin(){
		if(ranger instanceof Rect){
			Rect rangeRect = (Rect)ranger;
			sliderMin = rangeRect.top;
			sliderMax = rangeRect.top+rangeRect.height-sliderHeight;
		}
		if(ranger instanceof Line){
			Line rangeLine = (Line)ranger;
			double yTop = Math.min(rangeLine.y1, rangeLine.y2);
			double yBot = Math.max(rangeLine.y1, rangeLine.y2);
			sliderMin = yTop;
			sliderMax = yBot-sliderHeight;
		}
	}
	
	private double valueToModel(double v){
		return max-((v-sliderMin)*(max-min)/(sliderMax-sliderMin));
	}
	
	private double valueFromModel(double v){
		return sliderMax-((v-min)*(sliderMax-sliderMin)/(max-min));
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
//				ActiveListener listener = (ActiveListener)shape;
//				listeners.add(listener);
//				if(state.equals("active")){
//					listener.stateChanged(active);
//				}
//				else if(state.equals("idle")){
//					listener.stateChanged(idle);
//				}
//			}
//			if(classVal != null && classVal.equals("range")){
//				Interactable rangerShape = (Interactable)shape;
//				ranger = rangerShape;
//			}
//			if(classVal != null && classVal.equals("slide")){
//				Dragable dragableSlider = (Dragable)shape;
//				slider = dragableSlider;
//				sliderHeight = slider.getSliderHeight();
//				setSliderMaxAndMin();
//			}
//		}
		
		root = getPanel();
		root.model.addListener(models, root.model, 0, this);
		
//		for(int j = 0; j < contents.size(); j++){
//			SOReflect shape = (SOReflect)contents.get(j);
//			String classVal = shape.getString("class");
//			if(classVal != null && classVal.equals("slide")){
//				Object value = root.model.getValue(models, root.model, 0);
//				if(value != null){
//					double modelValue = Double.valueOf(root.model.getValue(models, root.model, 0));
//					slider.moveTo(-1, valueFromModel(modelValue), sliderMax, sliderMin);
//				}
//			}
//		}
		
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
						d_dragStart = endp.getY();
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
			
			double d_delta = (endp.getY() - d_dragStart);
			d_dragStart = endp.getY();
			double newModelValue = valueToModel(slider.move(0, d_delta, sliderMax, sliderMin));
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
					if(classVal != null && classVal.equals("up") && models.size() > 0){
						String value = (String)root.model.getValue(models, root.model, 0);
						Double newValue = Double.valueOf(value)+step;
						if(newValue > max){
							newValue = max;
						}
						root.model = root.model.update(models, root.model, 0, newValue.toString());
						slider.moveTo(-1, valueFromModel(newValue), sliderMax, sliderMin);
					}
					else if(classVal != null && classVal.equals("down") && models.size() > 0){
						String value = (String)root.model.getValue(models, root.model, 0);
						Double newValue = Double.valueOf(value)-step;
						if(newValue < min){
							newValue = min;
						}
						root.model = root.model.update(models, root.model, 0, newValue.toString());
						slider.moveTo(-1, valueFromModel(newValue), sliderMax, sliderMin);
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
	public void modelChanged(String newValue) {
		try{
			double modelValue = Double.valueOf(newValue);
			if(modelValue != slider.getCurrentY()){
				slider.moveTo(-1, valueFromModel(modelValue), sliderMax, sliderMin);
			}
		}
		catch(NumberFormatException e){}
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
		this.left = left;
		this.right = right;
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
		return Double.MAX_VALUE;
	}

	@Override
	public void setVBounds(double top, double bottom) {
		this.top = top;
		this.bottom = bottom;
	}
	
	@Override
	public void paint(Graphics g){
		
	}

}
