package TestReports;

import java.util.Optional;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import testListener.TestListener;
import testManager.TestCase;
import testManager.TestStatus;
import testManager.TestStep;
import testManager.TestSuite;

public class ReportTestListener implements TestListener {
	private ExtentReports report;
	
	public ReportTestListener() {
		report = new ExtentReports();
	}
	
	@Override
	public void addTestSuite(TestSuite testSuite) {
		ExtentTest node = this.report.createTest(testSuite.getSuiteName());
		testSuite.createTestSuiteNode(node);
	}
	
	@Override
	public void finishTest(Optional<String> suffix) {
		new TestReports().createTestReport(this.report, suffix);

	}

	@Override
	public void addTestCase(TestCase testCase, TestSuite suiteNode) {
		ExtentTest node = suiteNode.getTestSuiteNode().createNode(testCase.getTestCaseId());
		testCase.createTestCaseNode(node);

	}

	@Override
	public void addTestStep(TestStep testStep, TestCase testCase) {
		ExtentTest node = testCase.getTestCaseNode().createNode(testStep.getStepDescription());
		testStep.createTestStepNode(node);
	}

	@Override
	public void setTestStepStatus(TestStep testStep) {
		TestStatus testStatus = testStep.getResult();
		if (testStatus.isPassed()) {
			testStep.getTestStepNode().pass(testStep.getStepDescription());
		} else if (testStatus.isFailed()) {
			testStep.getTestStepNode().fail(testStep.getStepDescription(), testStep.getStepScreenshot());
		} else if (testStatus.isPending()) {
			testStep.getTestStepNode().skip(testStep.getStepDescription(), testStep.getStepScreenshot());
		}

	}

}
