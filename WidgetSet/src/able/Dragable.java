package able;

public interface Dragable {
	
	public void moveTo(double x, double y);
	
	public double move(double dx, double dy, double max, double min);
	
	public double getSliderHeight();

	public double getSliderWidth();
}
