package Problems;

import java.util.ArrayList;
import java.util.Random;

public class ZDT3 implements Problem{
	//Size of the input of the problem
	int numberOfDomains = 30;
	//the domain
	double[][] domainSize = new double[numberOfDomains][2];
	//Number of objectives
	int resultSize = 2;
	
	public ZDT3(){
		for(int i = 0; i<numberOfDomains;i++){
			domainSize[i] = new double[]{0, 1};
		}
	}
	
	public double[] calculateProblem(ArrayList<Double> x, boolean noise){
		double[] objectives = new double[resultSize];
		objectives[0] = x.get(0);
		double g = 1 + 9*(makeSum(x)/(numberOfDomains-1));
		objectives[1] = g*(1-Math.sqrt(objectives[0]/g)-objectives[0]/g*Math.sin(10*Math.PI*objectives[0]));
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
	
	private double makeSum(ArrayList<Double> x){
		double sum = 0;
		for(int i = 1;i<numberOfDomains;i++){
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
