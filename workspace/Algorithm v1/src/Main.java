import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;


public class Main {

	public static void main(String[] args) {
		double[] domain = {-10.0, 10.0};
		int stopCriterion = 500;
		int numberOfSections = 10;//stopCriterion/50;
		double sectionSize = (Math.abs(domain[0])+Math.abs(domain[1]))/numberOfSections;
		ArrayList<Section> sections = new ArrayList<Section>();
		ArrayList<Solution> bestSolutions = new ArrayList<Solution>();
		double[] z = new double[2];
		
		//First one is manual because we need to fill bestSolutions with atleast 1 solution 
		// for performance and to instantiate z
		double[] temp = new double[2];
		temp[0] = domain[0];
		temp[1] = domain[0] + 2;
		Section firstSection = new Section(temp);
		sections.add(firstSection);
		bestSolutions.add(firstSection.getCurrentSolution());
		z = firstSection.getCurrentSolution().getObjectives();
		
		for(int i = 1; i<numberOfSections;i++){
			double[] newDomain = new double[2];
			newDomain[0] = domain[0] + (sectionSize*i);
			newDomain[1] = domain[0] + (sectionSize*(i+1));
			Section sec = new Section(newDomain);
			sections.add(sec);
			changeDominationList(bestSolutions, sec.getCurrentSolution());
			updateZ(z, sec.getCurrentSolution());
		}
		
		for(int i = 0; i<sections.size();i++){
			sections.get(i).calculateWeightVector(z);
		}
		
		for(int i = 0; i<stopCriterion;i++){
			double totalFitnessLevel = calculateTotalFitnessLevel(sections, z);
			HashMap<double[], Integer> possibleSection = calculateProbabilities(sections, z, totalFitnessLevel);
			
			int index = getIndex(possibleSection);
			double[] oldDomain = sections.get(index).getDomain();
			double[] newDomain = new double[oldDomain.length];
			int newIndexPosition = index;
			double fitnessLeft, fitnessRight;
			
			if(index == 0){
				fitnessLeft = calculateTchebycheff(sections.get(index), z);
				fitnessRight = calculateTchebycheff(sections.get(index+1), z);			
			}else if(index == sections.size()-1){
				fitnessLeft = calculateTchebycheff(sections.get(index-1), z);
				fitnessRight = calculateTchebycheff(sections.get(index), z);
			}else{
				fitnessLeft = calculateTchebycheff(sections.get(index-1), z);
				fitnessRight = calculateTchebycheff(sections.get(index+1), z);
			}
			
			if(fitnessLeft<fitnessRight){
				newDomain[0] = oldDomain[0];
				newDomain[1] = oldDomain[1]/2;
				oldDomain[0] = oldDomain[1]/2;
			}else{
				newDomain[0] = oldDomain[1]/2; 
				newDomain[1] = oldDomain[1];
				oldDomain[1] = oldDomain[1]/2;
				newIndexPosition++;
			}
			sections.get(index).setDomain(oldDomain);
			Section sec = new Section(newDomain);
			updateZ(z, sec.getCurrentSolution());
			sec.calculateWeightVector(z);
			sections.add(newIndexPosition, sec);
			changeDominationList(bestSolutions, sec.getCurrentSolution());
		}
		for(int i = 0; i<bestSolutions.size();i++){
			System.out.println(bestSolutions.get(i).getObjectives()[0]+","+bestSolutions.get(i).getObjectives()[1]);
		}
		System.out.println("Number of solutions: " +bestSolutions.size());
	}

	private static HashMap<double[], Integer> calculateProbabilities(
			ArrayList<Section> sections, double[] z, double totalFitnessLevel) {
		HashMap<double[], Integer> possibleSection = new HashMap<double[], Integer>();	
		double sumProbabilities = 0;
		
		for(int j = 0; j<sections.size(); j++){
			double[] lowerUpper = new double[2];
			double fitnessLevel = calculateTchebycheff(sections.get(j), z);
			double probability = (2.0/sections.size()) - (fitnessLevel/totalFitnessLevel);
			lowerUpper[0] = sumProbabilities;
			sumProbabilities = sumProbabilities + probability;
			lowerUpper[1] = sumProbabilities;
			possibleSection.put(lowerUpper, j);
		}
		
		return possibleSection;
	}

	private static double calculateTotalFitnessLevel(ArrayList<Section> sections, double[] z) {
		double totalFitnessLevel = 0.0;
		for(int j = 0; j<sections.size(); j++){
			double fitnessLevel = calculateTchebycheff(sections.get(j), z);
			totalFitnessLevel = totalFitnessLevel + fitnessLevel;
		}
		return totalFitnessLevel;
	}

	private static int getIndex(HashMap<double[], Integer> possibleParents){
		Random r = new Random();
		int index = 0;
		double number = r.nextDouble();
		Iterator<Entry<double[], Integer>> it = possibleParents.entrySet().iterator();
		while (it.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry pairs = (Map.Entry)it.next();
			double[] key = (double[]) pairs.getKey();
			if(number >= key[0] && number < key[1]){
				index = (Integer) pairs.getValue();
		    }
		}
		return index;
	}

	private static double calculateTchebycheff(Section section, double[]z) {
		double tchebycheff = Double.NEGATIVE_INFINITY;
		for(int i = 0; i<z.length;i++){
			double tempX = section.getWeightVector()[i]*Math.abs(section.getCurrentSolution().getObjectives()[i] - z[i]);
			if(tempX > tchebycheff){
				tchebycheff = tempX;
			}
		}
		return tchebycheff;
	}

	private static void updateZ(double[] z, Solution currentSolution) {
		for(int i=0;i<2;i++){
			z[i] = Math.min(z[i], currentSolution.getObjectives()[i]);
		}
	}
	
	private static boolean checkDomination(Solution sol, Solution bestSol) {
		boolean domination = false;
		for(int i = 0;i<sol.getObjectives().length;i++){
			if(sol.getObjectives()[i] > bestSol.getObjectives()[i]){
				return false;
			}else if(sol.getObjectives()[i] < bestSol.getObjectives()[i]){
				domination = true;
			}
		}
		return domination;
	}

	private static void changeDominationList(ArrayList<Solution> bestSolutions,
			Solution sol) {
		
		//Remove all solutions if new solution dominates them
		for(int j = 0; j<bestSolutions.size();j++){
			if(checkDomination(sol, bestSolutions.get(j))){
				bestSolutions.remove(j);
			}
		}
		
		//Don't add solution y if someone dominates y, or there is already a same solution in EP
		boolean domination = false;
		for(int j = 0; j<bestSolutions.size();j++){
			if(bestSolutions.get(j).getX() == sol.getX() || 
					checkDomination(bestSolutions.get(j), sol)){
				domination = true;
			}
		}
		if(domination == false){
			bestSolutions.add(sol);
		}
	}

}
