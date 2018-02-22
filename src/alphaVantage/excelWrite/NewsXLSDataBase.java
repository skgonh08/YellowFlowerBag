package alphaVantage.excelWrite;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import alphaVantage.input.ReadDataFromWeb.UpdateAllCompanyInfo;
import ta4j.ProjectConstants;

public class NewsXLSDataBase implements XLSDatabase {
	private String fileName = "";
	private String sheetName = "";
	private String className = "NewsXLSDataBase~";
	private String fullqualifiedFileName = null;

	public NewsXLSDataBase(String wbName, String sheetName) {
		this.sheetName = sheetName;
		this.fileName = wbName;
		this.fullqualifiedFileName = ProjectConstants.outputXLSFileLoc + fileName + ".xls";
	}

	public void updateDatabase(ArrayList<String> StocksSymbol, String dataDate) {
		String methodName = "updateDatabase~";
		HSSFWorkbook workbook = null;
		int maxColn = 5;
		int maxRow = 10;
		int colNumtoInsertData = 0;
		boolean colToInsertFound = false;
		boolean firstColDeleteNeeded = false;
		try {
			isFileExistOrMakeIt(fullqualifiedFileName, sheetName);
			FileInputStream file = new FileInputStream(fullqualifiedFileName);
			workbook = new HSSFWorkbook(file);
			HSSFSheet sheet = workbook.getSheet(sheetName);

			int lastRowNum = sheet.getLastRowNum();
			// If last Row is 0 that means no data exist... Crating sheet for
			// the first time
			if (lastRowNum == 0) {
				colNumtoInsertData = 0;
				colToInsertFound = true;
			} else {
				HSSFRow row = (HSSFRow) sheet.getRow(0);
				for (int i = 0; i < maxColn; i++) {
					if (row.getCell(i) == null) {
						colNumtoInsertData = i;
						colToInsertFound = true;
						break;
					}
				}
				// All five colns are having data...
				if (!colToInsertFound) {
					colNumtoInsertData = maxColn - 1;
					colToInsertFound = true;
					firstColDeleteNeeded = true;
				}
			}
			// All five cols exist .. Deleting First coln
			if (firstColDeleteNeeded) {
				for (int i = 0; i <= maxRow; i++) {
					HSSFRow row = (HSSFRow) sheet.getRow(i);
					for (int j = 0; j < maxColn - 1; j++) {
						row.getCell(j).setCellValue(row.getCell(j + 1).getStringCellValue());
					}
				}

			}

			// Inserting new data
			if (colToInsertFound) {
				HSSFRow row = null;
				for (int i = 0; i <= maxRow; i++) {

					if (colNumtoInsertData == 0) {
						row = (HSSFRow) sheet.createRow(i);
					} else {
						row = (HSSFRow) sheet.getRow(i);
					}
					if (i == 0) {
						row.createCell(colNumtoInsertData).setCellValue(dataDate);
					} else {
						String value = StocksSymbol.get(i-1);
						//System.out.println("value"+value);
						row.createCell(colNumtoInsertData).setCellValue(value);
					}

				}
			}

		} catch (IOException e) {
			System.out.println(className + methodName + "Error while calling isFileExistOrMakeIt");
			e.printStackTrace();
		}
		FileOutputStream fileOut;
		try {
			fileOut = new FileOutputStream(fullqualifiedFileName);
			workbook.write(fileOut);
			fileOut.flush();
			fileOut.close();
			workbook.close();
			System.out.println(className + methodName + "Excel sheet successfully updated from Web");

		} catch (FileNotFoundException e) {
			System.out.println(className + methodName + "Error while Closing work book");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println(className + methodName + "Error while Closing work book");
			e.printStackTrace();
		}

		// write this workbook to an Outputstream.

	}

}
