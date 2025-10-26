package loader;

import java.io.File;
import java.util.List;

import dataExtractor.XlsxDataExtractor;
import testManager.TestSuite;
import utilities.CleanupManager;

public class TestSuiteLoader extends XlsxDataExtractor {
	
	String locatorDir, keywordDir, pathSep;
	
	public TestSuiteLoader(){
		this.pathSep = File.separator.toString();
		this.locatorDir = "src"+this.pathSep+"main"+this.pathSep+"resources"+this.pathSep+"CompilerDictionary"+this.pathSep+"LocatorDictionary";
		this.keywordDir = "src"+this.pathSep+"main"+this.pathSep+"resources"+this.pathSep+"CompilerDictionary"+this.pathSep+"KeywordDictionary";
	}
	
	
	public void setupTest(String testPlanTags) {
		CleanupManager clean = new CleanupManager();
		
		clean.flush();
		
		this.loadLocatorMap(locatorDir);
		this.loadFunctionNames(keywordDir);
		this.generateSuiteFromTestPlan(testPlanTags);
		this.generateCompilationReport(listOfTestSuites);
	}
	
	public List<TestSuite> getListOfTestSuite(String tags){
		for(TestSuite suite : this.listOfTestSuites) {
			suite.extractHooks();
			suite.addHooksToTestCases();
			suite.filterByTags(tags);
			System.out.println("test cases to be executed : "+suite.getTestCases().size());
		}
		return this.listOfTestSuites;
	}
}
