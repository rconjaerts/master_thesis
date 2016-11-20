package Problems;

import java.util.Random;

public class ZDT4 implements Problem{
	//Size of the input of the problem
	int numberOfDomains = 10;
	//the domain
	double[][] domainSize = new double[numberOfDomains][2];
	//Number of objectives
	int resultSize = 2;
	
	public ZDT4(){
		domainSize[0] = new double[]{0, 1};
		for(int i = 1; i<numberOfDomains;i++){
			domainSize[i] = new double[]{-5, 5};
		}
	}
	
	public double[] calculateProblem(double[] x, boolean noise){
		double[] objectives = new double[resultSize];
		objectives[0] = x[0];
		double g = 1 + 10*(numberOfDomains-1) + makeSum(x);
		objectives[1] = g*(1-Math.sqrt(objectives[0]/g));
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
	
	private double makeSum(double[] x){
		double sum = 0;
		for(int i = 1;i<numberOfDomains;i++){
			sum += Math.pow(x[i],2)-10*Math.cos(4*Math.PI*x[i]);
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
