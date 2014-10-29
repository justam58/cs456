package sparkClass.layout;

import able.Drawable;
import able.Interactable;
import able.Layout;
import sparkClass.Group;

public class HStack extends Group implements Layout, Drawable, Interactable {
	
	// HStack{ contents:[...] } 

	@Override
	public double getMinWidth() {
		double result = 0;
		for(int i = 0; i < contents.size(); i++){
			Layout layout = (Layout)contents.get(i);
			result += layout.getMinWidth();
		}
		return result;
	}

	@Override
	public double getDesiredWidth() {
		double result = 0;
		for(int i = 0; i < contents.size(); i++){
			Layout layout = (Layout)contents.get(i);
			result += layout.getDesiredWidth();
		}
		return result;
	}

	@Override
	public double getMaxWidth() {
		double result = 0;
		for(int i = 0; i < contents.size(); i++){
			Layout layout = (Layout)contents.get(i);
			result += layout.getMaxWidth();
		}
		return result;
	}

	@Override
	public void setHBounds(double left, double right) {
		double min = getMinWidth();
		double max = getMaxWidth();
		double desired = getDesiredWidth();
		double width = right-left;
		System.out.println("hstack h " + left + ", " + (right-left));
		
		if(min >= width){
			// give all children their min and let them be clipped
			System.out.println("hstack give all children their min and let them be clipped");
			double childLeft = left;
			for(int i = 0; i < contents.size(); i++){
				Layout child = (Layout)contents.get(i);
				double childWidth = child.getMinWidth();
				child.setHBounds(childLeft, childLeft+childWidth);
				childLeft += childWidth;
			}
		}
		else if(desired >= width){
			// give min to all and proportional on what is available for desired
			System.out.println("hstack give min to all and proportional on what is available for desired");
			double desiredMargin = (desired-min) == 0 ? 1 : (desired-min);
			double fraction = (width-min)/desiredMargin;
			double childLeft = left;
			for(int i = 0; i < contents.size(); i++){
				Layout child = (Layout)contents.get(i);
				double childMinWidth = child.getMinWidth();
				double childDesiredWidth = child.getDesiredWidth();
				double childWidth = childMinWidth+(childDesiredWidth-childMinWidth)*fraction;
				child.setHBounds(childLeft, childLeft+childWidth);
				childLeft += childWidth;
			}
		}
		else{
			// allocate what remains based on max width
			System.out.println("hstack allocate what remains based on max width");
			double maxMargin = (max-desired) == 0 ? 1 : (max-desired);
			double fraction = (width-desired)/maxMargin;
			double childLeft = left;
			for(int i = 0; i < contents.size(); i++){
				Layout child = (Layout)contents.get(i);
				double childDesiredWidth = child.getDesiredWidth();
				double childMaxWidth = child.getMaxWidth();
				double childWidth = childDesiredWidth+(childMaxWidth-childDesiredWidth)*fraction;
				child.setHBounds(childLeft, childLeft+childWidth);
				childLeft += childWidth;
			}
		}
	}

	@Override
	public double getMinHeight() {
		double result = 0;
		for(int i = 0; i < contents.size(); i++){
			Layout layout = (Layout)contents.get(i);
			double childMinHeight = layout.getMinHeight();
			if(result < childMinHeight){
				result = childMinHeight;
			}
		}
		return result;
	}

	@Override
	public double getDesiredHeight() {
		double result = 0;
		for(int i = 0; i < contents.size(); i++){
			Layout layout = (Layout)contents.get(i);
			double childDesiredHeight = layout.getDesiredHeight();
			if(result < childDesiredHeight){
				result = childDesiredHeight;
			}
		}
		return result;
	}

	@Override
	public double getMaxHeight() {
		double result = 0;
		for(int i = 0; i < contents.size(); i++){
			Layout layout = (Layout)contents.get(i);
			double childMaxHeight = layout.getMaxHeight();
			if(result < childMaxHeight){
				result = childMaxHeight;
			}
		}
		return result;
	}

	@Override
	public void setVBounds(double top, double bottom) {
		System.out.println("hstack v " + top + ", " + (bottom-top));
		for(int i = 0; i < contents.size(); i++){
			Layout child = (Layout)contents.get(i);
			child.setVBounds(top, bottom);
		}
	}

}
