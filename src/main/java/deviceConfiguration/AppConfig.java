package deviceConfiguration;

import com.fasterxml.jackson.databind.JsonNode;

public class AppConfig {
	String appName, packageName;
	int appNumber;
	boolean resetAppData;

	public AppConfig(JsonNode appConfig, int appNumber) {

		this.appName = appConfig.get("AppName").asText();
		this.packageName = appConfig.get("PackageName").asText();
		this.appNumber = appNumber;

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

}
