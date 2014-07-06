package de.unima.peoplesearch.extraction;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;

import edu.stanford.nlp.trees.GrammaticalRelation.KillGRAnnotation;

public class PersonExtractionTask extends Thread {
	private ParallelPipeline pipeline;
	
	
	public PersonExtractionTask(ParallelPipeline pipeline) {
		super();
		this.pipeline = pipeline;
	}


	@Override
	public void run() {
		try{
		System.out.println("worker running");
		String link = null;
		
		while((link =pipeline.getNextLink())!= null){
			String docu = null;
			try {
				docu = getDocumentforPerson(link);
			
			}catch (Exception e){
				System.err.println("some error occured in the worker");
			}	
			if(docu == null) continue;
		
			try {
				this.extractPerson(docu, link);
			} catch (IOException e) {
				System.err.println("some error occured in worker");
			}
		}
		System.out.println("worker done");
		
		}catch (Exception e){
			
		}finally{
			this.pipeline.callBack();
		
		}
		//this.pipeline.callBack();
		
	}
	/**
	 * 
	 * Triggers a get call to get an html page for a given link
	 * @param link
	 * @return
	 * @throws IOException
	 */
	public String getDocumentforPerson(String link) {
		String input = null;
		try {
			input = Jsoup.connect(link).ignoreContentType(true).ignoreHttpErrors(true).timeout(10*1000).get().toString();
			input = input.replaceAll("(?i)<br[^>]*>", " br2n ");			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("timeout no problemo");
		}

		return input;
	}
	
	public void extractPerson(String document, String link) throws IOException{
			
			String baseUrl = link.replaceAll("/[a-zA-Z0-9]+\\.htm[l]?", "/");
			Person newPerson = this.pipeline.extractPerson(document,baseUrl);
			if(this.pipeline.onTheFlyPruning(newPerson)){
				newPerson.setUrl(link);
				this.pipeline.handleDuplicate(newPerson);
			}
		}
	
	
}
