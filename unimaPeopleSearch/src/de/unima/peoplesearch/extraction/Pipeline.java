package de.unima.peoplesearch.extraction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;

import de.unima.peoplesearch.database.PersonDAO;
import de.unima.peoplesearch.extraction.qualitychecks.AbstractNamedEntityChecker;
import de.unima.peoplesearch.extraction.qualitychecks.NamedEntityChecker;
import de.unima.peoplesearch.extraction.qualitychecks.NamedEntityCheckerCombiner;
import de.unima.peoplesearch.extraction.qualitychecks.NamedEntityCheckerGerman;

public class Pipeline {
	
	public static final double THRESHOLD = 0.33;
	//TODO implement clean pipeline and remove ugly coding
	private AbstractNamedEntityChecker neChecker;
	
	private int getRequestTimeOut;
	protected int maxLinks;
	protected List<Person> persons;
	protected List<String> links;
	
	public Pipeline(int maxLinks, int NECHECKERTYPE, double threshold, int getRequestTimeOut){
		//Determine which type
		switch (NECHECKERTYPE) {
		case 0:
			this.neChecker = new NamedEntityCheckerGerman(threshold);
			break;
		case 1:
			this.neChecker = new NamedEntityChecker(threshold);
			break;
		case 2:
			this.neChecker = new NamedEntityCheckerCombiner(threshold);
			break;
		}
		persons = new ArrayList<Person>();
		this.maxLinks = maxLinks;
		this.getRequestTimeOut = getRequestTimeOut;
	}
	


	
	/**
	 * Method that triggers the overall Extraciton Pipeline
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	public void startExtraction() throws IOException, InterruptedException{
	
		this.links = getLinks();
		//extraction Step
		extractPeople();
		
		savePersonEntitiesToDB();
		
		//qualityCheck
		
		postExtractionQualityPruning();
		
		
		printSummary();
	}
	
	/**
	 * Method that stores Entities in the Database
	 */
	private void savePersonEntitiesToDB() {
		for(Person person: this.persons){
			PersonDAO.savePerson(person);
		}
	}
	


	/**
	 * Get the links from the Html
	 * @return
	 */
	private ArrayList<String> getLinks(){
		HtmlExtractor ex = new HtmlExtractor();
		return ex.readLinks();
	}

	/**
	 * 
	 * Triggers a get call to get an html page for a given link
	 * @param link
	 * @return
	 * @throws IOException
	 */
	private String getDocumentforPerson(String link) {
		String input = null;
		try {
			input = Jsoup.connect(link).ignoreContentType(true).ignoreHttpErrors(true).timeout(1000).get().toString();
			input = input.replaceAll("(?i)<br[^>]*>", " br2n ");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("timeout no problemo");
		}

		return input;
	}
	
	/**
	 * Extracts People from a given set of links 
	 * @param links
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	protected void extractPeople() throws IOException, InterruptedException{
		
		Person newPerson;
		int counter =0;
		for (String link : links) {
			if(counter >= this.maxLinks) return;
			counter++;
			String baseUrl = link.replaceAll("/[a-zA-Z0-9]+\\.htm[l]?", "/");
			String document = getDocumentforPerson(link);
			if(document == null) continue;
		
			newPerson = extractPerson(document,baseUrl);
			if(onTheFlyPruning(newPerson)){
				newPerson.setUrl(link);
				this.handleDuplicate(newPerson);
			}
		}
	
	}

	
	/**
	 * Triggers the Person Extraction
	 * @param document
	 * @param baseUrl
	 * @return
	 */
	public Person extractPerson(String document,String baseUrl){
		Person person = new Person();
			
			try {
				person.tryExtract(document, baseUrl, neChecker);
			} catch (NoPersonDataFoundException e) {
				return null;
			}
		
		return person;
	}

	
	/**
	 * Performs on the fly Pruning of Candidates, by some Rules
	 * @param p
	 * @return
	 */
	public boolean onTheFlyPruning(Person p ){
		if(p==null){
			return false;
		}else{
			return true;
			//return CandidatePruner.checkCandidate(p);
		}
	}
	
	public synchronized void handleDuplicate(Person p){
		if (p.isPerson()) {
			
			if (!p.hasDuplicate(persons)) {
				
				System.out.println("added person forreal");
				persons.add(p);
			} else {
				Person duplicate = p.getDuplicate(persons);
				if (p.getFieldsNotNull() > duplicate.getFieldsNotNull()){
					
					persons.add(p);
					persons.remove(duplicate);
				} 
			}
		}
		notifyAll();
	}
	
	/** Saves a Person to the Database
	 * @param newPerson
	 */
	public void savePersonToDB(Person newPerson){
		PersonDAO.savePerson(newPerson);
	}
	
	/**
	 * Post Extraction Quality run containing:
	 *  2. Post Processing of Contact Data
	 */
	public void postExtractionQualityPruning(){
		//TODO
	}
	
	public void printPerson(Person p){
		System.out.println(p.toString());
	}

	/**
	 * Prints summary of data extracted
	 */
	private void printSummary(){
		int cPerson = 0;
		int cFN = 0;
		int cLN = 0;
		int cTitle = 0;
		int cEmail = 0;
		int cZip = 0;
		int cStreet = 0;
		int cRoom = 0;
		int cPhone = 0;
		int cImage = 0;

		// Output
		System.out.println();
		System.out.println("Extracted people information: ");
		for (Person p : persons) {
		
			if (p.isPerson())
				cPerson++;
			if (p.getFirstNames().length() > 1)
				cFN++;
			if (p.getLastName().length() > 1)
				cLN++;
			if (p.getTitles() != null && p.getTitles().length() > 1)
				cTitle++;
			if (p.getEmail() != null && p.getEmail().length() > 1)
				cEmail++;
			if (p.getPhoneNumber() != null && p.getPhoneNumber().length() > 1)
				cPhone++;
			if (p.getLocation_zip() != null && p.getLocation_zip().length() > 1)
				cZip++;
			if (p.getLocation_room() != null
					&& p.getLocation_room().length() > 1)
				cRoom++;
			if (p.getLocation_street() != null
					&& p.getLocation_street().length() > 1)
				cStreet++;
			if (p.getImageUrl() != null && p.getImageUrl().length() > 1)
				cImage++;
		}

		System.out.println("Some stats:");

		System.out.println("Found persons: " + persons.size());
		System.out.println("###########");
		System.out.println("First names-%: " + (double) cFN / persons.size());
		System.out.println("Last names-%: " + (double) cLN / persons.size());
		System.out.println("Titles-%: " + (double) cTitle / persons.size());
		System.out.println("Emails-%: " + (double) cEmail / persons.size());
		System.out.println("Phones-%: " + (double) cPhone / persons.size());
		System.out.println("ZIPs-%: " + (double) cZip / persons.size());
		System.out.println("Streets-%: " + (double) cStreet / persons.size());
		System.out.println("Rooms-%: " + (double) cRoom / persons.size());
		System.out.println("Images-%: " + (double) cImage / persons.size());

		
	}
	
	public static void main (String args[]){
		Pipeline p = new Pipeline(1000,2,0.33,100);
		
		try {
			p.startExtraction();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
