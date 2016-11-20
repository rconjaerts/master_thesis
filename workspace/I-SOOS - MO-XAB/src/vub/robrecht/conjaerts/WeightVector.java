package vub.robrecht.conjaerts;

public class WeightVector {
	
	double[] weights;

	public WeightVector(double currWeight, int numberOfObjectives) {
		this.weights = new double[numberOfObjectives];
		weights[0] = currWeight;
		weights[1] = 1-currWeight;
	}
	
	public WeightVector(double currWeight, double otherWeights, int currPos, int numberOfObjectives) {
		this.weights = new double[numberOfObjectives];
		for(int i=0; i<currPos;i++){
			weights[i] = otherWeights;
		}
		weights[currPos] = currWeight;
		for(int i=currPos+1; i<numberOfObjectives;i++){
			weights[i] = otherWeights;
		}
	}

	public double[] getWeights() {
		return weights;
	}
}
