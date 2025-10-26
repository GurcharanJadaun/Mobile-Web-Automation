package testListener;

import java.util.Optional;

import com.aventstack.extentreports.ExtentReports;

import testManager.TestCase;
import testManager.TestStatus;
import testManager.TestStep;
import testManager.TestSuite;

public interface TestListener {
	
	public void addTestSuite(TestSuite suiteNode);

	public void addTestCase(TestCase testCase,TestSuite suiteNode);

	public void addTestStep (TestStep testStep, TestCase testCase);

	public void setTestStepStatus(TestStep testStep);

	public void finishTest(Optional<String> suffix);

}
