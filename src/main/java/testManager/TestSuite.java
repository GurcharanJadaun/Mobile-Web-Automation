package testManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.aventstack.extentreports.ExtentTest;

public class TestSuite {

	String suiteName;
	List<TestCase> testSuite;
	TestCase beforeEachTest, afterEachTest;
	TestStatus isTestSuiteValid;
	ExtentTest suiteNode;

	public TestSuite(List<TestCase> listOfTestCases) {
		this.testSuite = listOfTestCases;
		this.isTestSuiteValid = TestStatus.PENDING;
		this.beforeEachTest = new TestCase();
		this.afterEachTest = new TestCase();
	}

	public TestSuite() {
		this.testSuite = new ArrayList<TestCase>();
		this.beforeEachTest = new TestCase();
		this.afterEachTest = new TestCase();

		this.isTestSuiteValid = TestStatus.PENDING;
	}

	// deep copy values of test suite
	public TestSuite(TestSuite suite) {
		this.suiteName = suite.suiteName;
		this.isTestSuiteValid = suite.isTestSuiteValid;
		this.suiteNode = null;

		this.testSuite = new ArrayList<TestCase>();
		this.beforeEachTest = new TestCase();
		this.afterEachTest = new TestCase();

		for (TestCase tc : suite.testSuite) {
			this.testSuite.add(new TestCase(tc));
		}
		this.beforeEachTest = new TestCase(suite.beforeEachTest);
		this.beforeEachTest = new TestCase(suite.afterEachTest);

	}

	/**
	 * adds one test case at a time to the test suite.
	 * 
	 * @param "testCase" add test case to the test suite.
	 */
	public void addTestCase(TestCase testCase) {
		testSuite.add(testCase);
	}

	/**
	 * adds list of test cases to the test suite.
	 * 
	 * @param "testCases" add list of test cases to the test suite.
	 */
	public void addTestCases(List<TestCase> testCases) {
		testSuite.addAll(testCases);
	}

	/**
	 * returns List<TestCase> from the test suite. These are the list of test cases
	 * for each test suite.
	 */
	public List<TestCase> getTestCases() {
		return this.testSuite;
	}

	/**
	 * returns beforeEachTest hook TestCase from the test suite. This is the test
	 * case which should be executed before each test case in test suite.
	 */
	public TestCase getBeforeEachTest() {
		return this.beforeEachTest;
	}

	/**
	 * returns afterEachTest hook TestCase from the test suite. This is the test
	 * case which should be executed after each test case in test suite.
	 */
	public TestCase getAfterEachTest() {
		return this.afterEachTest;
	}

	/**
	 * sets name for the test suite.
	 * 
	 * @param "suiteName" adds name for the test suite.
	 */
	public void setSuitName(String suiteName) {
		this.suiteName = suiteName;
	}

	/**
	 * gets name of Test Suite.
	 */
	public String getSuiteName() {
		return this.suiteName;
	}

	/**
	 * returns first occurrence of Optional<TestCase> from the test suite.
	 */
	public Optional<TestCase> getFirstOccurenceOfTestCaseById(String testCaseId) {
		Optional<TestCase> tc;

		tc = testSuite.stream().filter(testCase -> testCase.getTestCaseId().equalsIgnoreCase(testCaseId)).findFirst();

		return tc;
	}

	/**
	 * returns List of TestCases from the test suite.
	 */
	public List<TestCase> getTestCasesById(String testCaseId) {
		List<TestCase> tc = Collections.emptyList();

		tc = testSuite.stream().filter(testCase -> testCase.getTestCaseId().equalsIgnoreCase(testCaseId))
				.collect(Collectors.toList());

		return tc;
	}

	/**
	 * returns List of TestCases from the test suite.
	 */
	public Optional<TestCase> getTestCaseById(String testCaseId) {
		Optional<TestCase> tc = null;

		tc = testSuite.stream().filter(testCase -> testCase.getTestCaseId().equalsIgnoreCase(testCaseId)).findFirst();

		return tc;
	}

	/**
	 * extracts "before each" test cases from the test suite.
	 */
	public void extractBeforeEachMethodFromTestSuite() {
		this.beforeEachTest = this.getTestCaseById("beforeEach").orElse(new TestCase());
		this.testSuite.remove(beforeEachTest);
	}

	/**
	 * extracts "after each" test cases from the test suite.
	 */
	public void extractAfterEachMethodFromTestSuite() {
		this.afterEachTest = this.getTestCaseById("afterEach").orElse(new TestCase());
		this.testSuite.remove(afterEachTest);
	}

	/**
	 * returns List of Test Cases from the test suite filtered by TestStatus
	 */
	public List<TestCase> getListOfTestCasesByStatus(TestStatus status) {
		List<TestCase> shortListedTests = Collections.emptyList();
		shortListedTests = this.testSuite.stream().filter(tc -> tc.getTestCaseResult() == status)
				.collect(Collectors.toList());
		return shortListedTests;
	}

	/**
	 * removes all Invalid Test Cases from the test suite.
	 */
	public List<TestCase> removeInvalidTestCasesFromSuite() {
		List<TestCase> removedTests = Collections.emptyList();
		removedTests = getListOfTestCasesByStatus(TestStatus.INVALID);
		this.testSuite.removeAll(removedTests);
		return removedTests;
	}

	/**
	 * returns true when all the test cases in test suite have status as passed.
	 */
	public boolean hasTestSuitePassed() {

		boolean status = true;
		status = getListOfTestCasesByStatus(TestStatus.PASSED).size() == this.testSuite.size() ? true : false;
		return status;
	}

	/**
	 * returns true if any of the hooks (beforeEach, afterEach, afterAll, beforeAll)
	 * is present in test suite.
	 */
	public boolean suiteContainsHooks() {
		boolean result = true;
		result = this.beforeEachTest.steps.size() == 0 || this.afterEachTest.steps.size() == 0;
		return result;
	}

	/**
	 * adds test status for the test suite.
	 * 
	 * @param "status" set status to the test suite.
	 */
	public void setTestSuiteStatus(TestStatus status) {
		this.isTestSuiteValid = status;
	}

	/**
	 * returns status of the test suite.
	 */
	public TestStatus getTestSuiteStatus() {
		return this.isTestSuiteValid;
	}

	/**
	 * returns true if status is not INVALID for test suite.
	 */
	public boolean isTestSuiteValid() {
		return !(this.isTestSuiteValid == TestStatus.INVALID);
	}

	/**
	 * add hooks (test steps) to all the test cases
	 */
	public void addHooksToTestCases() {
		for (int index = 0; index < this.testSuite.size(); index++) {
			TestCase testCase = testSuite.get(index);
			TestCase tmp = new TestCase();

			tmp.caseNode = testCase.caseNode;
			tmp.reason = testCase.reason;
			tmp.result = testCase.result;
			tmp.testCaseId = testCase.testCaseId;
			tmp.tags = testCase.tags;

			tmp.addSteps(this.beforeEachTest.steps);
			tmp.addSteps(testCase.steps);
			tmp.addSteps(this.afterEachTest.steps);

			this.testSuite.set(index, tmp);
		}

	}

	public ExtentTest getTestSuiteNode() {
		return this.suiteNode;
	}

	/**
	 * Stores ExtentTest instance to be used as suite node by the listener
	 */
	public void createTestSuiteNode(ExtentTest suiteNode) {
		this.suiteNode = suiteNode;
	}
	/**
	 * Extracts hooks from the imported test suite.
	 */
	public void extractHooks() {
		this.extractAfterEachMethodFromTestSuite();
		this.extractBeforeEachMethodFromTestSuite();

	}
	
	public void filterByTags(String tag) {
		
		for(TestCase tc : this.testSuite ) {
			if(!tc.getTestCaseTags().contains(tag)) {
				tc.setTestCaseResult(TestStatus.INVALID);
			}
		}
		this.removeInvalidTestCasesFromSuite();
	}

}
