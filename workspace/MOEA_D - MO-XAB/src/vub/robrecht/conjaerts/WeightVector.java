package vub.robrecht.conjaerts;

import java.util.ArrayList;
import java.util.Random;

import Problems.DTLZ1;
import Problems.DTLZ7;
import Problems.Problem;
import Problems.ZDT4;
import Problems.ZDT2;
import Problems.ZDT3;
import Problems.ZDT6;

public class WeightVector {
	ArrayList<Double> weights = new ArrayList<Double>();
	ArrayList<Integer> closestVectors = new ArrayList<Integer>();
	Problem problem;
	PossibleSolution currSolution;
	
	public WeightVector(double currWeight, int numberOfDomains, Problem problem) {
		this.problem=problem;
		this.currSolution  = new PossibleSolution(problem.getNumberOfDomains());
		weights.add(currWeight);
		weights.add(1-currWeight);
		
		Random r = new Random();
		ArrayList<Double> values = new ArrayList<Double>();
		
		for(int i = 0; i<numberOfDomains;i++){
			double domain = problem.getDomainSize()[i][1] - problem.getDomainSize()[i][0];
			values.add(problem.getDomainSize()[i][0] + domain*r.nextDouble());
		}
		currSolution.setValues(values);
		currSolution.setfValue(problem.calculateProblem(values, true));
	}
	
	public WeightVector(double currWeight, double otherWeights, int index, int numberOfObjectives, int numberOfDomains, Problem problem) {
		this.problem=problem;
		this.currSolution  = new PossibleSolution(problem.getNumberOfDomains());
		for(int i = 0; i<numberOfObjectives-1;i++){
			weights.add(otherWeights);
		}
		weights.add(index, currWeight);
		
		Random r = new Random();
		ArrayList<Double> values = new ArrayList<Double>();
		
		for(int i = 0; i<numberOfDomains;i++){
			double domain = problem.getDomainSize()[i][1] - problem.getDomainSize()[i][0];
			values.add(problem.getDomainSize()[i][0] + domain*r.nextDouble());
		}
		currSolution.setValues(values);
		currSolution.setfValue(problem.calculateProblem(values, true));
	}

	public ArrayList<Double> getWeights() {
		return weights;
	}

	public void setWeights(ArrayList<Double> weights) {
		this.weights = weights;
	}

	public ArrayList<Integer> getClosestVectors() {
		return closestVectors;
	}

	public void setClosestVectors(ArrayList<Integer> closestVectors) {
		this.closestVectors = closestVectors;
	}

	public PossibleSolution getCurrSolution() {
		return currSolution;
	}

	public void setCurrSolution(PossibleSolution currSolution) {
		this.currSolution = currSolution;
	}
}

