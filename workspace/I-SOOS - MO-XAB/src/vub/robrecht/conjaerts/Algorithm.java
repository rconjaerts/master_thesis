package vub.robrecht.conjaerts;

import java.util.ArrayList;

import Problems.Problem;

public class Algorithm {
	Problem problem;
	int popSize;
	int H;
	double[] zValues;
	WeightVector[] weightVectors;
	int armPulls;
	Cell root;
	int maxDepth, treeDepth;
	int c = 0;
	
	public Algorithm(Problem problem, int popSize, int splitSize, int pullMin, int maxDepth, double[] zValues){
		this.problem=problem;
		this.popSize=popSize;
		this.H = popSize-1;
		this.zValues = zValues;
		/*
		zValues = new double[problem.getResultSize()];
		for(int i=0; i<problem.getResultSize();i++){
			zValues[i] = Double.POSITIVE_INFINITY;
		}*/
		
		this.weightVectors = createWeightVectors(problem.getResultSize());
		this.root = new Cell(splitSize, problem, pullMin, weightVectors);
		updateZvalues(root);
		this.armPulls = pullMin;
		this.maxDepth=maxDepth;
		this.treeDepth=0;
	}

	public ArrayList<Cell> run(int maxArmPulls){
		while(maxArmPulls>armPulls){
			for(WeightVector weightVector : weightVectors){
				double bestResult = Double.POSITIVE_INFINITY;
				
				ArrayList<Cell> cellsAtDepth = new ArrayList<Cell>();
				cellsAtDepth.add(root);
				int currDepth=0;
				while(!cellsAtDepth.isEmpty() && currDepth < maxDepth){
					Cell bestCell = null;
					ArrayList<Cell> nextDepth = new ArrayList<Cell>();
					for(Cell cell : cellsAtDepth){
						if(!cell.isMember(weightVector)){
							cell.addMember(weightVector);
						}
						
						if(cell.getChildren()[0] != null){
							addToNextDepth(cell.getChildren(), nextDepth);
						}else if(calculateFitness(cell.getResult(), weightVector) < bestResult){
							bestCell = cell;
							bestResult = calculateFitness(cell.getResult(), weightVector);
						}
					}
					if(bestCell != null){
						armPulls+= bestCell.evaluate(weightVector);
						updateZvalues(bestCell);
						if(bestCell.getChildren()[0] != null){
							for(Cell child : bestCell.getChildren()){
								nextDepth.add(child);
								updateZvalues(child);
							}
						}
					}
					cellsAtDepth = new ArrayList<Cell>();
					cellsAtDepth.addAll(nextDepth);
					currDepth++;
				}
				treeDepth++;
			}
		}
		accurateInfo(root);
		ArrayList<Cell> bestSolutions = new ArrayList<Cell>();
		bestSolutions.add(root);
		checkSolutions(bestSolutions, root);
		return bestSolutions;
	}
	
	private void accurateInfo(Cell cell) {
		cell.actualResult();
		if(cell.getChildren()[0] != null){
			for(int i = 0;i<cell.getChildren().length;i++){
				accurateInfo(cell.getChildren()[i]);
			}
		}
	}

	private void checkSolutions(ArrayList<Cell> bestSolutions, Cell cell) {
		changeDominationList(bestSolutions, cell);
		if(cell.getChildren()[0] != null){
			for(int i = 0;i<cell.getChildren().length;i++){
				checkSolutions(bestSolutions, cell.getChildren()[i]);
			}
		}
	}
	
	private void changeDominationList(ArrayList<Cell> bestSolutions,
			Cell cell) {
		
		//Remove all solutions if new solution dominates them
		for(int j = 0; j<bestSolutions.size();j++){
			if(checkDomination(cell.getResult(), bestSolutions.get(j).getResult())){
				bestSolutions.remove(j);
			}
		}
		
		//Don't add solution y if someone dominates y, or there is already a same solution in EP
		boolean domination = false;
		for(Cell bestSol : bestSolutions){
			if(compareSols(bestSol,cell) || 
					checkDomination(bestSol.getResult(), cell.getResult())){
				domination = true;
			}
		}
		if(domination == false){
			bestSolutions.add(cell);
		}
	}
	
	private static boolean compareSols(Cell sol1, Cell sol2){
		for(int i = 0; i< sol1.getResult().length;i++){
			if(Math.floor(sol1.getResult()[i]*100000000)/100000000 == Math.floor(sol2.getResult()[i]*100000000)/100000000){
				return true;
			}
		}
		return false;
	}

	private boolean checkDomination(double[] cell, double[] bestEval) {
		boolean domination = false;
		for(int i = 0;i<cell.length;i++){
			if(cell[i] > bestEval[i]){
				return false;
			}else if(cell[i] < bestEval[i]){
				domination = true;
			}
		}
		return domination;
	}
	
	private void addToNextDepth(Cell[] children, ArrayList<Cell> nextDepth) {
		for(Cell cell : children){
			nextDepth.add(cell);
		}
	}

	private double calculateFitness(double[] result, WeightVector weightVector) {
		double fitness = Double.NEGATIVE_INFINITY;
		double[] weights = weightVector.getWeights();
		for (int i = 0; i < zValues.length; i++) {
			fitness = Math.max(fitness, weights[i] * Math.abs(result[i] - zValues[i]));
		}
		return fitness;
	}
	
	private void updateZvalues(Cell cell) {
		for(int i = 0; i<zValues.length;i++){
			zValues[i] = Math.min(zValues[i], cell.getResult()[i]);
		}
	}

	private WeightVector[] createWeightVectors(int numberOfObjectives) {
		WeightVector[] vectors;
		// Create uniform spread of N weight vectors
		if (numberOfObjectives == 2) {
			vectors = new WeightVector[popSize];
			for (double i = 0.0; i < popSize; i++) {
				double currWeight = (i / H);
				WeightVector vector = new WeightVector(currWeight,numberOfObjectives);
				vectors[(int) i]=vector;
			}
		} else {
			int count=0;
			vectors = new WeightVector[popSize*numberOfObjectives];
			for (double i = 0.0; i < popSize; i++) {
				double currWeight = (i / H);
				double otherWeights = (1 - currWeight) / (numberOfObjectives - 1);
				for (int j = 0; j < numberOfObjectives; j++) {
					WeightVector vector = new WeightVector(currWeight, otherWeights, j, numberOfObjectives);
					vectors[count]=vector;
					count++;
				}
			}
		}
		return vectors;
	}
}
