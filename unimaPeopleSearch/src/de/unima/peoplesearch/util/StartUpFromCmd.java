package de.unima.peoplesearch.util;

import java.io.IOException;

import de.unima.peoplesearch.database.PersonDAO;
import de.unima.peoplesearch.extraction.ParallelPipeline;
import de.unima.peoplesearch.extraction.Person;
import de.unima.peoplesearch.extraction.Pipeline;

public class StartUpFromCmd {

	public static void main(String[] args) throws IOException, InterruptedException {
		//Test db connection
		PersonDAO.getPeopleByMail("test");
		
		int method = Integer.parseInt(args[0]);
		
		int nermethod = Integer.parseInt(args[1]);
		
		int numberOfThreads = Integer.parseInt(args[2]);
		
		double threshold = Double.parseDouble(args[3]);
		
		int getRequestTimeOut = Integer.parseInt(args[4]);
		
		
		Pipeline pp = null;
		switch (method) {
		case 0:
			//parallel
			pp  =new ParallelPipeline(Integer.MAX_VALUE, numberOfThreads, nermethod, threshold,getRequestTimeOut);
			
			break;
		case 1: 
			//serial
			pp = new Pipeline(Integer.MAX_VALUE, nermethod, threshold,getRequestTimeOut);
			break;
		}
		
		pp.startExtraction();
	}

}
