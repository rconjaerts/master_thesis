import java.util.Random;


public class Section {
	double[] domain;
	Solution currentSolution;
	double[] weightVector;
	double mutateSize;
	
	public Section(double[] domain){
		this.domain = domain;
		//double[] x = {(domain[0] + domain[1]) /2};
		this.mutateSize = (domain[1] - domain[0]);
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
	
	public void mutateSolution(double mutatePercentage){
		Random r = new Random();
		double decider = r.nextDouble();
		double newX;
		if(decider<0.5){
			newX = currentSolution.getX() - mutateSize*mutatePercentage;
		}else{
			newX = currentSolution.getX() + mutateSize*mutatePercentage; 
		}
		currentSolution.setX(newX);
	}
}
