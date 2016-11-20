package Problems;

public interface Problem {
	
	public abstract double[] calculateProblem(double[] points, boolean noise);
	
	public int getNumberOfDomains();
	public double[][] getDomainSize();
	public int getResultSize();
	
}
