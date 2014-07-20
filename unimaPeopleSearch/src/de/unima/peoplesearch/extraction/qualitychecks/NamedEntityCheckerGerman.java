package de.unima.peoplesearch.extraction.qualitychecks;

import java.io.IOException;
import java.util.List;

import de.unima.peoplesearch.extraction.Pipeline;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;

public class NamedEntityCheckerGerman extends AbstractNamedEntityChecker {

	
	private AbstractSequenceClassifier<CoreLabel> classifier;
	public NamedEntityCheckerGerman(double threshold){
		super(threshold);
		
		 String serializedClassifier = "data/classifiers/hgc_175m_600.crf.ser.gz";
		 try {
			 classifier = CRFClassifier.getClassifier(serializedClassifier);
		} catch (ClassCastException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		 	
	}

	@Override
	public boolean containsNERPerson(String heading) {
		
		int personcounter = 0;
		int size =0;
		 for (List<CoreLabel> lcl : classifier.classify(heading)) {
			 size = size  +lcl.size();
	          for (CoreLabel cl : lcl) {
	        	  
	             String ne = cl.get(CoreAnnotations.AnswerAnnotation.class);
	            if(ne.equals("I-PER")){
	            	personcounter++;
	        
	            }
	          }
	        }
		double ratio = (double) (((double) personcounter) /((double)size) );
		 
		return  ratio  >= Pipeline.THRESHOLD;
	}
	
}
