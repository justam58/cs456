package drawable;
import java.awt.Graphics;

import spark.data.SO;

public interface Drawable {
	
	public void setStyle(SO style, Transformation t);
	
	public void paint(Graphics g);
	
}
