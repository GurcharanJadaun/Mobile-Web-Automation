package runner;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import TestReports.ReportTestEventManager;
import TestReports.ReportTestListener;
import deviceConfiguration.BrowserConfig;
import deviceConfiguration.DeviceManager;
import loader.TestSuiteLoader;
import testEnvironmentConfig.TestEnvironmentConfig;
import testManager.TestCase;
import testManager.TestStatus;
import testManager.TestStep;
import testManager.TestSuite;
import utilities.ExecuteStep;

public class RunTests {
	ReportTestEventManager[] report;
	
	public static void main(String args[]) {
		Instant start = Instant.now();
		TestSuiteLoader loadTests= new TestSuiteLoader();
		String urlConfig = System.getProperty("testEnv","Product");
		String deviceConfig = System.getProperty("deviceConfig","TestRunner");
		String testCaseTags = System.getProperty("testCaseTags","@Regression");
		String testPlanTags = System.getProperty("testPlanTags","@Debug");
		
		
		loadTests.setupTest(testPlanTags);
		
		TestEnvironmentConfig testEnvConfig= new TestEnvironmentConfig("TestURLConfig");
		JSONObject urlDetails= testEnvConfig.getTestEnvConfigFromJson(urlConfig);
		DeviceManager device = new DeviceManager("DeviceConfig");
		device.setupBrowserForDevice(deviceConfig,urlDetails);
				
		RunTests runner = new RunTests();
		List<TestSuite> listOfTestSuites= loadTests.getListOfTestSuite(testCaseTags);
		
		if(device.runTestsOnBrowsersInParallel()) {
		runner.testBrowsersInParallel(device, listOfTestSuites);
			}
		else {
		runner.testBrowsersSequentially(device, listOfTestSuites);	
		}
			
		Instant end = Instant.now();
		Duration timeElapsed = Duration.between(start, end);

		long seconds = timeElapsed.toSeconds();
		double minutes = timeElapsed.toMinutes();
		System.out.println("Time taken: " + seconds + " seconds (" + minutes + " minutes)");
	
		System.exit(0);
	}
	
	public void testBrowsersSequentially(DeviceManager device, List<TestSuite> testSuites) {
		report = new ReportTestEventManager[device.getBrowserList().size()];
		
		device.getBrowserList().forEach(browser -> {
			report[browser.getBrowserSerialNumber()] = new ReportTestEventManager();
			report[browser.getBrowserSerialNumber()].addTestListener(new ReportTestListener()); // creates fresh report for each browser
			this.testSuiteSequential(testSuites, browser);
			Optional<String> suffix = Optional.ofNullable(browser.getBrowserName()+" ");
			report[browser.getBrowserSerialNumber()].fireFinishTest(suffix);
		});
	

	}
	
	public void testBrowsersInParallel(DeviceManager device, List<TestSuite> testSuites) {
		// do this later
		int numberOfThreads = device.getBrowserList().size();
		ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
		report = new ReportTestEventManager[device.getBrowserList().size()];
		
		for (BrowserConfig browser : device.getBrowserList()) {
			report[browser.getBrowserSerialNumber()] = new ReportTestEventManager();
			report[browser.getBrowserSerialNumber()].addTestListener(new ReportTestListener()); // creates fresh report for each browser
			executor.submit(()-> testSuiteSequential(testSuites, browser));
		}
		
		executor.shutdown();
		try {
		    if (!executor.awaitTermination(60, TimeUnit.MINUTES)) {
		        executor.shutdownNow();
		    }
		} catch (InterruptedException e) {
		    executor.shutdownNow();
		    e.printStackTrace();
		    Thread.currentThread().interrupt();
		}finally{

			for (BrowserConfig browser : device.getBrowserList()) {
				Optional<String> suffix = Optional.ofNullable(browser.getBrowserName()+" ");
				report[browser.getBrowserSerialNumber()].fireFinishTest(suffix);
			}
		}
		
	}
	
	public void testSuiteSequential(List<TestSuite> testSuites, BrowserConfig browser) {
		for(TestSuite testSuite : testSuites) {
	
		TestSuite suite = (new TestSuite(testSuite));
		report[browser.getBrowserSerialNumber()].fireCreateTestSuite(suite);
		
		if(browser.runTestsInParallel()) {
		this.testCaseInParallel(suite,browser);}
		else {
		this.runTestSuite(suite, browser);	
		}
	}
	}
	
 	private void testCaseInParallel(TestSuite testSuite, BrowserConfig browserDetails) {
		int numberOfThreads = browserDetails.getCountOfNumberOfTests();
		ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
		
		for (TestCase testCase : testSuite.getTestCases()) {
			
			List<TestCase> combineTestCase = new ArrayList<TestCase>();
			
			combineTestCase.add(testCase);		
				
			final TestSuite minisuite = new TestSuite(); 
			minisuite.addTestCases(combineTestCase);
			minisuite.setSuitName(testSuite.getSuiteName());
			minisuite.createTestSuiteNode(testSuite.getTestSuiteNode());
			
			// create parallel runner here.
			executor.submit(() -> runTestSuite(minisuite, browserDetails));
	
		}

		executor.shutdown();
		try {
		    if (!executor.awaitTermination(30, TimeUnit.MINUTES)) {
		        executor.shutdownNow();
		    }
		} catch (InterruptedException e) {
		    executor.shutdownNow();
		    e.printStackTrace();
		    Thread.currentThread().interrupt();
		}

	}
	
	private void runTestSuite(TestSuite testSuite, BrowserConfig browserDetails) {
				
			for (TestCase testCase : testSuite.getTestCases()) {
			
				BrowserConfig deviceConfig = browserDetails;
				ExecuteStep ex = new ExecuteStep(deviceConfig);
				report[browserDetails.getBrowserSerialNumber()].fireAddTestCaseEvent(testCase, testSuite);
				
				this.runTestCase(testCase, ex);
					
				if(testCase.getTestCaseResult().isFailed() && browserDetails.retryFailedTestCase()) {
					System.out.println("<< Retrying failed test case >>");
					this.cleanUp(ex);
					TestCase retryTestCase = new TestCase(testCase); 
					String testName = testCase.getTestCaseId();
					
					retryTestCase.insertTestCaseId("Retry > "+testName);
					report[browserDetails.getBrowserSerialNumber()].fireAddTestCaseEvent(retryTestCase, testSuite);
					
					ex = new ExecuteStep(deviceConfig);
					this.runTestCase(retryTestCase, ex);
					
					report[browserDetails.getBrowserSerialNumber()].fireRemoveTestCase(testSuite, testCase);
				}
			
				this.cleanUp(ex);
				}
	}
	
	private void cleanUp(ExecuteStep ex) {
		ex.executeStep("closeSession");
	}


	private void runTestCase(TestCase testCase, ExecuteStep ex) {
		Instant start = Instant.now();

		Iterator<TestStep> it = testCase.getSteps().iterator();
		while (it.hasNext()) {
			TestStep ts = it.next();
			report[ex.getBrowserConfig().getBrowserSerialNumber()].fireAddTestStepEvent(ts, testCase);
			if (testCase.getTestCaseResult().isFailed()) {
				ts.setFailureReason(">> Skipped because of error in " + testCase.getTestCaseId() + " <<");
				this.skipStep(ts, ex.getBrowserConfig());
			} else {
				runTestStep(ts, ex);

				if (ts.getResult().isFailed()) {
					testCase.setTestCaseResult(ts.getResult().setStatusTo());
				}
			}
			ex.flush();
		}
		if (testCase.getTestCaseResult() == TestStatus.PENDING) {
			testCase.setTestCaseResult(TestStatus.PASSED);
		}
		Instant end = Instant.now();
		Duration timeElapsed = Duration.between(start, end);

		System.out.println("Executing : " +ex.getBrowserConfig().getBrowserName()+"\t"+ testCase.getTestCaseId() + "\t" + testCase.getTestCaseResult() + "\t"
				+ timeElapsed.toSeconds() + "\t" + testCase.getTestCaseReason());
	}

	
	@SuppressWarnings("unused")
	private void skipTestCase(TestCase testCase, BrowserConfig browserConfig, String reason) {
		testCase.getSteps().forEach(step -> {
			step.setResult(TestStatus.PENDING, reason);
			this.skipStep(step, browserConfig);
		});

	}
	
	private void skipStep(TestStep testStep ,BrowserConfig browserConfig) {
		report[browserConfig.getBrowserSerialNumber()].fireSetTestStepStatus(testStep);
	}

	private void runTestStep(TestStep testStep, ExecuteStep ex) {

		String action = testStep.getAction() == null ? "" : testStep.getAction();
		String locator = testStep.getLocator() == null ? "" : testStep.getLocator();
		String testData = testStep.getTestData() == null ? "" : testStep.getTestData();

		long start = System.currentTimeMillis();
		boolean condition = true;

		do {
			if (locator.length() == 0 && testData.length() == 0) {
				ex.executeStep(action);
			} else if (locator.length() == 0 && testData.length() != 0) {
				ex.executeStep(action, testData);
			} else if (locator.length() != 0 && testData.length() == 0) {
				ex.executeStep(action, locator);
			} else if (locator.length() != 0 && testData.length() != 0) {
				ex.executeStep(action, locator, testData);
			} else {
				// Log error in logs here with step details like action, locator and testData
				ex.result = TestStatus.INVALID;
				ex.reason = "Something missed by compiler\n<<-Didn't find a proper match->>\n";
			}
			condition = (ex.result != TestStatus.INVALID) && ex.result.isFailed() && (System.currentTimeMillis() - start) < 3000;
			if (condition) {
				try {
						ex.flush();
						Thread.sleep(500);
						System.out.println("Retyring Step : "+ testStep.getStepDescription());
						
				} catch (Exception exception) {

				}
			}

		} while (condition);

		testStep.setResult(ex.result, ex.reason);
		testStep.attachScreenshot(ex.screenshot);

		report[ex.getBrowserConfig().getBrowserSerialNumber()].fireSetTestStepStatus(testStep);
	}
	
}
