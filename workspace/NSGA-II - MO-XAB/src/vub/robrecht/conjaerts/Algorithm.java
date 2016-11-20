package vub.robrecht.conjaerts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import Problems.Problem;

public class Algorithm {
	// Stopping criterion
	int maxPulls;
	// N: number of subproblems
	int popSize;
	Problem problem;
	int numberOfObjectives;
	double[] maxResults;
	double[] minResults;
	
	public Algorithm(int maxPulls,int popSize, Problem problem){
		this.maxPulls=maxPulls;
		this.popSize=popSize;
		this.problem=problem;
		this.numberOfObjectives = problem.getResultSize();
		this.maxResults = new double[numberOfObjectives];
		this.minResults = new double[numberOfObjectives];
	}

	public ArrayList<Solution> run() {
		ArrayList<Solution> solutions = new ArrayList<Solution>();
		for (int i = 0; i < popSize; i++) {
			Solution sol = new Solution(true);
			solutions.add(sol);
			maxPulls--;
			updateMinMax(sol, maxResults, minResults);
		}

		ArrayList<Solution> newSolutions = new ArrayList<Solution>();

		for (int i = 0; i < popSize; i++) {
			// ADJUST THIS, PARENTS NOT SELCETED RANDOMLY!!!!
			int[] parentIndexes = getParentsRandom(popSize);
			Solution parent1 = solutions.get(parentIndexes[0]);
			Solution parent2 = solutions.get(parentIndexes[1]);

			ArrayList<Solution> children = getChildren(parent1, parent2);
			// 1% mutation
			children.get(0).mutateSolution(0.01);
			children.get(1).mutateSolution(0.01);
			// After mutation, we pull the arm
			maxPulls = maxPulls - 2;

			Solution newSolution;
			// We check which child dominates who, in case of no-domination we
			// choose randomly
			if (children.get(0).checkDomination(children.get(1))) {
				newSolution = children.get(0);
			} else if (children.get(1).checkDomination(children.get(0))) {
				newSolution = children.get(1);
			} else {
				Random r = new Random();
				if (r.nextInt(2) == 0) {
					newSolution = children.get(0);
				} else {
					newSolution = children.get(1);
				}
			}
			newSolutions.add(newSolution);
			updateMinMax(newSolution, maxResults, minResults);
		}
		solutions.addAll(newSolutions);

		while (maxPulls > 0) {
			// We add the created children with the parents,

			ArrayList<ArrayList<Solution>> nonDominatedFronts = fastNonDominatedSort(solutions);
			calculateCrowdingDistance(nonDominatedFronts, numberOfObjectives,
					maxResults, minResults);

			ArrayList<Solution> nextSolutions = new ArrayList<Solution>();
			int count = 0;
			while ((nonDominatedFronts.get(count).size() + nextSolutions.size()) <= popSize) {
				nextSolutions.addAll(nonDominatedFronts.get(count));
				count++;
			}

			ArrayList<Solution> lastUsedFront = nonDominatedFronts.get(count);
			Collections.sort(lastUsedFront, new Comparator<Solution>() {
				@Override
				public int compare(Solution sol1, Solution sol2) {
					return Double.compare(sol1.getCrowdingDistance(),
							sol2.getCrowdingDistance());
				}
			});

			int getLast = 0;
			while (popSize > nextSolutions.size()) {
				nextSolutions.add(lastUsedFront.get(getLast));
				getLast++;
			}

			nextSolutions.addAll(generateOffspring(nextSolutions));
			// We created 200 new children, but only used 100
			maxPulls = maxPulls - 200;
			solutions.clear();
			solutions.addAll(nextSolutions);
		}
		getActualResult(solutions);
		return solutions;
	}

	private void getActualResult(ArrayList<Solution> solutions) {
		for(Solution sol : solutions){
			sol.setResult(sol.getActualResult());
		}
	}

	private ArrayList<Solution> generateOffspring(
			ArrayList<Solution> nextSolutions) {
		ArrayList<Solution> offspring = new ArrayList<Solution>();
		for (int i = 0; i < nextSolutions.size(); i++) {
			Solution[] parents = getParentsCrowdingDistance(nextSolutions);

			ArrayList<Solution> children = getChildren(parents[0], parents[1]);
			// Polynomial Mutation, something is wrong!
			// children.get(0).polynomialMutation(mutationRate,
			// distributionIndex);
			// children.get(1).polynomialMutation(mutationRate,
			// distributionIndex);
			// 1% mutation
			children.get(0).mutateSolution(0.01);
			children.get(1).mutateSolution(0.01);

			Solution newSolution;
			// We check which child dominates who, in case of no-domination we
			// choose randomly
			if (children.get(0).checkDomination(children.get(1))) {
				newSolution = children.get(0);
			} else if (children.get(1).checkDomination(children.get(0))) {
				newSolution = children.get(1);
			} else {
				Random r = new Random();
				if (r.nextInt(2) == 0) {
					newSolution = children.get(0);
				} else {
					newSolution = children.get(1);
				}
			}
			offspring.add(newSolution);
		}

		return offspring;
	}

	private Solution[] getParentsCrowdingDistance(
			ArrayList<Solution> nextSolutions) {
		ArrayList<Solution> groupToSelectFrom = copyArrayList(nextSolutions);
		int tournamentSize = nextSolutions.size() / 2;
		Solution[] randomSolution = new Solution[tournamentSize];
		for (int i = 0; i < tournamentSize; i++) {
			Random r = new Random();
			randomSolution[i] = groupToSelectFrom.remove(r
					.nextInt(groupToSelectFrom.size()));
		}
		Arrays.sort(randomSolution, new Comparator<Solution>() {
			@Override
			public int compare(Solution sol1, Solution sol2) {
				return Double.compare(sol1.getCrowdingDistance(),
						sol2.getCrowdingDistance());
			}

		});
		return new Solution[] { randomSolution[0], randomSolution[1] };
	}

	// This function was created purely to copy an arraylist so it's a separate
	// object and it doesn't point to the same on
	private ArrayList<Solution> copyArrayList(ArrayList<Solution> oldList) {
		ArrayList<Solution> newList = new ArrayList<Solution>();
		for (int i = 0; i < oldList.size(); i++) {
			newList.add(oldList.get(i));
		}
		return newList;
	}

	private void calculateCrowdingDistance(
			ArrayList<ArrayList<Solution>> nonDominatedFronts,
			int numberOfObjectives, double[] maxResults, double[] minResults) {
		for (ArrayList<Solution> front : nonDominatedFronts) {
			int l = front.size();
			for (Solution sol : front) {
				sol.setCrowdingDistance(0.0);
			}

			for (int i = 0; i < numberOfObjectives; i++) {
				final int count = i;
				Collections.sort(front, new Comparator<Solution>() {
					@Override
					public int compare(Solution sol1, Solution sol2) {
						return Double.compare(sol1.getResult()[count],
								sol2.getResult()[count]);
					}
				});
				front.get(0).setCrowdingDistance(Double.POSITIVE_INFINITY);
				front.get(l - 1).setCrowdingDistance(Double.POSITIVE_INFINITY);
				for (int j = 1; j < l - 1; j++) {
					front.get(j)
							.setCrowdingDistance(
									front.get(j).getCrowdingDistance()
											+ ((front.get(j + 1).getResult()[i] - front
													.get(j - 1).getResult()[i]) / (maxResults[i] - minResults[i])));
				}
			}
		}
	}

	private void updateMinMax(Solution sol, double[] maxResults,
			double[] minResults) {
		int count = 0;
		for (double result : sol.getResult()) {
			maxResults[count] = Math.max(maxResults[count], result);
			minResults[count] = Math.min(minResults[count], result);
			count++;
		}
	}

	private ArrayList<ArrayList<Solution>> fastNonDominatedSort(
			ArrayList<Solution> solutions) {
		ArrayList<ArrayList<Solution>> nonDominatedFronts = new ArrayList<ArrayList<Solution>>();
		ArrayList<Solution> firstFront = new ArrayList<Solution>();

		for (Solution sol : solutions) {
			ArrayList<Solution> dominatedBySol = new ArrayList<Solution>();
			sol.setDominationCounter(0);
			for (Solution compareSol : solutions) {
				if (sol.checkDomination(compareSol)) {
					dominatedBySol.add(compareSol);
				} else if (compareSol.checkDomination(sol)) {
					sol.incrementDominationCounter();
				}
			}
			if (sol.getDominationCounter() == 0) {
				sol.setRank(1);
				firstFront.add(sol);
			}
			sol.setDominatedBySol(dominatedBySol);
		}
		nonDominatedFronts.add(firstFront);
		int i = 0;
		while (!nonDominatedFronts.get(i).isEmpty()) {
			ArrayList<Solution> nextFront = new ArrayList<Solution>();
			for (Solution sol : nonDominatedFronts.get(i)) {
				for (Solution dominatedSol : sol.getDominatedBySol()) {
					dominatedSol.decrementDominationCounter();
					if (dominatedSol.getDominationCounter() == 0) {
						dominatedSol.setRank(i + 1);
						nextFront.add(dominatedSol);
					}
				}
			}
			i++;
			nonDominatedFronts.add(nextFront);
		}
		nonDominatedFronts.remove(nonDominatedFronts.size() - 1);
		return nonDominatedFronts;
	}

	private ArrayList<Solution> getChildren(Solution parent1, Solution parent2) {
		// We split the parents on a random position
		Random r = new Random();
		int splitIndex = 0;
		while (splitIndex == 0) {
			splitIndex = r.nextInt(parent1.getInput().size());
		}

		// create offsprings and add them to a list
		ArrayList<Solution> offsprings = new ArrayList<Solution>();

		for (int i = 0; i < 2; i++) {
			Solution y = new Solution(false);
			ArrayList<Double> newInput = new ArrayList<Double>();
			for (int j = 0; j < parent1.getInput().size(); j++) {
				if (i == 0) {
					if (j < splitIndex) {
						newInput.add(parent1.getInput().get(j));
					} else {
						newInput.add(parent2.getInput().get(j));
					}
				} else {
					if (j < splitIndex) {
						newInput.add(parent2.getInput().get(j));
					} else {
						newInput.add(parent1.getInput().get(j));
					}
				}
			}
			y.setInput(newInput);
			offsprings.add(y);
		}
		return offsprings;
	}

	private int[] getParentsRandom(int popSize) {
		int[] parentIndexes = new int[2];
		Random r = new Random();
		parentIndexes[0] = r.nextInt(popSize);
		parentIndexes[1] = parentIndexes[0];
		while (parentIndexes[0] == parentIndexes[1]) {
			parentIndexes[1] = r.nextInt(popSize);
		}
		return parentIndexes;
	}
}
