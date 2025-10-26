package TestCaseCompiler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import TestReports.TestReports;
import testManager.TestCase;
import testManager.TestStatus;
import testManager.TestStep;
import testManager.TestSuite;

public abstract class TestCaseCompiler {

	public HashMap<String, String> functionDetails;
	public HashMap<String, String> locators;

	public void generateCompilationReport(TestSuite suite) {

		TestReports reports = new TestReports(suite);
		reports.createCompilerReport();

		List<TestCase> shortListed = suite.removeInvalidTestCasesFromSuite();
		boolean status = new TestSuite(shortListed).suiteContainsHooks();
		TestStatus suiteStatus = status ? TestStatus.INVALID : TestStatus.PENDING;

		suite.setTestSuiteStatus(suiteStatus);

		this.createReport(suite.getSuiteName(), shortListed);

	}

	public void generateCompilationReport(List<TestSuite> listOfSuites) {

		TestReports reports = new TestReports(listOfSuites);
		reports.createCompilerReport();

		for (TestSuite suite : listOfSuites) {
			List<TestCase> shortListed = suite.removeInvalidTestCasesFromSuite();
			boolean status = new TestSuite(shortListed).suiteContainsHooks();
			TestStatus suiteStatus = status ? TestStatus.INVALID : TestStatus.PENDING;
			suite.setTestSuiteStatus(suiteStatus);
			this.createReport(suite.getSuiteName(), shortListed);

		}

	}

	void createReport(String suiteName, List<TestCase> shortListed) {
		System.out.println("--Test Compilation Completed For Suite " + suiteName + "--");
		int numberOfInvalidTests = shortListed.size(), index = 0;
		System.out.println("Number Of Invalid Test Cases : " + numberOfInvalidTests);
		for (TestCase tc : shortListed) {
			index++;
			System.out.println(
					index + " : " + tc.getTestCaseId() + "\t" + tc.getTestCaseResult() + "\n" + tc.getTestCaseReason());
		}
	}

	protected void compileTestCases(List<TestCase> testCaseList) {

		for (TestCase tc : testCaseList) {
			List<TestStep> listOfSteps = tc.getSteps();
			String failReason = "";
			for (TestStep step : listOfSteps) {
				String result = "";

				if (!functionDetails.containsKey(step.getAction())) {
					result = result + (">> Invalid Action Name : " + step.getAction() + "\n");
					step.setResult(TestStatus.INVALID, result);
					step.setStepDescription(Optional.empty());
				} else {
					Optional<String> stepDescription = Optional.ofNullable(functionDetails.get(step.getAction()));
					step.setStepDescription(stepDescription);
				}

				if (step.getLocator() != null && step.getLocator().length() > 0
						&& !locators.containsKey(step.getLocator())) {
					result = result + (">> Invalid Locator Name : " + step.getLocator() + "\n");
					step.setResult(TestStatus.INVALID, result);
					result = result + assignKeywordParameters(step);
				}

				// generate step description for test cases with correct action and locator
				if (step.getResult() != TestStatus.INVALID) {
					result = result + assignKeywordParameters(step);

				}
				failReason += result;
			}
			if (tc.isTestCaseValid()) {
				assignLocators(listOfSteps);
			} else {
				tc.setTestCaseReason(failReason);
			}

		}
	}

	private String assignKeywordParameters(TestStep step) {

		String stepDesc = step.getStepDescription();

		boolean hasLocator = step.getLocator() != null && step.getLocator().length() > 0;

		if (hasLocator) {
			if (stepDesc.contains("{@locator}")) {
				step.updateStepDescription("{@locator}", "'" + step.getLocator() + "'");
			} else {
				String result = ">> Did not Expect Locator for this Step <<" + "\n";
				step.setResult(TestStatus.INVALID, result);
			}
		} else {
			if (stepDesc.contains("{@locator}")) {
				String result = ">> Expected Locator for this Step <<" + "\n";
				step.setResult(TestStatus.INVALID, result);
			}
		}
		// check if step description expects test data
		boolean hasTestData = step.getTestData() != null && step.getTestData().trim().length() > 0;

		if (hasTestData) {
			if (stepDesc.contains("{@testData}")) {
				step.updateStepDescription("{@testData}", "'" + step.getTestData() + "'");
			} else {
				String result = ">> Did not Expect Test Data for this Step <<" + "\n";
				step.setResult(TestStatus.INVALID, result);
			}
		} else {
			if (stepDesc.contains("{@testData}")) {
				String result = ">> Expected Test Data for this Step <<" + "\n";
				step.setResult(TestStatus.INVALID, result);
			}
		}
		return step.getTestStepReason();
	}

	public void assignLocators(List<TestStep> listOfSteps) {
		Iterator<TestStep> it = listOfSteps.iterator();
		while (it.hasNext()) {
			TestStep step = it.next();
			String loc = step.getLocator();
			String value = locators.get(loc);
			step.insertLocator(Optional.ofNullable(value));
		}
	}

	public abstract void loadFunctionNames(String dir);

	public abstract void loadLocatorMap(String dir);

}
