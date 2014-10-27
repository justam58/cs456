package sparkClass.layout;

import able.Drawable;
import able.Interactable;
import able.Layout;
import sparkClass.Group;

public class VStack extends Group implements Layout, Drawable, Interactable {
	
	// VStack{ contents:[...] }

	@Override
	public double getMinWidth() {
		double result = 0;
		for(int i = 0; i < contents.size(); i++){
			Layout layout = (Layout)contents.get(i);
			double childMinWidth = layout.getMinWidth();
			if(result < childMinWidth){
				result = childMinWidth;
			}
		}
		return result;
	}

	@Override
	public double getDesiredWidth() {
		double result = 0;
		for(int i = 0; i < contents.size(); i++){
			Layout layout = (Layout)contents.get(i);
			double childDesiredWidth = layout.getDesiredWidth();
			if(result < childDesiredWidth){
				result = childDesiredWidth;
			}
		}
		return result;
	}

	@Override
	public double getMaxWidth() {
		double result = 0;
		for(int i = 0; i < contents.size(); i++){
			Layout layout = (Layout)contents.get(i);
			double childMaxWidth = layout.getMaxWidth();
			if(result < childMaxWidth){
				result = childMaxWidth;
			}
		}
		return result;
	}

	@Override
	public void setHBounds(double left, double right) {
		for(int i = 0; i < contents.size(); i++){
			Layout child = (Layout)contents.get(i);
			child.setHBounds(left, right);
		}
	}

	@Override
	public double getMinHeight() {
		double result = 0;
		for(int i = 0; i < contents.size(); i++){
			Layout layout = (Layout)contents.get(i);
			result += layout.getMaxHeight();
		}
		return result;
	}

	@Override
	public double getDesiredHeight() {
		double result = 0;
		for(int i = 0; i < contents.size(); i++){
			Layout layout = (Layout)contents.get(i);
			result += layout.getDesiredHeight();
		}
		return result;
	}

	@Override
	public double getMaxHeight() {
		double result = 0;
		for(int i = 0; i < contents.size(); i++){
			Layout layout = (Layout)contents.get(i);
			result += layout.getMaxHeight();
		}
		return result;
	}

	@Override
	public void setVBounds(double top, double bottom) {
		double min = getMinHeight();
		double max = getMaxHeight();
		double desired = getDesiredHeight();
		double height = bottom-top;
		
		if(min >= height){
			// give all children their min and let them be clipped
			double childTop = top;
			for(int i = 0; i < contents.size(); i++){
				Layout child = (Layout)contents.get(i);
				double childHeight = child.getMinHeight();
				child.setVBounds(childTop, childTop+childTop);
				childTop += childHeight;
			}
		}
		else if(desired >= height){
			// give min to all and proportional on what is available for desired
			double desiredMargin = desired-min;
			double fraction = (height-min)/desiredMargin;
			double childTop = top;
			for(int i = 0; i < contents.size(); i++){
				Layout child = (Layout)contents.get(i);
				double childMinHeight = child.getMinHeight();
				double childDesiredHeight = child.getDesiredHeight();
				double childHeight = childMinHeight+(childDesiredHeight-childMinHeight)*fraction;
				child.setHBounds(childTop, childTop+childHeight);
				childTop += childHeight;
			}
		}
		else{
			// allocate what remains based on max width
			double maxMargin = max-desired;
			double fraction = (height-desired)/maxMargin;
			double childTop = top;
			for(int i = 0; i < contents.size(); i++){
				Layout child = (Layout)contents.get(i);
				double childDesiredHeight = child.getDesiredHeight();
				double childMaxHeight = child.getMaxHeight();
				double childHeight = childDesiredHeight+(childMaxHeight-childDesiredHeight)*fraction;
				child.setHBounds(childTop, childTop+childHeight);
				childTop += childHeight;
			}
		}
	}

}
