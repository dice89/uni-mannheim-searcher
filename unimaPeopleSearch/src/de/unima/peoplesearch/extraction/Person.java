/**
 * 
 */
package de.unima.peoplesearch.extraction;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities.EscapeMode;
import org.jsoup.select.Elements;

/**
 * @author Michi,Alex
 * 
 */
@Entity
@Table
public class Person {

	public static final Pattern EMAIL_REGEX = Pattern.compile(
			"[A-Z0-9._%+-]*[\\s\\xA0]*(@|[\\(\\[]?at[\\)\\]]?)[\\s\\xA0]*[A-Z0-9.-]+\\.[A-Z]{2,6}",
			Pattern.CASE_INSENSITIVE);// (\\(at\\))?

	public static final Pattern PHONE_REGEX = Pattern
			.compile("(\\+[0-9]{2,3})?[-.?????????/\\s]{0,3}(\\(0\\)[-.?????????/\\s]{0,3})?\\(?([0-9]{3,4})\\)?[-.?????????/\\s]{0,3}([0-9]{3,4})[-.?????????/\\s]{0,3}([0-9]{4})");

	public static final Pattern PLZ_MANNHEIM_REGEX = Pattern.compile("[0-9]{5} Mannheim");
	public static final Pattern STREET_REGEX = Pattern.compile("[A-U]\\s?[0-9]{1,2}([,\\s]+[0-9]+)?");// Currently
																										// for
																										// mannheims
																										// squares
																										// only!
	public static final Pattern OFFICE_REGEX = Pattern.compile(
			"(room|raum|zimmer|zi\\.).*\\s[A-Z]?[0-9]{1,4}(\\.[0-9]{1,3})?", Pattern.CASE_INSENSITIVE);

	
	// Parameter used for JPA Handling
	
	
	@Id  
	@GeneratedValue  
	private Integer id;  
	
	
	private String label = "";
	private String firstNames = "";
	private String lastName = "";
	private String titles = "";
	private String location_zip;
	private String location_street;
	private String location_room;
	private String phoneNumber;
	private String imageUrl;
	private String email;
	
	
	//private Empty Constructor for JPA
	public Person(){
		
	}

	
	public Person(Integer id, String label, String firstNames, String lastName,
			String titles, String location_zip, String location_street,
			String location_room, String phoneNumber, String imageUrl,
			String email) {
		super();
		this.id = id;
		this.label = label;
		this.firstNames = firstNames;
		this.lastName = lastName;
		this.titles = titles;
		this.location_zip = location_zip;
		this.location_street = location_street;
		this.location_room = location_room;
		this.phoneNumber = phoneNumber;
		this.imageUrl = imageUrl;
		this.email = email;
	}


	public void tryExtract(String input, String baseUrl) {

		// TODO:Change base url
		Document doc = Jsoup.parse(input, baseUrl);

		doc.outputSettings().charset("ISO-8859-1");
		doc.outputSettings().escapeMode(EscapeMode.xhtml);

		Elements elements;

		Elements content = doc.select("#content");

		// Get page title
		Elements headings = content.select("h1, h2, h3");
		for (int i = 0; i < headings.size(); i++) {
			if (headings.get(i).text().split(" ").length > 1) {
				System.err.println(headings.get(i).text());
				this.testForName(headings.get(i).text());
				if (this.label.length() <1)this.label = headings.get(i).ownText();
				break;
			}
		}
		// Look for contact details in all elements
		elements = content.select("*");
		for (Element element : elements) {
			// Skipp any elements with to less text
			if (element.ownText().length() < 5)
				continue;

			// Try extract email from href
			if (element.hasAttr("href")) {

				String relHref = element.attr("href");
				if (relHref.toLowerCase().startsWith("mailto")) {
					System.err.println(relHref.split(":")[1].replace(" ", ""));
					this.email = relHref.split(":")[1].replace(" ", "");
				}
			}

			// Handle line breaks within DOM element as distinct elements
			String[] p = element.ownText().split("br2n");
			for (int i = 0; i < p.length; i++) {

				Matcher m;
				// System.out.println(Jsoup.parse(element.html()).text());

				// //Try matching zip code + city
				m = PLZ_MANNHEIM_REGEX.matcher(p[i]);
				if (m.find()) {
					System.err.println(m.group());
					if (this.location_zip == null)
						this.location_zip = m.group();
				}

				// //Try matching street
				m = STREET_REGEX.matcher(p[i]);
				if (m.find()) {
					System.err.println(m.group());
					if (this.location_street == null)
						this.location_street = m.group();
				}

				// Try extract office room
				m = OFFICE_REGEX.matcher(p[i]);
				if (m.find()) {
					System.err.println(m.group());
					if (this.location_room == null)
						this.location_room = m.group();
				}

				// Try extract phone numbers
				m = PHONE_REGEX.matcher(p[i]);
				if (m.find()) {
					System.err.println(m.group());
					if (this.phoneNumber == null)
						this.phoneNumber = m.group();
				}

				// Try extract email from text
				m = EMAIL_REGEX.matcher(p[i]);
				if (m.find()) {
					System.err.println(m.group());
					if (this.email == null)
						this.email = m.group();
				}

				// System.out.println();
			}

		}

		// Try extract image
		Elements imgTags = content.select("img");
		for (Element imgTag : imgTags) {
			System.err.println(imgTag.attr("abs:src"));
			try {
				if (Integer.parseInt(imgTag.attr("width")) > 50) // Ignore icons
					this.imageUrl = imgTag.attr("abs:src");

			} catch (NumberFormatException e) {
				// e.printStackTrace();
			}
		}
		
		this.sanitizeFields();
	}

	private void sanitizeFields() {
		if (email!=null) {
			this.email = this.email.replace("AT", "@");
			this.email = this.email.toLowerCase().replace(" ", "");
			this.email = this.email.replace("[at]", "@").replace("(at)", "@").replace("{at}", "@");
		}
		
	}

	private void testForName(String text) {
		String[] parts = text.split(" ");
		String names = "";

		// Set names if not already set
		for (int i = 0; i < parts.length; i++) {
			if (parts[i].startsWith("(")) continue;
			if (parts[i].endsWith("."))
				this.titles += parts[i] + " ";
			else names += parts[i]+" ";
		}
		
		String[] nameParts = names.split(" ");
		if (nameParts.length > 1) {
			this.lastName = nameParts[nameParts.length-1];
			for (int i = 0; i < nameParts.length-1; i++) {
				if (nameParts[i].startsWith("(")) continue;
				this.firstNames += nameParts[i]+" "; 
			}
		}

		// Some sanatizing
		this.firstNames = this.firstNames.replace(",", "").trim();
		this.lastName = this.lastName.replace(",", "").trim();

	}
	
	public boolean isPerson() {
		return this.firstNames.length() >1 && this.lastName.length() >1
				&& (this.phoneNumber != null || this.email != null);
	}

	@Override
	public String toString() {
		return "Person [label= " + label + "\n firstNames= " + firstNames + "\n lastName= " + lastName + "\n titles= "
				+ titles + "\n location_zip= " + location_zip + "\n location_street= " + location_street
				+ "\n location_room= " + location_room + "\n phoneNumber= " + phoneNumber + "\n imageUrl= " + imageUrl
				+ "\n email= " + email + "\n";
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @return the firstNames
	 */
	public String getFirstNames() {
		return firstNames;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @return the titles
	 */
	public String getTitles() {
		return titles;
	}

	/**
	 * @return the location_zip
	 */
	public String getLocation_zip() {
		return location_zip;
	}

	/**
	 * @return the location_street
	 */
	public String getLocation_street() {
		return location_street;
	}

	/**
	 * @return the location_room
	 */
	public String getLocation_room() {
		return location_room;
	}

	/**
	 * @return the phoneNumber
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * @return the imageUrl
	 */
	public String getImageUrl() {
		return imageUrl;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setFirstNames(String firstNames) {
		this.firstNames = firstNames;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setTitles(String titles) {
		this.titles = titles;
	}

	public void setLocation_zip(String location_zip) {
		this.location_zip = location_zip;
	}

	public void setLocation_street(String location_street) {
		this.location_street = location_street;
	}

	public void setLocation_room(String location_room) {
		this.location_room = location_room;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getId() {
		return id;
	}
	
	
	

}
