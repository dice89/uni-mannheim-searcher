package de.unima.peoplesearch.extraction.qualitychecks;

import java.io.IOException;

import de.unima.peoplesearch.extraction.Person;

public class InternationalNameCheck extends QualityCheck {
	
	
	// name list form http://www.netzmafia.de/software/net-tools/wordlists/names.txt
	private NameChecker checker;

	public InternationalNameCheck(QualityCheck next) {
		super(next);
		this.initNameChecker();
	}

	public InternationalNameCheck() {
		this.initNameChecker();
	}
	
	private void initNameChecker(){
		try {
			this.checker = new NameChecker("data/internationalnames.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean checkInternal(Person p) {
		
		String[] firstNames = p.getFirstNames().split(" ");
		
		int correctCounter = 0;
		for (String name : firstNames) {
			if(checker.containsNames(name)){
				System.out.println(name);
				correctCounter++;
			}
		}
		System.out.println("INTERNATIONAL CORRECT COUNTER"+correctCounter );
		double ratio =  ((double)((double)correctCounter)/ ((double) firstNames.length));
		return (ratio >= 0.20);
	}

}
