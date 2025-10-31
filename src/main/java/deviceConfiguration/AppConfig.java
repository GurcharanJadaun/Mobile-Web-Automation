package deviceConfiguration;

import org.json.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;

public class AppConfig {
	String appName, packageName, appiumServerUrl, platformName;
	int appNumber;
	boolean retryFailedTestCases, resetAppData;
	JSONObject testUrlDetails;

	public AppConfig(JsonNode appConfig, int appNumber) {

		this.appName = appConfig.get("AppName").asText();
		this.packageName = appConfig.get("PackageName").asText();
		this.appNumber = appNumber;
		this.appiumServerUrl = appConfig.get("AppiumServerUrl").asText();
		this.platformName = appConfig.get("TargetOperatingSystem").asText();

		if (appConfig.hasNonNull("RetryFailedTestCases")) {
			this.retryFailedTestCases = appConfig.get("RetryFailedTestCases").asBoolean();
		} else {
			this.retryFailedTestCases = true;
		}

		if (appConfig.hasNonNull("ResetAppData")) {
			this.resetAppData = appConfig.get("ResetAppData").asBoolean();
		} else {
			this.resetAppData = true;
		}
		
	}

	/**
	 * returns the browser index of target App.
	 */
	public int getAppSerialNumber() {
		return this.appNumber;
	}

	public void setTestUrlDetails(JSONObject testUrlDetails) {
		this.testUrlDetails = testUrlDetails;
	}

	/**
	 * returns Application Name on which test would run.
	 */
	public String getAppName() {
		return this.appName;
	}

	/**
	 * returns Application Package Name on which test would run.
	 */
	public String getPackageName() {
		return this.packageName;
	}

	/**
	 * returns Application Package Name on which test would run.
	 */
	public String getTargetPlatform() {
		return this.platformName;
	}

	
	/**
	 * returns Appium server details on which test would run.
	 */
	public String getAppiumServerUrl() {
		return this.appiumServerUrl;
	}

	/**
	 * returns URL details for the target App.
	 */
	public JSONObject getTestUrlDetails() {
		return this.testUrlDetails;
	}

	public boolean retryFailedTestCase() {
		return this.retryFailedTestCases;
	}
}
