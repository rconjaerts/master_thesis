package vub.robrecht.conjaerts;

import java.util.ArrayList;

public class Quality {
	private static double smallD(double[] row, ArrayList<Cell> EP){
		double min = Double.POSITIVE_INFINITY;
		for(int i = 0; i<EP.size();i++){
			Cell sol = EP.get(i);
			double[] res = sol.getResult();
			double count = 0;
			for(int j = 0; j<res.length;j++){
				count += Math.pow(row[j]-res[j], 2);
			}
			min = Math.min(min, count);
		}
		return Math.sqrt(min);
	}
	
	public static double[] IGD(double[][] PF, ArrayList<Cell> EP){
		double[] myArray = new double[PF.length];
		for(int i = 0; i<PF.length;i++){
			myArray[i] = smallD(PF[i], EP);
		}
		return myArray;
	}
	
	public static int Cardinality(ArrayList<Cell> EP){
		return EP.size();
	}
}
