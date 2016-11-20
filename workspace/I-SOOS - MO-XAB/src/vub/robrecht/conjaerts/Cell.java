package vub.robrecht.conjaerts;

import java.util.HashSet;
import java.util.Set;

import Problems.Problem;

public class Cell {
	private int splitSize;
	private int lastSplitDomain;
	private Set<WeightVector> members;
	double[] points;
	private double[][] pointDomains;
	private double[] result;
	 int evaluations;
	private int pullMin;
	private Cell[] children;
	private Problem problem;
	
	public Cell(int splitSize, Problem problem, int pullMin, WeightVector[] weightVectors){
		this.members = new HashSet<WeightVector>();
		addMembers(weightVectors);
		this.problem=problem;
		this.splitSize=splitSize;
		this.children= new Cell[splitSize];
		this.lastSplitDomain = problem.getNumberOfDomains();
		this.points = new double[problem.getNumberOfDomains()];	
		this.pointDomains = problem.getDomainSize();
		for(int i = 0; i<problem.getNumberOfDomains();i++){
			points[i] = (pointDomains[i][1] - pointDomains[i][0])/2;
		}
		this.evaluations=1;
		this.result = problem.calculateProblem(points,true);
		this.pullMin=pullMin;
		while(pullMin>evaluations){
			updateResults(problem.calculateProblem(points, true));
			evaluations++;
		}
	}
	
	public Set<WeightVector> getMembers() {
		return members;
	}

	private void addMembers(WeightVector[] weightVectors) {
		for(WeightVector vector : weightVectors){
			members.add(vector);
		}
	}

	public Cell(int splitSize, Problem problem, int pullMin, int lastSplitDomain, double[] points, double[][] pointDomains, WeightVector weightVector){
		this.members = new HashSet<WeightVector>();
		members.add(weightVector);
		this.problem=problem;
		this.splitSize=splitSize;
		this.children= new Cell[splitSize];
		this.lastSplitDomain = lastSplitDomain+1;
		this.points = points;
		this.pointDomains = pointDomains;
		this.evaluations=1;
		this.result = problem.calculateProblem(points, true);
		this.pullMin=pullMin;
	}

	private void createChildren(WeightVector weightVector){
		int index = lastSplitDomain % problem.getNumberOfDomains();
		double interval = (pointDomains[index][1]-pointDomains[index][0])/splitSize;
		for(int i=0; i<splitSize;i++){			
			double[] newPoints = copyArray(points);
			double[][] newPointDomains = copyArray(pointDomains);
			newPointDomains[index][0] = newPointDomains[index][0] + i*interval;
			newPointDomains[index][1] = newPointDomains[index][0]+interval;
			newPoints[index] = newPointDomains[index][0]+(interval/2);
			children[i] = new Cell(splitSize, problem, pullMin, lastSplitDomain, newPoints, newPointDomains, weightVector);
		}
	}

	private double[][] copyArray(double[][] pointDomains2) {
		double[][] returnValue = new double[pointDomains2.length][2];
		for(int i = 0; i<pointDomains2.length;i++){
			for(int j = 0; j<pointDomains2[i].length;j++){
				returnValue[i][j] = pointDomains2[i][j];
			}
		}
		return returnValue;
	}

	private double[] copyArray(double[] points2) {
		double[] returnValue = new double[points2.length];
		for(int i = 0; i<points2.length;i++){
			returnValue[i] = points2[i];
		}
		return returnValue;
	}
	
	public int evaluate(WeightVector weightVector) {
		if(evaluations >= pullMin){
			createChildren(weightVector);
			return splitSize;
		}else{
			updateResults(problem.calculateProblem(points, true));
			evaluations++;
			return 1;
		}
	}

	private void updateResults(double[] calculateProblem) {
		for(int i=0;i<calculateProblem.length;i++){
			result[i] = (result[i]*evaluations+calculateProblem[i])/(evaluations+1);
		}
	}

	public Cell[] getChildren() {
		return children;
	}

	public double[] getResult() {
		return result;
	}

	public boolean isMember(WeightVector weightVector) {
		return members.contains(weightVector);
	}

	public void addMember(WeightVector weightVector) {
		members.add(weightVector);
	}

	public void actualResult() {
		result = problem.calculateProblem(points, false);
	}
}
