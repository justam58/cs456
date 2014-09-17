package drawable;

public class Transformation {
	
	public double sx;
	public double sy;
	public double tx;
	public double ty;
	public double rotate;
	
	public Transformation(double sx, double sy, double tx, double ty, double rotate) {
		super();
		this.sx = sx;
		this.sy = sy;
		this.tx = tx;
		this.ty = ty;
		this.rotate = rotate;
	}
	
	public Transformation() {
		super();
		this.sx = 1;
		this.sy = 1;
		this.tx = 0;
		this.ty = 0;
		this.rotate = 0;
	}

	@Override
	public String toString() {
		return "Transformation [sx=" + sx + ", sy=" + sy + ", tx=" + tx
				+ ", ty=" + ty + ", rotate=" + rotate + "]";
	}
	
}
