package able;

public interface Dragable {
	
	public double moveTo(double x, double y, double max, double min);
	
	public double move(double dx, double dy, double max, double min);
	
	public double getSliderHeight();

	public double getSliderWidth();
	
	public double getCurrentX();
	
	public double getCurrentY();
}
