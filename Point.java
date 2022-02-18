package EllipseMath;

public class Point {
	public double x;
	public double y;
	
	public Point(double[] coords){
		x = coords[0];
		y = coords[1];
	}
	
	public Point(double x, double y){
		this.x = x;
		this.y = y;
	}
	
	public static Point[] points(double[][] points){
		Point[] res = new Point[points.length];
		
		for(int i = 0; i < points.length; i++){
			res[i] = new Point(points[i]);
		}
		
		return res;
	}
	
	public Point(){
		
	}
}
