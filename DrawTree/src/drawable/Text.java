package drawable;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import spark.data.SO;
import spark.data.SOReflect;

public class Text extends SOReflect implements Drawable {
	
	// Text{ text:"Draw This", x:10,y:100, font:"Times", size:10 }
	public String text = "";
	public double x;
	public double y;
	public String font = ""; // this should also support the names "serif" and "sans-serif" to select the corresponding standard fonts.
	public double size;

	@Override
	public void setStyle(SO style) {

	}

	@Override
	public void paint(Graphics g) {
		g.setColor(Color.black);
		g.setFont(new Font(font,Font.PLAIN,(int)size));
		g.drawChars(text.toCharArray(), 0, text.length(), (int)x, (int)y);
	}
}
