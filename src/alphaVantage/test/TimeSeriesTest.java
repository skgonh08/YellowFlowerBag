package alphaVantage.test;

import alphaVantage.InputDataType;
import alphaVantage.excelWrite.XLSDatabase;
import ta4j.TimeSeries;

public class TimeSeriesTest {

	public static void main(String[] args) {
		String stockName = "TCS";
		   
		
		   XLSDatabase db = new XLSDatabase(stockName, "Day");
		db.updateDatabase();
		TimeSeries timeSeries;
		try {
			timeSeries = db.loadTicks(1, InputDataType.YEAR);
			System.out.println("row count:"+timeSeries.getTickCount());
			System.out.println("Last Update Date: "+timeSeries.getTick(timeSeries.getTickCount()-1).getDateName());
		} catch (Exception e) {
			
			//e.printStackTrace();
		}
		

	}

}
