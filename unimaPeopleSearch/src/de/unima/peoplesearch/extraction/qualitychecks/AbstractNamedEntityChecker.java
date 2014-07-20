package de.unima.peoplesearch.extraction.qualitychecks;

public abstract class AbstractNamedEntityChecker {
	public static final int NE_GER = 0;
	public static final int NE_ENG = 1;
	public static final int NE_COMBINE =2;
	
	protected double threshold;
	
	public AbstractNamedEntityChecker(double threshold) {
		super();
		this.threshold = threshold;
	}

	public abstract boolean containsNERPerson(String heading);
}
