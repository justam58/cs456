package drawable;

import java.awt.Font;
import java.awt.Graphics;

import spark.data.SO;
import spark.data.SOReflect;

public class Text extends SOReflect implements Drawable {
	
	// Text{ text:"Draw This", x:10,y:100, font:"Times", size:10 }
	public String text;
	public int x;
	public int y;
	public String font; // TODO this should also support the names "serif" and "sans-serif" to select the corresponding standard fonts.
	public int size;
	
	@Override
	public void setStyle(SO style) {
		x = (int)style.getDouble("x");
		y = (int)style.getDouble("y");
		size = (int)style.getDouble("size");
		text = style.getString("text");
		font = style.getString("font");
	}

	@Override
	public void paint(Graphics g) {
		g.setFont(new Font(font,Font.PLAIN,size));
		g.drawChars(text.toCharArray(), 0, text.length(), x, y);
	}

}
