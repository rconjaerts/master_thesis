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

public class Solution {
	//The input
	ArrayList<Double> input = new ArrayList<Double>();
	//The result from an arm pull
	private double[] result;
	private int rank;
	private ArrayList<Solution> dominatedBySol = new ArrayList<Solution>();
	private int dominationCounter;
	private double crowdingDistance;
	Problem problem = new ZDT2();
	
	public double getCrowdingDistance() {
		return crowdingDistance;
	}

	public void setCrowdingDistance(double crowdingDistance) {
		this.crowdingDistance = crowdingDistance;
	}

	public int getDominationCounter() {
		return dominationCounter;
	}

	public void setDominationCounter(int dominationCounter) {
		this.dominationCounter = dominationCounter;
	}

	public Solution(boolean pullArm){
		if(pullArm){
			Random r = new Random();
			for(int i = 0; i<problem.getNumberOfDomains();i++){
				double domain = problem.getDomainSize()[i][1] - problem.getDomainSize()[i][0];
				input.add(problem.getDomainSize()[i][0] + domain*r.nextDouble());
			}
			result = problem.calculateProblem(input, true);
		}
	}
	
	public boolean checkDomination(Solution sol) {
		boolean domination = false;
		for(int i = 0;i<result.length;i++){
			if(result[i] > sol.getResult()[i]){
				return false;
			}else if(result[i] < sol.getResult()[i]){
				domination = true;
			}
		}
		return domination;
	}
	
	public void mutateSolution(double percentage) {
		Random r = new Random();
		int index = r.nextInt(input.size());
		double mutation = 1;
		
		if(r.nextInt(2)==0){
			mutation = mutation + percentage;
		}else{
			mutation = mutation - percentage;
		}
		
		input.set(index, input.get(index)*mutation);
		setResult(problem.calculateProblem(input, true));
	}
	
	public void incrementDominationCounter(){
		this.dominationCounter++;
	}

	public void decrementDominationCounter(){
		this.dominationCounter--;
	}
	
	public ArrayList<Double> getInput() {
		return input;
	}

	public void setInput(ArrayList<Double> input) {
		this.input = input;
	}

	public double[] getResult() {
		return result;
	}

	public void setResult(double[] result) {
		this.result = result;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public ArrayList<Solution> getDominatedBySol() {
		return dominatedBySol;
	}

	public void setDominatedBySol(ArrayList<Solution> dominatedBySol) {
		this.dominatedBySol = dominatedBySol;
	}

	public double[] getActualResult() {
		return problem.calculateProblem(input, false);
	}
}