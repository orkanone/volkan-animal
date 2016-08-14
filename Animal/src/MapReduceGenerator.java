import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import algoanim.animalscript.AnimalScript;
import algoanim.primitives.SourceCode;
import algoanim.primitives.generators.Language;


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
