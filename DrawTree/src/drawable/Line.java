package drawable;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import spark.data.SO;
import spark.data.SOReflect;

public class Line extends SOReflect implements Drawable{
	
	// Line{ x1:10, y1:5, x2: 20, y2:40, thickness:3, color:{r:0,g:0,b:0} }
	public int x1;
	public int y1;
	public int x2;
	public int y2;
	public int thickness;
	public Color color;
	
	@Override
	public void setStyle(SO style){
		x1 = (int)style.getDouble("x1");
		x2 = (int)style.getDouble("x2");
		y1 = (int)style.getDouble("y1");
		y2 = (int)style.getDouble("y2");
		
		SO colorObj = style.getObj("color");
		int r = (int)colorObj.getDouble("r");
		int g = (int)colorObj.getDouble("g");
		int b = (int)colorObj.getDouble("b");
		color = new Color(r, g, b);
		thickness = (int)style.getDouble("thickness");
	}

	@Override
	public void paint(Graphics g) {
		g.setColor(color);
		Graphics2D g2d = (Graphics2D)g;
		g2d.setStroke(new BasicStroke(thickness));
		g.drawLine(x1, y1, x2, y2);
	}

}
