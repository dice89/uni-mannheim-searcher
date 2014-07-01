package de.unima.peoplesearch.extraction;

import de.unima.peoplesearch.extraction.qualitychecks.EnglishGivenNameCheck;

public class CandidatePruner {
	//TODO add more
	private static EnglishGivenNameCheck e = new EnglishGivenNameCheck();
	
	public static boolean checkCandidate(Person p){
		return e.check(p);
	}
}
