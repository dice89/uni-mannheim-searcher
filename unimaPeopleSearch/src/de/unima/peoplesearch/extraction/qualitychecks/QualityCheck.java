package de.unima.peoplesearch.extraction.qualitychecks;

import de.unima.peoplesearch.extraction.Person;

public abstract class QualityCheck {
	private QualityCheck next;

	public QualityCheck() {
		
	}
	public QualityCheck(QualityCheck next) {
		super();
		this.next = next;
	}
	
	public abstract boolean checkInternal(Person p);
	
	public boolean check(Person p){
		//if no next so last one in chain return own check result
		if(next == null){
			return checkInternal(p);
		}
		
		//if one is false all are false
		if(!next.check(p)) return false;
		
		//predecessor was true so return own check result
		return checkInternal(p);
	}
}
