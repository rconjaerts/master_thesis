package vub.robrecht.conjaerts;

import java.util.ArrayList;
import java.util.HashMap;

import Problems.DTLZ1;
import Problems.DTLZ7;
import Problems.Problem;
import Problems.ZDT4;
import Problems.ZDT2;
import Problems.ZDT3;
import Problems.ZDT6;

public class Cell {
	
	//If we want to use a different problem, we'll have to change that here.
	private Problem problem = new ZDT3();
	//The input points of this cell
	ArrayList<Double> points = new ArrayList<Double>();
	//The domain in which our points from our input lay
	ArrayList<double[]> pointDomains = new ArrayList<double[]>();
	//The result from an arm pull
	double[] result;
	// Amount of sub-cells a cell has to be split into
	int k = 3;
	private int lastSplitDomain;
	
	public Cell(){
		this.lastSplitDomain = problem.getNumberOfDomains();
		double[][] domainSize = problem.getDomainSize();
		for(int i = 0; i<problem.getNumberOfDomains();i++){
			pointDomains.add(domainSize[i]);
			points.add((domainSize[i][1] - domainSize[i][0])/2);
		}
		result = problem.calculateProblem(points, true);
	}
	
	public Cell(ArrayList<Double> points, ArrayList<double[]> pointDomains, int lastSplitDomain){
		this.pointDomains = pointDomains;
		this.points = points;
		this.lastSplitDomain = lastSplitDomain+1;
		result = problem.calculateProblem(points, true);
	}
	
	public ArrayList<Cell> splitCell(){
		ArrayList<Cell> children = new ArrayList<Cell>();
		int index = lastSplitDomain % problem.getNumberOfDomains();
		for(int i = 0; i<k;i++){
			ArrayList<double[]> newPointDomains = copyArrayListArray(pointDomains);
			ArrayList<Double> newPoints = copyArrayListDouble(points);
			double[] newDomain = new double[2];
			// To get the interval between the k-new domains
			double interval = (newPointDomains.get(index)[1]-newPointDomains.get(index)[0])/k;
			newDomain[0] = newPointDomains.get(index)[0] + i*interval;
			newDomain[1] = newDomain[0]+interval;
			newPointDomains.set(index, newDomain);
			newPoints.set(index, (newDomain[0]+(interval/2)));
			children.add(new Cell(newPoints, newPointDomains, lastSplitDomain));
		}
		return children;
	}
	
	public boolean checkEquality(Cell sol) {
		for(int i = 0;i<result.length;i++){
			if(result[i] != sol.getResult()[i]){
				return false;
			}
		}
		return true;
	}
	
	public boolean checkDomination(Cell sol) {
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

	// This function was created purely to copy an array so it's a separate object and it doesn't point to the same on
	private double[] copy2dArray(double[] oldArray){ 
		double[] newArray = new double[oldArray.length]; 
		for(int i = 0; i < oldArray.length; i++){ 
			newArray[i] = Double.valueOf(oldArray[i]);
		} 
		return newArray; 
	}
	
	// This function was created purely to copy an arraylist so it's a separate object and it doesn't point to the same on
	private ArrayList<Double> copyArrayListDouble(ArrayList<Double> oldList){
		ArrayList<Double> newList = new ArrayList<Double>();
		for(int i = 0; i < oldList.size(); i++){
			newList.add(oldList.get(i));
		}
		return newList;
	}
	
	// This function was created purely to copy an arraylist so it's a separate object and it doesn't point to the same on
	private ArrayList<double[]> copyArrayListArray(ArrayList<double[]> oldList){
		ArrayList<double[]> newList = new ArrayList<double[]>();
		for(int i = 0; i < oldList.size(); i++){
			newList.add(copy2dArray(oldList.get(i)));
		}
		return newList;
	}

	public ArrayList<Double> getPoints() {
		return points;
	}

	public void setPoints(ArrayList<Double> points) {
		this.points = points;
	}

	public ArrayList<double[]> getPointDomains() {
		return pointDomains;
	}

	public void setPointDomains(ArrayList<double[]> pointDomains) {
		this.pointDomains = pointDomains;
	}

	public double[] getResult() {
		return result;
	}

	public void setResult(double[] result) {
		this.result = result;
	}

	public void calculateActualResult() {
		result = problem.calculateProblem(points, false);
	}
}