package vub.robrecht.conjaerts;

import java.util.ArrayList;

public class Quality {
	private static double smallD(double[] row, ArrayList<PossibleSolution> EP){
		double min = Double.POSITIVE_INFINITY;
		for(int i = 0; i<EP.size();i++){
			PossibleSolution sol = EP.get(i);
			double[] res = sol.fValue;
			double count = 0;
			for(int j = 0; j<res.length;j++){
				count += Math.pow(row[j]-res[j], 2);
			}
			min = Math.min(min, count);
		}
		return Math.sqrt(min);
	}
	
	public static double[] IGD(double[][] PF, ArrayList<PossibleSolution> EP){
		double[] myArray = new double[PF.length];
		for(int i = 0; i<PF.length;i++){
			myArray[i] = smallD(PF[i], EP);
		}
		return myArray;
	}
	
	public static int Cardinality(ArrayList<PossibleSolution> EP){
		return EP.size();
	}
}
