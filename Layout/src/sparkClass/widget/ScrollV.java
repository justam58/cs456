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
import sparkClass.shape.Rect;
import sparkClass.shape.Polyline;
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
	
	private Selectable upper;
	private Selectable downer;
	private Rect activer;
	private double barWidth = 10;
	private double margin = 2;
	
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
//		Rect{ class:"active", left:10,top:10, width:20, height:190, fill:{r:255,g:255,b:255}},
//		Rect{ class:"range", left:10, top:32, width:20, height:130, fill:{r:200,g:200,b:200} },
//		Polyline{ class:"up", points:[{x:12,y:30},{x:20,y:12},{x:28,y:30}], color:{r:0,g:0,b:0}, thickness: 3 },
//		Polyline{ class:"down", points:[{x:12,y:170},{x:20,y:198},{x:28,y:170}], color:{r:0,g:0,b:0}, thickness: 3 },
//		Rect{ class:"slide", left:10, top:100, width:20, height:30,fill:{r:0,g:0,b:0} }
		
		
		SA modelsObj = style.getArray("model");
		if(modelsObj != null){
			for(int i = 0; i < modelsObj.size(); i++){
				models.add(modelsObj.get(i).toString().replace("\"", ""));
			}
		}
		
		root = getPanel();

		Rect rectActive = new Rect(0,0,barWidth,100);
		Rect rectRange = new Rect(0,barWidth,barWidth,80);
		Rect rectSlide = new Rect(0,barWidth,barWidth,barWidth);
		Polyline arrowUp = new Polyline(new int[]{(int) margin,(int) (barWidth/margin),(int) (barWidth-margin)},new int[]{(int) (barWidth-margin),(int) margin,(int) (barWidth-margin)},3);
		Polyline arrowDown = new Polyline(new int[]{(int) margin,(int) (barWidth/2),(int) (barWidth-margin)},new int[]{92,98,92},3);
		
		contents.add(rectActive);
		contents.add(rectRange);
		contents.add(rectSlide);
		contents.add(arrowUp);
		contents.add(arrowDown);
		
		root.model.addListener(models, root.model, 0, this);
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
		sliderHeight = slider.getSliderHeight();
		setSliderMaxAndMin();
		
		upper = (Selectable)arrowUp;
		downer = (Selectable)arrowDown;
		activer = rectActive;
		
		Object value = root.model.getValue(models, root.model, 0);
		if(value != null){
			double modelValue = Double.valueOf(root.model.getValue(models, root.model, 0));
			slider.moveTo(-1, valueFromModel(modelValue), sliderMax, sliderMin);
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
					if(content.equals((Selectable)upper) && models.size() > 0){
						String value = (String)root.model.getValue(models, root.model, 0);
						Double newValue = Double.valueOf(value)+step;
						if(newValue > max){
							newValue = max;
						}
						root.model = root.model.update(models, root.model, 0, newValue.toString());
						slider.moveTo(-1, valueFromModel(newValue), sliderMax, sliderMin);
					}
					else if(content.equals((Selectable)downer) && models.size() > 0){
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
		return barWidth;
	}

	@Override
	public double getDesiredWidth() {
		return barWidth;
	}

	@Override
	public double getMaxWidth() {
		return barWidth;
	}

	@Override
	public void setHBounds(double left, double right) {
		activer.left = left;
		activer.width = barWidth;
	}

	@Override
	public double getMinHeight() {
		return 50;
	}

	@Override
	public double getDesiredHeight() {
		return 100;
	}

	@Override
	public double getMaxHeight() {
		return Double.MAX_VALUE;
	}

	@Override
	public void setVBounds(double top, double bottom) {
		double height = bottom-top;
		activer.top = top;
		activer.height = height;
		
		Rect rangerRect = (Rect)ranger;
		rangerRect.top = top+barWidth;
		rangerRect.height = height - barWidth*2;
		
		Polyline arrowUp = (Polyline)upper;
		arrowUp.setYPoints(new int[]{(int) (top+barWidth-margin),(int) (top+margin),(int) (top+barWidth-margin)});
		Polyline arrowDown = (Polyline)downer;
		arrowDown.setYPoints(new int[]{(int) (bottom-barWidth+margin), (int) (bottom-margin), (int) (bottom-barWidth+margin)});
		
		setSliderMaxAndMin();
		
		Object value = root.model.getValue(models, root.model, 0);
		if(value != null){
			double modelValue = Double.valueOf(root.model.getValue(models, root.model, 0));
			slider.moveTo(-1, valueFromModel(modelValue), sliderMax, sliderMin);
		}
	}

}
