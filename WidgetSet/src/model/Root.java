package model;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import able.Drawable;
import able.Interactable;
import spark.data.SA;
import spark.data.SO;
import spark.data.SOReflect;
import view.ContentPanel;

public class Root extends SOReflect implements Drawable, Interactable {
	
    private ContentPanel contentPanel = ContentPanel.getInstance();
	
	public ArrayList<Drawable> contents = new ArrayList<Drawable>(); // SArray of Drawable objects
	public double sx = 1;
	public double sy = 1;
	public double rotate; // in degrees counter clockwise.
	public double tx;
	public double ty;
	
	public TreeNode model = new TreeNode("","");
	
	public void setKeyFocus(Interactable focus){
		// TODO
		// When this method is called on a Root object 
		// it saves the pointer to the focus object
		// but not as a SPARK attribute. 
		// Whenever the Root receives a key() event 
		// it will call key() on the focus object if there is one.
	}
	
	public void releaseKeyFocus(){
		// TODO
		// Sets the key focus to null.
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
		SO modelObj = style.getObj("model");
		model = buildTree(model, modelObj);
		model.print();
		
		SA contentsArray = style.getArray("contents");
		for(int i = 0; i < contentsArray.size(); i++){
			SO shapeObj = contentsArray.getSO(i);
			Drawable shape = (Drawable)shapeObj;
			shape.setStyle(shapeObj);
			contents.add(shape);
		}
	}
	
	private TreeNode buildTree(TreeNode current, SO obj){
		String[] attributes = obj.attributes();
		for(int i = 0; i < attributes.length; i++){
			String attr = attributes[i];
			TreeNode kid = null;
			try{
				SO valObj = obj.getObj(attr);
				kid = buildTree(new TreeNode(attr,""), valObj);
			} catch(java.lang.RuntimeException e){
				String val = obj.get(attr).toString();
				kid = new TreeNode(attr,val);
			}
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
		for(int i = contents.size()-1; i >= 0; i--){ // back to front order
			Interactable shape = (Interactable)contents.get(i);
			boolean handled = shape.mouseDown(x,y,getTransform());
			if(handled){
				return true;
			}
		}
		return false;
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
		return false;
	}

	@Override
	public boolean key(char key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Root getPanel() {
		contentPanel.repaint();
		return this;
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
