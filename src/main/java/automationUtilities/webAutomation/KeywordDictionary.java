package automationUtilities.webAutomation;

import java.text.Normalizer;
import java.util.Optional;

import org.json.JSONObject;
import org.openqa.selenium.By;

import TestExceptions.SoftAssert;
import deviceConfiguration.BrowserConfig;
import utilities.LocatorInfo;

public class KeywordDictionary  {
	BrowserKeeper browser;
	BrowserConfig browserConfig;
	JSONObject testUrlDetails;
	
	
	public KeywordDictionary(BrowserConfig browserConfig){
		this.browserConfig = browserConfig;
	}
	

	public void openBrowser(String param) {
		this.testUrlDetails = browserConfig.getTestUrlDetails();
		if(this.testUrlDetails.get("UserName").toString().length() > 0) {
			String userName = this.testUrlDetails.get("UserName").toString();
			String password = this.testUrlDetails.get("Password").toString();
			browser = new BrowserKeeper(userName,password);
		}else {
			browser = new BrowserKeeper();
		}
		
		if(param.equalsIgnoreCase("deviceConfig.browser")) {
			param = browserConfig.getBrowserName();
		}
		if(browserConfig.headlessBrowser()) {
			this.openHeadlessBrowser(param);
		}
		else {
			browser.initiateBrowser(param);
			
		}
	}
	
	public void openHeadlessBrowser(String param) {
		browser.initiateHeadlessBrowser(param);
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
		browser.openUrl(url);
	}
	
	public void click(String locatorData) {
		locatorData = this.getLocatorFromString(locatorData);
		browser.waitForPresenceOfElement(locatorData);
		browser.clickWebElement(locatorData);
	}
	
	public void triggerLazyLoadAndClick(String locatorData) throws InterruptedException {
		locatorData = this.getLocatorFromString(locatorData);
		browser.movePageToTackleLazyLoad();
		browser.waitForPresenceOfElement(locatorData);
		browser.checkVisibilityOfElement(locatorData);
		Thread.sleep(250);
		browser.clickWebElement(locatorData);
	}
	
	public void waitForPageToRender() {
		browser.waitForPageToRender();
		
	}
	
	public void enterTextInTextField(String locatorData, String text)throws InterruptedException  {
		locatorData = this.getLocatorFromString(locatorData);
		browser.waitForPresenceOfElement(locatorData);
		browser.scrollIntoView(locatorData);
		browser.checkVisibilityOfElement(locatorData);
		browser.clickWebElement(locatorData);
		Thread.sleep(250);
		browser.enterTextInTextBox(locatorData, text);
		Thread.sleep(250);
		
	}
	
	
	public void selectValueFromDropDown(String locatorData, String text) {
		locatorData = this.getLocatorFromString(locatorData);
		browser.waitForPresenceOfElement(locatorData);
		browser.scrollIntoView(locatorData);
		browser.selectValueFromDropDown(locatorData, text);
	}
	
	public void selectIndexFromDropDown(String locatorData, String index) {
		locatorData = this.getLocatorFromString(locatorData);
		int indexToBeSelected = Integer.parseInt(index);
		browser.waitForPresenceOfElement(locatorData);
		browser.scrollIntoView(locatorData);
		browser.selectIndexFromDropDown(locatorData, indexToBeSelected-1);
	}
	
	public void waitForPresenceAndClick(String locatorData) {
		locatorData = this.getLocatorFromString(locatorData);
		browser.waitForPresenceOfElement(locatorData);
		browser.scrollIntoView(locatorData);
		browser.clickWebElement(locatorData);
	}
	
	public void elementMustBeVisible(String locatorData) throws Exception{
		locatorData = this.getLocatorFromString(locatorData);
		browser.waitForPresenceOfElement(locatorData);
		browser.scrollIntoView(locatorData);
		boolean result = browser.checkVisibilityOfElement(locatorData);
		if(!result) {
			throw new Exception("Element Visibility Validation Failed");
		}
	}
	
	public void elementShouldBeVisible(String locatorData) throws SoftAssert{
		locatorData = this.getLocatorFromString(locatorData);
		browser.waitForPresenceOfElement(locatorData);
		browser.scrollIntoView(locatorData);
		boolean result = browser.checkVisibilityOfElement(locatorData);
		if(!result) {
			throw new SoftAssert("Element Visibility Validation Failed");
		}
	}
	
	public void textBoxMustHaveValue(String locatorData, String expectedData) throws Exception{
		locatorData = this.getLocatorFromString(locatorData);
		browser.waitForPresenceOfElement(locatorData);
		browser.scrollIntoView(locatorData);
		String actualData = browser.getTextFromTextBox(locatorData);
		
		expectedData = expectedData.equalsIgnoreCase("{NULL}") || expectedData.equalsIgnoreCase("{EMPTY}") ? "" : expectedData ;
		
		if(!actualData.equals(expectedData.trim())) {
			throw new Exception("<< Expected and Actual Data don't match >>"+
			"\nActual Data : "+ actualData +
			"\nExpected Data : " + expectedData);
		}
	}
	
	public void textBoxShouldHaveValue(String locatorData, String expectedData) throws SoftAssert{
		locatorData = this.getLocatorFromString(locatorData);
		browser.waitForPresenceOfElement(locatorData);
		browser.scrollIntoView(locatorData);
		
		String actualData = browser.getTextFromTextBox(locatorData);
		expectedData = expectedData.equalsIgnoreCase("{NULL}") || expectedData.equalsIgnoreCase("{EMPTY}") ? "" : expectedData ;
		
		if(!actualData.equals(expectedData)) {
			throw new SoftAssert("<< Expected and Actual Data don't match >>"+
			"\nActual Data : "+ actualData +
			"\nExpected Data : " + expectedData);
		}
	}
	
	public void elementMustHaveText(String locatorData, String expectedData) throws Exception {
		locatorData = this.getLocatorFromString(locatorData);
		browser.waitForPresenceOfElement(locatorData);
		browser.scrollIntoView(locatorData);
		
		String actualData = browser.getTextFromElement(locatorData);
		expectedData = expectedData.equalsIgnoreCase("{NULL}") || expectedData.equalsIgnoreCase("{EMPTY}") ? "" : expectedData ;
		
		if(!this.normaliseString(actualData).equals(this.normaliseString(expectedData))) {
			throw new Exception("<< Expected and Actual Data don't match >>"+
								"\nActual Data   : "+ actualData +
								"\nExpected Data : " + expectedData);
		}
	}
	
	public void isButtonEnabled(String locatorData) throws Exception {
		locatorData = this.getLocatorFromString(locatorData);
		boolean result = browser.isButtonEnabled(locatorData);
		if(!result) {
			throw new Exception("<< Button is not enabled >>");
		}
	}
	
	public void isButtonDisabled(String locatorData) throws Exception {
		locatorData = this.getLocatorFromString(locatorData);
		boolean result = browser.isButtonDisabled(locatorData);
		if(!result) {
			throw new Exception("<< Button is not disabled >>");
		}
	}
	
	public void elementShouldHaveText(String locatorData, String expectedData) throws SoftAssert {
		locatorData = this.getLocatorFromString(locatorData);
		browser.waitForPresenceOfElement(locatorData);
		browser.scrollIntoView(locatorData);
		
		String actualData = browser.getTextFromElement(locatorData);
		expectedData = expectedData.equalsIgnoreCase("{NULL}") || expectedData.equalsIgnoreCase("{EMPTY}") ? "" : expectedData ;
		
		if(!this.normaliseString(actualData).equals(this.normaliseString(expectedData))) {
			throw new SoftAssert("<< Expected and Actual Data don't match >>"+
								"\nActual Data   : "+ actualData +
								"\nExpected Data : " + expectedData);
		}
	}
	
	public void elementShouldNotBePresent(String locatorData) throws SoftAssert {
		locatorData = this.getLocatorFromString(locatorData);
		boolean result = browser.isElementAbsentInDom(locatorData);
		if(!result) {
			throw new SoftAssert("<< Element is Present in DOM >>");
		}
	}
	
	public void pressKey(String keyName) {
		browser.pressKeyboardKey(keyName);
	}
	
	public void verifyWarningIsDisplayedForTheField(String locatorData) throws Exception {
		locatorData = this.getLocatorFromString(locatorData);
		String value = browser.getClassOfTheElement(locatorData);
		boolean result = value.contains("customInvalid") || value.contains("mktoInvalid");
		if(!result) {
			throw new Exception("<< No Warning is displayed for the field >>");
		}
	}
	
	public void verifyNoWarningIsDisplayedForTheField(String locatorData) throws Exception {
		locatorData = this.getLocatorFromString(locatorData);
		String value = browser.getClassOfTheElement(locatorData);
		boolean result = (value.contains("customInvalid") || value.contains("mktoInvalid"));
		if(result) {
			throw new Exception("<< Warning is displayed for the field >>");
		}
	}
	
	public void closeActivePage() {
		browser.closePage();
	}
	
	public void closeBrowser() {
		browser.closeBrowserSession();
	}
	public void closeSession() {
		browser.closeBrowserSession();
	}
	
	public byte[] takeScreenshot() {
		return browser.takeScreenshot();
	}
// helper functions
	
	 String normaliseString(String text) {
		 try{
				text = Normalizer.normalize(text, Normalizer.Form.NFC)
	             .replaceAll("[^\\p{ASCII}]", " ");
				 text = text.trim();
			 }catch(Exception ex) {
				 text = "";
			 }
		 return text;
	}
	 
	  String getLocatorFromString(String loc) {
			String locator = "";
			
			LocatorInfo locatorInfo = new LocatorInfo(loc , " : ");
			locator = locatorInfo.getLocatorValue();
			
			return locator;
		}
	
}
