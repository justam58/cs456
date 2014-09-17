package drawable;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import spark.data.SO;
import spark.data.SOReflect;

public class Ellipse extends SOReflect implements Drawable{
	
	// Ellipse{ left:0, top:100, width:10, height:10, thickness:2, border:{r:0,g:0,b:0}, fill:{r:0,g:0,b:128} } 
	public double top;
	public double left;
	public double width;
	public double height;
	public double thickness;
	public Color border; // If there is no border color then no border is drawn.
	public Color fill; // If there is no fill color then the rectangle is not filled. 

	@Override
	public void setStyle(SO style) {
		SO borderObj = style.getObj("border");
		if(borderObj != null){
			int r = (int)borderObj.getDouble("r");
			int g = (int)borderObj.getDouble("g");
			int b = (int)borderObj.getDouble("b");
			border = new Color(r, g, b);
		}
		
		SO fillObj = style.getObj("fill");
		if(fillObj != null){
			int r = (int)fillObj.getDouble("r");
			int g = (int)fillObj.getDouble("g");
			int b = (int)fillObj.getDouble("b");
			fill = new Color(r, g, b);
		}
	}

	@Override
	public void paint(Graphics g) {
		if(fill != null){
			g.setColor(fill);
			g.fillOval((int)left, (int)top, (int)width, (int)height);
		}
		if(border != null){
			g.setColor(border);
            Graphics2D g2d = (Graphics2D)g;
			g2d.setStroke(new BasicStroke((int)thickness));
			g.drawOval((int)left, (int)top, (int)width, (int)height);
		}

        if(border == null && fill == null){
        	g.setColor(Color.black);
            Graphics2D g2d = (Graphics2D)g;
            g2d.setStroke(new BasicStroke((int)thickness));
            g.drawOval((int)left, (int)top, (int)width, (int)height);
        }
	}
}
