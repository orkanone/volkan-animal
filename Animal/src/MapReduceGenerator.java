import generators.framework.Generator;
import generators.framework.GeneratorType;
import generators.framework.ValidatingGenerator;
import generators.framework.properties.AnimationPropertiesContainer;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
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
import algoanim.util.Offset;


public class MapReduceGenerator  {
	private SourceCode src;
	private SourceCode src_2;
    private Language lang;
    
    private LinkedList<String[]> lines;
    
    private Translator translator;
    private Locale generatorLocale;
    
    public MapReduceGenerator(Locale locale) {
    	this.generatorLocale = locale;
    	translator = new Translator("resources/MapReduce", locale);
    }
    
    public MapReduceGenerator(){
    	
    }
    
    public void init(){
    	lang = new AnimalScript("MapReduce", "Volker Hartmann, Orkan Özyurt", 800, 600);
    	lang.setStepMode(true);
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
    	//TODO Translator, Wizard, Parametrisierung
    	
    	//Headline
    	TextProperties headlineProps = new TextProperties();
		headlineProps.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font(
		        Font.SANS_SERIF, Font.BOLD, 18));
    	
    	TextProperties textprops = new TextProperties();
	    textprops.set(AnimationPropertiesKeys.CENTERED_PROPERTY, true);
	    textprops.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font(Font.SANS_SERIF, Font.BOLD, 16));
	    Text headline = lang.newText(new Coordinates(350, 20), "MapReduce Algorithm (Hadoop approach)", 
	    								"Headline", null, headlineProps);
    	
    	// introduction pages in animal
    	this.generateDescription();
    	
    	headline.show();
    	
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
	    
    	SourceCodeProperties sourceCodeProps = new SourceCodeProperties();
	    sourceCodeProps.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font(Font.MONOSPACED, Font.PLAIN, 14));
	    sourceCodeProps.set(AnimationPropertiesKeys.HIGHLIGHTCOLOR_PROPERTY, Color.RED);
    	src = lang.newSourceCode(new Coordinates(600, 55), "sourceCode",
		        null, sourceCodeProps);
    	src_2 = lang.newSourceCode(new Coordinates(600, 55), "sourceCode_2",
		        null, sourceCodeProps);
    	this.generateSourceCode();
    	src_2.hide();
    	
    	//get number of words in document for array scale
    	int doc_size = 0;
    	for(int i=0; i<input.length; i++){
    		doc_size += input[i].length;
    	}
    	
    	StringArray inputArray[] = new StringArray[input.length];
    	for(int i=0; i < input.length; i++){
    		inputArray[i] = lang.newStringArray(new Coordinates(60,(55+i*26)), input[i], "input", null, ap);
    	}
    	
    	lang.nextStep();
    	
    	TextProperties labelprops = new TextProperties();
	    labelprops.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font(Font.SANS_SERIF, Font.PLAIN, 12));
	    
	    
	    /*############################ SPLIT #################################################*/
	    StringArray inputArray_split[] = new StringArray[input.length];
    	
	    int offset = input.length*26 + 90;
	    lang.newText(new Coordinates(100, offset), "1. Schritt: Input Split", "label:step1", null, textprops); //translator.translateMessage("step1")
	    offset += 50;

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
    	StringArray mapArray[][] = new StringArray[input.length][doc_size];
    	
    	lang.newText(new Coordinates(100, offset), "2. Schritt: Mapping", "label:step2", null, textprops); //translator.translateMessage("step2")
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
    	offset = 70;
    	for (String line[] : lines){
    		Integer count = new Integer(1);	
    		vert_i = 0;
    		for (String data : line){
        		mapArray[hor_i][vert_i] = lang.newStringArray(new Coordinates(60+hor_i*100, (offset+vert_i*30)), 
        				new String[]{data, count.toString()}, "mapArray", null, ap);
    	    	vert_i++;
    		}
    		lang.newText(new Coordinates(60+hor_i*100, (offset-25)), "Sets "+(hor_i+1), "set_x", null, labelprops);
    		hor_i++;
    	}
    	lang.nextStep();
    	
    	
    	/*############################ SHUFFLE #################################################*/
    	HashMap<String, LinkedList<SimpleEntry<String, Integer>>> hashmap =
    			new HashMap<String, LinkedList<SimpleEntry<String, Integer>>>();
    	
    	StringArray shuffleArray[][] = new StringArray[doc_size][doc_size];
    	int size_x = 0;
    	int size_y;
    	offset += input.length*26 + 70;
    	lang.newText(new Coordinates(100, offset), "3. Schritt: Shuffling", "label:step3", null, textprops); //translator.translateMessage("step3")
    	ArrayList<String> shuffle_index = new ArrayList<String>();
	    offset += 20;
	    hor_i = 0;
	    vert_i = 0;
    	
    	for (LinkedList<SimpleEntry<String, Integer>> setlist : maps){
    		for (SimpleEntry<String, Integer> set : setlist){
    			src_2.highlight(4);
    			mapArray[hor_i][vert_i].highlightCell(0, null, null);
    			mapArray[hor_i][vert_i].highlightCell(1, null, null);
    			String currentkey = set.getKey();
    			lang.nextStep();
    			src_2.unhighlight(4);
    			if (hashmap.containsKey(currentkey)){
    				src_2.highlight(7);
    				hashmap.get(currentkey).add(set);
    				size_x = shuffle_index.indexOf(currentkey);
    			}
    			else {
    				src_2.highlight(10);
    				size_x = hashmap.size();
    				LinkedList<SimpleEntry<String, Integer>> newlist =
    						new LinkedList<SimpleEntry<String, Integer>>();
    				newlist.add(set);
    				hashmap.put(currentkey, newlist);
    				shuffle_index.add(currentkey);
    				lang.newText(new Coordinates(60+size_x*100, (offset)), currentkey, "label:shuffle", null, labelprops);
    			}
    			size_y = hashmap.get(currentkey).size();
    			shuffleArray[size_x][size_y] = lang.newStringArray(new Coordinates(60+size_x*100, (offset+size_y*30)),
						new String[]{currentkey, set.getValue().toString()}, "shuffled map", null, ap);
    			lang.nextStep();
    			mapArray[hor_i][vert_i].unhighlightCell(0, null, null);
    			mapArray[hor_i][vert_i].unhighlightCell(1, null, null);
				src_2.unhighlight(7);
				src_2.unhighlight(10);
				vert_i++;
    		}
    		vert_i=0;
    		hor_i++;
    	}
    	
    	
    	/*############################ REDUCE #################################################*/
    	HashMap<String, Integer> reduced_map = new HashMap<String, Integer>();
    	int shuffle_size = shuffle_index.size();
    	StringArray reduceArray[] = new StringArray[shuffle_size];
    	hor_i = 0;
    	int it = 0;
    	offset += input.length*26 + 70;
    	lang.newText(new Coordinates(100, offset), "4. Schritt: Reducing", "label:step4", null, textprops); //translator.translateMessage("step4")
    	offset += 50;
    	
    	for (Entry<String, LinkedList<SimpleEntry<String, Integer>>> entry : hashmap.entrySet()) {
    		String current_key = entry.getKey();
    		LinkedList<SimpleEntry<String, Integer>> current_list = entry.getValue();
    		src_2.highlight(16);
    		lang.nextStep();
    		src_2.unhighlight(16);
    		
    		for (SimpleEntry<String,Integer> current_entry : current_list){
    			src_2.highlight(18);
    			Integer current_value = current_entry.getValue();
    			if (reduced_map.containsKey(current_key)){
    				current_value = Integer.sum(current_value, reduced_map.get(current_key));
    			}
    			reduced_map.put(current_key, current_value);
    			if(it == 0){
    	    		reduceArray[hor_i] = lang.newStringArray(new Coordinates(60+hor_i*100, offset),
    						new String[]{current_key, current_value.toString()}, "reduced map", null, ap);
    			} else {
    				reduceArray[hor_i].put(1, current_value.toString(), null, null);
    			}
    			it++;
    			lang.nextStep();
    			src_2.unhighlight(18);
    		}
    		it=0;
    		hor_i++;
    	}
    	
    	lang.hideAllPrimitives();
    	
    	//summary
    	headline.show();
    	generateSummary();
    	for(int i=0; i<hor_i;i++)
    		reduceArray[i].show();
    	
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
    	TextProperties headlineProps = new TextProperties();
		headlineProps.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font(
		        Font.SANS_SERIF, Font.BOLD, 16));
		TextProperties headlineLargeProps = new TextProperties();
		headlineLargeProps.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font(
		        Font.SANS_SERIF, Font.BOLD, 18));
		
		lang.newText(new Coordinates(10, 60),
		        "Einleitung", //translator.translateMessage("intro")
		        "mapred_intro_headline", null, headlineProps);
		
		TextProperties textProps = new TextProperties();
	    textProps.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font(
	        Font.SANS_SERIF, Font.PLAIN, 16));
	    lang.newText(new Coordinates(10, 100),
	        "MapReduce ist ein von Google entwickeltes Modell für nebenläufige Berechnungen von großen Datensätzen.", //translator.translateMessage("intro1")
	        "description1", null, textProps);
	    lang.newText(new Offset(0, 25, "description1",
	        AnimalScript.DIRECTION_NW),
	        "Eine verbreitete Implementierung in Java ist Apache Hadoop.", //translator.translateMessage("intro2")
	        "description2", null, textProps);
	    lang.newText(new Offset(0, 50, "description2",
	        AnimalScript.DIRECTION_NW),
	        "Der Algorithmus besteht aus 4 Phasen:", //translator.translateMessage("intro3")
	        "description3", null, textProps);
	    lang.newText(new Offset(0, 25, "description3",
	        AnimalScript.DIRECTION_NW),
	        "1. Split",
	        "description4", null, headlineProps);
	    lang.newText(new Offset(0, 25, "description4",
	        AnimalScript.DIRECTION_NW),
	        "Der Input wird in n (Anzahl der Zeilen) Chunks geteilt", //translator.translateMessage("intro4")
	        "description5", null, textProps);
	    lang.newText(new Offset(0, 25, "description5",
	        AnimalScript.DIRECTION_NW),
	        "2. Map",
	        "description6", null, headlineProps);
	    lang.newText(new Offset(0, 25, "description6",
		    AnimalScript.DIRECTION_NW),
		    "Die Daten werden in eine Map übertragen; Key ist der String, Value ist die Anzahl (in diesem Schritt noch bei 1).", //translator.translateMessage("intro5")
		    "description7", null, textProps);
	    lang.newText(new Offset(0, 25, "description7",
		    AnimalScript.DIRECTION_NW),
		    "3. Shuffle",
		    "description8", null, headlineProps);
		lang.newText(new Offset(0, 25, "description8",
			AnimalScript.DIRECTION_NW),
			"Daten werden anhand des Keys sortiert, sodass k (verschiedene Daten) Chunks entstehen.", //translator.translateMessage("intro6")
			"description9", null, textProps);
		lang.newText(new Offset(0, 25, "description9",
		     AnimalScript.DIRECTION_NW),
		     "Jeder Worker kann nun einen Chunk verarbeiten.", //translator.translateMessage("intro7")
		     "description10", null, textProps);
		lang.newText(new Offset(0, 25, "description10",
		     AnimalScript.DIRECTION_NW),
		     "4. Reduce",
		     "description11", null, headlineProps);
		lang.newText(new Offset(0, 25, "description11",
			 AnimalScript.DIRECTION_NW),
			 "Die Values der jeweiligen Keys werden addiert und auf eine Map reduziert.", //translator.translateMessage("intro8")
			 "description12", null, textProps);
		lang.newText(new Offset(0, 50, "description12",
		     AnimalScript.DIRECTION_NW),
		     "Im finalen Output haben wir nun k Key-Value Paare.", //translator.translateMessage("intro9")
		     "description13", null, textProps);
		
	    lang.nextStep();
	    lang.hideAllPrimitives();
    }
    
    
    public void generateSummary(){
    	TextProperties headlineProps = new TextProperties();
		headlineProps.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font(
		        Font.SANS_SERIF, Font.BOLD, 16));
		TextProperties headlineLargeProps = new TextProperties();
		headlineLargeProps.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font(
		        Font.SANS_SERIF, Font.BOLD, 18));
		TextProperties textProps = new TextProperties();
	    textProps.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font(
	        Font.SANS_SERIF, Font.PLAIN, 16));
	    
		lang.newText(new Coordinates(10, 60),
		        "Zusammenfassung", //translator.translateMessage("recap")
		        "mapred_summary_headline", null, headlineProps);
		lang.newText(new Coordinates(10, 100),
		        "Es wurde das MapReduce Modell am Beispiel eines WordCount-Algorithmus dargestellt.", //translator.translateMessage("outro1")
		        "description1", null, textProps);
		    lang.newText(new Offset(0, 25, "description1",
		        AnimalScript.DIRECTION_NW),
		        "Generell wird MapReduce für jegliche Form der verteilten Verarbeitung großer Datensätze verwendet.", //translator.translateMessage("outro2")
		        "description2", null, textProps);
		    lang.newText(new Offset(0, 50, "description2",
		        AnimalScript.DIRECTION_NW),
		        "Der Algorithmus hat den eingehenden Datensatz in 4 Phasen verarbeitet.", //translator.translateMessage("outro3")
		        "description3", null, textProps);
		    lang.newText(new Offset(0, 25, "description3",
		        AnimalScript.DIRECTION_NW),
		        "Dabei wurden die Daten reduziert, bis nur noch die unterschiedlich vorkommenden", //translator.translateMessage("outro4")
		        "description4", null, textProps);
		    lang.newText(new Offset(0, 25, "description4",
		        AnimalScript.DIRECTION_NW),
		        "Worte des Input Strings und deren jeweilige Anzahl übrig blieben.", //translator.translateMessage("outro5")
		        "description5", null, textProps);
		    lang.newText(new Offset(0, 150, "description5",
		        AnimalScript.DIRECTION_NW),
		        "Als Ausgabe erhalten wir folgendes Ergebnis:", //translator.translateMessage("outro6")
		        "description6", null, textProps);
		    
    }
    
    
    private void generateSourceCode(){ 
    	src.addCodeLine("// split between every line in the document", null, 0, null); // 0 //translator.translateMessage("sourceComment1")
		src.addCodeLine("split(String document) {", null, 0, null); // 1
	    src.addCodeLine("for each line l in document {", null, 2, null); // 2
	    src.addCodeLine("lines.add(l);", null, 4, null); // 3
	    src.addCodeLine("}", null, 2, null); // 4
	    src.addCodeLine("}", null, 0, null); // 5
	    src.addCodeLine("// map every word to a key-value pair, where key is the word ", null, 0, null); // 6 //translator.translateMessage("sourceComment2")
	    src.addCodeLine("// and value the number of occurences (here always 1) ", null, 0, null); // 7 //translator.translateMessage("sourceComment3")
	    src.addCodeLine("map(String[] lines) {", null, 0, null); // 8
	    src.addCodeLine("for each line l in lines {", null, 2, null); // 9
	    src.addCodeLine("for each word w in l {", null, 4, null); // 10
		src.addCodeLine("sets.add(w/key, count/value);", null, 6, null); // 11
	    src.addCodeLine("}", null, 4, null); // 12
	    src.addCodeLine("maps.add(sets);", null, 4, null); // 13
	    src.addCodeLine("}", null, 2, null); // 14
	    src.addCodeLine("}", null, 0, null); // 15
	    src_2.addCodeLine("// create a word list for each distinct ", null, 0, null); // 0 //translator.translateMessage("sourceComment4")
	    src_2.addCodeLine("// word occurence (collect same words in one list) ", null, 0, null); // 1 //translator.translateMessage("sourceComment5")
	    src_2.addCodeLine("shuffle(maps) {", null, 0, null); // 2
	    src_2.addCodeLine("shuffled_map;", null, 2, null); // 3
	    src_2.addCodeLine("for each set s in maps {", null, 2, null); // 4
	    src_2.addCodeLine("// is there already a list for this word ?", null, 4, null); // 5 //translator.translateMessage("sourceComment6")
	    src_2.addCodeLine("if(word/key of s is already in shuffled_map) {", null, 4, null); // 6
	    src_2.addCodeLine("shuffled_map.get(word).add(s);", null, 6, null); // 7
	    src_2.addCodeLine("} else {", null, 4, null); // 24
	    src_2.addCodeLine("// create new list for this word", null, 6, null); // 8 //translator.translateMessage("sourceComment7")
	    src_2.addCodeLine("shuffled_map.add(word);", null, 6, null); // 9
	    src_2.addCodeLine("}", null, 4, null); // 10
	    src_2.addCodeLine("}", null, 2, null); // 11
	    src_2.addCodeLine("}", null, 0, null); // 12
	    src_2.addCodeLine("// for each wordlist created in the shuffle step:", null, 0, null); // 13 //translator.translateMessage("sourceComment8")
	    src_2.addCodeLine("// reduce sets to one set including the number of word occurences", null, 0, null); // 14 //translator.translateMessage("sourceComment9")
	    src_2.addCodeLine("reduce(shuffled_map(word)) {", null, 0, null); // 15
	    src_2.addCodeLine("for each set in shuffled_map(word) {", null, 2, null); // 16
	    src_2.addCodeLine("shuffled_map.increaseWordCount(count/value);", null, 4, null); // 17
	    src_2.addCodeLine("}", null, 2, null); // 18
	    src_2.addCodeLine("}", null, 0, null); // 19
    }
    
    public static void main(String[] args) {
		MapReduceGenerator gen = new MapReduceGenerator();
		
		//too many inputs result in overlap with sourcecode! dunno how too fix this properly anyway.
		String input[][] = {
				{"Beer", "Wine", "Cider"}, 
				{"Bourbon","Beer","Beer", "Gin"}, 
				{"Gin","Bourbon", "Cider"}, 
				//{"Absinth","Rum", "Cider"},
				//{"Beer", "Wine", "Cider"},
				//{"Beer", "Wine", "Cider"}
				};
		//gen.mapReduce(input);
		gen.init();
		System.out.println(gen.generate(input));
	}
}
