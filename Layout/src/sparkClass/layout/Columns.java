package sparkClass.layout;

import java.util.ArrayList;

import able.Drawable;
import able.Interactable;
import able.Layout;
import spark.data.SA;
import spark.data.SO;
import spark.data.SOReflect;
import sparkClass.Group;

public class Columns extends Group implements Layout, Drawable, Interactable {
	
	// Columns{ contents:[...], nColumns:12, gutter:10 }
	public double nColumns;
	public double gutter;
	
	private double minWidth;
	private double maxWidth;
	private double desiredWidth;
	
	private ArrayList<Double> columnSpan = new ArrayList<Double>();
	private ArrayList<ArrayList<Layout>> childRows = new ArrayList<ArrayList<Layout>>();
	private ArrayList<Layout> currentRow = new ArrayList<Layout>();	

	@Override
	public double getMinWidth() {
		return minWidth;
	}

	@Override
	public double getDesiredWidth() {
		return desiredWidth;
	}

	@Override
	public double getMaxWidth() {
		return maxWidth;
	}

	@Override
	public void setHBounds(double left, double right) {
		childRows = new ArrayList<ArrayList<Layout>>();
		currentRow = new ArrayList<Layout>();
		// When Columns receives its width range it will compute a column width of (width-(nColumns-1)*gutter)/nColumns). 
		// Using this width, it will lay out all of its children in rows, giving each the width specified by its columnSpan attribute.  
		double width = right-left;
		double columnWidth = (width-(nColumns-1)*gutter)/nColumns;
		double columnsLeft = nColumns;
		for(int i = 0; i < contents.size(); i++){
			Layout child = (Layout)contents.get(i);
			double childMinWidth = child.getMinWidth();
			double columnsNeeded = Math.ceil(childMinWidth/columnWidth);
			// If the total width is not enough then that child is given a row of its own and all the columns in that row. 
			if(nColumns < columnsNeeded){
				nextRow();
				currentRow.add(child);
				nextRow();
				child.setHBounds(left,right);
				columnsLeft = nColumns;
				continue;
			}
			// If the column width is less than the minimum for some child, then it is given more columns until it has enough.
			// If it has more children than fit in nColumns it will wrap them onto new rows. 
			if(columnsLeft < columnsNeeded){
				nextRow();
				currentRow.add(child);
				child.setHBounds(left,columnWidth*columnsNeeded);
				columnsLeft = nColumns - columnsNeeded;
			}
			else{
				currentRow.add(child);
				double currentLeft = right-columnWidth*columnsLeft;
				child.setHBounds(currentLeft,currentLeft+columnWidth*columnsNeeded);
				columnsLeft -= columnsNeeded;
			}
		}
		// It will now know how many rows it needs and which children are in each row.
	}
	
	private void nextRow(){
		childRows.add(currentRow);
		currentRow = new ArrayList<Layout>();
	}

	@Override
	public double getMinHeight() {
		double result = 0;
		for(int i = 0; i < contents.size(); i++){
			Layout layout = (Layout)contents.get(i);
			result += layout.getMinHeight();
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
		// When it gets its height bounds, it will use the min,desired, and max values for the children in each row to determine how much of the vertical space to give to each row. 
		// This is then set as the height for each object in a row.
		double currentTop = top;
		for(int i = 0; i < childRows.size(); i++){
			ArrayList<Layout> children = childRows.get(i);
			double rowHeight = 0; //TODO
			for(int j = 0; j < children.size(); j++){
				Layout child = children.get(j);
				child.setVBounds(currentTop, currentTop+rowHeight);
			}
			currentTop += rowHeight;
		}
	}

	@Override
	public void setStyle(SO style) {
		SA contentsArray = style.getArray("contents");
		double maxMinColumnWidth = -1;
		double maxMaxColumnWidth = -1;
		double maxDesiredColumnWidth = -1;
		for(int i = 0; i < contentsArray.size(); i++){
			SO shapeObj = contentsArray.getSO(i);
			Drawable shape = (Drawable)shapeObj;
			shape.setStyle(shapeObj);
			contents.add(shape);
			
			SOReflect shapeSOR = (SOReflect)shape;
			String classVal = shapeSOR.getString("columnSpan");
			double columnSpan = 1;
			if(classVal != null && classVal.equals("columnSpan")){
				columnSpan = Double.valueOf(classVal);
			}
			this.columnSpan.add(columnSpan);
			
			Layout layout = (Layout)shape;
			double minColumnWidth = computeColumnWidth(layout.getMinWidth(),columnSpan);
			if(maxMinColumnWidth == -1 || minColumnWidth > maxMinColumnWidth){
				maxMinColumnWidth = minColumnWidth;
			}
			double maxColumnWidth = computeColumnWidth(layout.getMaxWidth(),columnSpan);
			if(maxMaxColumnWidth == -1 || maxColumnWidth > maxMaxColumnWidth){
				maxMaxColumnWidth = maxColumnWidth;
			}
			double desiredColumnWidth = computeColumnWidth(layout.getDesiredWidth(),columnSpan);
			if(maxDesiredColumnWidth == -1 || desiredColumnWidth > maxDesiredColumnWidth){
				maxDesiredColumnWidth = desiredColumnWidth;
			}
		}
		
		minWidth = computeWidth(maxMinColumnWidth);
		maxWidth = computeWidth(maxMaxColumnWidth);
		desiredWidth = computeWidth(maxDesiredColumnWidth);
	}

	private double computeColumnWidth(double width, double columnSpan){
		return (width-((columnSpan-1)*gutter))/columnSpan;
	}
	
	private double computeWidth(double maxChildWith){
		return (maxChildWith*nColumns)+(gutter*(nColumns-1));
	}

}
