package alphaVantage.test;

import java.util.List;

import alphaVantage.InputDataType;
import alphaVantage.excelWrite.TicksXLSDatabase;
import alphaVantage.input.ReadDataFromWeb.ReadTopGainersFromReligare;
import alphaVantage.input.ReadDataFromWeb.UpdateAllCompanyInfo;
import ta4j.TimeSeries;

public class TimeSeriesTest {

	public static void main(String[] args) {
//		String stockName = "TCS";
//		   
//		
//		   TicksXLSDatabase db = new TicksXLSDatabase(stockName, "Day");
//		db.updateDatabase();
//		TimeSeries timeSeries;
//		try {
//			timeSeries = db.loadTicks(1, InputDataType.YEAR);
//			System.out.println("row count:"+timeSeries.getTickCount());
//			System.out.println("Last Update Date: "+timeSeries.getTick(timeSeries.getTickCount()-1).getDateName());
//		} catch (Exception e) {
//			
//			//e.printStackTrace();
//		}
//		UpdateAllCompanyInfo.update();
		ReadTopGainersFromReligare read = new ReadTopGainersFromReligare();
		
		//System.out.println(read.read());
		read.readNUpdateXLSSheet();
		
//		UpdateAllCompanyInfo update = new UpdateAllCompanyInfo();
//		List<String[]> outputfile = update.read();
//		
//		System.out.println(outputfile.toString());

	}

}
