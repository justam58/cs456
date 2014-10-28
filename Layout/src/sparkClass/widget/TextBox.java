package sparkClass.widget;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import listener.ActiveListener;
import listener.ModelListener;
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

public class TextBox extends Group implements Interactable, Drawable, ModelListener, Layout {
	
	// TextBox{ state:"idle", contents:[...], idle:{r:0,g:0,b:0}, hover:{r:100,g:100,b:100}, active:{r:255,g:255,b:0}, model:[...], desiredChars:10 }
	public String label;
	public ArrayList<String> models = new ArrayList<String>(); 
	public String state;
	public Color idle;
	public Color hover;
	public Color active;
	public boolean edit;
	public double cursor = -1;
	
	public double desiredChars;
	
	private Text textContent;
	private Rect textBox;
	private ArrayList<ActiveListener> listeners = new ArrayList<ActiveListener>();
	private Root root = null;
	
	private FontMetrics fontMetrics;
	private double marginWidth = 10;
	private double marginHeight = 5;
	
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
		
		SA modelsObj = style.getArray("model");
		if(modelsObj != null){
			for(int i = 0; i < modelsObj.size(); i++){
				models.add(modelsObj.get(i).toString().replace("\"", ""));
			}
		}
		
		root = getPanel();
		
		Rect rect = new Rect(0,0,200,30);
		Text text = new Text(root.model.getValue(models, root.model, 0),10,25,"serif",15,edit,cursor,models);
		contents.add(rect);
		contents.add(text);
		textBox = rect;
		textContent = text;
		textContent.root = root;
		
		root.model.addListener(models, root.model, 0, this);
		ActiveListener listener = (ActiveListener)rect;
		listeners.add(listener);
		
		
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
		
		if(state.equals("idle")){
			listener.stateChanged(idle);
		}
		
		if(state.equals("active")){
			updateState(true, false);
			if(edit){
				textContent.editing(cursor, true);
			}
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
						textContent.editing(x,false);
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
	public void modelChanged(String newValue) {
		if(textContent.text != newValue){
			textContent.text = newValue;
		}
	}

	@Override
	public double getMinWidth() {
		String wString = "";
		for(int i = 0; i < desiredChars; i++){
			wString += "W";
		}
		int stringWidth = fontMetrics.stringWidth(wString);
		return stringWidth;
	}

	@Override
	public double getDesiredWidth() {
		String wString = "";
		for(int i = 0; i < desiredChars; i++){
			wString += "W";
		}
		int stringWidth = fontMetrics.stringWidth(wString);
		return stringWidth+marginWidth*2;
	}

	@Override
	public double getMaxWidth() {
		String wString = "";
		for(int i = 0; i < desiredChars; i++){
			wString += "W";
		}
		int stringWidth = fontMetrics.stringWidth(wString);
		return stringWidth+marginWidth*2;
	}

	@Override
	public void setHBounds(double left, double right) {
		double width = right-left;
		textBox.left = left;
		textBox.width = width;
		if(width > getDesiredWidth()){
			textBox.width = getDesiredWidth();
			width = getDesiredWidth();
		}
		System.out.println("textbox h " + left + ", " + width);
		int contentWidth = fontMetrics.stringWidth(textContent.text);
		if(contentWidth > width){
			// TODO what?
		}
		else{
			double spaceLeft = width - contentWidth;
			if(spaceLeft > (marginWidth*2)){
				textContent.x = left + marginWidth;
			}
			else{
				textContent.x = left + spaceLeft/2;
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
		System.out.println("textbox v " + top + ", " + height);
		textBox.top = top;
		textBox.height = height;
		if(height > getDesiredHeight()){
			textBox.height = getDesiredHeight();
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
			textContent.y = top + contentHeight + spaceLeft/2;
		}
	}
	
	@Override
	public void paint(Graphics g) {
		for(int i = 0; i < contents.size(); i++){
            contents.get(i).paint(g);
		}
		Graphics2D g2d = (Graphics2D)g;
		fontMetrics = textContent.setFontMetrics(g2d);
		textContent.setBoundingBox(g2d);
	}

}
