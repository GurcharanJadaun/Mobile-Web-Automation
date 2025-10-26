package testManager;

import java.util.Optional;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.model.Media;

public class TestStep {

	String action,locator,testData,reason,stepDescription;
	TestStatus result;
	int stepNumber;
	ExtentTest stepNode;
	public Media screenshot;
	
	public TestStep(){
		reason = "";
		stepDescription = "";
		this.result = TestStatus.PENDING;
		this.stepNode = null;
		this.screenshot = null;
	}
	public TestStep(TestStep ts) {
		this.reason = "";
		this.result = TestStatus.PENDING;
		this.action = ts.action;
		this.locator = ts.locator;
		this.testData = ts.testData;
		this.stepNumber = ts.stepNumber;
		this.stepDescription = ts.stepDescription;
		this.stepNode = null;
		this.screenshot = null;
	}
	
	/**
	 * Sets the action to be performed in this test step.
	 *
	 * @param action the action string to set
	 */
	public void insertAction(String action) {
	    this.action = action;
	}

	/**
	 * Sets the locator used in this test step.
	 *
	 * @param locator an {@link Optional} containing the locator string
	 */
	public void insertLocator(Optional<String> locator) {
	    this.locator = locator.orElse(null);
	}

	/**
	 * Sets the test data used in this test step.
	 *
	 * @param testData an {@link Optional} containing the test data string
	 */
	public void insertTestData(Optional<String> testData) {
	    this.testData = testData.orElse(null);
	}

	/**
	 * Sets the step description. If the optional description is empty,
	 * a default description is created using action, locator, and test data.
	 *
	 * @param description an {@link Optional} description for the test step
	 */
	public void setStepDescription(Optional<String> description) {
	    String data = description.orElse(this.action + "\t" + this.locator + "\t" + this.testData);
	    this.stepDescription = data;
	}

	/**
	 * Replaces part of the step description with the given replacement.
	 *
	 * @param replace the string to be replaced
	 * @param replaceWith the string to replace with
	 */
	public void updateStepDescription(String replace, String replaceWith) {
	    this.stepDescription = this.stepDescription.replace(replace, replaceWith);
	}

	/**
	 * Sets the result status of this test step.
	 *
	 * @param result the {@link TestStatus} to set
	 */
	public void setResult(TestStatus result) {
	    this.result = result;
	}

	/**
	 * Returns the reporting node associated with this test step.
	 *
	 * @return the {@link ExtentTest} node for the test step
	 */
	public ExtentTest getTestStepNode() {
	    return this.stepNode;
	}

	/**
	 * Sets the reporting node for this test step.
	 *
	 * @param stepNode the {@link ExtentTest} node to associate
	 */
	public void createTestStepNode(ExtentTest stepNode) {
	    this.stepNode = stepNode;
	}

	/**
	 * Sets the result status and appends a reason for this test step.
	 *
	 * @param result the {@link TestStatus} to set
	 * @param reason the reason to append to the current reason
	 */
	public void setResult(TestStatus result, String reason) {
	    this.result = result;
	    this.reason = (this.reason == null ? "" : this.reason) + reason;
	}

	/**
	 * Sets the step number in the test sequence.
	 *
	 * @param stepNumber the step number to set
	 */
	public void setStepNumber(int stepNumber) {
	    this.stepNumber = stepNumber;
	}

	/**
	 * Sets the failure reason for this test step.
	 *
	 * @param reason the reason for failure
	 */
	public void setFailureReason(String reason) {
	    this.reason = reason;
	}

	/**
	 * Returns the description of this test step.
	 *
	 * @return the step description
	 */
	public String getStepDescription() {
	    return this.stepDescription;
	}

	/**
	 * Returns the action of this test step.
	 *
	 * @return the action string
	 */
	public String getAction() {
	    return action;
	}

	/**
	 * Returns the locator used in this test step.
	 *
	 * @return the locator string
	 */
	public String getLocator() {
	    return locator;
	}

	/**
	 * Returns the test data used in this step.
	 *
	 * @return the test data string
	 */
	public String getTestData() {
	    return testData;
	}

	/**
	 * Returns the result status of this test step.
	 *
	 * @return the {@link TestStatus} result
	 */
	public TestStatus getResult() {
	    return result;
	}

	/**
	 * Returns the reason associated with the test step's result.
	 *
	 * @return the reason string
	 */
	public String getTestStepReason() {
	    return this.reason;
	}

	/**
	 * Returns the status of this test step.
	 * (Alias for {@link #getResult()})
	 *
	 * @return the {@link TestStatus} result
	 */
	public TestStatus getStepStatus() {
	    return this.result;
	}

	/**
	 * Attaches a screenshot to this test step.
	 *
	 * @param screenshot the {@link Media} object representing the screenshot
	 */
	public void attachScreenshot(Media screenshot) {
	    this.screenshot = screenshot;
	}

	/**
	 * Returns the screenshot attached to this test step, if any.
	 *
	 * @return the {@link Media} screenshot
	 */
	public Media getStepScreenshot() {
	    return this.screenshot;
	}
	
}
