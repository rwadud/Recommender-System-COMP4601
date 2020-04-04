package edu.carleton.comp4601.utility;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

import com.google.common.io.Files;

import edu.carleton.comp4601.database.DatabaseManager;
import net.lingala.zip4j.ZipFile;

public class Utils {
	
	public static final String HOME_DIR = System.getProperty("user.home");
	public static final String TEMP_DIR = DatabaseManager.getInstance().getTempDir();
	
	
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
	
	public static String newTempDir() {
		return Files.createTempDir().getAbsolutePath();
	}
	
	public static void downloadFile(String FILE_URL) throws Exception {
		URL url = new URL(FILE_URL);
		String file = FilenameUtils.getName(url.getPath());
		System.out.println("Downloading " + FILE_URL);
		
		String output = Paths.get(TEMP_DIR, file).toString();
		
		try (BufferedInputStream in = new BufferedInputStream(url.openStream());
				  FileOutputStream fileOutputStream = new FileOutputStream(output)) {
				    byte dataBuffer[] = new byte[1024];
				    int bytesRead;
				    while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
				        fileOutputStream.write(dataBuffer, 0, bytesRead);
				    }
				} catch (IOException e) {
				    e.printStackTrace();
				    throw new Exception("file not found");
				}
		System.out.println("Done downloading  "+ file);
		/*
		if(file.endsWith(".zip")) {
			System.out.println("Extracting "+ file);
			new ZipFile(output).extractAll(TEMP_DIR);
			System.out.println("Done extracting "+ file);
		}*/
	}
}
