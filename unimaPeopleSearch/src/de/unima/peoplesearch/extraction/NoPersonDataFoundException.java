package de.unima.peoplesearch.extraction;

public class NoPersonDataFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2688632288291790363L;

	@Override
	public String getMessage() {
		return "No Named Entity of Type Person Found in the Headings";
	}

	
}
