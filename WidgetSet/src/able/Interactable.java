package able;

import java.awt.Color;
import java.awt.geom.AffineTransform;

import model.Root;

public interface Interactable {
	
	// This is called whenever a mouse down event is received. It returns true if the object handled this event, false otherwise.
	public boolean mouseDown(double x, double y, AffineTransform myTransform);
	
	// This is called whenever a mouse move event is received. It returns true if the object handled this event, false otherwise.
	public boolean mouseMove(double x, double y, AffineTransform myTransform);
	
	// This is called whenever a mouse up event is received. It returns true if the object handled this event, false otherwise.
	public boolean mouseUp(double x, double y, AffineTransform myTransform);
	
	// This is called whenever a mouse event is received. It returns true if the object handled this event, false otherwise.
	public boolean key(char key);
	
	// If the object is Root then it returns itself. Otherwise this method is called on the parent().
	public Root getPanel();
	
	public void changeBackgroundColor(Color c);
	
	public void changeLabel(String label);
	
	public double move(double dx, double dy, Interactable ranger);
	
	public double getSliderHeight();
	
	public void moveTo(double x, double y);

}
