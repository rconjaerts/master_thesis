package vub.robrecht.conjaerts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import Problems.Problem;

public class Algorithm {

	// Stopping criterion
	int maxPulls;
	// N: number of subproblems
	int popSize = 100;
	int H = popSize - 1;
	// Number of neighbors for weight vectors
	Problem problem;
	int numberOfObjectives;
	ArrayList<Cell> EP = new ArrayList<Cell>();

	double[] zValues = new double[] { Double.POSITIVE_INFINITY,
			Double.POSITIVE_INFINITY };

	// Contains weight vector per subproblem
	ArrayList<WeightVector> weightVectors = new ArrayList<WeightVector>();

	public Algorithm(int maxPulls, Problem problem) {
		this.maxPulls = maxPulls;
		this.problem = problem;
		this.numberOfObjectives = problem.getResultSize();
	}
	
	public ArrayList<Cell> run() {
		// Create uniform spread of N weight vectors
		if (numberOfObjectives == 2) {
			for (double i = 0.0; i < popSize; i++) {
				double currWeight = (i / H);
				WeightVector vector = new WeightVector(currWeight,
						problem.getNumberOfDomains(), problem);
				weightVectors.add(vector);

				zValues = updateZValues(zValues, vector.getCurrCell()
						.getResult());
				maxPulls--;
			}
		} else {
			for (double i = 0.0; i < popSize; i++) {
				double currWeight = (i / H);
				double otherWeights = (1 - currWeight)
						/ (numberOfObjectives - 1);
				for (int j = 0; j < numberOfObjectives; j++) {
					WeightVector vector = new WeightVector(currWeight,
							otherWeights, j, numberOfObjectives, problem);
					weightVectors.add(vector);

					zValues = updateZValues(zValues, vector.getCurrCell()
							.getResult());
					maxPulls--;
				}
			}
		}
		
		// Main loop
		do {
			for (WeightVector weightVector : weightVectors) {
				ArrayList<Cell> children = weightVector.getCurrCell()
						.splitCell();
				maxPulls = maxPulls - 3;
				Cell nextChild = getChild(children, weightVector, zValues);
				weightVector.setCurrCell(nextChild);
				zValues = updateZValues(zValues, nextChild.getResult());

				// Remove all solutions if new solution y dominates them
				for (int j = 0; j < EP.size(); j++) {
					if (nextChild.checkDomination(EP.get(j))) {
						EP.remove(j);
					}
				}

				// Don't add solution y if someone dominates y, or there is
				// already a same solution in EP
				boolean domination = false;
				for (Cell sol : EP) {
					if (sol.checkEquality(nextChild)
							|| sol.checkDomination(nextChild)) {
						domination = true;
					}
				}
				if (domination == false) {
					EP.add(nextChild);
				}
			}
		} while (maxPulls > 0);
		for(Cell cell : EP){
			cell.calculateActualResult();
		}
		return EP;
	}
	
	private Cell getChild(ArrayList<Cell> children, WeightVector weightVector,
			double[] zValues) {
		HashMap<double[], Cell> possibleParents = new HashMap<double[], Cell>();
		double totalFitnessLevel = 0;
		double sumProbabilities = 0;
		for (Cell cell : children) {
			double fitnessLevel = calculateTchebycheff(weightVector, zValues,
					cell);
			totalFitnessLevel = totalFitnessLevel + fitnessLevel;
		}

		for (Cell cell : children) {
			double[] lowerUpper = new double[2];
			double fitnessLevel = calculateTchebycheff(weightVector, zValues,
					cell);
			double probability = (2.0 / children.size())
					- (fitnessLevel / totalFitnessLevel);
			lowerUpper[0] = sumProbabilities;
			sumProbabilities = sumProbabilities + probability;
			lowerUpper[1] = sumProbabilities;
			possibleParents.put(lowerUpper, cell);
		}

		Random r = new Random();
		double number = r.nextDouble();
		Iterator<Entry<double[], Cell>> it = possibleParents.entrySet()
				.iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			double[] key = (double[]) pairs.getKey();
			if (number >= key[0] && number < key[1]) {
				return (Cell) pairs.getValue();
			}
		}
		return children.get(r.nextInt(children.size()));
	}
	
	//Calculates the tchebycheff value
	private double calculateTchebycheff(WeightVector weightVector, double[] zValues,
			Cell cell){
		double tchebycheff = Double.NEGATIVE_INFINITY;
		for(int i = 0; i<zValues.length;i++){
			double tempX = weightVector.getWeights().get(i)*Math.abs(cell.getResult()[i] - zValues[i]);
			if(tempX > tchebycheff){
				tchebycheff = tempX;
			}
		}
		return tchebycheff;
	}

	//updates z-values
	private double[] updateZValues(double[] zValues, double[] fValue) {
		zValues[0] = Math.min(fValue[0], zValues[0]);
		zValues[1] = Math.min(fValue[1], zValues[1]);
		return zValues;
	}
}
