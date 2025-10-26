package TestReports;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import testListener.TestListener;
import testManager.TestCase;
import testManager.TestStep;
import testManager.TestSuite;

public class ReportTestEventManager {
	private List<TestListener> listeners ;
	
	public ReportTestEventManager() {
		listeners = new ArrayList<TestListener>();
	}
	
	public void addTestListener(TestListener listener) {
        listeners.add(listener);
    }
		
	public void fireCreateTestSuite(TestSuite testSuite) {
		for (TestListener listener : listeners) {
            listener.addTestSuite(testSuite);
        }
	}
	
	public void fireAddTestCaseEvent(TestCase testCase, TestSuite testSuite) {
		for (TestListener listener : listeners) {
            listener.addTestCase(testCase, testSuite);
        }
	}
	
	public void fireAddTestStepEvent(TestStep testStep, TestCase testCase) {
		for (TestListener listener : listeners) {
            listener.addTestStep(testStep, testCase);
        }
	}
	
	public void fireSetTestStepStatus(TestStep testStep) {
		for (TestListener listener : listeners) {
            listener.setTestStepStatus(testStep);
        }
	}
	
	public void fireFinishTest(Optional<String> browserName) {
		for (TestListener listener : listeners) {
            listener.finishTest(browserName);
        }
	}
}
