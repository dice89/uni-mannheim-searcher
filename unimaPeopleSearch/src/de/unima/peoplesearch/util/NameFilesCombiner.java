package de.unima.peoplesearch.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class NameFilesCombiner {
  public static void main (String [] args) throws IOException{
	  List<String> names = new ArrayList<String>();
	  String stanfordFile ="data/namesstanford.txt";
	  BufferedReader stanfordReader = new BufferedReader(new InputStreamReader(new FileInputStream(stanfordFile)));
	  
	  String stanfordLine = "";
	  while((stanfordLine=stanfordReader.readLine())!=null){
		  names.add(stanfordLine.split("\t")[0].trim());
	  }
	  
	  stanfordReader.close();
	  List<String> additionalFiles = new ArrayList<String>();
	  
	  additionalFiles.add("data/englishgivennames.txt");
	  
	  for (String file : additionalFiles) {
		  BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		  
		  String theLine = "";
		  while((theLine=reader.readLine())!=null){
			  names.add(theLine.trim());
		  }
		  reader.close();
	}
	  
	 PrintWriter pw = new PrintWriter("data/names.txt");
	  
	  for (String name : names) {
		pw.println(name.trim().toLowerCase());
	}
	  pw.flush();
	  pw.close();
  }
}
