package automationUtilities.mobileAutomation;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

public class AndroidMobileDriver extends MobileDriver {

	AndroidDriver driver;
	UiAutomator2Options options;

	public AndroidMobileDriver(String appiumServerUrl) {
		super(appiumServerUrl);
		options = new UiAutomator2Options()
				.setPlatformName("Android")
				.setAutomationName("UiAutomator2")
				.setNoReset(false);
		options.setCapability("appium:chromedriverExecutableDir", "./chromedrivers");
	}

	public void openBrowser(String browserName) {
		options.setCapability("browserName", browserName);
		this.initiateAppiumServer();
		System.out.println(driver.getContext());

	}

	public void closeBrowser() {
		driver.close();
	}

	public void openApp(String packageName) {
		this.initiateAppiumServer();
		driver.activateApp(packageName);

	}

	public void closeApp(String packageName) {
		driver.terminateApp(packageName);

	}

	public void closeSession() {
		driver.quit();
	}
	
	private void initiateAppiumServer() {
		try {
			driver = new AndroidDriver(new URL(appiumServerUrl), options);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void openUrl(String targetUrl) {
		driver.get(targetUrl);
	}

	public byte[] takeScreenshot() {
		byte[] screenShot = null;
		if (driver != null) {
			screenShot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
		}
		return screenShot;
	}

	@Override
	protected void clickWebElement(By locatorData) {
		WebElement ele = this.getWebElement(driver, locatorData);
		driver.executeScript("arguments[0].click();", ele);
		//ele.click();
	}

	@Override
	protected void scrollScreen(int pixels) {
	Dimension screenSize = driver.manage().window().getSize();
		int y = screenSize.getHeight()/2;
		int x = screenSize.getWidth()/2;
		ScreenCoordinates middle = new ScreenCoordinates(x, y);
		ScreenCoordinates end = new ScreenCoordinates(x, y-pixels);
		
		this.scrollScreen(driver, middle, end);
	}

	@Override
	protected void scrollIntoView(By locator) {
		WebElement element = this.getWebElement(driver, locator);
		driver.executeScript("arguments[0].scrollIntoView(true);", element);
	}

	@Override
	protected boolean checkVisibilityOfElement(By locator) {
		WebElement element = this.getWebElement(driver, locator);
		return element.isDisplayed();
	}

	@Override
	protected void waitForPageToRender() {
		long start = System.currentTimeMillis();
        long lastActivity = start;
        long idleMs = 100;
        int timeoutSec = 7;
        
		while (System.currentTimeMillis() - start < timeoutSec * 1000) {
            // Get all in-flight network requests
            List<?> entries = (List<?>) ((JavascriptExecutor) driver).executeScript(
                "return performance.getEntriesByType('resource').filter(r => r.responseEnd === 0);"
            );

            if (entries.isEmpty()) {
                
				if (System.currentTimeMillis() - lastActivity >= idleMs) {
                    System.out.println("Network idle after " + (System.currentTimeMillis() - start) + "ms");
                    return;
                }
            } else {
                lastActivity = System.currentTimeMillis();
            }

            try { Thread.sleep(100); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
        
    }

	@Override
	protected void enterTextInTextBox(By locator, String text) {
		
		WebElement element = this.getWebElement(driver, locator);
		element.sendKeys(text);
		
	}

	@Override
	protected String getTextFromTextBox(By locator) {
		String data = "";
		WebElement ele = this.getWebElement(driver, locator);
		data = driver.executeScript("return arguments[0].value", ele).toString();
		return data;
	}

	@Override
	protected void selectValueFromDropDown(By locator, String text) {
		Select dropDown = this.getDropDown(driver, locator);
		dropDown.selectByValue(text);
	}

	@Override
	protected boolean isButtonEnabled(By locator) {
		WebElement ele = this.getWebElement(driver, locator);
		return ele.isEnabled();
	}
}
