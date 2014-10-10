package model;

import java.awt.Graphics;
import java.awt.geom.AffineTransform;

import able.Drawable;
import able.Interactable;
import able.Selectable;
import spark.data.SO;

public class Root implements Drawable, Interactable {
	
	public Drawable root;
	
	public void setRoot(Drawable root){
		this.root = root;
	}
	
	public void clean(){
		root = null;
	}
	
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
	
	@Override
	public boolean mouseDown(double x, double y, AffineTransform myTransform) {
		// TODO Auto-generated method stub
		((Selectable) root).select(x, y, 0, myTransform);
		((Interactable) root).mouseDown(x, y, myTransform);
		return false;
	}

	@Override
	public boolean mouseMove(double x, double y, AffineTransform myTransform) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseUp(double x, double y, AffineTransform myTransform) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean key(char key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Root getPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setStyle(SO style) {
		// TODO Auto-generated method stub

	}

	@Override
	public void paint(Graphics g) {
		if(root != null){
			root.paint(g);
		}
	}

}
