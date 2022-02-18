package EllipseMath.projectionMath;

public class Ellipse {
	
	/**Circle arcs per degree.*/
	private static double precisionLevel = 10;
	
	//1. aelip function for angle from movemnt.
	//2. elip function for movement from angle change.
	//3. Very precise ellipse circumference equation by Ramanujan (especially when a and b are close):
	//C=PI*(a+b)(1+3h/(10+sqrt(4-3h)))
	//h = (a-b)^2/(a+b)^2
	
	/**Find angle from a movement along an elliptic arc.*/
	public static double aelip(double movement, double startAngle, double a, double b){
		//1. Need a circle arc object/helper functions -> start with functions/here.
		//2. Need to get angle change or partial change for movement along partial circle arc.
		
		//Need to get 3 points from ellipse for defining circles.
		//Start angle or specific angle segments? Start at exact start, end segment partial.
		
		int direction = movement > 0 ? 1 : -1;
		double[][] points;
		double moved = 0;
		CircleSegment segment;
		int i = 0;
		
		while(moved < Math.abs(movement)){
			points = find3Points(startAngle+(double)(direction*i)/(double)precisionLevel, a, b, direction);
			segment = new CircleSegment(points);
			if(moved+segment.length < Math.abs(movement))
				moved += segment.length;
			else
				break;
			i++;	
		}
		double remaining = Math.abs(movement) - moved;
		points = find3Points(startAngle+(double)(direction*i)/(double)precisionLevel, a, b, direction);
		segment = new CircleSegment(points);
		
		return (double)i/precisionLevel+(1d/precisionLevel)*remaining/segment.length;
	}
	
	/**
	 * @param angle Angle of mid point.
	 * @param angleInterval Change in angle to either side to generate the three points.
	 * @param a
	 * @param b
	 * @return
	 */
	private static double[][] find3Points(double angle, double angleInterval, double a, double b){
		double[][] result = new double[3][];
		
		result[0] = getEllipsePoint(angle-angleInterval, a, b);
		result[1] = getEllipsePoint(angle, a, b);
		result[2] = getEllipsePoint(angle+angleInterval, a, b);
		
		return result;
	}
	
	/**Finds 3 points on an ellipse separated by the precision level constant.
	 * @param angle Angle of mid point.
	 * @param a
	 * @param b
	 * @param direction Positive 1 for counterclockwise, else -1.
	 * @return
	 */
	private static double[][] find3Points(double angle, double a, double b, int direction){
		return find3Points(angle, direction/precisionLevel, a, b);
	}
	
	private static double[] getEllipsePoint(double angle, double a, double b){
		double[] result = new double[2];
		result[0] = a * Math.cos(Math.toRadians(angle));
		result[1] = b * Math.sin(Math.toRadians(angle));
		return result;
	}
	
	/**Find movement along an elliptic arc from a change in angle.*/
	public static double elip(double newAngle, double startAngle, double a, double b){
		
		double startPos = startAngle < 0 ? 360 - (Math.abs(startAngle)%360) : startAngle % 360;
		double newPos = newAngle < 0 ? 360 - (Math.abs(newAngle)%360) : newAngle % 360;
		
		double counter = newPos < startPos ? 360-startPos+newPos : newPos-startPos;
		double clockwise = startPos < newPos ? startPos+360-newPos : startPos-newPos;
		
		int direction = counter < clockwise ? 1 : -1;
		double targetAngle = counter < clockwise ? counter : clockwise;
		double[][] points;
		double moved = 0;
		CircleSegment segment;
		int i = 0;
		
		while((double)i/precisionLevel < targetAngle){

			if((double)i/precisionLevel+(double)1/precisionLevel < targetAngle)
				points = find3Points(startAngle+(double)(direction*i)/(double)precisionLevel, a, b, direction);
			else
				points = find3Points(startAngle+(double)(direction*i)/(double)precisionLevel, direction*(targetAngle-(double)i/precisionLevel), a, b);
			segment = new CircleSegment(points);
			
			moved += segment.length; //Points adjusted above for max accuracy.
			
			i++;	
		}
		return moved;
	}
	
	/**Very precise estimate of the elliptical circumference.*/
	public static double ramanujanEst(double a, double b){
		//C=PI*(a+b)(1+3h/(10+sqrt(4-3h)))
		//h = (a-b)^2/(a+b)^2
		
		double h = Math.pow(a-b, 2)/Math.pow(a+b, 2);
		
		return Math.PI*(a+b)*(1+3*h/(10+Math.sqrt(4-3*h)));
	}
}

