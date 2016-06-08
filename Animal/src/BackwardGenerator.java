import java.awt.Color;

import algoanim.primitives.*;
import algoanim.primitives.generators.AnimationType;
import algoanim.primitives.generators.Language;
import algoanim.properties.AnimationPropertiesKeys;
import algoanim.properties.ArrayMarkerProperties;
import algoanim.properties.ArrayProperties;
import algoanim.properties.MatrixProperties;
import algoanim.util.Coordinates;
import algoanim.util.TicksTiming;
import algoanim.util.Timing;


public class BackwardGenerator {
	
	//TODO for dynamic sizes of matrix b_i has to check how many states there are; variable numberOfStates for example
	private double b_i[] = {1, 1}; //start vector
	private double helper[] = {0, 0}; //
	private double output[] = {0, 0};
	private int sequenceCounter;
	
	private void computeProbabilities(double[][] T, double[][] B, int[] input){

		Language lang = Language.getLanguageInstance(AnimationType.ANIMALSCRIPT,
				"HMM Backward Algorithm", "Volker Hartmann & Orkan Özyurt", 640, 480);
		lang.setStepMode(true); //schrittmodus aktivieren
		
		//Set Display Properties for Matrix and Array
		MatrixProperties mp = new MatrixProperties();
		mp.set(AnimationPropertiesKeys.FILL_PROPERTY, Color.WHITE);
		mp.set(AnimationPropertiesKeys.ELEMHIGHLIGHT_PROPERTY, Color.RED);
	    mp.set(AnimationPropertiesKeys.CELLHIGHLIGHT_PROPERTY, Color.YELLOW);
		ArrayProperties ap = new ArrayProperties();
		ap.set(AnimationPropertiesKeys.FILL_PROPERTY, Color.WHITE);
		ap.set(AnimationPropertiesKeys.ELEMHIGHLIGHT_PROPERTY, Color.RED);
	    ap.set(AnimationPropertiesKeys.CELLHIGHLIGHT_PROPERTY, Color.YELLOW);
	    
		//Primitives to display
		//FIX
		DoubleMatrix Tr = lang.newDoubleMatrix(new Coordinates(20,40), T, "Transitions", null, mp);
		DoubleMatrix Br = lang.newDoubleMatrix(new Coordinates(100,40), B, "Emissions", null, mp);
		IntArray sequence = lang.newIntArray(new Coordinates(180, 40), input, "Input Sequence", null, ap);
		//CHANGING
		DoubleArray bi = lang.newDoubleArray(new Coordinates(20, 100), b_i, "Backward Probabilities", null, ap);
		DoubleArray result = lang.newDoubleArray(new Coordinates(200, 200), output, "Current Result", null, ap);
		DoubleArray mult_helper = lang.newDoubleArray(new Coordinates(200, 150), helper, "Matrix Multiplication Helper", null, ap);
		
		// Create two markers to point on i and j
	    sequenceCounter = input.length-1;
	    // Array, current index, name, display options, properties
	    ArrayMarkerProperties arrayIMProps = new ArrayMarkerProperties();
	    arrayIMProps.set(AnimationPropertiesKeys.LABEL_PROPERTY, "sequenceIndex");
	    arrayIMProps.set(AnimationPropertiesKeys.COLOR_PROPERTY, Color.BLACK);
	    ArrayMarker seqenceMarker = lang.newArrayMarker(sequence, sequenceCounter, "sequenceIndex" + sequenceCounter,
	        null, arrayIMProps);
	    
	    Timing defaultTiming = new TicksTiming(30);
		
		for(int i = input.length-1; i >= 0; i--){
			int seq_in = input[i];
			sequenceCounter = i;
			seqenceMarker.move(sequenceCounter, null, defaultTiming);
			
			lang.nextStep();
			
			for(int j = 0; j < Br.getNrRows(); j++){
				//helper[j] = bi.getData(j) * Br.getElement(j, seq_in);
				mult_helper.put(j, bi.getData(j) * Br.getElement(j, seq_in), null, null);
				//highlight cells of matrix multiplication
				mult_helper.highlightElem(j, null, null);
				bi.highlightCell(j, null, null);
				Br.highlightCell(j, seq_in, null, null);
				//result of first matrix mult
				lang.nextStep();
				//unhighlight all cells
				mult_helper.unhighlightElem(j, null, null);
				bi.unhighlightCell(j, null, null);
				Br.unhighlightCell(j, seq_in, null, null);
			}
			
			lang.nextStep();

			for(int n = 0; n < result.getLength(); n++){
				result.put(n, 0, null,null);
				//result.highlightElem(n, null, null);
				//lang.nextStep();
				//result.unhighlightElem(n, null, null);
			}	
			
			for(int m = 0; m < Tr.getNrRows(); m++){
				for(int n = 0; n < Tr.getNrRows(); n++){ 
					result.put(m, (result.getData(m) + Tr.getElement(m,n) * mult_helper.getData(n)), null, null);
					result.highlightElem(m, null, null);
					Tr.highlightCell(m, n, null, null);
					mult_helper.highlightCell(n, null, null);
					lang.nextStep();
					result.unhighlightElem(m, null, null);
					Tr.unhighlightCell(m, n, null, null);
					mult_helper.unhighlightCell(n, null, null);
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
