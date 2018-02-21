package alphaVantage.excelWrite;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public interface XLSDatabase {
	
	public default void isFileExistOrMakeIt(String fileName, String sheetName) throws IOException {

		// timeInterval = timeInterval.trim();
		Path path = Paths.get(fileName);
		HSSFWorkbook workbook;
		HSSFSheet sheet;
		if (Files.exists(path)) {
			FileInputStream is = new FileInputStream(fileName);
			workbook = new HSSFWorkbook(is);
			sheet = workbook.getSheet(sheetName);

			if (sheet == null) {
				workbook.createSheet(sheetName);
			}
			try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
				workbook.write(fileOut);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		if (Files.notExists(path)) {

			workbook = new HSSFWorkbook();
			sheet = workbook.createSheet(sheetName);

			try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
				workbook.write(fileOut);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}
	public default void updateDatabase() {
		
	}
}
