package de.unima.peoplesearch.extraction;

import java.io.IOException;

public class ParallelPipeline extends Pipeline {
	
	
	private int numberOfWorker = 0;
	private int curr_link = 0;
	private int calledBackWorkers = 0;

	/**
	 * Initialize pipeline
	 * @param maxLinks
	 * @param numberOfWorker
	 */
	public ParallelPipeline(int maxLinks, int numberOfWorker, int NECHECKERTYPE,double threshold, int getRequestTimeOut) {
		super(maxLinks, NECHECKERTYPE, threshold, getRequestTimeOut);
		this.numberOfWorker = numberOfWorker;
	}
	
	
	
	public synchronized String getNextLink(){
		if(curr_link >= super.maxLinks) return null;
		if(curr_link >= links.size()) return null;
		String link = links.get(curr_link);
		curr_link++;
		notifyAll();
		return link;
	}
	
	
	public synchronized void callBack(){
		System.out.println("callback");
		calledBackWorkers++;
		System.out.println(calledBackWorkers +"---- "+ this.numberOfWorker);
		notifyAll();
	}



	@Override
	public void extractPeople() throws IOException, InterruptedException {
		for(int i = 0; i < numberOfWorker; i++){
			PersonExtractionTask pet = new PersonExtractionTask(this);
			pet.start();
		}
		boolean test1= false;
		//busy waiting for all workers
		while(calledBackWorkers < numberOfWorker){
			if(test1 == true){
				continue;
			}
			Thread.sleep(1000);
			System.out.println(curr_link);
		}
		System.out.println("Execute post processing steps");
	}
	
	public static void main(String args[]){

		ParallelPipeline pp = new ParallelPipeline(Integer.MAX_VALUE, 5,0,0.33,1000);

		try {
			pp.startExtraction();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

}
