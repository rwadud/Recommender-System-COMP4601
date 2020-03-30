package edu.carleton.comp4601.utility;

import java.util.Map;

public class Utils {

	// returns max entry in map
	public static <K, V extends Comparable<V>> Map.Entry<K, V> maxEntryInMap(Map<K, V> map) {
	    Map.Entry<K, V> maxEntry = null;
	    for (Map.Entry<K, V> entry : map.entrySet()) {
	        if (maxEntry == null || entry.getValue()
	            .compareTo(maxEntry.getValue()) > 0) {
	            maxEntry = entry;
	        }
	    }
	    return maxEntry;
	}
	
	public static void printTimeElapsed(long start) {
		long finish = System.currentTimeMillis();
		long timeElapsed = finish - start;
		System.out.println("Took "+ (timeElapsed/1000) + " seconds");
	}
}
