package sparkClass.widget;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import listener.ActiveListener;
import spark.data.SA;
import spark.data.SO;
import sparkClass.Group;
import sparkClass.Root;
import sparkClass.shape.Rect;
import sparkClass.shape.Text;
import able.Drawable;
import able.Interactable;
import able.Layout;
import able.Selectable;

public class Button extends Group implements Drawable, Interactable, Layout {
	
	// Button{ label:"my label", contents:[...],state:"idle", idle:{r:0,g:0,b:0}, hover:{r:100,g:100,b:100}, active:{r:255,g:255,b:0}, model:[...], value:10 } 
	public String label;
	public ArrayList<String> models = new ArrayList<String>(); 
	public String state;
	public Color idle;
	public Color hover;
	public Color active;
	public double value;
	
	private ArrayList<ActiveListener> listeners = new ArrayList<ActiveListener>();
	private Root root = null;
	
	private Rect buttonBackground;
	private Text buttonLabel;
	
	private FontMetrics fontMetrics;
	private double marginWidth = 10;
	private double marginHeight = 10;
	
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
						root.updateModel(models, root.model, 0, String.valueOf(value));
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
		
		Rect rect = new Rect(0,0,50,50);
		Text text = new Text(label,0,0,"serif",15,false,-1,models);
		
		contents.add(rect);
		contents.add(text);
		
		buttonBackground = rect;
		buttonLabel = text;
		
		ActiveListener listener = (ActiveListener)rect;
		listeners.add(listener);
		
		if(state.equals("idle")){
			listener.stateChanged(idle);
		}

	}
	
	@Override
	public double getMinWidth() {
		return fontMetrics.stringWidth(label);
	}

	@Override
	public double getDesiredWidth() {
		return fontMetrics.stringWidth(label) + marginWidth*2;
	}

	@Override
	public double getMaxWidth() {
		return fontMetrics.stringWidth(label) + marginWidth*2;
	}

	@Override
	public void setHBounds(double left, double right) {
		double width = right-left;
		buttonBackground.left = left;
		buttonBackground.width = width;
		if(width > getDesiredWidth()){
			buttonBackground.width = getDesiredWidth();
			width = getDesiredWidth();
		}
//		System.out.println("button h " + left + ", " + width);
		int contentWidth = fontMetrics.stringWidth(label);
		if(contentWidth > width){
			// TODO what?
		}
		else{
			double spaceLeft = width - contentWidth;
			if(spaceLeft > (marginWidth*2)){
				buttonLabel.x = left + marginWidth;
			}
			else{
				buttonLabel.x = left + spaceLeft/2;
			}
		}
	}

	@Override
	public double getMinHeight() {
		return fontMetrics.getHeight();
	}

	@Override
	public double getDesiredHeight() {
		return fontMetrics.getHeight()+marginHeight*2;
	}

	@Override
	public double getMaxHeight() {
		return fontMetrics.getHeight()+marginHeight*2;
	}

	@Override
	public void setVBounds(double top, double bottom) {
		double height = bottom-top;
//		System.out.println("button v " + top + ", " + height);
		buttonBackground.top = top;
		buttonBackground.height = height;
		if(height > getDesiredHeight()){
			buttonBackground.height = getDesiredHeight();
			height = getDesiredHeight();
		}
		int contentHeight = fontMetrics.getHeight();
//		System.out.println("contentHeight " + contentHeight);
		if(contentHeight > height){
			// TODO what?
		}
		else{
			double spaceLeft = height - contentHeight;
//			System.out.println("spaceLeft " + spaceLeft);
			buttonLabel.y = top + contentHeight + spaceLeft/2;
		}
	}
	
	@Override
	public void paint(Graphics g) {
		for(int i = 0; i < contents.size(); i++){
            contents.get(i).paint(g);
		}
		Graphics2D g2d = (Graphics2D)g;
		fontMetrics = buttonLabel.setFontMetrics(g2d);
		buttonLabel.setBoundingBox(g2d);
	}

}
