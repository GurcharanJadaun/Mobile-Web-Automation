package TestReports;

import java.util.List;
import java.util.Optional;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.model.Test;

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
		List<Test> testSuites = this.report.getReport().getTestList();

		for (Test suite : testSuites) {
			if (suite.getStatus() != Status.PASS) {
				List<Test> listOfCases = suite.getChildren();
				Status currentStatus = Status.PASS;

				for (Test tc : listOfCases) {
					Status testStatus = tc.getStatus();
					currentStatus = this.getStatus(currentStatus, testStatus);
					if (currentStatus == Status.FAIL) {
						break;
					}
				}
				suite.setStatus(currentStatus);
			}
		}
		new TestReports().createTestReport(this.report, suffix);
	}

	private Status getStatus(Status currentStatus, Status testStatus) {

		if (currentStatus == Status.FAIL)
			currentStatus = Status.FAIL;
		else if (testStatus == Status.FAIL)
			currentStatus = Status.FAIL;
		else if (testStatus == Status.SKIP && currentStatus != Status.FAIL)
			currentStatus = Status.SKIP;
		else
			currentStatus = Status.PASS;

		return currentStatus;
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

	@Override
	public void removeTestCase(TestSuite suiteNode, TestCase testCase) {
		System.out.println("<< Removing Test Case >>");
		List<Test> testSuites = this.report.getReport().getTestList();
		Test selectedSuite = null;
		for(Test suite: testSuites) {
			if(suite.getName().equalsIgnoreCase(suiteNode.getSuiteName())) {
				selectedSuite = suite;
				break;
			}
		}
		
		if(selectedSuite!=null) {
			List<Test> listOfCases = selectedSuite.getChildren();
			Test removeTest = null;
			for(Test tc : listOfCases) {
				if(tc.getName().equalsIgnoreCase(testCase.getTestCaseId())) {
					removeTest = tc;
					break;
				}
		}
			if(removeTest != null) {
				listOfCases.remove(removeTest);
			}
		}
	}

}
