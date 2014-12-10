package sparkClass.shape;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import command.DeleteCommand;
import command.InsertCommand;
import able.Drawable;
import able.Interactable;
import able.Selectable;
import spark.data.SO;
import spark.data.SOReflect;
import spark.data.SParented;
import sparkClass.Root;

public class Text extends SOReflect implements Drawable, Selectable, Interactable {
	
	// Text{ text:"Draw This", x:10,y:100, font:"Times", size:10 }
	public String text = "";
	public double x;
	public double y;
	public String font = ""; // this should also support the names "serif" and "sans-serif" to select the corresponding standard fonts.
	public double size;
	
	public boolean edit;
	public double cursor = -1;
	public ArrayList<String> models = null; 
	
	private Rectangle boundingBox = null;
	private FontMetrics fontMetrics;
	public Root root = null;
	
	public Text() {
		super();
	}

	public Text(String text, double x, double y, String font, double size, boolean edit, double cursor, ArrayList<String> models) {
		super();
		this.text = text;
		this.x = x;
		this.y = y;
		this.font = font;
		this.size = size;
		this.edit = edit;
		this.cursor = cursor;
		this.models = models;
	}

	@Override
	public void setStyle(SO style) {
		root = getPanel();
	}

	@Override
	public void paint(Graphics g) {
		g.setColor(Color.black);
		g.setFont(new Font(font,Font.PLAIN,(int)size));
		if(cursor >= 0){
			if(cursor > text.length()){
				cursor = text.length();
			}
			String textWithCursor = text.substring(0, (int)cursor) + "|" + text.substring((int)cursor);
			g.drawChars(textWithCursor.toCharArray(), 0, textWithCursor.length(), (int)x, (int)y);
		}
		else
		{
			g.drawChars(text.toCharArray(), 0, text.length(), (int)x, (int)y);
		}
		setBoundingBox((Graphics2D)g);
		setFontMetrics((Graphics2D)g);

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
	
	public void setBoundingBox(Graphics2D g){
		AffineTransform t = g.getTransform();
		g.setTransform(new AffineTransform());
		Font font = new Font(this.font,Font.PLAIN,(int)size);
	    GlyphVector gv = font.layoutGlyphVector(
	            g.getFontRenderContext(), text.toCharArray(),
	            0, text.length(), Font.LAYOUT_LEFT_TO_RIGHT);
        boundingBox = gv.getPixelBounds(g.getFontRenderContext(), (float)x, (float)y);
        g.setTransform(t);
	}
	
	public FontMetrics setFontMetrics(Graphics2D g){
		fontMetrics = g.getFontMetrics(new Font(this.font,Font.PLAIN,(int)size));
		return fontMetrics;
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
				editing(x,false);
				return true;
			}
			cursor = -1;
		}
		return false;
	}
	
	public double calculateCursor(double x) {
		String textWithCursor;
		if(cursor >= 0){
			textWithCursor = text.substring(0, (int)cursor) + "|" + text.substring((int)cursor);
		}
		else
		{
			textWithCursor = text;
		}
		for(int i = 0; i < textWithCursor.length(); i++){
			String sub = textWithCursor.substring(0,i);
			int width = fontMetrics.stringWidth(sub);
			if((x-this.x) < width)
			{
				return (i-1) == -1 ? 0 : i-1;
			}
		}
		return textWithCursor.length();
	}

	public void editing(double x, boolean knowCursor){
		root.setKeyFocus(this);
		if(!knowCursor){
			this.cursor = calculateCursor(x);
		}
		else{
			cursor = x;
		}
		root.repaint();
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
		if((int)key == 8 && cursor > 0){// back space
			text = text.substring(0,(int)cursor-1) + text.substring((int)cursor);
			if(models != null){
				DeleteCommand c = new DeleteCommand(root,models,text,cursor,this);
				root.doIt(c);
			}
			cursor--;
		}
		else{
			text = text.substring(0,(int)cursor) + key + text.substring((int)cursor);
			if(models != null){
				InsertCommand c = new InsertCommand(root,models,text,cursor,this);
				root.doIt(c);
			}
			cursor++;
		}
		root.repaint();
		return true;
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


	public void changeLabel(String label) {
		text = label;
	}

	@Override
	public Point2D getCenter() {
		return new Point2D.Double(x,y);
	}

}
