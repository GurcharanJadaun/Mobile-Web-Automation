package deviceConfiguration;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;

public class DeviceConfig {
	String appiumServerUrl, targetOperatingSystem;
	String platformVersion, udid, deviceName; //optional fields
	int deviceNumber;
	List<AppConfig> apps = new ArrayList<AppConfig>();
	JSONObject testUrlDetails;
	boolean retryFailedTestCases;
	
	public DeviceConfig(JsonNode deviceConfig, int deviceNumber) {
		this.deviceNumber = deviceNumber;
		this.appiumServerUrl = deviceConfig.get("AppiumServerUrl").asText();
		this.targetOperatingSystem = deviceConfig.get("TargetOperatingSystem").asText();
		
		if (deviceConfig.hasNonNull("platformVersion")) {
			this.platformVersion = deviceConfig.get("platformVersion").asText();
		}
		
		if (deviceConfig.hasNonNull("udid")) {
			this.udid = deviceConfig.get("udid").asText();
		}
		
		if (deviceConfig.hasNonNull("deviceName")) {
			this.deviceName = deviceConfig.get("deviceName").asText();
		}
		
		if (deviceConfig.hasNonNull("RetryFailedTestCases")) {
			this.retryFailedTestCases = deviceConfig.get("RetryFailedTestCases").asBoolean();
		} else {
			this.retryFailedTestCases = true;
		}
	}
	
	public int getDeviceNumber() {
		return this.deviceNumber;
	}
	
	/**
	 * returns target operating system of device on which test would run.
	 */
	public String getTargetPlatform() {
		return this.targetOperatingSystem;
	}

	
	/**
	 * returns Appium server details on which test would run.
	 */
	public String getAppiumServerUrl() {
		return this.appiumServerUrl;
	}
	
	/**
	 * returns List<AppConfig>. The list has the browser configuration details.
	 */
	public List<AppConfig> getAppList() {
		return this.apps;
	}
	
	public void setAppsForDevice(List<AppConfig> apps) {
		this.apps = apps;
	}
	
	public void setTestUrlDetails(JSONObject testUrlDetails) {
		this.testUrlDetails = testUrlDetails;
	}
	
	public boolean retryFailedTestCase() {
		return this.retryFailedTestCases;
	}
	
	/**
	 * returns URL details for the target App.
	 */
	public JSONObject getTestUrlDetails() {
		return this.testUrlDetails;
	}

}
