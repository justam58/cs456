package able;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public interface Selectable {
	
	public ArrayList<Integer> select(double x, double y, int myIndex, AffineTransform transform) ;
	
	public ArrayList<Point2D> controls() ;

}
