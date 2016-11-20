import Problems.Schaffer1;


public class Solution {
	double[] x;
	double[] objectives;
	
	public Solution(double[] x){
		this.x = x;
		objectives = Schaffer1.calculateSchaffer(x);
	}

	public double[] getX() {
		return x;
	}

	public void setX(double[] x) {
		this.x = x;
		calculateNewObjectives();
	}

	public double[] getObjectives() {
		return objectives;
	}

	public void setObjectives(double[] objectives) {
		this.objectives = objectives;
	}
	
	private void calculateNewObjectives(){
		objectives = Schaffer1.calculateSchaffer(x);
	}
}
