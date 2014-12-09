package able;

import java.awt.Graphics;
import java.awt.geom.Point2D;

import spark.data.SO;

public interface Drawable {
	
	public void setStyle(SO style);
	
	public void paint(Graphics g);
	
	public Point2D getCenter();
}
