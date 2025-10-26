package testManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.aventstack.extentreports.ExtentTest;

public class TestCase {
	String testCaseId, reason;
	List<TestStep> steps = new ArrayList<TestStep>();
	TestStatus result;
	ExtentTest caseNode;
	HashSet<String> tags = new HashSet<String>();

	public TestCase() {
		result = TestStatus.PENDING;
		reason = "";
		tags.add("@Regression");
	}

	public TestCase(TestCase tc) {
		this.result = TestStatus.PENDING;
		this.reason = "";
		this.steps = new ArrayList<TestStep>();
		for (TestStep ts : tc.getSteps()) {
			this.steps.add(new TestStep(ts));
		}
		this.testCaseId = tc.testCaseId;
		this.caseNode = null;
		this.tags = tc.tags;

	}

	
	/**
	 * Sets the test case ID for this test case.
	 *
	 * @param testCaseId the ID of the test case to set
	 */
	public void insertTestCaseId(String testCaseId) {
		this.testCaseId = testCaseId;
	}

	/**
	 * adds the test step to this test case.
	 *
	 * @param step the step of the test case to be added
	 */
	public void addSteps(TestStep step) {
		steps.add(new TestStep(step));
	}

	/**
	 * adds the list of test steps to this test case.
	 *
	 * @param listOfSteps the list of steps to be added to the test case
	 */
	public void addSteps(List<TestStep> listOfSteps) {
		for (TestStep step : listOfSteps) {
			this.addSteps(step);
		}
	}

	/**
	 * Returns the list of test steps associated with this test case.
	 *
	 * @return the list of {@link TestStep} objects
	 */
	public List<TestStep> getSteps() {
	    return steps;
	}

	/**
	 * Sets the result status of the test case.
	 *
	 * @param result the {@link TestStatus} result to set
	 */
	public void setTestCaseResult(TestStatus result) {
	    this.result = result;
	}

	/**
	 * Sets the result status and appends a reason for the test case outcome.
	 *
	 * @param result the {@link TestStatus} result to set
	 * @param reason the reason to append to the current reason string
	 */
	public void setTestCaseResult(TestStatus result, String reason) {
	    this.result = result;
	    this.reason += reason;
	}

	/**
	 * Appends a reason for the test case result.
	 *
	 * @param reason the reason to append
	 */
	public void setTestCaseReason(String reason) {
	    this.reason += reason;
	}

	/**
	 * Returns the reason associated with the test case result.
	 *
	 * @return the reason as a string
	 */
	public String getTestCaseReason() {
	    return this.reason;
	}

	/**
	 * Returns the result status of the test case.
	 *
	 * @return the {@link TestStatus} of the test case
	 */
	public TestStatus getTestCaseResult() {
	    return this.result;
	}

	/**
	 * Returns the ID of the test case.
	 *
	 * @return the test case ID
	 */
	public String getTestCaseId() {
	    return this.testCaseId;
	}

	/**
	 * Checks whether the test case has passed.
	 *
	 * @return {@code true} if the test case result is PASSED; {@code false} otherwise
	 */
	public boolean hasTestCasePassed() {
	    return this.result == TestStatus.PASSED;
	}

	/**
	 * Validates the test case by checking all test steps.
	 * If any test step is marked INVALID, the test case is also marked INVALID.
	 *
	 * @return {@code true} if all steps are valid; {@code false} if any are invalid
	 */
	public boolean isTestCaseValid() {
	    steps.forEach(ts -> {
	        if (ts.getResult() == TestStatus.INVALID) {
	            result = TestStatus.INVALID;
	        }
	    });
	    return result != TestStatus.INVALID;
	}

	/**
	 * Returns the reporting node associated with this test case (used for reporting in ExtentReports).
	 *
	 * @return the {@link ExtentTest} node for this test case
	 */
	public ExtentTest getTestCaseNode() {
	    return this.caseNode;
	}

	/**
	 * Sets the reporting node for this test case.
	 *
	 * @param caseNode the {@link ExtentTest} node to associate with this test case
	 */
	public void createTestCaseNode(ExtentTest caseNode) {
	    this.caseNode = caseNode;
	}
	
	public void addTag(String tag) {
		this.tags.add(tag);
	}
	
	public HashSet<String> getTestCaseTags(){
		return this.tags;
	}

}
