import java.awt.Color;

import algoanim.primitives.*;
import algoanim.primitives.generators.AnimationType;
import algoanim.primitives.generators.Language;
import algoanim.properties.AnimationPropertiesKeys;
import algoanim.properties.ArrayProperties;
import algoanim.properties.MatrixProperties;
import algoanim.util.Coordinates;


public class BackwardGenerator {
	private double b_i[] = {1, 1}; //start vector
	private double helper[] = {0, 0}; //
	private double output[] = {0, 0};
	private double norm;
	
	private Language lang;
	
	private void computeProbabilities(double[][] T, double[][] B, int[] input){

		Language lang = Language.getLanguageInstance(AnimationType.ANIMALSCRIPT,
				"HMM Backward Algorithm", "Volker und Orkan", 640, 480);
		
		MatrixProperties mp = new MatrixProperties();
		mp.set(AnimationPropertiesKeys.FILL_PROPERTY, Color.WHITE);
		

		ArrayProperties ap = new ArrayProperties();
		ap.set(AnimationPropertiesKeys.FILL_PROPERTY, Color.WHITE);
		
		
		//Primitives to display
		DoubleMatrix Tr = lang.newDoubleMatrix(new Coordinates(20,40), T, "Transitions", null, mp);
		DoubleMatrix Br = lang.newDoubleMatrix(new Coordinates(20,80), B, "Emissions", null, mp);
		DoubleArray bi = lang.newDoubleArray(new Coordinates(100, 40), b_i, "Backward Probabilities", null, ap);
		IntArray sequence = lang.newIntArray(new Coordinates(100, 80), input, "Input Sequence", null, ap);
		DoubleArray result = lang.newDoubleArray(new Coordinates(200, 200), output, "Current Result", null, ap);
		
		
		lang.nextStep();
		
		for(int i = input.length-1; i >= 0; i--){
			
			int seq_in = input[i];
			
			for(int j = 0; j < Br.getNrRows(); j++){
				helper[j] = bi.getData(j) * Br.getElement(j, seq_in);
			}
			
			lang.nextStep();
			
			for(int n = 0; n < result.getLength(); n++){
				result.put(n, 0, null,null);
			}
			
			lang.nextStep();
			
			for(int m = 0; m < Tr.getNrRows(); m++){
				for(int n = 0; n < Tr.getNrRows(); n++){ 
					result.put(m, (result.getData(m) + Tr.getElement(m,n) * helper[n]), null, null);
				}	
			}
			
			lang.nextStep();
			
			for(int n = 0; n < output.length; n++){
				bi.put(n, result.getData(n), null, null);
			}
			
			lang.nextStep();
			
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
