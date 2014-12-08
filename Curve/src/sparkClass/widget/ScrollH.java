package sparkClass.widget;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import listener.ActiveListener;
import listener.ModelListener;
import spark.data.SA;
import spark.data.SO;
import sparkClass.Group;
import sparkClass.Root;
import sparkClass.shape.Line;
import sparkClass.shape.Polyline;
import sparkClass.shape.Rect;
import able.Dragable;
import able.Drawable;
import able.Interactable;
import able.Layout;
import able.Selectable;

public class ScrollH extends Group implements Interactable, Drawable, ModelListener, Layout {
	
	//ScrollV{ state:"idle",contents:[...], idle:{r:0,g:0,b:0}, hover:{r:100,g:100,b:100}, active:{r:255,g:255,b:0}, model:[...], max:1.0, min:0.0, step:0.1}
	public ArrayList<String> models = new ArrayList<String>(); 
	public String state;
	public double min;
	public double max;
	public double step = 1;
	public Color idle;
	public Color hover;
	public Color active;
	
	private boolean dragging = false;
	private Dragable slider;
	private Interactable ranger;
	private double sliderMax;
	private double sliderMin;
	private double sliderWidth;
	private ArrayList<ActiveListener> listeners = new ArrayList<ActiveListener>();
	private Root root = null;
	
	private Selectable upper;
	private Selectable downer;
	private Rect activer;
	private double barWidth = 20;
	private double margin = 5;
	
	private double currentValue = -1;
	
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
		
		SA modelsObj = style.getArray("model");
		if(modelsObj != null){
			for(int i = 0; i < modelsObj.size(); i++){
				models.add(modelsObj.get(i).toString().replace("\"", ""));
			}
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
		
		root = getPanel();
		
		Rect rectActive = new Rect(0,0,100,barWidth);
		Rect rectRange = new Rect(barWidth,0,80,barWidth);
		Rect rectSlide = new Rect(barWidth,0,barWidth,barWidth);
		Polyline arrowUp = new Polyline(new int[]{92,98,92},new int[]{(int) margin,(int) (barWidth/2),(int) (barWidth-margin)},3);
		Polyline arrowDown = new Polyline(new int[]{(int) (barWidth-margin),(int) margin,(int) (barWidth-margin)},new int[]{(int) margin,(int) (barWidth/margin),(int) (barWidth-margin)},3);
		
		rectRange.fill = Color.LIGHT_GRAY;
		arrowUp.color = Color.BLACK;
		arrowDown.color = Color.BLACK;
		rectSlide.fill = Color.BLACK;
		
		contents.add(rectActive);
		contents.add(rectRange);
		contents.add(rectSlide);
		contents.add(arrowUp);
		contents.add(arrowDown);
		
		root.addModelListener(models, root.model, 0, this);
		ActiveListener listener = (ActiveListener)rectActive;
		listeners.add(listener);
		
		if(state.equals("active")){
			listener.stateChanged(active);
		}
		else if(state.equals("idle")){
			listener.stateChanged(idle);
		}
		
		Interactable rangerShape = (Interactable)rectRange;
		ranger = rangerShape;
		
		Dragable dragableSlider = (Dragable)rectSlide;
		slider = dragableSlider;
		sliderWidth = slider.getSliderWidth();
		setSliderMaxAndMin();
		
		upper = (Selectable)arrowUp;
		downer = (Selectable)arrowDown;
		activer = rectActive;
		
		Object value = root.getModelValue(models, root.model, 0);
		if(value != null){
			double modelValue = Double.valueOf(root.getModelValue(models, root.model, 0));
			slider.moveTo(valueFromModel(modelValue), -1, sliderMax, sliderMin);
		}
		
		if(state.equals("idle")){
			listener.stateChanged(idle);
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
					if(content.equals((Selectable)slider)){
						dragging = true;
						Point2D endp = new Point2D.Double();
						myTransform.transform(new Point2D.Double(x,y), endp);
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

			currentValue = slider.moveTo(endp.getX(), -1, sliderMax, sliderMin);
			double newModelValue = valueToModel(currentValue);
			root.updateModel(models, root.model, 0, String.valueOf(newModelValue));
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
					if(content.equals((Selectable)upper)){
						if(models.size() > 0){
							String value = (String)root.getModelValue(models, root.model, 0);
							Double newValue = Double.valueOf(value)+step;
							if(newValue > max){
								newValue = max;
							}
							root.updateModel(models, root.model, 0, newValue.toString());
							slider.moveTo(valueFromModel(newValue), -1, sliderMax, sliderMin);
						}
						else{
							currentValue += step;
							if(currentValue > sliderMax){
								currentValue = sliderMax;
							}
							slider.moveTo(currentValue, -1, sliderMax, sliderMin);
						}
					}
					else if(content.equals((Selectable)downer)){
						if(models.size() > 0){
							String value = (String)root.getModelValue(models, root.model, 0);
							Double newValue = Double.valueOf(value)-step;
							if(newValue < min){
								newValue = min;
							}
							root.updateModel(models, root.model, 0, newValue.toString());
							slider.moveTo(valueFromModel(newValue), -1, sliderMax, sliderMin);
						}
						else{
							currentValue -= step;
							if(currentValue < sliderMin){
								currentValue = sliderMin;
							}
							slider.moveTo(currentValue, -1, sliderMax, sliderMin);
						}
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
			if(modelValue != slider.getCurrentX()){
				slider.moveTo(valueFromModel(modelValue), -1, sliderMax, sliderMin);
			}
		}
		catch(NumberFormatException e){}
	}

	@Override
	public double getMinWidth() {
		return barWidth*4;
	}

	@Override
	public double getDesiredWidth() {
		return 200;
	}

	@Override
	public double getMaxWidth() {
		return 1000000;
	}

	@Override
	public void setHBounds(double left, double right) {
		double width = right-left;
//		System.out.println("scrollh h " + left + ", " + width);
		
		if(width < getMinWidth()){
			width = getMinWidth();
			right = left + width;
		}
		
		activer.left = left;
		activer.width = width;
		
		Rect rangerRect = (Rect)ranger;
		rangerRect.left = left+barWidth;
		rangerRect.width = width - barWidth*2;
		
		Polyline arrowUp = (Polyline)upper;
		arrowUp.setXPoints(new int[]{(int) (right-barWidth+margin), (int) (right-margin), (int) (right-barWidth+margin)});
		Polyline arrowDown = (Polyline)downer;
		arrowDown.setXPoints(new int[]{(int) (left+barWidth-margin),(int) (left+margin),(int) (left+barWidth-margin)});
		
		double oldMin = sliderMin;
		double oldMax = sliderMax;
		
		setSliderMaxAndMin();
		
		Object value = root.getModelValue(models, root.model, 0);
		if(value != null){
			double modelValue = Double.valueOf(root.getModelValue(models, root.model, 0));
			slider.moveTo(valueFromModel(modelValue), -1, sliderMax, sliderMin);
		}
		else{
			if(currentValue == -1){
				currentValue = sliderMin;
			}
			else{
				currentValue = (currentValue*(sliderMax-sliderMin))/(oldMax-oldMin);
			}
			slider.moveTo(currentValue, -1, sliderMax, sliderMin);
		}
	}

	@Override
	public double getMinHeight() {
		return barWidth;
	}

	@Override
	public double getDesiredHeight() {
		return barWidth;
	}

	@Override
	public double getMaxHeight() {
		return barWidth;
	}

	@Override
	public void setVBounds(double top, double bottom) {
//		System.out.println("scrollh v " + top + ", " + (bottom-top));
		
		activer.top = top;
		activer.height = barWidth;
		
		Polyline arrowUp = (Polyline)upper;
		arrowUp.setYPoints(new int[]{(int) (top+margin),(int) (top+barWidth/2),(int) (top+barWidth-margin)});
		Polyline arrowDown = (Polyline)downer;
		arrowDown.setYPoints(new int[]{(int) (top+margin),(int) (top+barWidth/2),(int) (top+barWidth-margin)});
		
		Rect rangerRect = (Rect)ranger;
		rangerRect.top = top;
		
		Rect sliderRect = (Rect)slider;
		sliderRect.top = top;
	}

}
