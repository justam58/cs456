package able;

public interface Layout {
	
	// Returns the minimum width that the widget will accept for drawing.
	public double getMinWidth();
	
	// Returns the desired width that the widget wants for drawing.
	public double getDesiredWidth();
	
	// Returns the maximum width that the widget can use for drawing.
	public double getMaxWidth();
	
	// This sets the horizontal range that the object can display within. This will also set up the necessary information for the height information.
	public void setHBounds(double left, double right);
	
	// Returns the minimum height that the widget will accept for drawing.
	public double getMinHeight();
	
	// Returns the desired height that the widget wants for drawing.
	public double getDesiredHeight();
	
	// Returns the maximum height that the widget can use for drawing.
	public double getMaxHeight();
	
	// This sets the vertical bounds for the object and causes it to layout any of its children.
	public void setVBounds(double top, double bottom);
}
