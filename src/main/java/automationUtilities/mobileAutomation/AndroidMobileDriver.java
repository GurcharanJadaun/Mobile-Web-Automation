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

	public void openApp(String packageName) {
		this.initiateAppiumServer();
		driver.activateApp(packageName);

	}
	
	public void closeApp(String packageName) {
		driver.terminateApp(packageName);

	}
	
	private void initiateAppiumServer() {
		try {
			driver = new AndroidDriver(new URL(appiumServerUrl), options);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			this.setDriver(driver);
		}
	}
}
