/**
 * 
 */
package de.unima.peoplesearch.extraction;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;

import de.unima.peoplesearch.database.PersonDAO;
import de.unima.peoplesearch.extraction.qualitychecks.NameChecker;
import de.unima.peoplesearch.extraction.qualitychecks.NamedEntityChecker;

/**
 * @author Michi
 * 
 */
@Deprecated
public class ExtractorTest {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		
		CandidatePruner cp = new CandidatePruner();
		
		NamedEntityChecker neChecker = new NamedEntityChecker(0.33);
		// String url =
		// "http://dws.informatik.uni-mannheim.de/en/people/professors/prof-dr-simone-paolo-ponzetto/";
		// Document doc = Jsoup.parse(new URL(url).openStream(), "ISO-8859-1",
		// url);
		// Document doc = Jsoup.connect(url).get();
		// doc.outputSettings().charset("UTF-8");

		ArrayList<Person> persons = new ArrayList<Person>();
		/*
		 * List<File> files = Arrays.asList(new File("data/pages").listFiles());
		 * 
		 * for(File file: files) { System.out.println(file.getAbsolutePath());
		 * String input = Resources.toString(file.toURL(), Charsets.UTF_8);
		 * input = input.replaceAll("(?i)<br[^>]*>", "br2n"); //
		 * System.out.println(input); Person newPerson = new Person();
		 * newPerson.tryExtract(input, "http://example.com");
		 * persons.add(newPerson); }
		 */
		HtmlExtractor ex = new HtmlExtractor();
		ArrayList<String> list = ex.readLinks();
		final int MAX = 10000;
		int counter = 1;
		int timeoutCounter = 0;
		for (String s : list) {
			try {
				if (counter >= MAX) break;
				System.out.println("GET " + counter++ + " " + s);
				String input = Jsoup.connect(s).ignoreContentType(true).ignoreHttpErrors(true).timeout(1000).get().toString();
				input = input.replaceAll("(?i)<br[^>]*>", " br2n ");
				Person newPerson = new Person();
				String baseUrl = s.replaceAll("/[a-zA-Z0-9]+\\.htm[l]?", "/");
				
				newPerson.tryExtract(input, baseUrl,neChecker);
				
				
				if (! CandidatePruner.checkCandidate(newPerson)){
					
					System.err.println("Not a Person : "+newPerson.getFirstNames()+"--> continue");
					continue;
				}
				System.err.println(newPerson.getFirstNames());
			
				
//				System.err.println(newPerson.getImageUrl());
			} catch (Exception e) {
				timeoutCounter++;
				e.printStackTrace();
			}
		}
		
		for (Person p : persons) {
			PersonDAO.savePerson(p);
		}

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
			System.out.println(p);
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
		System.out.println("Total pages vistited: " + counter);
		System.out.println("Found persons: " + persons.size());
		System.out.println("Timouts: " + timeoutCounter);
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

	public static ArrayList<String> findFiles(String rootPath, String ending) {
		ArrayList<String> result = new ArrayList<String>();
		File[] domains = new File(rootPath).listFiles();
		for (File domain : domains) {
			if (domain.isDirectory()) {
				File[] files = new File(domain.getAbsolutePath()).listFiles();
				for (File file : files) {
					if (file.isFile()
							&& file.getAbsolutePath().endsWith(ending)) {
						result.add(file.getAbsolutePath());
					}
				}
			}
		}
		return result;
	}

}
