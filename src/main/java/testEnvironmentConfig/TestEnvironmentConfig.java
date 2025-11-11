package testEnvironmentConfig;

import java.util.Iterator;

import org.json.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;

import FileManager.JsonFileManager;

public class TestEnvironmentConfig {
	String testUrlConfigName;

	public TestEnvironmentConfig(String testUrlConfigName) {
		this.testUrlConfigName = testUrlConfigName;
	}

	public JSONObject getTestEnvConfigFromJson(String targetEnvName) {
		JSONObject testEnvDetails = new JSONObject();
		JsonNode targetEnvDetails = null;
		try {

			JsonFileManager config = new JsonFileManager();
			JsonNode urlConfig = config.getItemFromJson(testUrlConfigName);
			targetEnvDetails = urlConfig.get(targetEnvName);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (targetEnvDetails != null) {
				testEnvDetails.put("Environment Name", targetEnvName);
				testEnvDetails.put("Base Url", targetEnvDetails.get("Base Url").asText());

				if (targetEnvDetails.hasNonNull("UserName")) {
					testEnvDetails.put("UserName", targetEnvDetails.get("UserName").asText());
				} else {
					testEnvDetails.put("UserName", "");
				}
				if (targetEnvDetails.hasNonNull("Password")) {
					testEnvDetails.put("Password", targetEnvDetails.get("Password").asText());
				} else {
					testEnvDetails.put("Password", "");
				}
			}
		}
		return testEnvDetails;
	}
}
