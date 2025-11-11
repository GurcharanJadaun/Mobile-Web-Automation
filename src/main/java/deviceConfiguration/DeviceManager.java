package deviceConfiguration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;

import FileManager.JsonFileManager;

public class DeviceManager {
	List<BrowserConfig> browsers;
	String operatingSystem, deviceConfigName;
	boolean runTestsOnBrowsersInParallel, isConfigValid;
	List<DeviceConfig> devices;

	public DeviceManager(String deviceConfigName) {
		this.deviceConfigName = deviceConfigName;
		isConfigValid = true;
		browsers = new ArrayList<BrowserConfig>();
		devices = new ArrayList<DeviceConfig>();
	}

	/**
	 * if true runs the configured browsers in parallel for testing on the device.
	 */
	public boolean runTestsOnBrowsersInParallel() {
		return this.runTestsOnBrowsersInParallel;
	}

	/**
	 * returns false if there was an issue while reading the device config file.
	 */
	public boolean isConfigValid() {
		return this.isConfigValid;
	}

	/**
	 * returns List<BrowserConfig>. The list has the browser configuration details.
	 */
	public List<BrowserConfig> getBrowserList() {
		return this.browsers;
	}

	/**
	 * returns List<DeviceConfig>. The list has the browser configuration details.
	 */
	public List<DeviceConfig> getDeviceList() {
		return this.devices;
	}

	/**
	 * returns JsonNode which contains one of the device configuration.
	 */
	private JsonNode getTestRunnerConfig(String configName) {
		JsonNode shortListedConfig = null;
		try {

			JsonFileManager config = new JsonFileManager("config");
			Iterator<JsonNode> listOfConfigs = config.getItemsFromJson(this.deviceConfigName);

			while (listOfConfigs.hasNext()) {
				JsonNode item = listOfConfigs.next();
				System.out.println("Checked config : " + item.get("ConfigName").asText());
				if (item.get("ConfigName").asText().equals(configName)) {

					shortListedConfig = item.get("RunConfig");
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			isConfigValid = false;
		}

		return shortListedConfig;

	}

	private List<AppConfig> setupAppsForDevice(JsonNode TargetAppDetails) {
		List<AppConfig> apps = new ArrayList<AppConfig>();

		if (TargetAppDetails != null) {
			int i = 0;
			Iterator<JsonNode> it = TargetAppDetails.elements();
			while (it.hasNext()) {
				JsonNode item = it.next();
				apps.add(new AppConfig(item, i));
				i++;
			}

		} else {
			System.out.println("<< Target App details is NULL in configuration >>");
		}
		return apps;
	}

	public void setupMobileDevices(String configName) {
		JsonNode shortListedConfig = this.getTestRunnerConfig(configName);
		if (shortListedConfig != null) {

			Iterator<JsonNode> deviceList = shortListedConfig.get("TargetDeviceDetails").elements();
			int i = 0;
			while (deviceList.hasNext()) {

				JsonNode deviceJSON = deviceList.next();
				DeviceConfig device = new DeviceConfig(deviceJSON, i);
				JsonNode targetAppDetails = deviceJSON.get("TargetAppDetails");

				List<AppConfig> apps = this.setupAppsForDevice(targetAppDetails);
				device.setAppsForDevice(apps);

				this.devices.add(device);
				i++;

			}

		} else {
			System.out.println("Config Name '" + configName + "' not available in " + this.deviceConfigName);
		}

	}

	/**
	 * Assigns Browser Details to the list of BrowserConfig from the JSON Array
	 * filtered by "ConfigName" parameter.
	 * 
	 * @param "configName" to be used for testing.
	 */
	public void setupBrowserForDevice(String configName) {

		JsonNode shortListedConfig = this.getTestRunnerConfig(configName);
		if (shortListedConfig != null) {
			if (shortListedConfig.hasNonNull("TargetOperatingSystem")) {
				this.operatingSystem = shortListedConfig.get("TargetOperatingSystem").asText();
			}

			if (shortListedConfig.hasNonNull("ParallelBrowserExecution")) {
				this.runTestsOnBrowsersInParallel = shortListedConfig.get("ParallelBrowserExecution").asBoolean();
			} else {
				this.runTestsOnBrowsersInParallel = false;
			}
			int i = 0;
			Iterator<JsonNode> it = shortListedConfig.get("TargetBrowsers").elements();
			while (it.hasNext()) {
				JsonNode item = it.next();
				browsers.add(new BrowserConfig(item, i));
				i++;

			}

		} else {
			System.out.println("Config Name '" + configName + "' not available in DeviceConfig.JSON");
		}

	}

	public void setupMobileDevices(String configName, JSONObject testEnvConfig) {
		this.setupMobileDevices(configName);
		this.devices.forEach(device -> {
			device.setTestUrlDetails(testEnvConfig);
		});
	}

	/**
	 * Assigns Browser Details to the list of BrowserConfig from the JSON Array
	 * filtered by "ConfigName" parameter & setup test environment details for each
	 * browser
	 * 
	 * @param "configName"    to be used for testing.
	 * @param "testEnvConfig" contains env details like browser url, username and
	 *                        password
	 */
	public void setupBrowserForDevice(String configName, JSONObject testEnvConfig) {
		this.setupBrowserForDevice(configName);
		this.browsers.forEach(browser -> {
			browser.setTestUrlDetails(testEnvConfig);
		});
	}
}
