package peopleSearch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import org.jsoup.Jsoup;

public class HtmlExtractor {
	private String path = "data/urls.txt";
	
	/*public static void main (String[] args){
		//readLinks();
		downloadPage("http://www.google.de");
	}*/
	
	public ArrayList<String> readLinks(){
		ArrayList<String> links = new ArrayList<String>();
		BufferedReader br = null;
		String line;
			
		try {
			br = new BufferedReader(new FileReader(path));
			
			while((line = br.readLine()) != null){
				links.add(line);
			}
			//System.out.println(links.size());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	return links;
	}
	
	public void downloadPage(String link){
		try {
			System.out.println(link);
			String html = Jsoup.connect(link).ignoreContentType(true).ignoreHttpErrors(true).timeout(10*1000).get().toString();
			BufferedWriter writer = null;
			writer = new BufferedWriter(new FileWriter("temp.html"));
			writer.write(html);
	        if ( writer != null) {
		        writer.close( );
	        }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
