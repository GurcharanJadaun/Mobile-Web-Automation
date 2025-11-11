package automationUtilities.mobileAutomation;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.common.collect.ImmutableMap;

import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;

public class IosMobileDriver extends MobileDriver { 
	
	IOSDriver driver;
	XCUITestOptions options;


	public IosMobileDriver(String appiumServerUrl) {
		super(appiumServerUrl);
	
		options = new XCUITestOptions()
				.setPlatformName("IOS")
				.setAutomationName("XCUITest")
				.setNoReset(false)
				.setUdid("DD1978B3-C6AA-4B1D-B7A1-7A0ED4ABFD88");
						
	//			.setUdid("DD1978B3-C6AA-4B1D-B7A1-7A0ED4ABFD88");
		
	//	options.setCapability("appium:chromedriverExecutableDir", "./chromedrivers");
		
	}

	@SuppressWarnings("deprecation")
	private void initiateAppiumServer() {
		try {
			Runtime.getRuntime().exec(
					  "xcrun simctl spawn booted rm -rf ~/Library/Developer/CoreSimulator/Devices/DD1978B3-C6AA-4B1D-B7A1-7A0ED4ABFD88/data/Library/Caches/com.apple.WebKit.*"
					);
			driver = new IOSDriver(new URL(appiumServerUrl), options);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			this.setDriver(driver);
		}
	}

	

	@Override
	protected void openBrowser(String browserName) {
		options.setCapability("browserName", browserName);
		options.setCapability("safari:resetSafari", true);
		options.setCapability("safari:openInPrivateBrowsing", true);
		options.setCapability("safari:reuseExistingBrowserSession", false);
		options.setCapability("safari:openInPrivateBrowsing", true);
		options.setCapability("safari:clearSystemFiles", true);
		options.setCapability("safari:resetSafari", true);
		options.setUseNewWDA(true);
		options.setWdaLocalPort(8100);
		options.setSkipLogCapture(true);
		
		this.initiateAppiumServer();
		System.out.println("Context : "+driver.getContext());
	}


	public void openApp(String packageName) {
		this.initiateAppiumServer();
		driver.activateApp(packageName);

	}

	@Override
	public void closeApp(String packageName) {
		driver.terminateApp(packageName);

	}

}
