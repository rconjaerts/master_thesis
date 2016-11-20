package vub.robrecht.conjaerts;

import java.util.ArrayList;
import java.util.Random;

public class Algorithm {
	// Max amount of time we can pull an arm
	int maxPulls;
	// Max depth of the tree
	double maxDepth; 
	int k;
	// Number of function evaluations being made, also called number of arm
	// pulls
	int armPulls = 0;
	// We keep track of how deep the tree already is
	int treeDepth = 0;
	Cell root;
	
	public Algorithm(int maxPulls, int k){
		this.maxPulls = maxPulls;
		this.maxDepth = 10 * Math.sqrt(Math.pow(Math.log(maxPulls), 3));
		this.k=k;
		this.root= new Cell(k);
	}
	
	public ArrayList<Cell> run(){
		do{
			ArrayList<Cell> cellsToBeSplitted = new ArrayList<Cell>();
			double[] bestEval = root.getResult();
			
			ArrayList<Cell> cellsAtDepthI = new ArrayList<Cell>();
			cellsAtDepthI.add(root);
			int currTreeDepth = 0;
			do{
				ArrayList<Cell> posCellsToSplit = new ArrayList<Cell>();
				for(Cell cell : cellsAtDepthI){
					if(cell.getChildren().isEmpty()){
						if(posCellsToSplit.isEmpty() && checkDomination(cell.getResult(), bestEval)){
							posCellsToSplit.add(cell);
						}else{
							changeDominationList(posCellsToSplit, cell);
						}
					}
				}
				if(!posCellsToSplit.isEmpty()){
					Random r = new Random();
					cellsToBeSplitted.add(posCellsToSplit.get(r.nextInt(posCellsToSplit.size())));
				}
				
				int cellsCheckSize = cellsAtDepthI.size();
				for(int i = 0; i<cellsCheckSize;i++){
					cellsAtDepthI.addAll(cellsAtDepthI.get(0).getChildren());
					cellsAtDepthI.remove(0);
				}
				currTreeDepth++;
			}while(currTreeDepth <= Math.min(treeDepth, maxDepth));
			for(Cell cell : cellsToBeSplitted){
				cell.splitCell();
				armPulls = armPulls+cell.k;
			}
			cellsToBeSplitted.clear();
			treeDepth++;
		}while(armPulls <= maxPulls);
		officialResult(root);
		ArrayList<Cell> bestSolutions = new ArrayList<Cell>();
		bestSolutions.add(root);
		checkSolutions(bestSolutions, root);
		return bestSolutions;
	}
	
	private void officialResult(Cell cell) {
		cell.getOfficialResult();
		if(!cell.getChildren().isEmpty()){
			for(int i = 0;i<cell.getChildren().size();i++){
				officialResult(cell.getChildren().get(i));
			}
		}
	}

	private void checkSolutions(ArrayList<Cell> bestSolutions, Cell cell) {
		changeDominationList(bestSolutions, cell);
		if(!cell.getChildren().isEmpty()){
			for(int i = 0;i<cell.getChildren().size();i++){
				checkSolutions(bestSolutions, cell.getChildren().get(i));
			}
		}
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
	
	private static boolean compareSols(Cell sol1, Cell sol2){
		for(int i = 0; i< sol1.getResult().length;i++){
			if(Math.floor(sol1.getResult()[i]*100000000)/100000000 == Math.floor(sol2.getResult()[i]*100000000)/100000000){
				return true;
			}
		}
		return false;
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
			if(compareSols(bestSol,cell) || checkDomination(bestSol.getResult(), cell.getResult())){
				domination = true;
			}
		}
		if(domination == false){
			bestSolutions.add(cell);
		}
	}
}
