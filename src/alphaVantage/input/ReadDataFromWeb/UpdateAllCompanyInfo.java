package alphaVantage.input.ReadDataFromWeb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

import ta4j.ProjectConstants;

public class UpdateAllCompanyInfo {
	
	private static final String FileNameCSV = "CompanyInformation.csv";
	private static final String FileNameXLS = "CompanyInformation.xls";
	private static final String SheetName = "Company Information";
	

	private static String className = "UpdateAllCompanyInfo~";

	public static void update () {
		String methodName = "update~";
		File file = new File(ProjectConstants.tempdir + FileNameCSV);
		URL url;
		try {
			url = new URL(ProjectConstants.NSEAllCompanyInfoSourceURL);
			FileUtils.copyURLToFile(url, file);
			System.out.println(className+methodName+"All Company Information have successfully been downloaded from NSE website");
		} catch (MalformedURLException e) {
			System.out.println(className + methodName + "MalFormed URL~ please check sourceURL ");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			CSVToExcelConverter();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	  private static  void CSVToExcelConverter () throws IOException
	    {	String methodName = "CSVToExcelConverter~";
	        String thisLine;
	        FileReader fileReaderInput = new FileReader(ProjectConstants.tempdir + FileNameCSV);
	        BufferedReader  bufferReaderInput = new BufferedReader (fileReaderInput);
	        bufferReaderInput.read();
            HSSFWorkbook workBook = new HSSFWorkbook();
            HSSFSheet sheetName = workBook.createSheet(SheetName);
            int rowNumber = 0;
            int cellNumber = 0;
	        
	        while ((thisLine = bufferReaderInput.readLine()) != null)
	        {
	        	HSSFRow row = sheetName.createRow(rowNumber);
	            String[] rowData = thisLine.split(",");
	            cellNumber =0;
	            for ( String celldata : rowData){
	            	row.createCell(cellNumber).setCellValue(celldata);
	            	cellNumber++;
	            }
	            rowNumber++;
	        }

	        FileOutputStream fileOut = new FileOutputStream(ProjectConstants.outputXLSFileLoc + FileNameXLS);
	        
	        workBook.write(fileOut);
			fileOut.flush();
			fileOut.close();
			workBook.close();
			bufferReaderInput.close();
			System.out.println(className+methodName+FileNameXLS +" has been saved successfully to output direcotry");
			
	    }
	  public Map<String, String> read(){
		  
		  HSSFWorkbook workbook = null;
		  try {
			HSSFRow row;
			FileInputStream  file = new FileInputStream(ProjectConstants.outputXLSFileLoc + FileNameXLS);
			workbook = new HSSFWorkbook(file);
			HSSFSheet sheet = workbook.getSheet(SheetName);
			Iterator<Row> rows = sheet.rowIterator();

			if (ProjectConstants.headerInExcelFiles) {
				 rows.next();
			}
			
			Map<String, String> allcompanyInfo = new HashMap<String, String>();
			while (rows.hasNext()) {
				row = (HSSFRow) rows.next();
				
				allcompanyInfo.put(row.getCell(1).getStringCellValue().toLowerCase(), row.getCell(0).getStringCellValue());
			}
			workbook.close();
			return allcompanyInfo;
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		  try {
			workbook.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		  return null;
	  }

}
