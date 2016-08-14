import generators.framework.properties.AnimationPropertiesContainer;

import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import algoanim.animalscript.AnimalScript;
import algoanim.primitives.SourceCode;
import algoanim.primitives.StringMatrix;
import algoanim.primitives.generators.Language;
import algoanim.properties.AnimationPropertiesKeys;
import algoanim.properties.MatrixProperties;
import algoanim.properties.SourceCodeProperties;
import algoanim.properties.TextProperties;
import algoanim.util.Coordinates;


public class MapReduceGenerator {
	private SourceCode src;
    private Language lang;
    
    private LinkedList<String[]> lines;
    
    public MapReduceGenerator(){
    	
    }
    
    public void init(){
    	lang = new AnimalScript("MapReduce", "Volker Hartmann, Orkan Özyurt", 800, 600);
    	lang.setStepMode(true);
    }

    public String generate(AnimationPropertiesContainer props,Hashtable<String, Object> primitives) {
    	
    	// introduction pages in animal
    	this.generateDescription(); //TODO description in animal
    	
    	// get user input values
    	String input[][] = (String[][])primitives.get("input");
    	MatrixProperties mp = (MatrixProperties)props.getPropertiesByName("matrixproperties");
    	
    	
    	mp.set(AnimationPropertiesKeys.FILL_PROPERTY, Color.WHITE);
    	
    	//TODO short description for each step
    	StringMatrix inputMatrix = lang.newStringMatrix(new Coordinates(60, 55), input, "input data", null, mp);
    	
    	
    	SourceCodeProperties sourceCodeProps = new SourceCodeProperties();
	    sourceCodeProps.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font(Font.MONOSPACED, Font.PLAIN, 14));
	    sourceCodeProps.set(AnimationPropertiesKeys.HIGHLIGHTCOLOR_PROPERTY, Color.RED);
    	src = lang.newSourceCode(new Coordinates(450, 180), "sourceCode",
		        null, sourceCodeProps);
    	this.generateSourceCode();
    	
    	lang.nextStep();
    	
    	String splitInput[][] = input;
    	for(int i=0; i < input.length; i++){
    		for(int j=0; j < input[i].length; j++){
    			splitInput[i][j] = "";
    		}
    	}
    	StringMatrix splitMatrix = lang.newStringMatrix(new Coordinates(60, 150), splitInput, "split input data", null, mp);
    	
    	TextProperties labelprops = new TextProperties();
	    labelprops.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font(Font.SANS_SERIF, Font.PLAIN, 12));
	    
	    //Splitting
    	lines = new LinkedList<String[]>();
    	for(int i=0; i < input.length; i++){
    		lines.add(input[i]);
    		lang.newText(new Coordinates(25,(150+i*14)), "Zeile " + (i+1) + ": ", "Zeilennummer", null, labelprops);
    		inputMatrix.highlightCellColumnRange(i, 0, input[i].length, null, null);
			for(int j=0; j < input[i].length; j++){
    			splitInput[i][j] = input[i][j];
    		}
    		lang.nextStep();
    		inputMatrix.unhighlightCellColumnRange(i, 0, input[i].length, null, null);
    	}
    	
    	
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
    
    public String generate(){
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
		src.addCodeLine("// split input data into lines", null, 0, null); // 0
		src.addCodeLine("private void split(String[][] input){", null, 0, null); // 1
	    src.addCodeLine("lines = new LinkedList<String[]>();", null, 2, null); // 2
	    src.addCodeLine("for(int i=0; i < input.length; i++){", null, 2, null); // 3
	    src.addCodeLine("lines.add(input[i]);", null, 4, null); // 4
	    src.addCodeLine("}", null, 2, null); // 5
	    src.addCodeLine("map(lines);", null, 2, null); // 6
	    src.addCodeLine("}", null, 0, null); // 7
	    src.addCodeLine("// generate map from words in lines", null, 4, null); // 8
	    src.addCodeLine("private void map(LinkedList<String[]> lines){", null, 0, null); // 9
	    src.addCodeLine("LinkedList <LinkedList<SimpleEntry<String, Integer>>> maps = "
	    		+ "new LinkedList <LinkedList<SimpleEntry<String, Integer>>>();", null, 2, null); // 10
	    src.addCodeLine("for (String line[] : lines){", null, 2, null); // 11
	    src.addCodeLine("Integer count = new Integer(1); // all word occurences have value 1", null, 4, null); // 12
	    src.addCodeLine("LinkedList<SimpleEntry<String, Integer>> sets = " 
    				+ "new LinkedList<SimpleEntry<String, Integer>>();", null, 2, null); // 13
	    src.addCodeLine("add each word in current line to the set", null, 2, null); // 14
	    src.addCodeLine("for (String data : line){", null, 2, null); // 15
	    src.addCodeLine("sets.add(new SimpleEntry<String,Integer>(data, count));", null, 4, null); // 16
	    src.addCodeLine("}", null, 2, null); // 17
	    src.addCodeLine("maps.add(sets); //add current line set to map", null, 2, null); // 18
	    src.addCodeLine("shuffle(maps);", null, 2, null); // 19
	    src.addCodeLine("}", null, 0, null); // 20
	    src.addCodeLine("// shuffle the line mappings to the right worker", null, 2, null); // 21
	    src.addCodeLine("private void shuffle(LinkedList <LinkedList<SimpleEntry<String, Integer>>> maps){", null, 0, null); // 22
	    src.addCodeLine("HashMap<String, LinkedList<SimpleEntry<String, Integer>>> hashmap = "
    			+ "new HashMap<String, LinkedList<SimpleEntry<String, Integer>>>();", null, 2, null); // 23
	    src.addCodeLine("// assign similar words to the same map entry (collection)", null, 2, null); // 24
	    src.addCodeLine("for (LinkedList<SimpleEntry<String, Integer>> setlist : maps){", null, 2, null); // 25
	    src.addCodeLine("for (SimpleEntry<String, Integer> set : setlist){", null, 4, null); // 26
	    src.addCodeLine("String currentkey = set.getKey();", null, 6, null); // 27
	    src.addCodeLine("if (hashmap.containsKey(currentkey)){", null, 6, null); // 28
	    src.addCodeLine("hashmap.get(currentkey).add(set);", null, 8, null); // 29
	    src.addCodeLine("}", null, 6, null); // 30
	    src.addCodeLine("else { // new word occurence", null, 6, null); // 31
	    src.addCodeLine("LinkedList<SimpleEntry<String, Integer>> newlist = "
    						+ "new LinkedList<SimpleEntry<String, Integer>>();", null, 8, null); // 32
	    src.addCodeLine("newlist.add(set);", null, 8, null); // 33
	    src.addCodeLine("hashmap.put(currentkey, newlist);", null, 8, null); // 34
	    src.addCodeLine("}", null, 6, null); // 35
	    src.addCodeLine("}", null, 4, null); // 36
	    src.addCodeLine("}", null, 2, null); // 37
	    src.addCodeLine("reduce(hashmap);", null, 2, null); // 38
	    src.addCodeLine("// reduce collected entries of same word occurences by counting the number of sets", null, 0, null); // 39
	    src.addCodeLine("private void reduce(HashMap<String, LinkedList<SimpleEntry<String, Integer>>> hashmap){", null, 0, null); // 40
	    src.addCodeLine("HashMap<String, Integer> reduced_map = new HashMap<String, Integer>();", null, 2, null); // 41
	    src.addCodeLine("for (Entry<String, LinkedList<SimpleEntry<String, Integer>>> entry : hashmap.entrySet()) {", null, 2, null); // 42
	    src.addCodeLine("String current_key = entry.getKey();", null, 4, null); // 43
	    src.addCodeLine("LinkedList<SimpleEntry<String, Integer>> current_list = entry.getValue();", null, 4, null); // 44
	    src.addCodeLine("for (SimpleEntry<String,Integer> current_entry : current_list){", null, 4, null); // 45
	    src.addCodeLine("Integer current_value = current_entry.getValue();", null, 6, null); // 46
	    src.addCodeLine("if (reduced_map.containsKey(current_key)){", null, 6, null); // 47
	    src.addCodeLine("current_value = Integer.sum(current_value, reduced_map.get(current_key));", null, 8, null); // 48
	    src.addCodeLine("}", null, 6, null); // 49
	    src.addCodeLine("reduced_map.put(current_key, current_value);", null, 6, null); // 50
	    src.addCodeLine("}", null, 4, null); // 51
	    src.addCodeLine("};", null, 2, null); // 52
	    src.addCodeLine("}", null, 0, null); // 53
	    
	}
    
    public static void main(String[] args) {
		MapReduceGenerator gen = new MapReduceGenerator();
		
		String input[][] = {
				{"Beer", "Wine", "Cider"}, 
				{"Bourbon","Beer","Beer", "Gin"}, 
				{"Gin","Bourbon", "Cider"}
				};
		gen.mapReduce(input);
		gen.init();
		gen.generate();
	}
}
