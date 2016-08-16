import generators.framework.Generator;
import generators.framework.GeneratorType;
import generators.framework.ValidatingGenerator;
import generators.framework.properties.AnimationPropertiesContainer;

import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import translator.Translator;
import algoanim.animalscript.AnimalScript;
import algoanim.primitives.SourceCode;
import algoanim.primitives.StringArray;
import algoanim.primitives.StringMatrix;
import algoanim.primitives.Text;
import algoanim.primitives.generators.Language;
import algoanim.properties.AnimationPropertiesKeys;
import algoanim.properties.ArrayProperties;
import algoanim.properties.MatrixProperties;
import algoanim.properties.SourceCodeProperties;
import algoanim.properties.TextProperties;
import algoanim.util.Coordinates;


public class MapReduceGenerator implements ValidatingGenerator {
	private SourceCode src;
	private SourceCode src_2;
    private Language lang;
    
    private LinkedList<String[]> lines;
    
    private Translator translator;
    private Locale generatorLocale;
    
    public MapReduceGenerator(Locale locale) {
    	this.generatorLocale = locale;
    	translator = new Translator("resources/BackwardAlgorithm", locale);
    }
    public MapReduceGenerator(){
    	
    }
    
    public void init(){
    	lang = new AnimalScript("MapReduce", "Volker Hartmann, Orkan Özyurt", 800, 600);
    	lang.setStepMode(true);
    }

    public String generate(AnimationPropertiesContainer props,Hashtable<String, Object> primitives) {
        return lang.toString();
    }
    
    //n Anzahl der Zeilen
    //k Anzahl der Worker Maps
    public void mapReduce(String[][] input){
    	
    	System.out.println("---------Input---------");
    	for(String[] line : input){
    		for (String data : line){
    			System.out.print(data + " ");;
    		}
    		System.out.println();
    	}
    	
    	/*############################ SPLIT #################################################*/
    	System.out.println("---------Split---------");
    	lines = new LinkedList<String[]>();
    	for(int i=0; i < input.length; i++){
    		lines.add(input[i]); 
    		
    		//Print result
    		System.out.print("Line " + i + ": " );
    		for(String data : input[i]){
    			System.out.print(data + " ");
    		}
    		System.out.println();
    	}
    	
    	/*############################ MAP #################################################*/
    	System.out.println("---------Map---------");
    	/*
    	 *  Java does not support Maps with duplicate values, so we do not actually map in this step.
    	 *  This makes it easier to properly display the results for our purposes.
    	*/
    	LinkedList <LinkedList<SimpleEntry<String, Integer>>> maps = 
    			new LinkedList <LinkedList<SimpleEntry<String, Integer>>>();
    	
    	for (String line[] : lines){
    		Integer count = new Integer(1);	
    		LinkedList<SimpleEntry<String, Integer>> sets = 
    				new LinkedList<SimpleEntry<String, Integer>>();
    		for (String data : line){
    			sets.add(new SimpleEntry<String,Integer>(data, count));
    		}
    		maps.add(sets);
    	}
    	
    	//Print----
    	for (LinkedList<SimpleEntry<String, Integer>> setlist : maps){
    		for (SimpleEntry<String, Integer> set : setlist){
    			System.out.println(set.getKey() + ", " + set.getValue());
    		}
    		System.out.println("---");
    	}
    	   	
    	/*############################ SHUFFLE #################################################*/
    	System.out.println("---------Shuffle---------");
    	HashMap<String, LinkedList<SimpleEntry<String, Integer>>> hashmap =
    			new HashMap<String, LinkedList<SimpleEntry<String, Integer>>>();
    	
    	for (LinkedList<SimpleEntry<String, Integer>> setlist : maps){
    		for (SimpleEntry<String, Integer> set : setlist){
    			String currentkey = set.getKey();
    			if (hashmap.containsKey(currentkey)){
    				hashmap.get(currentkey).add(set);
    			}
    			else {
    				LinkedList<SimpleEntry<String, Integer>> newlist =
    						new LinkedList<SimpleEntry<String, Integer>>();
    				newlist.add(set);
    				hashmap.put(currentkey, newlist);
    			}
    		}
    	}

    	//Print---
    	for (Entry<String, LinkedList<SimpleEntry<String, Integer>>> entry : hashmap.entrySet()) {
    	    System.out.println(entry.getKey() + " = " + entry.getValue());
    	}
    	
    	/*############################ REDUCE #################################################*/
    	System.out.println("---------Reduce---------");
    	HashMap<String, Integer> reduced_map = new HashMap<String, Integer>();
    	
    	for (Entry<String, LinkedList<SimpleEntry<String, Integer>>> entry : hashmap.entrySet()) {
    		String current_key = entry.getKey();
    		LinkedList<SimpleEntry<String, Integer>> current_list = entry.getValue();
    		
    		for (SimpleEntry<String,Integer> current_entry : current_list){
    			Integer current_value = current_entry.getValue();
    			if (reduced_map.containsKey(current_key)){
    				current_value = Integer.sum(current_value, reduced_map.get(current_key));
    			}
    			reduced_map.put(current_key, current_value);
    		}
    	}
    	
    	//Print---
    	for (Entry<String,Integer> entry : reduced_map.entrySet()) {
    	    System.out.println(entry.getKey() + " = " + entry.getValue());
    	}
	    
    }
    
    public String generate(String input_in[][]){
    	//Headline
	    TextProperties textprops = new TextProperties();
	    textprops.set(AnimationPropertiesKeys.CENTERED_PROPERTY, true);
	    textprops.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font(Font.SANS_SERIF, Font.BOLD, 18));
	    Text headline = lang.newText(new Coordinates(400, 20), "MapReduce Algorithm (Hadoop approach)", 
	    								"Headline", null, textprops);
    	
    	// introduction pages in animal
    	this.generateDescription(); //TODO description in animal
    	
    	// get user input values
    	String input[][] = input_in;
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
	    
    	
    	//TODO short description for each step
    	//StringMatrix inputMatrix = lang.newStringMatrix(new Coordinates(60, 55), input, "input data", null, mp);
    	
    	
    	SourceCodeProperties sourceCodeProps = new SourceCodeProperties();
	    sourceCodeProps.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font(Font.MONOSPACED, Font.PLAIN, 14));
	    sourceCodeProps.set(AnimationPropertiesKeys.HIGHLIGHTCOLOR_PROPERTY, Color.RED);
    	src = lang.newSourceCode(new Coordinates(450, 55), "sourceCode",
		        null, sourceCodeProps);
    	src_2 = lang.newSourceCode(new Coordinates(450, 55), "sourceCode_2",
		        null, sourceCodeProps);
    	this.generateSourceCode();
    	src_2.hide();
    	
    	StringArray inputArray[] = new StringArray[input.length];
    	for(int i=0; i < input.length; i++){
    		inputArray[i] = lang.newStringArray(new Coordinates(60,(55+i*26)), input[i], "input", null, ap);
    	}
    	
    	lang.nextStep();
    	
    	TextProperties labelprops = new TextProperties();
	    labelprops.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font(Font.SANS_SERIF, Font.PLAIN, 12));
	    
	    
	    StringArray inputArray_split[] = new StringArray[input.length];
    	
	    int offset = input.length*26 + 90;
	    lang.newText(new Coordinates(100, offset), "1. Schritt: Input Split", "label:step1", null, textprops);
	    offset += 50;
	    //Splitting
    	lines = new LinkedList<String[]>();
    	for(int i=0; i < input.length; i++){
    		src.highlight(2);
    		for(int j = 0; j < input[i].length; j++)
    			inputArray[i].highlightCell(j, null, null);
			lang.nextStep();
			src.unhighlight(2);
    		
    		lines.add(input[i]);
    		lang.newText(new Coordinates(10,(offset+i*30)), "Zeile " + (i+1) + ": ", "Zeilennummer", null, labelprops);
    		inputArray_split[i] = lang.newStringArray(new Coordinates(60,(offset+i*30)), input[i], "split input", null, ap);
    		src.highlight(3);
    		lang.nextStep();
    		for(int j = 0; j < input[i].length; j++)
    			inputArray[i].unhighlightCell(j, null, null);
    		src.unhighlight(3);
    	}
    	
    	
    	
    	/*############################ MAP #################################################*/
    	LinkedList <LinkedList<SimpleEntry<String, Integer>>> maps = 
    			new LinkedList <LinkedList<SimpleEntry<String, Integer>>>();
    	
    	offset += input.length*26 + 50;
    	int vert_i = 0;
    	int hor_i = 0;
    	int len_input = 0;
    	StringArray mapArray[][] = new StringArray[input.length][100];
    	
    	lang.newText(new Coordinates(100, offset), "2. Schritt: Mapping", "label:step2", null, textprops);
    	offset += 50;
    	for (String line[] : lines){
    		Integer count = new Integer(1);	
    		LinkedList<SimpleEntry<String, Integer>> sets = 
    				new LinkedList<SimpleEntry<String, Integer>>();
    		vert_i = 0;
    		src.highlight(9);
    		lang.nextStep();
    		src.unhighlight(9);
    		for (String data : line){
        		inputArray_split[hor_i].highlightCell(vert_i, null, null);
        		src.highlight(10);
        		lang.nextStep();
    			sets.add(new SimpleEntry<String,Integer>(data, count));
    			mapArray[hor_i][vert_i] = lang.newStringArray(new Coordinates(60+hor_i*100,(offset+vert_i*30)), new String[]{data, count.toString()}, "mapArray", null, ap);
    	    	src.unhighlight(10);
    	    	src.highlight(11);
    	    	lang.nextStep();
        		inputArray_split[hor_i].unhighlightCell(vert_i, null, null);
        		src.unhighlight(11);
        		vert_i++;
    		}
    		lang.newText(new Coordinates(60+hor_i*100, (offset-25)), "Sets "+(hor_i+1), "set_x", null, labelprops);
    		maps.add(sets);
    		src.highlight(13);
	    	lang.nextStep();
	    	src.unhighlight(13);
	    	hor_i++;
    	}

    	//hide all previous steps, but still keep the current progress 
    	lang.hideAllPrimitives();
    	headline.show();
    	src_2.show();
    	hor_i = 0;
    	offset = 55;
    	for (String line[] : lines){
    		Integer count = new Integer(1);	
    		vert_i = 0;
    		for (String data : line){
        		mapArray[hor_i][vert_i] = lang.newStringArray(new Coordinates(60+hor_i*100, (offset+vert_i*30)), new String[]{data, count.toString()}, "mapArray", null, ap);
    	    	vert_i++;
    		}
    		lang.newText(new Coordinates(60+hor_i*100, (offset-25)), "Sets "+(hor_i+1), "set_x", null, labelprops);
    		hor_i++;
    	}
    	lang.nextStep();
    	
    	return lang.toString();
    }
    
    public void generateDescription(){
    
    	/*
    	 * MapReduce ist ein von Google entwickeltes Modell für nebenläufige Berechnungen von großen Datensätzen 
    	 * Eine verbreitete Implementierung in Java ist Apache Hadoop.
    	 * 
    	 * 4 Phasen
    	 * 
    	 * 1. Split
    	 *     Der Input wird in n (Anzahl der Zeilen) Chunks geteilt
    	 * 2. Map
    	 *     Die Daten werden in eine map übertragen; 
    	 *     Key ist der String, Value ist die Anzahl; in diesem Schritt noch bei 1
    	 * 3. Shuffle
    	 *    Daten werden anhand des Keys sortiert, sodass k (verschiedene Daten) Chunks entstehen
    	 *    Jeder Worker kann nun einen Chunk verarbeiten
    	 * 4. Reduce
    	 * 	  Die Values der jeweiligen Keys werden addiert und auf eine Map reduziert.
    	 * 
    	 *  Im finalen Output haben wir nun k Key-Value Paare
    	 * 
    	 * */
    	
    }
    
    
    public void generateSummary(){
    	
    }
    
    
    private void generateSourceCode(){ 
    	src.addCodeLine("// split between every line in the document", null, 0, null); // 0
		src.addCodeLine("split(String document) {", null, 0, null); // 1
	    src.addCodeLine("for each line l in document {", null, 2, null); // 2
	    src.addCodeLine("lines.add(l);", null, 4, null); // 3
	    src.addCodeLine("}", null, 2, null); // 4
	    src.addCodeLine("}", null, 0, null); // 5
	    src.addCodeLine("// map every word to a key-value pair, where key is the word ", null, 0, null); // 6
	    src.addCodeLine("// and value the number of occurences (here always 1) ", null, 0, null); // 7
	    src.addCodeLine("map(String[] lines) {", null, 0, null); // 8
	    src.addCodeLine("for each line l in lines {", null, 2, null); // 9
	    src.addCodeLine("for each word w in l {", null, 4, null); // 10
		src.addCodeLine("sets.add(w/key, count/value);", null, 6, null); // 11
	    src.addCodeLine("}", null, 4, null); // 12
	    src.addCodeLine("maps.add(sets);", null, 4, null); // 13
	    src.addCodeLine("}", null, 2, null); // 14
	    src.addCodeLine("}", null, 0, null); // 15
	    src_2.addCodeLine("// create a word list for each distinct ", null, 0, null); // 6
	    src_2.addCodeLine("// word occurence (collect same words in one list) ", null, 0, null); // 7
	    src_2.addCodeLine("shuffle(maps) {", null, 0, null); // 8
	    src_2.addCodeLine("shuffled_map;", null, 2, null); // 9
	    src_2.addCodeLine("for each set s in maps {", null, 2, null); // 8
	    src_2.addCodeLine("// is there already a list for this word ?", null, 4, null); // 9
	    src_2.addCodeLine("if(word/key of s is already in shuffled_map) {", null, 4, null); // 0
	    src_2.addCodeLine("shuffled_map.get(word).add(s);", null, 6, null); // 1
	    src_2.addCodeLine("} else {", null, 4, null); // 2
	    src_2.addCodeLine("// create new list for this word", null, 6, null); // 3
	    src_2.addCodeLine("shuffled_map.add(word);", null, 6, null); // 4
	    src_2.addCodeLine("}", null, 4, null); // 5
	    src_2.addCodeLine("}", null, 2, null); // 6
	    src_2.addCodeLine("}", null, 0, null); // 7
	    src_2.addCodeLine("// for each wordlist created in the shuffle step:", null, 0, null); // 8
	    src_2.addCodeLine("reduce sets to one set including the number of word occurences", null, 0, null); // 9
	    src_2.addCodeLine("reduce(shuffled_map(word)) {", null, 0, null); // 3
	    src_2.addCodeLine("for each set in shuffled_map(word) {", null, 2, null); // 4
	    src_2.addCodeLine("shuffled_map.increaseWordCount(count/value);", null, 4, null); // 5
	    src_2.addCodeLine("}", null, 2, null); // 6
	    src_2.addCodeLine("}", null, 0, null); // 7
    }
    
    public static void main(String[] args) {
		MapReduceGenerator gen = new MapReduceGenerator();
		
		String input[][] = {
				{"Beer", "Wine", "Cider"}, 
				{"Bourbon","Beer","Beer", "Gin"}, 
				{"Gin","Bourbon", "Cider"}
				};
		//gen.mapReduce(input);
		gen.init();
		System.out.println(gen.generate(input));
	}
    
    public String getName() {
        return this.translator.translateMessage("name");
    }

    public String getAlgorithmName() {
        return this.translator.translateMessage("algorithmName");
    }

    public String getAnimationAuthor() {
        return "Volker Hartmann, Orkan Özyurt";
    }

    public String getDescription(){
        return this.translator.translateMessage("descriptionParagraph1") +"\n"
        		+ this.translator.translateMessage("descriptionParagraph2");
    }
    
    public String getCodeExample(){
        return "//Calculated probabilites are saved in b_i"
 +"\n"
 +"void computeProbabilities(){"
 +"\n"
 +"for(int i = input.length-1; i >= 0; i--){"
 +"\n"
 +"int seq_in = input[i];"
 +"\n"
 +"for(int j = 0; j < B[0].length; j++){"
 +"\n"
 +"helper[j] = b_i[j] * B[j][seq_in]; "
 +"\n"
 +"}"
 +"\n"
 +"\n"
 +"for(int n = 0; n < output.length; n++){"
 +"\n"
 +"output[n] = 0;"
 +"\n"
 +"}		"
 +"\n"
 +"	"
 +"\n"
 +"for(int m = 0; m < T[0].length; m++){"
 +"\n"
 +"for(int n = 0; n < T[0].length; n++){"
 +"\n"
 +"output[m] += T[m][n] * helper[n]; "
 +"\n"
 +"}	"
 +"\n"
 +"}"
 +"\n"
 +"for(int n = 0; n < output.length; n++){"
 +"\n"
 +"b_i[n] = output[n];"
 +"\n"
 +"}"
 +"\n"
 +"		"
 +"\n"
 +"}"
 +"\n"
 +"}";
    }

    public String getFileExtension(){
        return "asu";
    }

    public Locale getContentLocale() {
        return this.generatorLocale;
    }

    public GeneratorType getGeneratorType() {
        return new GeneratorType(GeneratorType.GENERATOR_TYPE_MORE);
    }

    public String getOutputLanguage() {
        return Generator.JAVA_OUTPUT;
    }

    
   /* Diese erhält die gleichen Parameter wie die generate(...)-Methode.
    * Sind die Parameter zulässig, soll die Methode true liefern, ansonsten false.
    */
	@Override
	public boolean validateInput(AnimationPropertiesContainer props,
			Hashtable<String, Object> primitives)
			throws IllegalArgumentException {
		for(int i : (int[])primitives.get("inputsequence")){
			if (i < 0){
				return false;
			}
		}		
		return true;
	}
}
