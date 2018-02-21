package alphaVantage.input.ReadDataFromWeb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import alphaVantage.excelWrite.NewsXLSDataBase;

public class ReadTopGainersFromReligare {

	public List<String> read() {

		List<String> stocks = new ArrayList<String>();
		String stokName = "";
		try {
			Document doc = Jsoup.connect("http://www.religareonline.com/market/stock/nse-bse-top-gainers")
					.userAgent("Mozilla/17.0").get();

			Elements temp = doc.select("td.aL.b");

			int i = 0;
			for (Element element : temp) {
				i++;
				stokName = element.getElementsByTag("a").first().text();

				stocks.add(stokName.replace("Ltd.", "Limited").replace("&", "and"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return stocks;
	}

	public void readNUpdateXLSSheet() {
		// Reading data from Website
		List<String> stocks = read();
		ArrayList<String> stocksSymbol = new ArrayList<String>();
		// Checking symbol from NSE Excel Sheet
		UpdateAllCompanyInfo companies = new UpdateAllCompanyInfo();
		Map<String, String> allCompanyInfo = companies.read();

		for (String stock : stocks) {
			stocksSymbol.add(allCompanyInfo.get(stock.toLowerCase()));
		}
		// Writing Data to output Excel File
		
		NewsXLSDataBase xlsDatabase = new NewsXLSDataBase("NewsFromWeb", "Religare");
		xlsDatabase.updateDatabase(stocksSymbol, "22-FEB-18");

	}
}
