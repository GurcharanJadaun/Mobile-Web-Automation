package automationUtilities.mobileAutomation;

import org.json.JSONObject;
import org.openqa.selenium.By;

import TestExceptions.SoftAssert;
import deviceConfiguration.AppConfig;
import utilities.LocatorInfo;

public class MobileKeywordDictionary {
	private MobileDriver driver;
	private AppConfig appConfig;
	private JSONObject testUrlDetails;

	public MobileKeywordDictionary(AppConfig appConfig) {
		this.appConfig = appConfig;
		String url = appConfig.getAppiumServerUrl();
		String platformName = appConfig.getTargetPlatform();
		testUrlDetails = appConfig.getTestUrlDetails();
		if(platformName.equalsIgnoreCase("Android")) {
		driver = new AndroidMobileDriver(url);}
		else {
			
		}
	}

	public void openBrowser(String param) {

		if (param.equalsIgnoreCase("deviceConfig.browser")) {
			param = appConfig.getAppName();
		}
		System.out.println("Working with package : "+param);
		driver.openBrowser(param);

	}
	
	public void gotoUrl(String url) {
		String[] details = url.split("\\+");
		url = "";
		for(String data : details) {
			if(data.equalsIgnoreCase("{BaseURL}")) {
				data = testUrlDetails.get("Base Url").toString();
			}
			url = url + data;
		}
		driver.openUrl(url);
	}
	
	public void click(String locatorData) {
		By loc = this.createLocatorFromString(locatorData);
		if (driver.isButtonEnabled(loc)) {
			driver.scrollIntoView(loc);
			driver.waitForPageToRender();
			driver.clickWebElement(loc);
		} else {
			System.out.print("<<Button not clickable>>");
		}
	}
	
	public void triggerLazyLoadAndClick(String locatorData) throws InterruptedException {
		driver.scrollScreen(300);
		By loc = this.createLocatorFromString(locatorData);
		Thread.sleep(250);
		driver.clickWebElement(loc);
	}
	
	public void waitForPageToRender() {
		driver.waitForPageToRender();
		
	}
	
	public void enterTextInTextField(String locatorData, String text)throws InterruptedException  {
		By locator = this.createLocatorFromString(locatorData);
		
		driver.clickWebElement(locator);
		Thread.sleep(250);
		driver.enterTextInTextBox(locator, text);
		Thread.sleep(250);
		
	}
	
	
	public void selectValueFromDropDown(String locatorData, String text) {
		By locator = this.createLocatorFromString(locatorData);
			driver.selectValueFromDropDown(locator, text);
	}
	
//	public void selectIndexFromDropDown(String locatorData, String index) {
//		int indexToBeSelected = Integer.parseInt(index);
//		browser.waitForPresenceOfElement(locatorData);
//		browser.scrollIntoView(locatorData);
//		browser.selectIndexFromDropDown(locatorData, indexToBeSelected-1);
//	}
//	
//	public void waitForPresenceAndClick(String locatorData) {
//		browser.waitForPresenceOfElement(locatorData);
//		browser.scrollIntoView(locatorData);
//		browser.clickWebElement(locatorData);
//	}
//	
	public void elementMustBeVisible(String locatorData) throws Exception{
		By locator = this.createLocatorFromString(locatorData);
		
		driver.scrollIntoView(locator);
		boolean result = driver.checkVisibilityOfElement(locator);
		
		if(!result) {
			throw new Exception("Element Visibility Validation Failed");
		}
	}
//	
//	public void elementShouldBeVisible(String locatorData) throws SoftAssert{
//		browser.waitForPresenceOfElement(locatorData);
//		browser.scrollIntoView(locatorData);
//		boolean result = browser.checkVisibilityOfElement(locatorData);
//		if(!result) {
//			throw new SoftAssert("Element Visibility Validation Failed");
//		}
//	}
//	
	public void textBoxMustHaveValue(String locatorData, String expectedData) throws Exception{
		
		By locator = this.createLocatorFromString(locatorData);
		
		driver.clickWebElement(locator);
		String actualData = driver.getTextFromTextBox(locator);
		
		expectedData = expectedData.equalsIgnoreCase("{NULL}") || expectedData.equalsIgnoreCase("{EMPTY}") ? "" : expectedData ;
		
		if(!actualData.equals(expectedData.trim())) {
			throw new Exception("<< Expected and Actual Data don't match >>"+
			"\nActual Data : "+ actualData +
			"\nExpected Data : " + expectedData);
		}
	}
//	
//	public void textBoxShouldHaveValue(String locatorData, String expectedData) throws SoftAssert{
//		browser.waitForPresenceOfElement(locatorData);
//		browser.scrollIntoView(locatorData);
//		
//		String actualData = browser.getTextFromTextBox(locatorData);
//		expectedData = expectedData.equalsIgnoreCase("{NULL}") || expectedData.equalsIgnoreCase("{EMPTY}") ? "" : expectedData ;
//		
//		if(!actualData.equals(expectedData)) {
//			throw new SoftAssert("<< Expected and Actual Data don't match >>"+
//			"\nActual Data : "+ actualData +
//			"\nExpected Data : " + expectedData);
//		}
//	}
//	
//	public void elementMustHaveText(String locatorData, String expectedData) throws Exception {
//		browser.waitForPresenceOfElement(locatorData);
//		browser.scrollIntoView(locatorData);
//		
//		String actualData = browser.getTextFromElement(locatorData);
//		expectedData = expectedData.equalsIgnoreCase("{NULL}") || expectedData.equalsIgnoreCase("{EMPTY}") ? "" : expectedData ;
//		
//		if(!this.normaliseString(actualData).equals(this.normaliseString(expectedData))) {
//			throw new Exception("<< Expected and Actual Data don't match >>"+
//								"\nActual Data   : "+ actualData +
//								"\nExpected Data : " + expectedData);
//		}
//	}
//	
//	public void isButtonEnabled(String locator) throws Exception {
//		boolean result = browser.isButtonEnabled(locator);
//		if(!result) {
//			throw new Exception("<< Button is not enabled >>");
//		}
//	}
//	
//	public void isButtonDisabled(String locator) throws Exception {
//		boolean result = browser.isButtonDisabled(locator);
//		if(!result) {
//			throw new Exception("<< Button is not disabled >>");
//		}
//	}
//	
//	public void elementShouldHaveText(String locatorData, String expectedData) throws SoftAssert {
//		browser.waitForPresenceOfElement(locatorData);
//		browser.scrollIntoView(locatorData);
//		
//		String actualData = browser.getTextFromElement(locatorData);
//		expectedData = expectedData.equalsIgnoreCase("{NULL}") || expectedData.equalsIgnoreCase("{EMPTY}") ? "" : expectedData ;
//		
//		if(!this.normaliseString(actualData).equals(this.normaliseString(expectedData))) {
//			throw new SoftAssert("<< Expected and Actual Data don't match >>"+
//								"\nActual Data   : "+ actualData +
//								"\nExpected Data : " + expectedData);
//		}
//	}
//	
//	public void elementShouldNotBePresent(String locator) throws SoftAssert {
//		boolean result = browser.isElementAbsentInDom(locator);
//		if(!result) {
//			throw new SoftAssert("<< Element is Present in DOM >>");
//		}
//	}
//	
//	public void pressKey(String keyName) {
//		browser.pressKeyboardKey(keyName);
//	}
//	
//	public void verifyWarningIsDisplayedForTheField(String locator) throws Exception {
//		String value = browser.getClassOfTheElement(locator);
//		boolean result = value.contains("customInvalid") || value.contains("mktoInvalid");
//		if(!result) {
//			throw new Exception("<< No Warning is displayed for the field >>");
//		}
//	}
//	
//	public void verifyNoWarningIsDisplayedForTheField(String locator) throws Exception {
//		String value = browser.getClassOfTheElement(locator);
//		boolean result = (value.contains("customInvalid") || value.contains("mktoInvalid"));
//		if(result) {
//			throw new Exception("<< Warning is displayed for the field >>");
//		}
//	}
	
	public void closeSession() {
		driver.closeSession();
	}
	
	public void closeBrowser() {
		driver.closeBrowser();
	}
	
	public byte[] takeScreenshot() {
		return driver.takeScreenshot();
	}
	
	public void closeApp() {
		driver.closeApp(appConfig.getPackageName());
	}

	
	public By createLocatorFromString(String loc) {
		By locator = null;
		
		LocatorInfo locatorInfo = new LocatorInfo(loc , " : ");
		locator = locatorInfo.getByLocator();
		
		return locator;
	}
}
