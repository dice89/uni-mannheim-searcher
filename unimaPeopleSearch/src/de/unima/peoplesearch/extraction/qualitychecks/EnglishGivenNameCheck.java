package de.unima.peoplesearch.extraction.qualitychecks;

import java.io.IOException;

import de.unima.peoplesearch.extraction.Person;

public class EnglishGivenNameCheck extends QualityCheck {
	
	private NameChecker checker;

	public EnglishGivenNameCheck(QualityCheck next) {
		super(next);
		this.initNameChecker();
	}

	public EnglishGivenNameCheck() {
		this.initNameChecker();
	}
	
	private void initNameChecker(){
		try {
			this.checker = new NameChecker("data/names.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean checkInternal(Person p) {
		
		String[] firstNames = p.getFirstNames().split(" ");
		
		int correctCounter = 0;
		for (String name : firstNames) {
			if(checker.containsNames(normalizeName(name))){
				correctCounter++;
				System.out.println(name);
			}
		}
		double ratio =  ((double)((double)correctCounter)/ ((double) firstNames.length));
		return (ratio >= 0.40);
	}
	
	public String normalizeName(String word){
		return word.replace("ü", "u").replace("ä", "a").replace("ö", "o");
	}

}
