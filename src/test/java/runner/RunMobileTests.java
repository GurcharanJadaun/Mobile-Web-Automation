package runner;

import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import org.json.JSONObject;

import TestReports.ReportTestEventManager;
import TestReports.ReportTestListener;
import deviceConfiguration.AppConfig;
import deviceConfiguration.DeviceConfig;
import deviceConfiguration.DeviceManager;
import loader.TestSuiteLoader;
import testEnvironmentConfig.TestEnvironmentConfig;
import testManager.TestCase;
import testManager.TestStatus;
import testManager.TestStep;
import testManager.TestSuite;
import utilities.ExecuteStep;

public class RunMobileTests {
	ReportTestEventManager[] report;

	public void run() {
		TestSuiteLoader loadTests = new TestSuiteLoader();
		String urlConfig = System.getProperty("testEnv", "Product");
		String deviceConfig = System.getProperty("deviceConfig", "AndroidEdgeRunner");
		String testCaseTags = System.getProperty("testCaseTags", "@Regression");
		String testPlanTags = System.getProperty("testPlanTags", "@Debug");

		loadTests.setupTest(testPlanTags);

		TestEnvironmentConfig testEnvConfig = new TestEnvironmentConfig("TestURLConfig");
		JSONObject urlDetails = testEnvConfig.getTestEnvConfigFromJson(urlConfig);
		DeviceManager device = new DeviceManager("MobileDeviceConfig");

		device.setupMobileDevices(deviceConfig, urlDetails);

		List<TestSuite> listOfTestSuites = loadTests.getListOfTestSuite(testCaseTags);

		this.testDevicesSequentially(device, listOfTestSuites);

	}

	public void testDevicesSequentially(DeviceManager deviceManger, List<TestSuite> testSuites) {
		report = new ReportTestEventManager[deviceManger.getDeviceList().size()];

		deviceManger.getDeviceList().forEach(device -> {
			report[device.getDeviceNumber()] = new ReportTestEventManager();
			report[device.getDeviceNumber()].addTestListener(new ReportTestListener()); // creates fresh report for each
																						// browser
			this.testSuiteSequential(testSuites, device);
			Optional<String> suffix = Optional.ofNullable(device.getAppList().getFirst().getAppName() + " ");
			report[device.getDeviceNumber()].fireFinishTest(suffix);
		});

	}

	public void testSuiteSequential(List<TestSuite> testSuites, DeviceConfig deviceConfig) {
		for (TestSuite testSuite : testSuites) {

			TestSuite suite = (new TestSuite(testSuite));
			report[deviceConfig.getDeviceNumber()].fireCreateTestSuite(suite);

			this.runTestSuite(suite, deviceConfig);
		}
	}

	private void runTestSuite(TestSuite testSuite, DeviceConfig deviceConfig) {

		for (TestCase testCase : testSuite.getTestCases()) {

			ExecuteStep ex = new ExecuteStep(deviceConfig);
			report[deviceConfig.getDeviceNumber()].fireAddTestCaseEvent(testCase, testSuite);

			this.runTestCase(testCase, ex);

			if (testCase.getTestCaseResult().isFailed() && deviceConfig.retryFailedTestCase()) {
				System.out.println("<< Retrying failed test case >>");
				this.cleanUp(ex);
				TestCase retryTestCase = new TestCase(testCase);
				String testName = testCase.getTestCaseId();

				retryTestCase.insertTestCaseId("Retry > " + testName);
				report[deviceConfig.getDeviceNumber()].fireAddTestCaseEvent(retryTestCase, testSuite);

				ex = new ExecuteStep(deviceConfig);
				this.runTestCase(retryTestCase, ex);
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
			report[ex.getDeviceConfig().getDeviceNumber()].fireAddTestStepEvent(ts, testCase);
			if (testCase.getTestCaseResult().isFailed()) {
				ts.setFailureReason(">> Skipped because of error in " + testCase.getTestCaseId() + " <<");
				this.skipStep(ts, ex.getDeviceConfig());
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

		System.out.println("Executing : " + ex.getDeviceConfig().getAppList().getFirst().getAppName() + "\t"
				+ testCase.getTestCaseId() + "\t" + testCase.getTestCaseResult() + "\t" + timeElapsed.toSeconds() + "\t"
				+ testCase.getTestCaseReason());
	}

	@SuppressWarnings("unused")
	private void skipTestCase(TestCase testCase, DeviceConfig deviceConfig, String reason) {
		testCase.getSteps().forEach(step -> {
			step.setResult(TestStatus.PENDING, reason);
			this.skipStep(step, deviceConfig);
		});

	}

	private void skipStep(TestStep testStep, DeviceConfig deviceConfig) {
		report[deviceConfig.getDeviceNumber()].fireSetTestStepStatus(testStep);
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
			condition = (ex.result != TestStatus.INVALID) && ex.result.isFailed()
					&& (System.currentTimeMillis() - start) < 500;
			if (condition) {
				try {
					ex.flush();
					Thread.sleep(500);
					System.out.println("Retyring Step : " + testStep.getStepDescription());

				} catch (Exception exception) {

				}
			}

		} while (condition);

		testStep.setResult(ex.result, ex.reason);
		testStep.attachScreenshot(ex.screenshot);

		report[ex.getDeviceConfig().getDeviceNumber()].fireSetTestStepStatus(testStep);
	}

}
