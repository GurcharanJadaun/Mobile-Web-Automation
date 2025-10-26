package dataExtractor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.json.JSONArray;
import org.json.JSONObject;

import FileManager.XlsxFileManager;
import TestCaseCompiler.TestCaseCompiler;
import testManager.CreateTestSuite;
import testManager.TestCase;
import testManager.TestStep;
import testManager.TestSuite;

public class XlsxDataExtractor extends TestCaseCompiler implements CreateTestSuite {

	protected List<TestSuite> listOfTestSuites;
	private Sheet sheet;
	String pathSep, dir;

	protected XlsxDataExtractor() {
		pathSep = File.separator.toString();
		listOfTestSuites = new ArrayList<TestSuite>();
		dir = "src" + pathSep + "main" + pathSep + "resources" + pathSep + "FeatureFiles";
	}
	
	public void generateTestSuite() {

		XlsxFileManager fileManager = new XlsxFileManager();
		List<String> fileNames = fileManager.getExcelFileNamesFrom(dir);

		for (String fileName : fileNames) {
			this.loopOverTestSuites(fileName);
		}

	}
	
	public void generateSuiteFromTestPlan() {
		XlsxFileManager fileManager = new XlsxFileManager();
		
		sheet = fileManager.getFirstExcelSheet("TestPlan.xlsx");
		
		HashMap<String, String> tmp = fileManager.createDataDictionary(sheet, 3, 4, 1);
			
		for(Map.Entry<String, String> item : tmp.entrySet()) {
			if(item.getValue().equalsIgnoreCase("y")) {
				this.loopOverTestSuites(item.getKey());
			}
		}
	}
	
	public void generateSuiteFromTestPlan(String tags) {
		XlsxFileManager fileManager = new XlsxFileManager();
		
		sheet = fileManager.getFirstExcelSheet("TestPlan.xlsx");
		
		JSONArray testPlan = fileManager.excelSheetToJsonArray(sheet);
		
		for(int i=0;i<testPlan.length();i++) {
			JSONObject planRow = testPlan.getJSONObject(i);
			boolean flag = false;
			String testSuiteName = planRow.get("Test Suite Name").toString();
			
			String[] testPlanTags = planRow.get("Tags").toString().split(",");
			String[] executionTags = tags.split(",");
			
			for(String planTag: testPlanTags) {
				for(String executionTag : executionTags) {
					if(planTag.trim().equalsIgnoreCase(executionTag.trim())) {
						flag = true;
					}
				}
			}
		if(flag) {
				this.loopOverTestSuites(testSuiteName);}
	
		}
	}

	 void loopOverTestSuites(String suiteName) {
		try {
			TestSuite suite = loadTestCasesFromFile(suiteName);
			listOfTestSuites.add(suite);
		} catch (IOException e) {
			// Add logs here for File reading failure
			e.printStackTrace();
		}
	}
	
	@Override
	public TestSuite loadTestCasesFromFile(String fileName) throws IOException {
		TestSuite suite = new TestSuite();
		XlsxFileManager fileManager = new XlsxFileManager();
		suite.setSuitName(fileName);

		sheet = fileManager.getFirstExcelSheet(this.dir, fileName);
		JSONArray testCasesJson = fileManager.excelSheetToJsonArray(sheet);
		List<TestCase> listOfTests = createListOfTestCases(testCasesJson);

		this.compileTestCases(listOfTests);
		suite.addTestCases(listOfTests);

		return suite;
	}

	@Override
	public List<TestCase> createListOfTestCases() {
		String tmp = "";
		List<TestCase> listOfTestCases = new ArrayList<TestCase>();
		int stepNumber = 0, skipRow = 1, rowCount = 0;
		TestCase tc = null;
		
		
		
		for (Row row : sheet) {
			if (skipRow > rowCount) {
				rowCount++;
				continue;
			}
			TestStep ts = new TestStep();

			Cell testCaseIdCell = row.getCell(0);
			Cell actionCell = row.getCell(1);
			Cell locatorCell = row.getCell(2);
			Cell testDataCell = row.getCell(3);

			if (testCaseIdCell != null && testCaseIdCell.getStringCellValue().length() > 0) {

				if (tc != null) {
					listOfTestCases.add(tc);
				}

				tmp = testCaseIdCell.getStringCellValue();
				stepNumber = 1;
				tc = new TestCase();
				tc.insertTestCaseId(tmp);
			}
			if (actionCell != null && actionCell.getStringCellValue().length() > 0) {
				String action = actionCell.getStringCellValue();
				ts.insertAction(action);
			} else {
				continue;
			}
			Optional<String> locator = Optional.ofNullable(locatorCell).map(Cell::toString)
					.filter(s -> !s.trim().isEmpty());
			;
			Optional<String> testData = Optional.ofNullable(testDataCell).map(Cell::toString)
					.filter(s -> !s.trim().isEmpty());

			ts.insertLocator(locator);
			ts.insertTestData(testData);
			ts.setStepNumber(stepNumber);

			tc.addSteps(ts);
			stepNumber++;

		}
		listOfTestCases.add(tc);

		return listOfTestCases;

	}
	
	public List<TestCase> createListOfTestCases(JSONArray listOfSteps) {
		List<TestCase> listOfTestCases = new ArrayList<TestCase>();
		TestCase tc = null;
		int stepNumber = 1;

		for (int i = 0; i < listOfSteps.length(); i++) {
			TestStep ts = new TestStep();

			String testCaseId = listOfSteps.getJSONObject(i).get("Test Case Id").toString();
			String tags = listOfSteps.getJSONObject(i).get("Tags").toString();
			String action = listOfSteps.getJSONObject(i).get("Action").toString();
			String locator = listOfSteps.getJSONObject(i).get("Locator").toString();
			String testData = listOfSteps.getJSONObject(i).get("Test Data").toString();

			if (testCaseId.length() > 0) {
				if (tc != null) {
					listOfTestCases.add(tc);
				}

				stepNumber = 1;
				tc = new TestCase();
				tc.insertTestCaseId(testCaseId);
				if (tags.length() != 0) {
					String[] arrOfTags = tags.split(",");
						for (String tag : arrOfTags) {
							tc.addTag(tag.trim());
					}
				}

			}

			if (action.length() > 0) {

				ts.insertAction(action);
				ts.insertLocator(Optional.ofNullable(locator));
				ts.insertTestData(Optional.ofNullable(testData));
				ts.setStepNumber(stepNumber);

				tc.addSteps(ts);
				stepNumber++;
			}
		}

		listOfTestCases.add(tc);
		
		

		return listOfTestCases;

	}

	@Override
	public void loadLocatorMap(String dir) {
		XlsxFileManager fileManager = new XlsxFileManager();
		this.locators = new HashMap<String, String>();

		List<Sheet> listOfSheets = fileManager.getFirstExcelSheetFromAllFiles(dir);
		Iterator<Sheet> it = listOfSheets.iterator();
		while (it.hasNext()) {
			Sheet sheet = it.next();
			HashMap<String, String> tmp = new HashMap<String, String>();

			JSONArray items = fileManager.excelSheetToJsonArray(sheet);
			
			for (int i = 0; i < items.length(); i++) {
				JSONObject row = items.getJSONObject(i);
				String locatorName = row.get("Locator Name").toString();
				String locatorValue = row.get("Locator Value").toString();

				tmp.put(locatorName, locatorValue);
			}

			this.locators.putAll(tmp);
		}

		System.out.println("Extracted total number of Locators : " + this.locators.size());
	}

	@Override
	public void loadFunctionNames(String dir) {
		XlsxFileManager fileManager = new XlsxFileManager();
		this.functionDetails = new HashMap<String, String>();

		List<Sheet> listOfSheets = fileManager.getFirstExcelSheetFromAllFiles(dir);
		Iterator<Sheet> it = listOfSheets.iterator();
		while (it.hasNext()) {
			Sheet sheet = it.next();
			HashMap<String, String> tmp = new HashMap<String, String>();

			JSONArray items = fileManager.excelSheetToJsonArray(sheet);

			for (int i = 0; i < items.length(); i++) {
				JSONObject row = items.getJSONObject(i);
				String functionName = row.get("Function Name").toString();
				String functionDescription = row.get("Function Description").toString();

				tmp.put(functionName, functionDescription);
			}
			functionDetails.putAll(tmp);
		}
		System.out.println("Extracted total number of Functions : " + this.functionDetails.size());

	}

}
