package Ellipse.projectionMath;

import EllipseMath.Logger;
import EllipseMath.Point;

public class CircleSegment {
	public double r;
	public double xC;
	public double yC;
	public double[] segmentStartPoint;
	public double[] segmentEndPoint;
	
	public double length;
	
	//https://math.stackexchange.com/questions/213658/get-the-equation-of-a-circle-when-given-3-points
	//Equation: (x-xC)^2+(y-yC)^2-r^2=0
	
	//(x1-xC)^2+(y1-yC)^2-r^2=0
	//(x2-xC)^2+(y2-yC)^2-r^2=0
	//(x3-xC)^2+(y3-yC)^2-r^2=0
	//=>
	//(x2-xC)^2-(x1-xC)^2+(y2-yC)^2-(y1-yC)^2=0
	//(x3-xC)^2-(x2-xC)^2+(y3-yC)^2-(y2-yC)^2=0
	//=>
	//x2^2+xC^2-2*x2*xC-x1^2-xC^2+2*x1*xC+y^2+yC^2-2*y2*yC-y1^2-yC^2+2*y1*yC=0
	//=>
	//x2^2-x1^2 + xC*2*(x1-x2) + y2^2-y1^2 + yC*2*(y1-y2) = 0 (Checked)
	//x3^2-x2^2 + xC*2*(x2-x3) + y3^2-y2^2 + yC*2*(y2-y3) = 0
	//=>
	//xC = [x1^2-x2^2 + y1^2-y2^2 + yC*2*(y2-y1)] / [2*(x1-x2)]
	//=> (Substitute)
	//x3^2-x2^2 + {[x1^2-x2^2 + y1^2-y2^2 + yC*2*(y2-y1)] / [2*(x1-x2)]}*2*(x2-x3) + y3^2-y2^2 + yC*2*(y2-y3) = 0
	//x3^2-x2^2 + {[x1^2-x2^2 + y1^2-y2^2 + yC*2*(y2-y1)] / (x1-x2)}*(x2-x3) + y3^2-y2^2 + yC*2*(y2-y3) = 0
	//x3^2-x2^2 + {[x1^2-x2^2 + y1^2-y2^2] / (x1-x2)}*(x2-x3) + yC*2*(y2-y1)*(x2-x3)/(x1-x2) + y3^2-y2^2 + yC*2*(y2-y3) = 0
	//=>
	//yC*2*(y2-y1)*(x2-x3)/(x1-x2) + yC*2*(y2-y3) = -x3^2+x2^2 - {[x1^2-x2^2 + y1^2-y2^2] / (x1-x2)}*(x2-x3) - y3^2+y2^2
	//yC*[2*(y2-y1)*(x2-x3)/(x1-x2) + 2*(y2-y3)] = -x3^2+x2^2 - {[x1^2-x2^2 + y1^2-y2^2] / (x1-x2)}*(x2-x3) - y3^2+y2^2
	//yC = {-x3^2+x2^2 - {[x1^2-x2^2 + y1^2-y2^2] / (x1-x2)}*(x2-x3) - y3^2+y2^2} / [2*(y2-y1)*(x2-x3)/(x1-x2) + 2*(y2-y3)]
	
	//Test:
	//4-1 + xC*2*(1-2) + 16-1 + yC*2*(1-4) = 0
	//3 - 2*xC + 15 - 6*yC = 0
	//-2*xC + 18 - 6*yC = 0
	//-2*xC - 6*(yC - 3) = 0
	
	public CircleSegment(double[][] threePoints){
		segmentStartPoint = threePoints[0];
		segmentEndPoint = threePoints[2];
		
		try{
			yC = centerY(threePoints);
			xC = centerX(threePoints[0][0], threePoints[1][0], threePoints[0][1], threePoints[1][1], yC);
			r = radius(threePoints[0][0], xC, threePoints[0][1], yC);
		}catch(Exception ex){
			Logger.error("Possible division by zero exception, no circle for three points given. TODO: Default to line segment.", ex);
		}
		
		//1. We have triangle with every side length known.
		//2. Find height.
		Point[] trianglePoints = new Point[]{new Point(xC,yC), new Point(threePoints[1][0], threePoints[1][1]), new Point(threePoints[2][0], threePoints[2][1])};
		double h = findHeight(trianglePoints, 2);
		double hyp = pointDistance(trianglePoints[0], trianglePoints[2]);
		//3. Height and a side length will allow using asin function.
		double segmentAngle = Math.toDegrees(Math.asin(h/hyp));
		//4. Angle/360*circumference = length.
		length = 2*Math.PI*r*segmentAngle/360;
	}
	
	/**Find height with respect to point index given and the opposite triangle side.*/
	private double findHeight(Point[] points, int heightPointIndex){
		//Heron's formular: A = sqrt[s*(s-a)*(sb)*(s-c)] and s = (a+b+c)/2
		//A = a*h/2
		//a*h/2=sqrt[s*(s-a)*(sb)*(s-c)]
		//h=2*sqrt[s*(s-a)*(sb)*(s-c)]/a
		double a = pointDistance(points[(heightPointIndex+2)%3], points[(heightPointIndex+1)%3]);
		double b = pointDistance(points[heightPointIndex], points[(heightPointIndex+1)%3]);
		double c = pointDistance(points[(heightPointIndex+2)%3], points[heightPointIndex]);
		double s = (a+b+c)/2;
		
		return 2*Math.sqrt(s*(s-a)*(s-b)*(s-c))/a;
	}
	
	private double radius(double x1, double xC, double y1, double yC){
		//(x1-xC)^2+(y1-yC)^2-r^2=0
		//=>
		//r=sqrt[(x1-xC)^2+(y1-yC)^2]
		
		return Math.sqrt(Math.pow(x1-xC,2)+Math.pow(y1-yC,2));
	}
	
	private double centerX(double x1, double x2, double y1, double y2, double yC){
		//xC = [x1^2-x2^2 + y1^2-y2^2 + yC*2*(y2-y1)] / [2*(x1-x2)]
		double nominator = Math.pow(x1, 2)-Math.pow(x2, 2)+Math.pow(y1, 2)-Math.pow(y2, 2)+yC*2*(y2-y1);
		double denominator = 2*(x1-x2);
		return nominator/denominator;
	}
	
	private double centerY(double[][] threePoints){
		double x1 = threePoints[0][0];
		double x2 = threePoints[1][0];
		double x3 = threePoints[2][0];
		
		double y1 = threePoints[0][1];
		double y2 = threePoints[1][1];
		double y3 = threePoints[2][1];
		
		//yC = {-x3^2+x2^2 - {[x1^2-x2^2 + y1^2-y2^2] / (x1-x2)}*(x2-x3) - y3^2+y2^2} / [2*(y2-y1)*(x2-x3)/(x1-x2) + 2*(y2-y3)]
		double nominator, denominator;
		
		nominator = -Math.pow(x3, 2)+Math.pow(x2, 2) - (x2-x3)*(Math.pow(x1, 2)-Math.pow(x2, 2)+Math.pow(y1, 2)-Math.pow(y2, 2))/(x1-x2) - Math.pow(y3, 2)+Math.pow(y2, 2);
		denominator = 2*(y2-y1)*(x2-x3)/(x1-x2)+2*(y2-y3);
		
		return nominator / denominator;
	}
	
	public static double pointDistance(Point a, Point b){
		double dx = Math.abs(a.x-b.x);
		double dy = Math.abs(a.y-b.y);
		
		return Math.sqrt(Math.pow(dx, 2) + Math.pow(dy,  2));
	}
}

