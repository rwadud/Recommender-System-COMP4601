package edu.carleton.comp4601.utility;

import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;

import edu.carleton.comp4601.database.DatabaseManager;
import edu.carleton.comp4601.model.Review;

public class CsvWriter {

	/**
	 * Sets up the processors used for the examples. There are 10 CSV columns, so 10
	 * processors are defined. All values are converted to Strings before writing
	 * (there's no need to convert them), and null values will be written as empty
	 * columns (no need to convert them to "").
	 * 
	 * @return the cell processors
	 */
	private static CellProcessor[] getProcessors() {

		final CellProcessor[] processors = new CellProcessor[] { new NotNull(), // name
				new NotNull(), // very pos
				new NotNull(), // pos
				new NotNull(), // neu
				new NotNull(), // neg
				new NotNull(), // very neg
		};

		return processors;
	}

	/**
	 * An example of reading using CsvMapWriter.
	 */
	public static void writeWithCsvMapWriter(List<Review> reviews) throws Exception {

		final String[] header = new String[] {"NAME", "VERY_POSITIVE", "POSITIVE", "NEUTRAL", "NEGATIVE", "VERY_NEGATIVE"};

		ICsvMapWriter mapWriter = null;
		try {
			mapWriter = new CsvMapWriter(new FileWriter("./resources/sentiment.csv"),
					CsvPreference.STANDARD_PREFERENCE);

			final CellProcessor[] processors = getProcessors();

			// write the header
			mapWriter.writeHeader(header);

			for (Review review : reviews) {
				
				Map<String, Integer> scores = review.getSentimentScores();
				Map<String, Object> map = new HashMap<String, Object>();
				
				map.put("NAME", review.toString());
				
				for (Map.Entry<String, Integer> score : scores.entrySet()) {
					
					if(score.getKey().equals("Very positive")) {
						map.put("VERY_POSITIVE", score.getValue());
					} else if(score.getKey().equals("Positive")) {
						map.put("POSITIVE", score.getValue());
					} else if(score.getKey().equals("Neutral")) {
						map.put("NEUTRAL", score.getValue());
					} else if(score.getKey().equals("Negative")) {
						map.put("NEGATIVE", score.getValue());
					} else if(score.getKey().equals("Very negative")) {
						map.put("VERY_NEGATIVE", score.getValue());
					}
				}
				
				mapWriter.write(map, header, processors);
			}

		} finally {
			if (mapWriter != null) {
				mapWriter.close();
			}
		}
	}
	
	public static void main(String[] args) {
		
		try {
			List<Review> reviews = DatabaseManager.getInstance().getReviews();
			CsvWriter.writeWithCsvMapWriter(reviews);	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}

}
