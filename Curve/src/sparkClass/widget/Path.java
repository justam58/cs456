package sparkClass.widget;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import listener.ModelListener;
import spark.data.SA;
import spark.data.SO;
import sparkClass.Group;
import sparkClass.Root;
import able.Drawable;
import able.Interactable;
import able.Layout;
import able.Selectable;

public class Path extends Group implements Interactable, Drawable, ModelListener, Layout {
	
	//Path{ contents:[ ... ], path:[ {x:10,y:20}, ...], slider:Group , sliderVal:0.5, width:100, height:200, model:[...] } 
	public ArrayList<String> models = new ArrayList<String>();
	public Group slider;
	public double sliderVal;
	
	private Root root = null;
	
	private Point2D.Double[] orgPoints;
	private Point2D.Double[] points; // control points
	private Path2D path;
	private Point2D.Double P, Q, R, S; // arch points
	private ArrayList<SegCurve> curves = new ArrayList<SegCurve>();
	
	private boolean dragging = false;	
	
	private void generatePath(){
		path = new Path2D.Double();
		path.moveTo(points[0].x, points[0].y);
		double segVal = 1.0/(points.length-1);
		double currentVal = 0;
		for (int i = 0; i < points.length-1; i++) {
		  	makeArch(points, points.length, i); 
		  	SegCurve c = new SegCurve(P,Q,R,S,currentVal,currentVal+segVal);
		  	curves.add(c);
		  	currentVal += segVal;
		  	path.curveTo(Q.x, Q.y, R.x, R.y, S.x, S.y);
		}
	}
	
	@Override
	public double getMaxHeight() {
		return height;
	}
	
	@Override
	public double getMaxWidth() {
		return width;
	}
	
	@Override
	public void setHBounds(double left, double right) {
		tx = left;
		slider.newTx = tx;
        for (int i = 0; i < points.length; i++) {
        	points[i].x = orgPoints[i].x+left;
        }
		generatePath();
	}
	
	@Override
	public void setVBounds(double top, double buttom) {
		ty = top;
		slider.newTy = ty;
        for (int i = 0; i < points.length; i++) {
        	points[i].y = orgPoints[i].y+top;
        }
		generatePath();
	}
	
	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		for(int i = 0; i < contents.size(); i++){
			AffineTransform atf = g2d.getTransform();
			g2d.translate(tx, ty);
			g2d.rotate(-Math.toRadians(rotate));
			g2d.scale(sx, sy);
            contents.get(i).paint(g);
			g2d.setTransform(atf);
		}
		
		AffineTransform atf = g2d.getTransform();
		g2d.translate(tx, ty);
		g2d.rotate(-Math.toRadians(rotate));
		g2d.scale(sx, sy);
		Drawable drawableSlider = (Drawable)slider;
		drawableSlider.paint(g);
		g2d.setTransform(atf);
	}
	
	@Override
	public void setStyle(SO style) {
		
		SA contentsArray = style.getArray("contents");
		for(int i = 0; i < contentsArray.size(); i++){
			SO shapeObj = contentsArray.getSO(i);
			Drawable shape = (Drawable)shapeObj;
			shape.setStyle(shapeObj);
			contents.add(shape);
		}
		
		SA pointsArray = style.getArray("path");
        if(pointsArray != null) {
            points = new Point2D.Double[pointsArray.size()];
            orgPoints = new Point2D.Double[pointsArray.size()]; 
            for (int i = 0; i < pointsArray.size(); i++) {
                SO pointObj = pointsArray.getSO(i);
                int x = (int) pointObj.getDouble("x");
                int y = (int) pointObj.getDouble("y");
                points[i] = new Point2D.Double(x,y);
                orgPoints[i] = new Point2D.Double(x,y);
            }
        }
        
        generatePath();
		
		SA modelsObj = style.getArray("model");
		if(modelsObj != null){
			for(int i = 0; i < modelsObj.size(); i++){
				models.add(modelsObj.get(i).toString().replace("\"", ""));
			}
		}
		
		root = getPanel();	
		root.model.addListener(models, root.model, 0, this);
		
		SO sliderObj = style.getObj("slider");
		Drawable drawableSlider = (Drawable)sliderObj;
		drawableSlider.setStyle(sliderObj);
		slider = (Group)sliderObj;
		slider.isSlider = true;
		
		moveSliderTo(sliderVal);
		
		Object value = root.model.getValue(models, root.model, 0);
		if(value != null){
			double modelValue = java.lang.Double.valueOf(root.model.getValue(models, root.model, 0));
			sliderVal = modelValue;
			moveSliderTo(modelValue);
		}
		
	}
	
	@Override
	public boolean mouseDown(double x, double y, AffineTransform myTransform) {
		Selectable selectableSlider = (Selectable) slider;
		AffineTransform atf = new AffineTransform(myTransform);
		ArrayList<Integer> selectPath = selectableSlider.select(x, y, 0, atf);
		if(selectPath != null){
			dragging = true;
			return true;
		}
		return false;
	}

	@Override
	public boolean mouseMove(double x, double y, AffineTransform myTransform) {
		if(dragging){
			Point2D endp = new Point2D.Double();
			myTransform.transform(new Point2D.Double(x,y), endp);
			
			double newModelValue = findNearestPoint(endp);
			root.model = root.model.update(models, root.model, 0, String.valueOf(newModelValue));
			return true;
		}
		return false;
	}

	@Override
	public boolean mouseUp(double x, double y, AffineTransform myTransform) {
		dragging = false;
		return false;
	}

	@Override
	public void modelChanged(String newValue) {
		double modelValue = Double.valueOf(newValue);
		if(modelValue != sliderVal){
			moveSliderTo(modelValue);
			sliderVal = modelValue;
			root.repaint();
		}
	}
	
	private double getValue(double t, SegCurve seg){
		return seg.startT + (seg.endT-seg.startT)*t;
	}
	
	private double findNearestPoint(Point2D p){
		double dist = Double.MAX_VALUE;
		double val = -1;
		for(int i = 0; i < curves.size(); i++){
			SegCurve seg = curves.get(i);
			double t = 0;
			while(t <= 1){
				double d = seg.getDistance(p,t);
				if(d < dist){
					dist = d;
					val = getValue(t,seg);
				}
				t+=0.01;
			}
		}
		return val;
	}
	
	private void moveSliderTo(double val){
		for(int i = 0; i < curves.size(); i++){
			SegCurve seg = curves.get(i);
			if(val <= seg.endT){
				Point2D moveToPoint = seg.getPoint(val);
				double rotation = seg.getRotation(val);
				slider.moveTo(moveToPoint.getX(),moveToPoint.getY(),rotation);
				return;
			}
		}
	}
	
	private void makeArch(Point2D.Double[] controlPoints, int numCP, int p){
		P = new Point2D.Double(controlPoints[p].x, controlPoints[p].y); 
		S = new Point2D.Double(controlPoints[p+1].x, controlPoints[p+1].y);
		Q = new Point2D.Double();
		R = new Point2D.Double();
		if (p == 0){
			Q = P;
		}
		else { 
			Q.x = controlPoints[p].x + (-controlPoints[p-1].x + controlPoints[p+1].x)/6;
			Q.y = controlPoints[p].y + (-controlPoints[p-1].y + controlPoints[p+1].y)/6;
		}
		
		if (p == numCP - 2){
			R = S;
		}
		else { 
			R.x = controlPoints[p+1].x - (-controlPoints[p].x + controlPoints[p+2].x)/6;
			R.y = controlPoints[p+1].y - (-controlPoints[p].y + controlPoints[p+2].y)/6;
		}
	}
	
	private class SegCurve {
		public double a,b,c,d,e,f,g,h;
		public double startT, endT;
		
		public SegCurve(Point2D P, Point2D Q, Point2D R, Point2D S, double startT, double endT){
			calcualte(P, Q, R, S);
			this.startT = startT;
			this.endT = endT;
		}

		private void calcualte(Point2D p, Point2D q, Point2D r, Point2D s) {
			a = -p.getX()+3*q.getX()-3*r.getX()+s.getX();
			b = 3*p.getX()-6*q.getX()+3*r.getX();
			c = -3*p.getX()+3*q.getX();
			d = p.getX();
			
			e = -p.getY()+3*q.getY()-3*r.getY()+s.getY();
			f = 3*p.getY()-6*q.getY()+3*r.getY();
			g = -3*p.getY()+3*q.getY();
			h = p.getY();
		}
		
		public double getRotation(double t){
			t = (t-startT)/(endT-startT);
			double x = 3*a*t*t+2*b*t+c;
			double y = 3*e*t*t+2*f*t+g;
			return Math.toDegrees(Math.atan2(x, y));
		}
		
		public Point2D getPoint(double t){
			t = (t-startT)/(endT-startT);
			double x = a*t*t*t+b*t*t+c*t+d;
			double y = e*t*t*t+f*t*t+g*t+h;
			return new Point2D.Double(x,y);
		}
		
		public double getDistance(Point2D p, double t){
			double x = a*t*t*t+b*t*t+c*t+d;
			double y = e*t*t*t+f*t*t+g*t+h;
			return p.distance(x,y);
		}
	
	}

}
