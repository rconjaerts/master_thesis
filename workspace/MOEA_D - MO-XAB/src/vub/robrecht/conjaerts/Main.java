package vub.robrecht.conjaerts;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;

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
			String header = br.readLine();
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
		
		ArrayList<PossibleSolution>bestSolutions = new ArrayList<PossibleSolution>();
		double bestResult = Double.POSITIVE_INFINITY;
		
		int iterations = 30;
		Result res = new Result(iterations);

		for(int i=0; i<iterations;i++){			
			Algorithm a = new Algorithm(20, 30000, problem);
			long startTime = System.currentTimeMillis();
			ArrayList<PossibleSolution> EP = a.run();
			
			long endTime = System.currentTimeMillis();
			double[] iterRes = Quality.IGD(PF, EP);
			res.addResult(i, (endTime-startTime), calculateAverage(iterRes), Quality.Cardinality(EP));
			if(calculateAverage(res.means) < bestResult){
				bestSolutions.clear();
				bestSolutions.addAll(EP);
				bestResult = calculateAverage(res.means);
			}
		}
		
		for(int i = 0; i<30;i++){
			System.out.print("MOEA/D, ");
		}
		System.out.println();
		String check = Arrays.toString(res.means);
		check = check.substring(1);
		check = check.substring(0, check.length()-1);
		System.out.print(check+", ");
		
		//System.out.println("MOEA/D & " +calculateAverage(res.means)+ " (" +calculateStDev(res.means)+ ") & "+calculateAverage(res.cardinalities)+" & " + calculateAverage(res.times) +"\\\\");
		//printEP(bestSolutions);
	}

	private static void printEP(ArrayList<PossibleSolution> EP){
		for(PossibleSolution sol : EP){
			String print = "";
			for(double fValue : sol.getfValue()){
				print = print.concat(String.valueOf(fValue) + ", ");
			}
			System.out.println(print.substring(0, print.length()-2));
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