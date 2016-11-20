package Problems;

import java.util.ArrayList;

public interface Problem {
	
	public abstract double[] calculateProblem(ArrayList<Double> x, boolean noise);
	
	public int getNumberOfDomains();
	public double[][] getDomainSize();
	public int getResultSize();
	
}
