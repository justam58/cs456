package model;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.TextField;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import able.Drawable;
import able.Interactable;
import able.Selectable;
import spark.data.SO;
import spark.data.SOReflect;
import spark.data.SParented;

public class Text extends SOReflect implements Drawable, Selectable, Interactable {
	
	// Text{ text:"Draw This", x:10,y:100, font:"Times", size:10 }
	public String text = "";
	public double x;
	public double y;
	public String font = ""; // this should also support the names "serif" and "sans-serif" to select the corresponding standard fonts.
	public double size;
	
	public boolean edit;
	public double cursor;
	
	private boolean editing = false;
	
	public Rectangle boundingBox = null;
	public TextField textField;

	@Override
	public void setStyle(SO style) {
	}

	@Override
	public void paint(Graphics g) {
		g.setColor(Color.black);
		g.setFont(new Font(font,Font.PLAIN,(int)size));
		if(editing){
			text.replace("|", "");
			text = text.substring(0, (int)cursor) + text.substring((int)cursor);
			System.out.println(text);
			g.drawChars(text.toCharArray(), 0, text.length(), (int)x, (int)y);
		}
		else
		{
			g.drawChars(text.toCharArray(), 0, text.length(), (int)x, (int)y);
		}
		setBoundingBox((Graphics2D)g);

//		g.drawRect((int)boundingBox.getX(), (int)boundingBox.getY(), (int)boundingBox.getWidth(), (int)boundingBox.getHeight());
	}

	@Override
	public ArrayList<Integer> select(double x, double y, int myIndex, AffineTransform transform) {
		Point2D ptSrc = new Point2D.Double(x,y);
		Point2D ptDst = transform.transform(ptSrc, null);
		x = ptDst.getX();
		y = ptDst.getY();
		
		// This is selected if the selection point is anywhere in the bounding box of the drawn length of the string and the full ascent of the font.
		ArrayList<Integer> result = null;
		double height = boundingBox.getHeight();
		double width = boundingBox.getWidth();
		double left = boundingBox.getX();
		double top = boundingBox.getY();
		if(y > top && x > left && y < (top+height) && x < (left+width)){
			result = new ArrayList<Integer>();
			result.add(myIndex);
		}
		return result;
	}

	@Override
	public ArrayList<Point2D> controls() {
		// returns only the left end of its baseline as the control point.
		ArrayList<Point2D> result = new ArrayList<Point2D>();
		result.add(new Point2D.Double(x,y));
		return result;
	}
	
	private void setBoundingBox(Graphics2D g){
		AffineTransform t = g.getTransform();
		g.setTransform(new AffineTransform());
		Font font = new Font(this.font,Font.PLAIN,(int)size);
	    GlyphVector gv = font.layoutGlyphVector(
	            g.getFontRenderContext(), text.toCharArray(),
	            0, text.length(), Font.LAYOUT_LEFT_TO_RIGHT);
        boundingBox = gv.getPixelBounds(g.getFontRenderContext(), (float)x, (float)y);
        g.setTransform(t);
	}

	@Override
	public boolean mouseDown(double x, double y, AffineTransform myTransform) {
		// If edit=true and select() is called and succeeds, 
		// then the cursor attribute is set to the character position where typing insertions should occur. 
		// When cursor is set to any number greater than or equal to zero, 
		// the cursor is painted at the insertion point and the Root object has its key focus set to this object.
		if(edit){
			ArrayList<Integer> selectedPath = select(x,y,0,myTransform);
			if(selectedPath != null){
				Root root = getPanel();
				root.setKeyFocus(this);
				editing = true;
				return true;
			}
			editing = false;
		}
		return false;
	}

	@Override
	public boolean mouseMove(double x, double y, AffineTransform myTransform) {
		return false;
	}

	@Override
	public boolean mouseUp(double x, double y, AffineTransform myTransform) {
		return false;
	}

	@Override
	public boolean key(char key) {
		System.out.println("text got key!");
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
		text = label;
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
