package de.unima.peoplesearch.util;

import de.unima.peoplesearch.database.PersonDAO;
import de.unima.peoplesearch.extraction.Person;

public class FinalCleaner {
 public static void main (String args[]) {
	for(Person person: PersonDAO.getAllPeople()){
		System.out.println(person.getFirstNames());
	}
 }
}
