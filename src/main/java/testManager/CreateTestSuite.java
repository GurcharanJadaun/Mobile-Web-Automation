package testManager;

import java.io.IOException;
import java.util.List;

public interface CreateTestSuite {
	/**
     * Reads the excel "fileName" present under resources/featureFiles .
     * @param "fileName" where TestCases are written will be used as suite name
     */
	public TestSuite loadTestCasesFromFile(String fileName) throws IOException; 
	
	/**
	 * creates and returns a list of test cases from the file read
	 */
	public List<TestCase> createListOfTestCases();
	
}
