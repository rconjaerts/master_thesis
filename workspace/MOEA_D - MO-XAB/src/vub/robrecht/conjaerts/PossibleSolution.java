package vub.robrecht.conjaerts;

import java.util.ArrayList;
import java.util.Random;

import Problems.Problem;

public class PossibleSolution {
	double[] fValue;
	ArrayList<Double> values;
	//These variables are used for the polynomial mutation
	double mutationRate;
	int distributionIndex = 20;
	
	public PossibleSolution(int valuesSize){
		this.mutationRate = 1/valuesSize;
	}
	
	public double[] getfValue() {
		return fValue;
	}
	public void setfValue(double[] fValue) {
		this.fValue = fValue;
	}
	public ArrayList<Double> getValues() {
		return values;
	}
	public void setValues(ArrayList<Double> values) {
		this.values = values;
	}
	
	public void mutateSolution(double percentage, Problem problem) {
		Random r = new Random();
		int index = r.nextInt(values.size());
		double mutation = 1;
		
		if(r.nextInt(2)==0){
			mutation = mutation + percentage;
		}else{
			mutation = mutation - percentage;
		}
		
		values.set(index, values.get(index)*mutation);
		setfValue(problem.calculateProblem(values, true));
	}
	
	public void polynomialMutation(Problem problem) {
		Random r = new Random();
		for(int i = 0; i<values.size();i++){
			if(r.nextDouble() <= mutationRate){
				double domainDifference = problem.getDomainSize()[i][1]-problem.getDomainSize()[i][0];
				double delta1 = (values.get(i) - problem.getDomainSize()[i][0])/domainDifference;
				double delta2 = (problem.getDomainSize()[i][1] - values.get(i))/domainDifference;
				double prob = r.nextDouble();
				double deltaQ;
				if(prob <= 0.5){
					deltaQ = Math.pow(((2*prob)+(1-2*prob)*Math.pow((1-delta1),(distributionIndex+1))), (1/(distributionIndex+1))) - 1;
				}else{
					deltaQ = 1-Math.pow((2*(1-prob)+2*(prob-0.5)*Math.pow((1-delta2), (distributionIndex+1))), (1/(distributionIndex+1)));
				}
				values.set(i, (values.get(i) + deltaQ*domainDifference));
			}
		}
		setfValue(problem.calculateProblem(values, true));
	}
	
	public boolean checkEquality(PossibleSolution sol) {
		for(int i = 0;i<fValue.length;i++){
			if(Math.floor(fValue[i]*1000000)/1000000 != Math.floor(sol.getfValue()[i]*1000000)/1000000){
				return false;
			}
		}
		return true;
	}
	
	public boolean checkDomination(PossibleSolution sol) {
		boolean domination = false;
		for(int i = 0;i<fValue.length;i++){
			if(fValue[i] > sol.getfValue()[i]){
				return false;
			}else if(fValue[i] < sol.getfValue()[i]){
				domination = true;
			}
		}
		return domination;
	}

	public void getActualResult(Problem problem) {
		setfValue(problem.calculateProblem(values, false));
	}
}
