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
	Problem problem;
	Cell cell = new Cell();
	
	public WeightVector(double currWeight, int numberOfDomains, Problem problem) {
		weights.add(currWeight);
		weights.add(1-currWeight);
		this.problem=problem;
	}
	
	public WeightVector(double currWeight, double otherWeights, int index, int numberOfObjectives, Problem problem) {
		for(int i = 0; i<numberOfObjectives-1;i++){
			weights.add(otherWeights);
		}
		weights.add(index, currWeight);
		this.problem = problem;
	}

	public ArrayList<Double> getWeights() {
		return weights;
	}

	public void setWeights(ArrayList<Double> weights) {
		this.weights = weights;
	}

	public Cell getCurrCell() {
		return cell;
	}

	public void setCurrCell(Cell cell) {
		this.cell = cell;
	}
}

