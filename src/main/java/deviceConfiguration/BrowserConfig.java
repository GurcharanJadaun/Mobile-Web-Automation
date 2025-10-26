package deviceConfiguration;

import org.json.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;

public class BrowserConfig {
	
	String browserName;
	int numberOfTests, browserNumber;
	boolean runTestCasesInParallel, headless, retryFailedTestCases;
	JSONObject testUrlDetails;
	
	BrowserConfig(JsonNode browserConfig,int browserNumber){
		this.browserName = browserConfig.get("BrowserName").asText();
		
		if (browserConfig.hasNonNull("NumberOfTests")) {
			this.numberOfTests = browserConfig.get("NumberOfTests").asInt();
		}else {
			this.numberOfTests = 1;
		}
		
		if (browserConfig.hasNonNull("ParallelTestExecution")) {
			this.runTestCasesInParallel = browserConfig.get("ParallelTestExecution").asBoolean();
		}else {
			this.runTestCasesInParallel = false;
		}
		
		if (browserConfig.hasNonNull("HeadlessBrowser")) {
			this.headless = browserConfig.get("HeadlessBrowser").asBoolean();
		}else {
			this.headless = true;
		}
		
		if (browserConfig.hasNonNull("RetryFailedTestCases")) {
			this.retryFailedTestCases = browserConfig.get("RetryFailedTestCases").asBoolean();
		}else {
			this.retryFailedTestCases = true;
		}
		
			testUrlDetails = new JSONObject();
			
		
		this.browserNumber = browserNumber;
	}
	
	/**
	 * returns Browser Name on which test would run.
	 */
	public String getBrowserName() {
		return this.browserName;
	}
	
	/**
	 * returns number of tests to be run in parallel.
	 */
	public int getCountOfNumberOfTests() {
		return this.numberOfTests;
	}
	
	/**
	 * if "true" then tests will run in parallel on the assigned browser.
	 */
	public boolean runTestsInParallel() {
		return this.runTestCasesInParallel;
	}
	
	/**
	 * if "true" then Browser will run tests in headless mode.
	 */
	public boolean headlessBrowser() {
		return this.headless;
	}
	
	/**
	 * returns the browser index of target browser.
	 */
	public int getBrowserSerialNumber() {
		return this.browserNumber;
	}
	
	public void setTestUrlDetails(JSONObject testUrlDetails) {
		this.testUrlDetails = testUrlDetails;
	}
	/**
	 * returns URL details for the target browser.
	 */
	public JSONObject getTestUrlDetails() {
		return this.testUrlDetails;
	}
	
	public boolean retryFailedTestCase() {
		return this.retryFailedTestCases;
	}

}
