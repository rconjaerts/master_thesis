package vub.robrecht.conjaerts;

public class Result {
	double[] times;
	double[] means;
	int[] cardinalities;
	
	public Result(int iterations){
		this.times = new double[iterations];
		this.means = new double[iterations];
		this.cardinalities = new int[iterations];
	}
	
	public void addResult(int number, double time, double mean, int cardinality){
		times[number] = time;
		means[number] = mean;
		cardinalities[number] = cardinality;
	}
}
