package drawable;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import spark.data.SO;
import spark.data.SOReflect;

public class Line extends SOReflect implements Drawable {
	
	// Line{ x1:10, y1:5, x2: 20, y2:40, thickness:3, color:{r:0,g:0,b:0} }
	public double x1 = 0;
	public double y1 = 0;
	public double x2 = 10;
	public double y2 = 10;
	public double thickness = 1;
	public Color color;

	@Override
	public void setStyle(SO style){
		SO colorObj = style.getObj("color");
        if(colorObj != null){
            int r = (int)colorObj.getDouble("r");
            int g = (int)colorObj.getDouble("g");
            int b = (int)colorObj.getDouble("b");
            color = new Color(r, g, b);
        }
		else{
            color = Color.black;
        }
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		g.setColor(color);
		g2d.setStroke(new BasicStroke((int)thickness));
		g.drawLine((int)x1, (int)y1, (int)x2, (int)y2);
	}
}
