package Problems;

import java.util.ArrayList;
import java.util.Random;

public class DTLZ7 implements Problem{
	//Size of the input of the problem
	int numberOfDomains = 10;
	//the domain
	double[][] domainSize = new double[numberOfDomains][2];
	//Number of objectives
	int resultSize = 3;
	
	public DTLZ7(){
		for(int i = 0; i<numberOfDomains;i++){
			domainSize[i] = new double[]{0, 1};
		}
	}
	
	public double[] calculateProblem(ArrayList<Double> x, boolean noise){
		double[] objectives = new double[resultSize];
		objectives[0] = x.get(0);
		objectives[1] = x.get(1);
		double g = 1+(9/22)*makeSum(x); 
		double h = 3 - makeSpecialSum(x,g);
		objectives[2] = (1+g)*h;
		if(noise){
			objectives = doNoise(objectives);
		}
		return objectives;
	}
	
	private double[] doNoise(double[] objectives) {
		Random r = new Random();
		for(int i = 0; i<objectives.length;i++){
			objectives[i] = objectives[i] + objectives[i]*((r.nextInt(21)-10)/100);
		}
		return objectives;
	}
	
	private double makeSpecialSum(ArrayList<Double> x, double g) {
		double sum = 0;
		for(int i = 0;i<2;i++){
			sum += (x.get(i)/(1+g))*(1+Math.sin(3*Math.PI*x.get(i)));
		}
		return sum;
	}

	private double makeSum(ArrayList<Double> x){
		double sum = 0;
		for(int i = 0;i<numberOfDomains;i++){
			sum += x.get(i);
		}
		return sum;
	}
	
	@Override
	public int getNumberOfDomains() {
		return numberOfDomains;
	}

	@Override
	public double[][] getDomainSize() {
		return domainSize;
	}

	@Override
	public int getResultSize() {
		return resultSize;
	}
}
