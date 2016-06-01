import java.awt.Color;

import algoanim.primitives.*;
import algoanim.primitives.generators.AnimationType;
import algoanim.primitives.generators.Language;
import algoanim.properties.AnimationPropertiesKeys;
import algoanim.properties.ArrayProperties;
import algoanim.properties.MatrixProperties;
import algoanim.util.Coordinates;


public class BackwardGenerator {
	
	//TODO for dynamic sizes of matrix b_i has to check how many states there are; variable numberOfStates for example
	private double b_i[] = {1, 1}; //start vector
	private double helper[] = {0, 0}; //
	private double output[] = {0, 0};
	private double norm;
	
	private Language lang;
	
	private void computeProbabilities(double[][] T, double[][] B, int[] input){

		Language lang = Language.getLanguageInstance(AnimationType.ANIMALSCRIPT,
				"HMM Backward Algorithm", "Volker Hartmann & Orkan Özyurt", 640, 480);
		
		//Set Display Properties for Matrix and Array
		MatrixProperties mp = new MatrixProperties();
		mp.set(AnimationPropertiesKeys.FILL_PROPERTY, Color.WHITE);
		ArrayProperties ap = new ArrayProperties();
		ap.set(AnimationPropertiesKeys.FILL_PROPERTY, Color.WHITE);
		
		//Primitives to display
		//FIX
		DoubleMatrix Tr = lang.newDoubleMatrix(new Coordinates(20,40), T, "Transitions", null, mp);
		DoubleMatrix Br = lang.newDoubleMatrix(new Coordinates(100,40), B, "Emissions", null, mp);
		IntArray sequence = lang.newIntArray(new Coordinates(180, 40), input, "Input Sequence", null, ap);
		//CHANGING
		DoubleArray bi = lang.newDoubleArray(new Coordinates(20, 100), b_i, "Backward Probabilities", null, ap);
		DoubleArray result = lang.newDoubleArray(new Coordinates(200, 200), output, "Current Result", null, ap);

		for(int i = input.length-1; i >= 0; i--){
			int seq_in = input[i];
			
			for(int j = 0; j < Br.getNrRows(); j++){
				helper[j] = bi.getData(j) * Br.getElement(j, seq_in);
			}
			

			for(int n = 0; n < result.getLength(); n++){
				result.put(n, 0, null,null);

			}	
			
			for(int m = 0; m < Tr.getNrRows(); m++){
				for(int n = 0; n < Tr.getNrRows(); n++){ 
					result.put(m, (result.getData(m) + Tr.getElement(m,n) * helper[n]), null, null);
				}	
			}
			for(int n = 0; n < result.getLength(); n++){
				bi.put(n, result.getData(n), null, null);
			}
			
		}

		System.out.println(lang.toString());
	}
	
	public static void main(String[] args) {
		BackwardGenerator backward = new BackwardGenerator();
		
		double T[][] = {{0.7, 0.3}, {0.3, 0.7}}; //transition prob
		double B[][] = {{0.9, 0.1}, {0.2, 0.8}}; //emission prob
		int input[] = {0, 0, 1, 0, 0}; //observations, input sequence
		
		backward.computeProbabilities(T,B,input);
	}
}
