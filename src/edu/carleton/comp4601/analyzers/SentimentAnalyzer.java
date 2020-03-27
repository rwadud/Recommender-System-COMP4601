package edu.carleton.comp4601.analyzers;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.spark_project.guava.collect.Lists;

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
	private final String document;
	
	public SentimentAnalyzer(String document) {
		this.document = document;
	}
	
	public Map<String, Integer> calculate() {
		return annotateAndScore();
	}
	
    public Map<String, Integer> annotateAndScore(){
    	Map<String, Integer> scoreMap = new HashMap<String, Integer>();
        List<Integer> scores = Lists.newArrayList();
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, parse, sentiment");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        Annotation document = new Annotation(this.document);
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
	
}
