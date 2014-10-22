package widget;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import model.TreeNode;
import able.Drawable;
import able.Interactable;
import spark.data.SA;
import spark.data.SO;
import spark.data.SOReflect;
import spark.data.SV;
import view.ContentPanel;

public class Root extends SOReflect implements Drawable, Interactable {
	
    private ContentPanel contentPanel = ContentPanel.getInstance();
	
	public ArrayList<Drawable> contents = new ArrayList<Drawable>(); // SArray of Drawable objects
	public double sx = 1;
	public double sy = 1;
	public double rotate; // in degrees counter clockwise.
	public double tx;
	public double ty;
	
	public Interactable focus = null;
	
	public TreeNode model = new TreeNode("","");
	
	public void setKeyFocus(Interactable focus){
		// When this method is called on a Root object 
		// it saves the pointer to the focus object
		// but not as a SPARK attribute. 
		this.focus = focus;
	}
	
	public void releaseKeyFocus(){
		// Sets the key focus to null.
		this.focus = null;
	}
	
	public void repaint(){
		contentPanel.repaint();
	}
	
	private AffineTransform getTransform(){
		AffineTransform atf = new AffineTransform();
		atf.scale(1/sx, 1/sy);
		atf.rotate(Math.toRadians(rotate));
		atf.translate(-tx, -ty);
		return atf;
	} 
	
	@Override
	public void setStyle(SO style){
		SV modelValue = style.get("model");
		model = build(model,"model",modelValue);
		model.print();
		
		SA contentsArray = style.getArray("contents");
		for(int i = 0; i < contentsArray.size(); i++){
			SO shapeObj = contentsArray.getSO(i);
			Drawable shape = (Drawable)shapeObj;
			shape.setStyle(shapeObj);
			contents.add(shape);
		}
	}
	
	private TreeNode build(TreeNode current, String name, SV value){
		if(value.isSO()){
			SO obj = value.getSO();
			current = buildObj(current,obj);
		}
		else if(value.isSA()){
			SA array = value.getSA();
			current = buildArray(current,array);
		}
		else if(value.isBoolean())
		{
			current = new TreeNode(name, String.valueOf(value.isTrue()));
		}
		else if(value.isString())
		{
			current = new TreeNode(name, String.valueOf(value.getString()));
		}
		else if(value.isLong())
		{
			current = new TreeNode(name, String.valueOf(value.getLong()));
		}
		else if(value.isDouble())
		{
			current = new TreeNode(name, String.valueOf(value.getDouble()));
		}
		return current;
	}
	
	private TreeNode buildObj(TreeNode current, SO obj){
		String[] attributes = obj.attributes();
		for(int i = 0; i < attributes.length; i++){
			String attr = attributes[i];
			SV value = obj.get(attr);
			TreeNode kid = build(new TreeNode(attr,""),attr,value);
			current.kids.add(kid);
		}
		return current;
	}
	
	private TreeNode buildArray(TreeNode current, SA array) {
		for(int i = 0; i < array.size(); i++){
			SV value = array.get(i);
			TreeNode kid = build(new TreeNode("",""),"array",value);
			current.kids.add(kid);
		}
		return current;
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		for(int i = 0; i < contents.size(); i++){
			AffineTransform atf = g2d.getTransform();
			g2d.translate(tx, ty);
			g2d.rotate(-Math.toRadians(rotate));
			g2d.scale(sx, sy);
            contents.get(i).paint(g);
			g2d.setTransform(atf);
		}
	}
	
	@Override
	public boolean mouseDown(double x, double y, AffineTransform myTransform) {
		boolean handled = false;
		for(int i = contents.size()-1; i >= 0; i--){ // back to front order
			Interactable shape = (Interactable)contents.get(i);
			boolean shapeHandled = shape.mouseDown(x,y,getTransform());
			if(!handled && shapeHandled){
				handled = true;
			}
		}
		if(!handled){
			releaseKeyFocus();	
		}
		repaint();
		return handled;
	}

	@Override
	public boolean mouseMove(double x, double y, AffineTransform myTransform) {
		for(int i = contents.size()-1; i >= 0; i--){ // back to front order
			Interactable shape = (Interactable)contents.get(i);
			boolean handled = shape.mouseMove(x,y,getTransform());
			if(handled){
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean mouseUp(double x, double y, AffineTransform myTransform) {
		for(int i = contents.size()-1; i >= 0; i--){ // back to front order
			Interactable shape = (Interactable)contents.get(i);
			boolean handled = shape.mouseUp(x,y,getTransform());
			if(handled){
				model.print();
				return true;
			}
		}
		model.print();
		return true;
	}

	@Override
	public boolean key(char key) {
		// Whenever the Root receives a key() event 
		// it will call key() on the focus object if there is one.
		if(focus != null){
			focus.key(key);
			return true;
		}
		return false;
	}

	@Override
	public Root getPanel() {
		return this;
	}

}
