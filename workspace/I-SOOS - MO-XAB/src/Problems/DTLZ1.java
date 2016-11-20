package Problems;

import java.util.Random;

public class DTLZ1 implements Problem{
	//Size of the input of the problem
	int numberOfDomains = 10;
	//the domain
	double[][] domainSize = new double[numberOfDomains][2];
	//Number of objectives
	int resultSize = 3;
	
	public DTLZ1(){
		for(int i = 0; i<numberOfDomains;i++){
			domainSize[i] = new double[]{0, 1};
		}
	}
	
	public double[] calculateProblem(double[] x, boolean noise){
		double[] objectives = new double[resultSize];
		double g = 100*(numberOfDomains-2)+100*makeSum(x); 
		objectives[0] = (1+g)*x[0]*x[1];
		objectives[1] = (1+g)*x[0]*(1-x[1]);
		objectives[2] = (1+g)*(1-x[0]);
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
		for(int i = 2;i<numberOfDomains;i++){
			sum += Math.pow(x[i]-0.5, 2)-Math.cos(20*Math.PI*(x[i]-0.5));
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
