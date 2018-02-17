package alphaVantage.excelWrite;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.joda.time.DateTime;

import alphaVantage.AlphaVantageConnector;
import alphaVantage.TimeSeriesInput;
import alphaVantage.input.timeseries.OutputSize;
import alphaVantage.output.timeseries.Daily;
import alphaVantage.output.timeseries.data.StockData;
import ta4j.ProjectConstants;
import ta4j.Tick;
import ta4j.TimeSeries;

public class XLSDatabase {

	private String timeInterval;
	private String folder = ProjectConstants.outputXLSFileLoc;
	private String fileName = "";
	private String stockName = "";
	private String fileExtension = ".xls";
	private DateTimeFormatter dtformatter = DateTimeFormatter.ofPattern(ProjectConstants.dateFormat);
	private HSSFWorkbook wb;
	private DateFormat dateFormat = new SimpleDateFormat(ProjectConstants.dateFormat);
	private HSSFWorkbook wb2;

	// Constructors
	public XLSDatabase(String scriptName, String timeInterval) {
		this.stockName = scriptName;
		this.fileName = folder + scriptName.toUpperCase() + fileExtension;
		this.timeInterval = timeInterval.replace(" ", "");
	}

	private void isFileExistOrMakeIt() throws IOException {

		// timeInterval = timeInterval.trim();
		Path path = Paths.get(fileName);
		HSSFWorkbook workbook;
		HSSFSheet sheet;
		if (Files.exists(path)) {
			FileInputStream is = new FileInputStream(fileName);
			workbook = new HSSFWorkbook(is);
			sheet = workbook.getSheet(timeInterval);

			if (sheet == null) {
				workbook.createSheet(timeInterval);
			}
			try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
				workbook.write(fileOut);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		if (Files.notExists(path)) {

			workbook = new HSSFWorkbook();
			sheet = workbook.createSheet(timeInterval);

			try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
				workbook.write(fileOut);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	private void updateFile(List<StockData> stockData) throws IOException {

		char dateDifferenceCalcReq = 'Y';
		long dateDifference = 0;
		String lastUpdateDate = null;
		int rowNumber = 0;

		InputStream ExcelFileToRead = new FileInputStream(fileName);
		wb = new HSSFWorkbook(ExcelFileToRead);
		HSSFSheet sheet = wb.getSheet(timeInterval);

		int lastRowNum = sheet.getLastRowNum();

		if (lastRowNum == 0) {
		    
			if (ProjectConstants.headerInExcelFiles){
				dateDifferenceCalcReq = 'N';
				rowNumber = 1;
				// Setting header...

				HSSFRow rowFirst = sheet.createRow(0);
				rowFirst.createCell(0).setCellValue("Date");
				rowFirst.createCell(1).setCellValue("Open");
				rowFirst.createCell(2).setCellValue("High");
				rowFirst.createCell(3).setCellValue("Low");
				rowFirst.createCell(4).setCellValue("Close");
				rowFirst.createCell(5).setCellValue("Volume");
			}
			else {
				dateDifferenceCalcReq = 'N';
				rowNumber = 0;
			}
			
		} else {
			dateDifferenceCalcReq = 'Y';
			rowNumber = lastRowNum + 1;
			lastUpdateDate = sheet.getRow(lastRowNum).getCell(0).toString();
		}

		for (StockData stock : stockData) {

			if (dateDifferenceCalcReq == 'Y') {
				dateDifference = getDateDiff(lastUpdateDate, stock.getDateTime().format(dtformatter));
			}

			if ((dateDifference > 0 || dateDifferenceCalcReq == 'N') && stock.getVolume() != 0) {

				dateDifferenceCalcReq = 'N';
				HSSFRow row = sheet.createRow(rowNumber);
				row.createCell(0).setCellValue(stock.getDateTime().format(dtformatter));
				row.createCell(1).setCellValue(stock.getOpen());
				row.createCell(2).setCellValue(stock.getHigh());
				row.createCell(3).setCellValue(stock.getLow());
				row.createCell(4).setCellValue(stock.getClose());
				row.createCell(5).setCellValue(stock.getVolume());

				rowNumber++;
			}
		}
		FileOutputStream fileOut = new FileOutputStream(fileName);

		// write this workbook to an Outputstream.
		wb.write(fileOut);
		fileOut.flush();
		fileOut.close();
		wb.close();

	}

	private String getLastDate() throws IOException {

		InputStream ExcelFileToRead = new FileInputStream(fileName);
		wb2 = new HSSFWorkbook(ExcelFileToRead);

		HSSFSheet sheet = wb2.getSheet(timeInterval);
		HSSFRow lastRow;
		HSSFCell cellDate;
		int lastRowNum = sheet.getLastRowNum();

		if (lastRowNum != 0) {
			lastRow = sheet.getRow(lastRowNum);
			cellDate = lastRow.getCell(0);
			return cellDate.toString();
		} else {
			return null;
		}

	}

	private long getDateDiff(String date1, String date2) {

		long diffDays = 0;
		try {
			Date date1Fomatted = dateFormat.parse(date1);
			Date date2Fomatted = dateFormat.parse(date2);
			long diffTime = date2Fomatted.getTime() - date1Fomatted.getTime();
			diffDays = diffTime / (24 * 60 * 60 * 1000);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return diffDays;
	}

	public void updateDatabase() {
		String methodName =  "updateDatabase()~";
		OutputSize outputSize = null;
		String apiCallRequired = "Yes";
		
		AlphaVantageConnector apiConnector = new AlphaVantageConnector(ProjectConstants.AlphaVnatageAPIKey, ProjectConstants.APICallTimeOut);

		try {
			isFileExistOrMakeIt();
			String lastUpdateDate = getLastDate();

			if (lastUpdateDate != null && !"".equals(lastUpdateDate)) {
				Date currDate = new Date();
				Date lastUpdateDateFomatted = dateFormat.parse(lastUpdateDate);
				long diffTime = currDate.getTime() - lastUpdateDateFomatted.getTime();
				long diffDays = diffTime / (24 * 60 * 60 * 1000);

				if (diffDays == 0) {
					apiCallRequired = "No";
				} else if (diffDays > 100) {
					outputSize = OutputSize.FULL;
				} else {
					outputSize = OutputSize.COMPACT;
				}
			} else {
				outputSize = OutputSize.FULL;
			}

		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		if (apiCallRequired == "Yes") {
			TimeSeriesInput timeSeries = new TimeSeriesInput(apiConnector);
			Daily resp = timeSeries.daily(stockName, outputSize);
			/*
			 * Map<String, String> metaData = resp.getMetaData();
			 * System.out.println("1. Information : " +
			 * metaData.get("1. Information") + "\n" + "2. Symbol : " +
			 * metaData.get("2. Symbol") + "\n" + "3. Last Refreshed : " +
			 * metaData.get("3. Last Refreshed") + "\n" + "4. Output Size : " +
			 * metaData.get("4. Output Size") + "\n" + "5. Time Zone : " +
			 * metaData.get("5. Time Zone"));
			 */

			List<StockData> stockData = resp.getStockData();
			Collections.reverse(stockData);

			// Writing to DB...
			try {
				updateFile(stockData);
				System.out.println(methodName+"Database successfully updated (Stock: " + stockName + " )");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println(methodName+"Database already updated with latest data. No API call required.");
		}
	}

	public TimeSeries loadTick() throws Exception {
		String methodName =  "loadTick()~";
		HSSFRow row;
		FileInputStream is;
		HSSFWorkbook workbook = null;
		try {
			is = new FileInputStream(fileName);
			workbook = new HSSFWorkbook(is);
			HSSFSheet sheet = workbook.getSheet(timeInterval);
			
			int lastRowNum = sheet.getLastRowNum();
			
			if (lastRowNum ==0){
				System.out.println(methodName+"File contains not data ~ Please execute updateDatabase method first ");
				throw new Exception();
			}

			List<Tick> ticks = new ArrayList<Tick>();
			Iterator<Row> rows = sheet.rowIterator();
			
			if (ProjectConstants.headerInExcelFiles) {
				row = (HSSFRow) rows.next();
			}
				
			while (rows.hasNext()) {
				row = (HSSFRow) rows.next();
				
				DateTime date = new DateTime(dateFormat.parse(row.getCell(0).getStringCellValue()));
				double open = row.getCell(1).getNumericCellValue();
				double high = row.getCell(2).getNumericCellValue();
				double low = row.getCell(3).getNumericCellValue();
				double close = row.getCell(4).getNumericCellValue();
				double volume = row.getCell(5).getNumericCellValue();

				ticks.add(new Tick(date, open, high, low, close, volume));
				
			}
			return new TimeSeries(fileName, ticks);

		} catch (FileNotFoundException e) {
			System.out.println(methodName + "File not found~ Please execute updateDatabase method first ");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			System.out.println(methodName + "Date format mismatch");
			e.printStackTrace();
		}  finally {
			try {
				workbook.close();
			} catch (IOException e) {
				System.out.println(methodName + "Error occured while closing workbook");
			}
		}

		return null;

	}

}
