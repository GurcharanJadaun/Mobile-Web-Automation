package automationUtilities.mobileAutomation;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;

public abstract class MobileDriver {

	String platformName, automationName, appiumServerUrl;
	private AppiumDriver driver;

	@SuppressWarnings("deprecation")
	public MobileDriver(String appiumServerUrl) {
		this.appiumServerUrl = appiumServerUrl;
	}

	public void setDriver(AndroidDriver androidDriver) {
		this.driver = androidDriver;
	}
	
	public void setDriver(IOSDriver iosDriver) {
		this.driver = iosDriver;
	}
	
	public void openUrl(String targetUrl) {
		driver.get(targetUrl);
	}
	
	public void closeBrowser() {
		try {
	//	driver.close();
		driver.executeScript("window.close()");
}
		catch(Exception ex) {
			System.out.println(" <<Check this out>> ");
			ex.printStackTrace();
		}
	}
	
	public void closeSession() {
		driver.quit();
	}
	
	public byte[] takeScreenshot() {
		byte[] screenShot = null;
		if (driver != null) {
			screenShot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
		}
		return screenShot;
	}
	
	protected void scrollScreen(int pixels) {
	Dimension screenSize = driver.manage().window().getSize();
		int y = screenSize.getHeight()/2;
		int x = screenSize.getWidth()/2;
		ScreenCoordinates middle = new ScreenCoordinates(x, y);
		ScreenCoordinates end = new ScreenCoordinates(x, y-pixels);
		
		this.scrollScreen(driver, middle, end);
	}

	protected abstract void openBrowser(String browserName);

	protected abstract void openApp(String packageName);

	protected abstract void closeApp(String packageName);

	protected void clickWebElement(By locatorData) {
		WebElement ele = this.getWebElement(locatorData);
		driver.executeScript("arguments[0].click();", ele);
	}
	
	protected void scrollIntoView(By locator) {
		WebElement element = this.getWebElement(locator);
		driver.executeScript("arguments[0].scrollIntoView(true);", element);
	}
	
	protected boolean checkVisibilityOfElement(By locator) {
		WebElement element = this.getWebElement(locator);
		return element.isDisplayed();
	}

	protected void enterTextInTextBox(By locator, String text) {
		
		WebElement element = this.getWebElement(locator);
		element.sendKeys(text);
		
	}
	
	protected String getTextFromTextBox(By locator) {
		String data = "";
		WebElement ele = this.getWebElement( locator);
		data = driver.executeScript("return arguments[0].value", ele).toString();
		return data;
	}
	
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
	
	protected void selectValueFromDropDown(By locator, String text) {
		Select dropDown = this.getDropDown(locator);
		dropDown.selectByValue(text);
	}

	protected boolean isButtonEnabled(By locator) {
		WebElement ele = this.getWebElement(locator);
		return ele.isEnabled();
	}

	private void waitForPresenceOfElement(By loc) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
		wait.until(ExpectedConditions.presenceOfElementLocated(loc));
	}
	
	private void waitForVisibilityOfElement(By loc) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
		wait.until(ExpectedConditions.presenceOfElementLocated(loc));
		wait.until(ExpectedConditions.visibilityOf(driver.findElement(loc)));
	}
	
	protected void waitForButtonToBeEnabled(By loc) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
		wait.until(ExpectedConditions.presenceOfElementLocated(loc));
		wait.until(ExpectedConditions.elementToBeClickable(loc));
	}

	protected WebElement getWebElement( By loc) {
		this.waitForVisibilityOfElement( loc);
		WebElement element = driver.findElement(loc);
		return element;
	}
	
	protected Select getDropDown( By loc) {
		this.waitForVisibilityOfElement( loc);
		WebElement element = driver.findElement(loc);
		
		return new Select(element);
	}

	protected void scrollScreen(AppiumDriver driver, ScreenCoordinates start, ScreenCoordinates end) {
		PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
		Sequence swipe = new Sequence(finger, 1);

		swipe.addAction(finger.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), start.getX(),
				start.getY()));
		swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
		swipe.addAction(
				finger.createPointerMove(Duration.ofMillis(1000), PointerInput.Origin.viewport(), end.x, end.y));
		swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
		
		driver.perform(Arrays.asList(swipe));
	}
	

}
