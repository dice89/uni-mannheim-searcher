package de.unima.peoplesearch.extraction.qualitychecks;

import java.util.ArrayList;
import java.util.List;

public class NamedEntityCheckerCombiner extends AbstractNamedEntityChecker {

	private List<AbstractNamedEntityChecker> checker;
	public NamedEntityCheckerCombiner(double threshold){
		super(threshold);
		checker = new ArrayList<AbstractNamedEntityChecker>();
		//add english one
		checker.add(new NamedEntityChecker(threshold));
		//and German one
		checker.add(new NamedEntityCheckerGerman(threshold));
		
	}
	@Override
	public boolean containsNERPerson(String heading) {
		int correctcount = 0;
		for (AbstractNamedEntityChecker check : this.checker) {
			if(check.containsNERPerson(heading)){
				correctcount++;
			}
		}
		return correctcount >0;
	}

}
