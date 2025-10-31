package utilities;

import org.json.JSONObject;
import org.openqa.selenium.By;

public class LocatorInfo {

	String locatorName, locatorValue, locatorType;

	public LocatorInfo(JSONObject loc) {
		this.locatorName = loc.get("locatorName").toString();
		this.locatorValue = loc.getString("locatorValue").toString();
		
		if (loc.getString("locatorType") != null) {
			this.locatorType = loc.getString("locatorType").toString();
		} else {
			this.locatorType = "default";
		}
	}

	public LocatorInfo(String locator, String sep) {
		String[] info = locator.split(sep);
		this.locatorType = info[0].toLowerCase();
		this.locatorValue = info[1];
	}

	public String getLocatorName() {
		return this.locatorName;
	}

	public String getLocatorValue() {
		return this.locatorValue;
	}

	public String getLocatorType() {
		return this.locatorType;
	}

	public String getLocatorTypeAndValue(String sep) {
		return (this.locatorType + sep + this.locatorValue);
	}

	public By getByLocator() {
		By loc = null;

		switch (locatorType) {
		case "xpath":
			loc = By.xpath(locatorValue);
			break;
		case "css":
			loc = By.cssSelector(locatorValue);
			break;
		case "id":
			loc = By.id(locatorValue);
			break;
		case "name":
			loc = By.name(locatorValue);
			break;
		default:
			loc = By.cssSelector(locatorValue);
			break;
		}

		return loc;

	}
}
