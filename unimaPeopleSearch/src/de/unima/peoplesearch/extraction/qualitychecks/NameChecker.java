package de.unima.peoplesearch.extraction.qualitychecks;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents a wordList Check
 * @author mueller
 *
 */
public class NameChecker {
	
	private List<String> names;

	public NameChecker(String fileName) throws IOException {
		super();
		this.names = createWordListFromFlatFile(fileName);
	}
	
	private List<String> createWordListFromFlatFile(String fileName) throws IOException{
		ArrayList<String> names = new ArrayList<String>();
		
		BufferedReader br = new BufferedReader(new InputStreamReader (new FileInputStream(fileName)));
		
		String theLine = null;
		while ((theLine = br.readLine())!=null){
			names.add(theLine.trim().toLowerCase());
		}
		br.close();
		return names;
	}
	
	public boolean containsNames(String name){
		return this.names.contains(name.toLowerCase().trim());
	}
	
}
