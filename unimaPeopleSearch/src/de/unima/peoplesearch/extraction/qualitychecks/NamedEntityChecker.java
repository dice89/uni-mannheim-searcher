package de.unima.peoplesearch.extraction.qualitychecks;

import java.util.List;
import java.util.Properties;

import de.unima.peoplesearch.extraction.Pipeline;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;

public class NamedEntityChecker extends AbstractNamedEntityChecker {
	
	private  StanfordCoreNLP pipeline;
	public NamedEntityChecker(double threshold){
			super(threshold);
		 Properties props = new Properties();
		 props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse");

		 pipeline = new StanfordCoreNLP(props);
	}
	
	public boolean containsNERPerson(String heading){
		Annotation annotation = new Annotation(heading);
		pipeline.annotate(annotation);
		int size = 0;
		int personcounter = 0;
	    List<CoreMap> sentences = annotation.get(SentencesAnnotation.class);
		  for(CoreMap sentence: sentences) {
		      // traversing the words in the current sentence
		      // a CoreLabel is a CoreMap with additional token-specific methods
		      for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
		    	  size++;
		        String ne = token.get(NamedEntityTagAnnotation.class);   
		        if(ne.equals("PERSON")){
		        	personcounter++;
		        	//return true;
		        }
		      }
		  	}
		  double ratio = (double) (((double) personcounter) /((double)size) );
		  
		  return ratio >= Pipeline.THRESHOLD;
	}
	
	
}
