package sparkClass;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Stack;

import listener.ModelListener;
import model.TreeNode;
import able.Command;
import able.Drawable;
import able.Interactable;
import able.Layout;
import spark.data.SA;
import spark.data.SO;
import spark.data.SOReflect;
import spark.data.SV;
import view.ContentPanel;

public class Root extends SOReflect implements Drawable, Interactable {
	
    private ContentPanel contentPanel = ContentPanel.getInstance();
	
	public Drawable content = null;
	public double sx = 1;
	public double sy = 1;
	public double rotate; // in degrees counter clockwise.
	public double tx;
	public double ty;
	
	public Interactable focus = null;
	
	public TreeNode model = null;
	
	private Stack<Command> doneCommands = new Stack<Command>();
	
	private Stack<Command> undoneCommands = new Stack<Command>();
	
	public void doIt(Command c){
		doneCommands.push(c);
		c.doIt();
		undoneCommands.clear();
	}
	
	public void undo(){
		if(!doneCommands.empty()){
			Command c = doneCommands.pop();
			c.undo();
			undoneCommands.push(c);
		}
		if(model != null){
			model.print();
		}
		repaint();
	}
	
	public void redo(){
		if(!undoneCommands.empty()){
			Command c = undoneCommands.pop();
			c.redo();
			doneCommands.push(c);
		}
		if(model != null){
			model.print();
		}
		repaint();
	}
	
	public void addModelListener(ArrayList<String> models, TreeNode current, int index, ModelListener listener){
		if(model != null){
			model.addListener(models, current, index, listener);
		}
	}
	
	public String getModelValue(ArrayList<String> models, TreeNode current, int index){
		if(model != null){
			return model.getValue(models,current,index);
		}
		return null;
	}
	
	public void updateModel(ArrayList<String> models, TreeNode current, int index, String value){
		if(model != null){
			model.update(models, current, index, value);
		}
	}
	
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
	
	public void resize(double width, double height){
//		System.out.println("resize " + width + ", " + height);
		Layout layout = (Layout)content;
		layout.setHBounds(0, width);
		layout.setVBounds(0, height);
		repaint();
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
		if(modelValue != null){
			model = new TreeNode("","");
			model = build(model,"model",modelValue);
			model.print();
		}
		
		SA contentsArray = style.getArray("contents");
		SO contentsObj = contentsArray.getSO(0);;
		Drawable shape = (Drawable)contentsObj;
		shape.setStyle(contentsObj);
		content = shape;
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
			TreeNode kid = build(new TreeNode(String.valueOf(i),""),String.valueOf(i),value);
			current.kids.add(kid);
		}
		return current;
	}

	@Override
	public void paint(Graphics g) {
		if(content != null){
			Graphics2D g2d = (Graphics2D)g;
			AffineTransform atf = g2d.getTransform();
			g2d.translate(tx, ty);
			g2d.rotate(-Math.toRadians(rotate));
			g2d.scale(sx, sy);
	        content.paint(g);
			g2d.setTransform(atf);
		}
	}
	
	@Override
	public boolean mouseDown(double x, double y, AffineTransform myTransform) {
		if(content != null){
			Interactable shape = (Interactable)content;
			boolean handled = shape.mouseDown(x,y,getTransform());
			if(!handled){
				releaseKeyFocus();	
			}
			repaint();
		}
		return true;
	}

	@Override
	public boolean mouseMove(double x, double y, AffineTransform myTransform) {
		if(content != null){
			Interactable shape = (Interactable)content;
			shape.mouseMove(x,y,getTransform());
		}
		return true;
	}

	@Override
	public boolean mouseUp(double x, double y, AffineTransform myTransform) {
		if(content != null){
			Interactable shape = (Interactable)content;
			shape.mouseUp(x,y,getTransform());
		}
		if(model != null){
			model.print();
		}
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

	@Override
	public Point2D getCenter() {
		return content.getCenter();
	}

}
