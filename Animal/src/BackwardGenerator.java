import java.awt.Color;
import java.awt.Font;
import java.text.DecimalFormat;

import algoanim.primitives.*;
import algoanim.primitives.generators.AnimationType;
import algoanim.primitives.generators.Language;
import algoanim.properties.AnimationPropertiesKeys;
import algoanim.properties.ArrayMarkerProperties;
import algoanim.properties.ArrayProperties;
import algoanim.properties.MatrixProperties;
import algoanim.properties.SourceCodeProperties;
import algoanim.properties.TextProperties;
import algoanim.util.Coordinates;
import algoanim.util.TicksTiming;
import algoanim.util.Timing;


public class BackwardGenerator {
	
	//TODO for dynamic sizes of matrix b_i has to check how many states there are; variable numberOfStates for example
	private double b_i[] = {1, 1}; //start vector
	private double helper[] = {0, 0}; //
	private double output[] = {0, 0};
	private int sequenceCounter;
	private SourceCode src;
	private Language lang = Language.getLanguageInstance(AnimationType.ANIMALSCRIPT,
			"HMM Backward Algorithm", "Volker Hartmann & Orkan Özyurt", 640, 480);
	
	
	private double roundDouble(double num){
		return (long) (num * 1e5) / 1e5;
	}
	
	private void computeProbabilities(double[][] T, double[][] B, int[] input){
		lang.setStepMode(true); //schrittmodus aktivieren
		
		//Set Display Properties for Matrix and Array
		MatrixProperties mp = new MatrixProperties();
		mp.set(AnimationPropertiesKeys.FILL_PROPERTY, Color.WHITE);
		mp.set(AnimationPropertiesKeys.ELEMHIGHLIGHT_PROPERTY, Color.RED);
	    mp.set(AnimationPropertiesKeys.CELLHIGHLIGHT_PROPERTY, Color.YELLOW);
	    mp.set(AnimationPropertiesKeys.GRID_STYLE_PROPERTY, "matrix");
	    
		ArrayProperties ap = new ArrayProperties();
		ap.set(AnimationPropertiesKeys.FILL_PROPERTY, Color.WHITE);
		ap.set(AnimationPropertiesKeys.ELEMHIGHLIGHT_PROPERTY, Color.RED);
	    ap.set(AnimationPropertiesKeys.CELLHIGHLIGHT_PROPERTY, Color.YELLOW);
	    ap.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font(Font.SANS_SERIF, Font.PLAIN, 16));
	    
	    this.generateLabelsAndHeadline();
	    
		//Primitives to display
	    
		//data structures that remain unchanged
		DoubleMatrix Tr = lang.newDoubleMatrix(new Coordinates(60, 55), T, "Transitions", null, mp);
		DoubleMatrix Br = lang.newDoubleMatrix(new Coordinates(200, 55), B, "Emissions", null, mp);
		IntArray sequence = lang.newIntArray(new Coordinates(350, 120), input, "Input Sequence", null, ap);
		
		//CHANGING data structures

		DoubleArray bi = lang.newDoubleArray(new Coordinates(45, 400), b_i, "Backward Probabilities", null, ap);
		DoubleArray mult_helper = lang.newDoubleArray(new Coordinates(45, 340), helper, 
				"Matrix Multiplication Helper", null, ap);
		DoubleArray result = lang.newDoubleArray(new Coordinates(45, 280), output, "Current Result", null, ap);
		
		//TODO change helper and result Array to Matrix?
		//TODO round results
		
		// Create two markers to point on i and j
	    sequenceCounter = input.length-1;
	    // Array, current index, name, display options, properties
	    ArrayMarkerProperties arrayIMProps = new ArrayMarkerProperties();
	    arrayIMProps.set(AnimationPropertiesKeys.LABEL_PROPERTY, "current Character");
	    arrayIMProps.set(AnimationPropertiesKeys.COLOR_PROPERTY, Color.BLACK);
	    ArrayMarker seqenceMarker = lang.newArrayMarker(sequence, sequenceCounter, "sequenceIndex" + sequenceCounter,
	        null, arrayIMProps);
	    
	    Timing defaultTiming = new TicksTiming(30);
	    
	    SourceCodeProperties sourceCodeProps = new SourceCodeProperties();
	    sourceCodeProps.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font(Font.MONOSPACED, Font.PLAIN, 14));
	    sourceCodeProps.set(AnimationPropertiesKeys.HIGHLIGHTCOLOR_PROPERTY, Color.RED);

	    src = lang.newSourceCode(new Coordinates(450, 180), "sourceCode",
		        null, sourceCodeProps);
	    generateSourceCode();
	    
		for(int i = input.length-1; i >= 0; i--){
			int seq_in = input[i];
			sequenceCounter = i;
			seqenceMarker.move(sequenceCounter, null, defaultTiming);
			
			lang.nextStep();

			for(int j = 0; j < Br.getNrRows(); j++){
				src.highlight(4);
				lang.nextStep();
				src.unhighlight(4);
				//helper[j] = bi.getData(j) * Br.getElement(j, seq_in);
				mult_helper.put(j, this.roundDouble(bi.getData(j) * Br.getElement(j, seq_in)), null, null);
				//highlight cells of matrix multiplication
				mult_helper.highlightElem(j, null, null);
				bi.highlightCell(j, null, null);
				Br.highlightCell(j, seq_in, null, null);
				src.highlight(5);
				//result of first matrix mult
				lang.nextStep();
				//unhighlight all cells
				mult_helper.unhighlightElem(j, null, null);
				bi.unhighlightCell(j, null, null);
				Br.unhighlightCell(j, seq_in, null, null);
				src.unhighlight(5);
			}
			
			lang.nextStep();

			for(int n = 0; n < result.getLength(); n++){
				src.highlight(8);
				lang.nextStep();
				src.unhighlight(8);
				result.put(n, 0, null,null);
				result.highlightElem(n, null, null);
				src.highlight(9);
				lang.nextStep();
				src.unhighlight(9);
				result.unhighlightElem(n, null, null);
			}	
			
			for(int m = 0; m < Tr.getNrRows(); m++){
				src.highlight(12);
				lang.nextStep();
				for(int n = 0; n < Tr.getNrRows(); n++){ 
					src.unhighlight(12);
					src.highlight(13);
					lang.nextStep();
					src.unhighlight(13);
					src.highlight(14);
					result.put(m, roundDouble(result.getData(m) + Tr.getElement(m,n) * mult_helper.getData(n)), null, null);
					result.highlightElem(m, null, null);
					Tr.highlightCell(m, n, null, null);
					mult_helper.highlightCell(n, null, null);
					lang.nextStep();
					result.unhighlightElem(m, null, null);
					Tr.unhighlightCell(m, n, null, null);
					mult_helper.unhighlightCell(n, null, null);
					src.unhighlight(14);
				}	
			}
			
			for(int n = 0; n < bi.getLength(); n++){
				bi.put(n, result.getData(n), null, null);
			}
			
			src.highlight(18);
			lang.nextStep();
			src.unhighlight(18);
		}

		System.out.println(lang.toString());
	}
	
	
	private void generateSourceCode(){
		//TODO Add Small headline? "Backward Algorithmus" 
		src.addCodeLine("private void backward(double b_i[], int input_index){", null, 0, null); // 0
		src.addCodeLine("	if(input_index >= 0){", null, 1, null); // 1
	    src.addCodeLine("		int input = input_sequence[input_index];", null, 1, null); // 2
	    src.addCodeLine("		//Multipliziere momentane Zustands- mit Emissionswahrscheinlichkeiten", null, 1, null); // 3
	    src.addCodeLine("		for(int j = 0; j < B[0].length; j++){", null, 1, null); // 4
	    src.addCodeLine("			helper[j] = b_i[j] * B[j][input];", null, 1, null); // 5
	    src.addCodeLine("		}", null, 1, null); // 6
	    src.addCodeLine("		//initialisiere ergebnisarray für matrix multiplikation", null, 1, null); // 7
	    src.addCodeLine("		for(int n = 0; n < output.length; n++){", null, 2, null); // 8
	    src.addCodeLine("			output[n] = 0;", null, 2, null); // 9
	    src.addCodeLine("		}", null, 2, null); // 10
	    src.addCodeLine("		// Zustandsübergang: multipliziere current emission mit Transitionsmatrix", null, 2, null); // 11
	    src.addCodeLine("		for(int m = 0; m < T[0].length; m++){", null, 2, null); // 12
	    src.addCodeLine("			for(int n = 0; n < T[0].length; n++){", null, 2, null); // 13
	    src.addCodeLine("				output[m] += T[m][n] * helper[n];", null, 2, null); // 14
	    src.addCodeLine("			}", null, 2, null); // 15
	    src.addCodeLine("		}", null, 2, null); // 16
	    src.addCodeLine("	}", null, 2, null); // 17
	    src.addCodeLine("	backward(output, input_index-1);", null, 1, null); // 18
	    src.addCodeLine("}", null, 1, null); // 19	
	}
	
	public void generateLabelsAndHeadline(){
	    //Headline
	    TextProperties textprops = new TextProperties();
	    textprops.set(AnimationPropertiesKeys.CENTERED_PROPERTY, true);
	    textprops.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font(Font.SANS_SERIF, Font.BOLD, 18));
	    Text headline = lang.newText(new Coordinates(400, 20), "Backward Algorithm für Hidden Markov Model", 
	    								"Headline", null, textprops);

	    //Labels for inputs
	    TextProperties labelprops = new TextProperties();
	    labelprops.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font(Font.SANS_SERIF, Font.PLAIN, 14));
	    Text TrLabel = lang.newText(new Coordinates(25,65), "A = ", "Label A", null, labelprops);
	    //Aus irgendeinem Grund nicht auf gleicher Höhe bei gleicher Y-Koordinate
	    Text BLabel = lang.newText(new Coordinates(165,69), "B = ", "Label B", null, labelprops);
	    
	    //Labels for outputs
	    Text helperLabel = lang.newText(new Coordinates(50, 260), "Zwischenergebnis:", "helper", null, labelprops);
	    Text resultLabel = lang.newText(new Coordinates(50,320), "Ergebnis:", "Result", null, labelprops);
	    Text bLabel = lang.newText(new Coordinates(50, 380), "Backward-Wahrscheinlichkeiten:", "blabel", null, labelprops);
	}
	
	
	public static void main(String[] args) {
		BackwardGenerator backward = new BackwardGenerator();
		
		double T[][] = {{0.7, 0.3}, {0.3, 0.7}}; //transition prob
		double B[][] = {{0.9, 0.1}, {0.2, 0.8}}; //emission prob
		int input[] = {0, 0, 1, 0, 0}; //observations, input sequence
		
		backward.computeProbabilities(T,B,input);
	}
}
