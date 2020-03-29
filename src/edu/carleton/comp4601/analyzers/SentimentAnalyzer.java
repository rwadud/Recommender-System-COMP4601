package edu.carleton.comp4601.analyzers;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.spark_project.guava.collect.Lists;

import com.google.common.collect.ImmutableMap;

import avro.shaded.com.google.common.collect.Maps;
import edu.carleton.comp4601.model.Review;
import edu.carleton.comp4601.model.Sentiment;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations.SentimentAnnotatedTree;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations.SentimentClass;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

public class SentimentAnalyzer {

	private static HashMap<String, Float> sentimentMap;
	
	private SentimentAnalyzer() {}
		
    public static Map<String, Integer> annotateAndScore(String doc){
    	Map<String, Integer> scoreMap = Maps.newHashMap(ImmutableMap.of("Very positive", 0, "Positive", 0, "Neutral", 0, "Negative", 0, "Very negative", 0));
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, parse, sentiment");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        Annotation document = new Annotation(doc);
        pipeline.annotate(document);
        for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
            Tree annotatedTree = sentence.get(SentimentAnnotatedTree.class);
            String sentiment = sentence.get(SentimentClass.class);
            int score = RNNCoreAnnotations.getPredictedClass(annotatedTree);
            Integer storedScore = scoreMap.get(sentiment);
            score = (storedScore == null) ? score : score + storedScore;
            
            scoreMap.put(sentiment, score);
        }
        return scoreMap;
    }
    
    public static Sentiment calculateSentiment(Map<String, Integer> scores) {
    	Integer pos = scores.get("Positive") + scores.get("Very positive");
    	Integer neg = scores.get("Negative") + scores.get("Very negative");
    	
    	Sentiment sentiment = (pos > neg) ? Sentiment.POSITIVE : ((pos < neg) ? Sentiment.NEGATIVE : Sentiment.NEUTRAL);
    	
    	System.out.println(sentiment);
    	return sentiment;
    }
    
}