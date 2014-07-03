package de.unima.peoplesearch.extraction;

import de.unima.peoplesearch.extraction.qualitychecks.EnglishGivenNameCheck;
import de.unima.peoplesearch.extraction.qualitychecks.InternationalNameCheck;

public class CandidatePruner {
	//TODO add more
	private static EnglishGivenNameCheck e = new EnglishGivenNameCheck();
	
	//private static InternationalNameCheck inc = new InternationalNameCheck();
	public static boolean checkCandidate(Person p){
		return ( e.check(p));
	}
}
