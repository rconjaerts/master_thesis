package vub.robrecht.conjaerts;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import Problems.DTLZ1;
import Problems.DTLZ7;
import Problems.Problem;
import Problems.ZDT4;
import Problems.ZDT2;
import Problems.ZDT3;
import Problems.ZDT6;

public class Main {

	public static void main(String[] args) throws FileNotFoundException {
		Problem problem = new ZDT2();
		
		String line = "";
		double[][] PF = new double[1000][problem.getResultSize()];
		int count=0;
		try{
			BufferedReader br = new BufferedReader(new FileReader("Pareto Fronts/zdt2_pf.csv"));
			String headers = br.readLine();
			while ((line = br.readLine()) != null) {
				String[] strObject=line.split(",");
				for(int i = 0; i<strObject.length;i++){
					PF[count][i] = Double.parseDouble(strObject[i]);
				}
				count++;	
			}
		}catch (Exception e){
			//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
		
		ArrayList<Solution>bestSolutions = new ArrayList<Solution>();
		double bestResult = Double.POSITIVE_INFINITY;
		
		int iterations = 30;
		Result res = new Result(iterations);
		for(int i = 0; i< iterations;i++){
			long startTime = System.currentTimeMillis();
			Algorithm a = new Algorithm(30000, 100, problem);
			ArrayList<Solution> solutions = a.run();
			long endTime = System.currentTimeMillis();
			
			ArrayList<Solution>ndSet = new ArrayList<Solution>();
			ndSet.add(solutions.remove(0));
			for(Solution sol : solutions){
				changeDominationList(ndSet, sol);
			}
			
			double[] iterRes = Quality.IGD(PF, ndSet);
			res.addResult(i, (endTime-startTime), calculateAverage(iterRes), Quality.Cardinality(ndSet));
			if(calculateAverage(res.means) < bestResult){
				bestSolutions.clear();
				bestSolutions.addAll(ndSet);
				bestResult = calculateAverage(res.means);
			}
		}
		for(int i = 0; i<30;i++){
			System.out.print("NSGA-II, ");
		}
		System.out.println();
		String check = Arrays.toString(res.means);
		check = check.substring(1);
		check = check.substring(0, check.length()-1);
		System.out.print(check+", ");
		//System.out.println("NSGA-II & " +calculateAverage(res.means)+ " (" +calculateStDev(res.means)+ ") & "+calculateAverage(res.cardinalities)+" & " + calculateAverage(res.times) +"\\\\");
		//print(bestSolutions);
	}
	
	private static boolean checkDomination(double[] cell, double[] bestEval) {
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
	
	private static void changeDominationList(ArrayList<Solution> ndSet,Solution cell) {
		
		//Remove all solutions if new solution dominates them
		for(int j = 0; j<ndSet.size();j++){
			if(checkDomination(cell.getResult(), ndSet.get(j).getResult())){
				ndSet.remove(j);
			}
		}
		
		//Don't add solution y if someone dominates y, or there is already a same solution in EP
		boolean domination = false;
		for(Solution bestSol : ndSet){
			if(compareSols(bestSol, cell) || checkDomination(bestSol.getResult(), cell.getResult())){
				domination = true;
			}
		}
		if(domination == false){
			ndSet.add(cell);
		}
	}
	
	private static boolean compareSols(Solution sol1, Solution sol2){
		for(int i = 0; i< sol1.getResult().length;i++){
			if(Math.floor(sol1.getResult()[i]*100000000)/100000000 == Math.floor(sol2.getResult()[i]*100000000)/100000000){
				return true;
			}
		}
		return false;
	}
	
	private static void print(ArrayList<Solution> solutions){
		for (Solution sol : solutions) {
			String print = "";
			for (double result : sol.getResult()) {
				print = print.concat(String.valueOf(result) + ", ");
			}
			System.out.println(print.substring(0, print.length() - 2));
		}
	}
	
	private static double calculateAverage(int[] cardinalities) {
		double count = 0;
		for(int d : cardinalities){
			count += d;
		}
		return Math.floor(count/cardinalities.length*100)/100;
	}
	
	private static double calculateAverage(double[] list){
		double count = 0;
		for(double d : list){
			count += d;
		}
		return Math.floor(count/list.length*10000)/10000;
	}
	
	private static double calculateStDev(double[] list){
		double average = calculateAverage(list);
		double count = 0;
		for(double d : list){
			count += Math.pow(d - average, 2);;
		}
		return Math.floor(count/list.length*10000)/10000;
	}
}
