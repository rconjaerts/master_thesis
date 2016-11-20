package vub.robrecht.conjaerts;

import java.io.BufferedReader;
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

	public static void main(String[] args) {
		int maxArmPulls = 30000;
		int maxDepth = (int) Math.floor(10*Math.sqrt(Math.pow(Math.log(maxArmPulls), 3)));
		int minPulls = (int) Math.floor(maxArmPulls/Math.pow(Math.log(maxArmPulls),3));
		
			Problem problem = new DTLZ7();
			double[] zValues = new double[]{0,0,0};
			
			String line = "";
			//1000,2500,2401
			double[][] PF = new double[1000][problem.getResultSize()];
			int count=0;
			try{
				String file = "Pareto Fronts/zdt2_pf.csv";
				@SuppressWarnings("resource")
				BufferedReader br = new BufferedReader(new FileReader(file));
				br.readLine();
				while ((line = br.readLine()) != null) {
					String[] strObject=line.split(",");
					for(int j = 0; j<strObject.length;j++){
						PF[count][j] = Double.parseDouble(strObject[j]);
					}
					count++;	
				}
			}catch (Exception e){
				//Catch exception if any
				System.err.println("Error: " + e.getMessage());
			}
			
			ArrayList<Cell>bestSolutions = new ArrayList<Cell>();
			double bestResult = Double.POSITIVE_INFINITY;
			
			int iterations = 1;
			Result res = new Result(iterations);

			for(int j=0; j<iterations;j++){		
				Algorithm a = new Algorithm(problem, 100, 3, Math.max(1, minPulls), maxDepth, zValues);
				long startTime = System.currentTimeMillis();
				ArrayList<Cell> EP = a.run(maxArmPulls);
				long endTime = System.currentTimeMillis();
				
				double[] iterRes = Quality.IGD(PF, EP);
				res.addResult(j, (endTime-startTime), calculateAverage(iterRes), Quality.Cardinality(EP));
				if(calculateAverage(res.means) < bestResult){
					bestSolutions.clear();
					bestSolutions.addAll(EP);
					bestResult = calculateAverage(res.means);
				}
			}
			
			for(int i = 0; i<30;i++){
				System.out.print("I-MOO-SOOS, ");
			}
			System.out.println();
			String check = Arrays.toString(res.means);
			check = check.substring(1);
			check = check.substring(0, check.length()-1);
			for(int i = 0; i<30;i++){
				System.out.print(check+", ");
			}
			
			//System.out.println(Arrays.toString(res.means));
			//System.out.println("I-SOOS & " +calculateAverage(res.means)+ " (" +calculateStDev(res.means)+ ") & "+calculateAverage(res.cardinalities)+" & " + calculateAverage(res.times) +"\\\\");
			//print(bestSolutions);
	}
	
	private static void print(ArrayList<Cell> EP) {
		for (Cell sol : EP) {
			String print = "";
			for (double fValue : sol.getResult()) {
				print = print.concat(String.valueOf(fValue) + ", ");
			}
			System.out.println(print.substring(0, print.length() - 2));
		}
	}

	private static double calculateAverage(int[] cardinalities) {
		double count = 0;
		for (int d : cardinalities) {
			count += d;
		}
		return Math.floor(count / cardinalities.length * 100) / 100;
	}

	private static double calculateAverage(double[] list) {
		double count = 0;
		for (double d : list) {
			count += d;
		}
		return Math.floor(count / list.length * 10000) / 10000;
	}

	private static double calculateStDev(double[] list) {
		double average = calculateAverage(list);
		double count = 0;
		for (double d : list) {
			count += Math.pow(d - average, 2);
			;
		}

		return Math.floor(count / list.length * 10000) / 10000;
	}
}