package vub.robrecht.conjaerts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.Map.Entry;

import Problems.DTLZ1;
import Problems.DTLZ7;
import Problems.Problem;
import Problems.ZDT4;
import Problems.ZDT2;
import Problems.ZDT3;
import Problems.ZDT6;

public class Algorithm {
	// Stopping criterion
	int maxPulls = 0;
	// N: number of subproblems
	int popSize = 100;
	int H = popSize - 1;
	// Number of neighbors for weight vectors
	int T=0;
	Problem problem;
	int numberOfObjectives;

	double[] zValues = new double[] {Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY};

	// Contains weight vector per subproblem
	ArrayList<WeightVector> weightVectors = new ArrayList<WeightVector>();

	// Initialization
	// External population, also the return value
	ArrayList<PossibleSolution> EP = new ArrayList<PossibleSolution>();

	public Algorithm(int T, int maxPulls, Problem problem) {
		this.problem=problem;
		this.numberOfObjectives=problem.getResultSize();
		this.T = T;
		this.maxPulls = maxPulls;
	}

	public ArrayList<PossibleSolution> run() {
		// Create uniform spread of N weight vectors
		if (numberOfObjectives == 2) {
			for (double i = 0.0; i < popSize; i++) {
				double currWeight = (i / H);
				WeightVector vector = new WeightVector(currWeight,problem.getNumberOfDomains(), problem);
				weightVectors.add(vector);

				zValues = updateZValues(zValues, vector.getCurrSolution()
						.getfValue());
				maxPulls--;
			}
		} else {
			for (double i = 0.0; i < popSize; i++) {
				double currWeight = (i / H);
				double otherWeights = (1 - currWeight)
						/ (numberOfObjectives - 1);
				for (int j = 0; j < numberOfObjectives; j++) {
					WeightVector vector = new WeightVector(currWeight,otherWeights, j, numberOfObjectives,problem.getNumberOfDomains(), problem);
					weightVectors.add(vector);

					zValues = updateZValues(zValues, vector.getCurrSolution()
							.getfValue());
					maxPulls--;
				}
			}
		}

		/*
		 * We compute the Euclidean distance between all the vectors, and keep
		 * the T closest ones for every vector. This list of indexes are put in
		 * the arraylist bValues.
		 */
		for (WeightVector weightVector1 : weightVectors) {
			HashMap<Integer, Double> hashMap = new HashMap<Integer, Double>();
			// Put all vectors with their distance in hashmap
			for (int j = 0; j < weightVectors.size(); j++) {
				double distance = 0;
				WeightVector vector2 = weightVectors.get(j);
				for (int k = 0; k < numberOfObjectives; k++) {
					distance = distance
							+ Math.pow(
									(weightVector1.getWeights().get(k) - vector2
											.getWeights().get(k)), 2);
				}
				hashMap.put(j, Math.sqrt(distance));
			}
			// sort the hashmap on distance
			ValueCompare vc = new ValueCompare(hashMap);
			TreeMap<Integer, Double> sortedMap = new TreeMap<Integer, Double>(
					vc);
			sortedMap.putAll(hashMap);

			// Get the T closest vectors and add them to the list.
			int count = 0;
			ArrayList<Integer> indexes = new ArrayList<Integer>();
			for (Entry<Integer, Double> entry : sortedMap.entrySet()) {
				if (count < T) {
					count++;
					indexes.add(entry.getKey());
				}
			}
			weightVector1.setClosestVectors(indexes);
		}

		// Main loop
		do {
			for (WeightVector weightVector : weightVectors) {
				/*
				 * Create a list filled with solution indexes and their
				 * probability of being chosen This probability is calculated
				 * based on the tchebycheff value of each solution
				 */
				HashMap<double[], Integer> possibleParents = new HashMap<double[], Integer>();
				double totalFitnessLevel = 0;
				double sumProbabilities = 0;
				PossibleSolution parent = weightVector.getCurrSolution();
				for (int index : weightVector.getClosestVectors()) {
					double fitnessLevel = calculateTchebycheff(
							weightVectors.get(index), zValues, parent);
					totalFitnessLevel = totalFitnessLevel + fitnessLevel;
				}

				for (int index : weightVector.getClosestVectors()) {
					double[] lowerUpper = new double[2];
					double fitnessLevel = calculateTchebycheff(
							weightVectors.get(index), zValues, parent);
					double probability = (2.0 / T)
							- (fitnessLevel / totalFitnessLevel);
					lowerUpper[0] = sumProbabilities;
					sumProbabilities = sumProbabilities + probability;
					lowerUpper[1] = sumProbabilities;
					possibleParents.put(lowerUpper, index);
				}

				// Choose two solutions based on their fitnesslevel which is
				// related to the probability
				int[] indexes = getTwoIndexes(possibleParents);
				PossibleSolution xK = weightVectors.get(indexes[0])
						.getCurrSolution();
				PossibleSolution xL = weightVectors.get(indexes[1])
						.getCurrSolution();

				// We use 1 point crossover and choose the best offspring of
				// both children.
				ArrayList<PossibleSolution> offspring = getOffspring(xK, xL,
						problem);
				// offspring.get(0).polynomialMutation(weightVector.problem);
				// offspring.get(1).polynomialMutation(weightVector.problem);
				offspring.get(0).mutateSolution(0.01, problem);
				offspring.get(1).mutateSolution(0.01, problem);
				// After mutation we pull the arms
				maxPulls = maxPulls - 2;

				PossibleSolution y;
				// We check which child dominates who, in case of no-domination
				// we choose randomly
				if (offspring.get(0).checkDomination(offspring.get(1))) {
					y = offspring.get(0);
				} else if (offspring.get(1).checkDomination(offspring.get(0))) {
					y = offspring.get(1);
				} else {
					Random r = new Random();
					if (r.nextInt(2) == 0) {
						y = offspring.get(0);
					} else {
						y = offspring.get(1);
					}
				}

				// Update z-values and neighboring solutions
				zValues = updateZValues(zValues, y.getfValue());
				updateNeighboringSolutions(weightVector, zValues, y,
						weightVectors);

				// Remove all solutions if new solution y dominates them
				for (int j = 0; j < EP.size(); j++) {
					if (y.checkDomination(EP.get(j))) {
						EP.remove(j);
					}
				}

				// Don't add solution y if someone dominates y, or there is
				// already a same solution in EP
				boolean domination = false;
				for (PossibleSolution sol : EP) {
					if (sol.checkEquality(y) || sol.checkDomination(y)) {
						domination = true;
					}
				}
				if (domination == false) {
					EP.add(y);
				}
			}
		} while (maxPulls > 0);
		for(PossibleSolution sol : EP){
			sol.getActualResult(problem);
		}
		return EP;
	}

	/*
	 * We use 1-point crossover and choose the best offspring
	 */
	private ArrayList<PossibleSolution> getOffspring(PossibleSolution xK,
			PossibleSolution xL, Problem problem) {
		int numberOfDomains;
		numberOfDomains = problem.getNumberOfDomains();
		// We split the parents on a random position
		Random r = new Random();
		int splitIndex;
		do {
			splitIndex = r.nextInt(numberOfDomains);
		} while (splitIndex == 0);

		// create offsprings and add them to a list
		ArrayList<PossibleSolution> offsprings = new ArrayList<PossibleSolution>();

		for (int i = 0; i < 2; i++) {
			PossibleSolution y = new PossibleSolution(
					problem.getNumberOfDomains());
			ArrayList<Double> newValues = new ArrayList<Double>();
			for (int j = 0; j < numberOfDomains; j++) {
				if (i == 0) {
					if (j < splitIndex) {
						newValues.add(xK.getValues().get(j));
					} else {
						newValues.add(xL.getValues().get(j));
					}
				} else {
					if (j < splitIndex) {
						newValues.add(xL.getValues().get(j));
					} else {
						newValues.add(xK.getValues().get(j));
					}
				}
			}
			y.setValues(newValues);
			offsprings.add(y);
		}
		return offsprings;
	}

	// Update neighbor solutions
	private int[] getTwoIndexes(HashMap<double[], Integer> possibleParents) {
		Random r = new Random();
		int[] indexes = new int[2];
		for (int j = 0; j < 2; j++) {
			double number = r.nextDouble();
			Iterator<Entry<double[], Integer>> it = possibleParents.entrySet()
					.iterator();
			while (it.hasNext()) {
				Map.Entry pairs = (Map.Entry) it.next();
				double[] key = (double[]) pairs.getKey();
				if (number >= key[0] && number < key[1]) {
					indexes[j] = (Integer) pairs.getValue();
				}
			}
		}
		return indexes;
	}

	private void updateNeighboringSolutions(WeightVector weightVector,
			double[] zValues, PossibleSolution y,
			ArrayList<WeightVector> weightVectors) {
		ArrayList<Integer> closestVectors = weightVector.getClosestVectors();
		for (int j = 0; j < closestVectors.size(); j++) {
			int index = closestVectors.get(j);
			if (checkTchebycheff(y, weightVector, zValues,
					weightVectors.get(index).getCurrSolution())) {
				weightVectors.get(index).setCurrSolution(y);
			}
		}
	}

	// Sees which solution has the lowest tchebycheff value
	private boolean checkTchebycheff(PossibleSolution y,
			WeightVector weightVector, double[] zValues, PossibleSolution x) {
		double tchebycheffX = calculateTchebycheff(weightVector, zValues, x);
		double tchebycheffY = calculateTchebycheff(weightVector, zValues, y);

		if (tchebycheffY <= tchebycheffX) {
			return true;
		} else {
			return false;
		}
	}

	// Calculates the tchebycheff value
	private double calculateTchebycheff(WeightVector weightVector,
			double[] zValues, PossibleSolution sol) {
		double tchebycheff = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < zValues.length; i++) {
			double tempX = weightVector.getWeights().get(i)
					* Math.abs(sol.getfValue()[i] - zValues[i]);
			if (tempX > tchebycheff) {
				tchebycheff = tempX;
			}
		}
		return tchebycheff;
	}

	// updates z-values
	private double[] updateZValues(double[] zValues, double[] fValue) {
		if (fValue[0] < zValues[0]) {
			zValues[0] = fValue[0];
		}
		if (fValue[1] < zValues[1]) {
			zValues[1] = fValue[1];
		}
		return zValues;
	}
}
