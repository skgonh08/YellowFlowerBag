package alphaVantage.input.ReadDataFromWeb;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class UpdateAllCompanyInfo {
	
public static void main (String[] args){
	
		try {
			Document doc = Jsoup.connect("http://www.religareonline.com/market/news").userAgent("Mozilla/17.0").get();
			
			Elements temp  = doc.select("div.s11.pT2.mB5");
			
			int i =0;
			for (Element element : temp){
				i++;
				System.out.println(i+". "+element.getElementsByTag("a").last().text()); 
			}
		} catch (IOException e) {
			e.printStackTrace();
		}	

}

}
