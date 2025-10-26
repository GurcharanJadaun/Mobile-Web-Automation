package FileManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;

import testManager.TestSuite;

public class XlsxFileManager {
	
	private Workbook excel;
	private FileInputStream fis;
	String pathSep,dir;
	public XlsxFileManager() {
		pathSep = File.separator.toString();
		this.dir = System.getProperty("user.dir");
	}
	
	/**
	 * returns first Excel <Sheet> from the Excel file. Needs file extension as well to read file correctly.
	 * @param "fileName"
	 */
	public Sheet getFirstExcelSheet(String fileName) {
		Sheet sheet;
		
	    try {
			fis = new FileInputStream(dir + pathSep + fileName);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			excel = new XSSFWorkbook(fis);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		sheet = excel.getSheetAt(0);
		
		return sheet;
	}
	
	/**
	 * returns first Excel <Sheet> from the Excel file. Needs file extension as well to read file correctly.
	 * @param "folderName"
	 * @param "fileName"
	 */
	public Sheet getFirstExcelSheet(String folderName, String fileName) {
		Sheet sheet;
		
   	    sheet =  this.getFirstExcelSheet(folderName + pathSep + fileName);
		
		return sheet;
	}
	
	/**
	 * returns List<String> from the Directory provided. The list stores the names of .xlsx files present in directory.
	 * @param "folderName"
	 */
	public List<String> getExcelFileNamesFrom(String folderName) {
		File folder = new File(dir + pathSep + folderName);
		File[] listOfFiles = folder.listFiles();
		List<String> fileNames = new ArrayList<String>();
		for(File file : listOfFiles) {
			String fileName =  file.getName();
			if(fileName.endsWith(".xlsx")) {
				fileNames.add(fileName);
				}
			}
		
		return fileNames;
	}
	
	/**
	 * returns List<Sheet>. List of Excel <Sheet> from all the Excel files present under the directory.
	 * @param "folderName"
	 */
	public List<Sheet> getFirstExcelSheetFromAllFiles(String folderName) {
		List<Sheet> listOfSheet= new ArrayList<Sheet>();
		
		File folder = new File(dir + pathSep + folderName);
		File[] listOfFiles = folder.listFiles();
		
		for(File file : listOfFiles) {
			String fileName =  file.getName();
			if(fileName.endsWith(".xlsx")) {
			try {
				fis = new FileInputStream(dir + pathSep + folderName + pathSep + fileName);
				try {
					
					excel = new XSSFWorkbook(fis);
					Sheet sheet = excel.getSheetAt(0);
					listOfSheet.add(sheet);
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("Directory looked into : "+dir + pathSep + folderName);
				}
				
				
				
			} catch (IOException e) {
				// Add logs here for File reading failure
				e.printStackTrace();
			}
		 }else {
			 // add logs for invalid fileType
			 System.out.println("Detected file with invalid Type : " + fileName);
		 }
		}
			
		return listOfSheet;
	}
	
	/**
	 * returns List<Sheet>. All Excel Sheets from the Excel file present under the directory.
	 * @param "folderName"
	 * @param "fileName"
	 */
	public List<Sheet> getAllExcelSheets(String folderName, String fileName) {
		List<Sheet> listOfSheets = new ArrayList<Sheet>();
		
	    try {
			fis = new FileInputStream(dir + pathSep + folderName + pathSep + fileName);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			excel = new XSSFWorkbook(fis);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int numberOfSheets = excel.getNumberOfSheets();
	
		while(numberOfSheets > 0) {
			numberOfSheets--;
			Sheet sheet = excel.getSheetAt(numberOfSheets);	
			listOfSheets.add(sheet);
		}
		
		return listOfSheets;
	}
	
	/**
	 * returns HashMap<String,String>. Reads @param "sheet" to create hash map having keyCellIndex from Sheet as keys and valueCellIndex as values. .
	 * @param "sheet"
	 * @param "keyCellIndex"
	 * @param "valueCellIndex"
	 * @param "skipRows"
	 */
	public HashMap<String,String> createDataDictionary(Sheet sheet,int keyCellIndex, int valueCellIndex, int skipRows){
		HashMap<String,String> dictionary= new HashMap<String,String>();
		int rowCount = 0;
		
		for(Row row : sheet) {
			if(skipRows > rowCount) {
				rowCount++;
				continue;
			}
			Cell keyCell = row.getCell(keyCellIndex-1);
			Cell valueCell = row.getCell(valueCellIndex-1);
			if(keyCell!=null && valueCell!=null) {
				dictionary.put(keyCell.getStringCellValue(), valueCell.getStringCellValue());
			}
		}
		return dictionary;
		
	}
	
	public JSONArray excelSheetToJsonArray(Sheet sheet) {
		JSONArray jsonArr =  new JSONArray();
		// Get the first row (the headers)
        Row headerRow = sheet.getRow(0);
        
        // Create an array to store the column headers
        Iterator<Cell> headerIterator = headerRow.cellIterator();
        String[] headers = new String[headerRow.getPhysicalNumberOfCells()];
        int i = 0;
        
        while (headerIterator.hasNext()) {
            headers[i++] = headerIterator.next().getStringCellValue();
        }
        
        for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
            Row row = sheet.getRow(rowNum);
            JSONObject rowObject = new JSONObject();
            if(row!=null) {
            for (int colNum = 0; colNum < headers.length; colNum++) {
                Cell cell = row.getCell(colNum);
                
                String cellValue = this.getCellValueAsString(cell);
                	rowObject.put(headers[colNum], cellValue);
               }
		
            jsonArr.put(rowObject);
            }
	}
        return jsonArr;

}
	private String getCellValueAsString(Cell cell) {
	    if (cell == null) return "";

	    switch (cell.getCellType()) {
	        case STRING:
	            return cell.getStringCellValue().trim();
	        case NUMERIC:
	            // Handle numeric as String (optionally format it)
	            return String.valueOf(cell.getNumericCellValue());
	        case BOOLEAN:
	            return String.valueOf(cell.getBooleanCellValue());
	        case FORMULA:
	            // Optionally evaluate or get as string
	            return cell.getCellFormula();
	        case BLANK:
	            return "";
	        default:
	            return "";
	    }
	}
}
