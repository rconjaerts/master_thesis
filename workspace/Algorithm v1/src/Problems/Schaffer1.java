package Problems;

public class Schaffer1 {
	public static double[] calculateSchaffer(double x){
		double[] objectives = new double[2];
		objectives[0] = Math.pow(x, 2);
		objectives[1] = Math.pow((x-2), 2);
		return objectives;
	}
}
