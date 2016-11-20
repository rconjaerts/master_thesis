
public class Section {
	double[] domain;
	Solution currentSolution;
	double[] weightVector;
	
	public Section(double[] domain){
		this.domain = domain;
		double x = (domain[0] + domain[1]) /2;
		this.currentSolution = new Solution(x);
	}

	public double[] getDomain() {
		return domain;
	}

	public void setDomain(double[] domain) {
		this.domain = domain;
	}

	public Solution getCurrentSolution() {
		return currentSolution;
	}

	public void setCurrentSolution(Solution currentSolution) {
		this.currentSolution = currentSolution;
	}

	public void setWeightVector(double[] weightVector) {
		this.weightVector = weightVector;
	}

	public double[] getWeightVector() {
		return weightVector;
	}

	public void calculateWeightVector(double[] z) {
		double[] objectives = currentSolution.getObjectives();
		double y = objectives[1]/(objectives[1]+objectives[0]); 
		double x = 1-y;
		double[] weightVector = {x, y};
		setWeightVector(weightVector);
	}
}
