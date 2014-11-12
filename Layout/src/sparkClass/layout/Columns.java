package sparkClass.layout;

import java.awt.Graphics;
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
	
	private ArrayList<Double> maxRowHeights = new ArrayList<Double>();
	private ArrayList<Double> minRowHeights = new ArrayList<Double>();
	private ArrayList<Double> desiredRowHeights = new ArrayList<Double>();

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
//		System.out.println("columns h " + left + ", " + (right-left));
		childRows = new ArrayList<ArrayList<Layout>>();
		currentRow = new ArrayList<Layout>();
		// When Columns receives its width range it will compute a column width of (width-(nColumns-1)*gutter)/nColumns). 
		// Using this width, it will lay out all of its children in rows, giving each the width specified by its columnSpan attribute.  
		double width = right-left;
		double columnWidth = (width-(nColumns-1)*gutter)/nColumns;
		double columnsLeft = nColumns;
//		System.out.println("columnWidth " + columnWidth);
		for(int i = 0; i < contents.size(); i++){
			Layout child = (Layout)contents.get(i);
			double childMinWidth = child.getMinWidth();
			double columnsNeeded = Math.ceil(childMinWidth/columnWidth);
			// If the total width is not enough then that child is given a row of its own and all the columns in that row. 
//			System.out.println("columnsNeeded " + columnsNeeded);
//			System.out.println("columnsLeft " + columnsLeft);
			if(nColumns < columnsNeeded){
//				System.out.println("child gets while row (not enough width)");
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
//				System.out.println("child gets to fit in the new row");
				nextRow();
				currentRow.add(child);
				child.setHBounds(left,columnWidth*columnsNeeded);
				columnsLeft = nColumns - columnsNeeded;
			}
			else{
//				System.out.println("child gets to fit in the current row");
				currentRow.add(child);
				double currentLeft = right-columnWidth*columnsLeft-(columnsLeft-1)*gutter;
				child.setHBounds(currentLeft,currentLeft+columnWidth*columnsNeeded+(columnsNeeded-1)*gutter);
				columnsLeft -= columnsNeeded;
			}
		}
		// It will now know how many rows it needs and which children are in each row.
		if(currentRow.size() != 0){
			nextRow();
		}
		setRowHegihts();
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
		double max = getMaxHeight();
		double min = getMinHeight();
		double desired = getDesiredHeight();
		double height = bottom-top;
//		System.out.println("columns v " + top + ", " + height);
		
		if(min >= height){
			// give all children their min and let them be clipped
//			System.out.println("columns give all children their min and let them be clipped");
			double currentTop = top;
			for(int i = 0; i < childRows.size(); i++){
				ArrayList<Layout> children = childRows.get(i);
				double rowHeight = minRowHeights.get(i);
				for(int j = 0; j < children.size(); j++){
					Layout child = children.get(j);
					child.setVBounds(currentTop, currentTop+rowHeight);
				}
				currentTop += rowHeight;
			}
		}
		else if(desired >= height){
			// give min to all and proportional on what is available for desired
//			System.out.println("columns give min to all and proportional on what is available for desired");
			double desiredMargin = (desired-min) == 0 ? 1 : (desired-min);
			double fraction = (height-min)/desiredMargin;
			double currentTop = top;
			for(int i = 0; i < childRows.size(); i++){
				ArrayList<Layout> children = childRows.get(i);
				double minRowHeight = minRowHeights.get(i);
				double desiredRowHeight = desiredRowHeights.get(i);
				double rowHeight = minRowHeight+(desiredRowHeight-minRowHeight)*fraction;
				for(int j = 0; j < children.size(); j++){
					Layout child = children.get(j);
					child.setVBounds(currentTop, currentTop+rowHeight);
				}
				currentTop += rowHeight;
			}
		}
		else{
			// allocate what remains based on max height
//			System.out.println("columns allocate what remains based on max height");
			double maxMargin = (max-desired) == 0 ? 1 : (max-desired);
			double fraction = (height-desired)/maxMargin;
			double currentTop = top;
			for(int i = 0; i < childRows.size(); i++){
				ArrayList<Layout> children = childRows.get(i);
				double desiredRowHeight = desiredRowHeights.get(i);
				double maxRowHeight = maxRowHeights.get(i);
				double rowHeight = desiredRowHeight+(maxRowHeight-desiredRowHeight)*fraction;
				for(int j = 0; j < children.size(); j++){
					Layout child = children.get(j);
					child.setVBounds(currentTop, currentTop+rowHeight);
				}
				currentTop += rowHeight;
			}
		}
	}

	@Override
	public void setStyle(SO style) {
		SA contentsArray = style.getArray("contents");
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
		}
	}

	private double computeColumnWidth(double width, double columnSpan){
		return (width-((columnSpan-1)*gutter))/columnSpan;
	}
	
	private double computeWidth(double maxChildWith){
		return (maxChildWith*nColumns)+(gutter*(nColumns-1));
	}
	
	private void setRowHegihts(){
		maxRowHeights = new ArrayList<Double>();
		minRowHeights = new ArrayList<Double>();
		desiredRowHeights = new ArrayList<Double>();
		for(int i = 0; i < childRows.size(); i++){
			ArrayList<Layout> children = childRows.get(i);
			double maxRowHeight = 0;
			double minRowHeight = 0;
			double desiredRowHeight = 0;
			for(int j = 0; j < children.size(); j++){
				Layout child = children.get(j);
				double childMaxHeight = child.getMaxHeight();
				if(childMaxHeight > maxRowHeight){
					maxRowHeight = childMaxHeight;
				}
				double childMinHeight = child.getMinHeight();
				if(childMinHeight > minRowHeight){
					minRowHeight = childMinHeight;
				}
				double childDesiredHeight = child.getDesiredHeight();
				if(childDesiredHeight > desiredRowHeight){
					desiredRowHeight = childDesiredHeight;
				}
			}
			maxRowHeights.add(maxRowHeight);
			minRowHeights.add(minRowHeight);
			desiredRowHeights.add(desiredRowHeight);
		}
	}
	
	private void setColumnWidth(){
		double maxMinColumnWidth = -1;
		double maxMaxColumnWidth = -1;
		double maxDesiredColumnWidth = -1;
		
		for(int i = 0; i < contents.size(); i++){
			Layout layout = (Layout)contents.get(i);
			double minColumnWidth = computeColumnWidth(layout.getMinWidth(),columnSpan.get(i));
			if(maxMinColumnWidth == -1 || minColumnWidth > maxMinColumnWidth){
				maxMinColumnWidth = minColumnWidth;
			}
			double maxColumnWidth = computeColumnWidth(layout.getMaxWidth(),columnSpan.get(i));
			if(maxMaxColumnWidth == -1 || maxColumnWidth > maxMaxColumnWidth){
				maxMaxColumnWidth = maxColumnWidth;
			}
			double desiredColumnWidth = computeColumnWidth(layout.getDesiredWidth(),columnSpan.get(i));
			if(maxDesiredColumnWidth == -1 || desiredColumnWidth > maxDesiredColumnWidth){
				maxDesiredColumnWidth = desiredColumnWidth;
			}
		}
		
		minWidth = computeWidth(maxMinColumnWidth);
		maxWidth = computeWidth(maxMaxColumnWidth);
		desiredWidth = computeWidth(maxDesiredColumnWidth);
	}
	
	@Override
	public void paint(Graphics g) {
		for(int i = 0; i < contents.size(); i++){
            contents.get(i).paint(g);
		}
		
		setColumnWidth();
	}

}
