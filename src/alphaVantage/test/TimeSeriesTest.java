package alphaVantage.test;

import java.util.List;

import alphaVantage.InputDataType;
import alphaVantage.excelWrite.TicksXLSDatabase;
import alphaVantage.input.ReadDataFromWeb.ReadTopGainersFromReligare;
import alphaVantage.input.ReadDataFromWeb.UpdateAllCompanyInfo;
import ta4j.TimeSeries;

public class TimeSeriesTest {

	public static void main(String[] args) {

		// Downloading data from alpha vantage and loading it into ta4j
		// TimeSeries
		String stockName = "TCS";
		TicksXLSDatabase db = new TicksXLSDatabase(stockName, "Day");
		db.updateDatabase();
		TimeSeries timeSeries;
		try {
			timeSeries = db.loadTicks(1, InputDataType.YEAR);
			System.out.println("row count:" + timeSeries.getTickCount());
			System.out.println("Last Update Date: " + timeSeries.getTick(timeSeries.getTickCount() - 1).getDateName());
		} catch (Exception e) {

			e.printStackTrace();
		}
		// Updatating NSE all Company Information into Excel Sheet

		UpdateAllCompanyInfo.update();

		// Reading top gainer from Religare and keeping data in a Excel Sheet

		ReadTopGainersFromReligare read = new ReadTopGainersFromReligare();
		read.readNUpdateXLSSheet();

	}

}
