package automationUtilities.mobileAutomation;

import java.time.Duration;
import java.util.Arrays;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.appium.java_client.AppiumDriver;

public abstract class MobileDriver {

	String platformName, automationName, appiumServerUrl;

	@SuppressWarnings("deprecation")
	public MobileDriver(String appiumServerUrl) {
		this.appiumServerUrl = appiumServerUrl;
	}

	protected abstract void openUrl(String targetUrl);

	protected abstract byte[] takeScreenshot();

	protected abstract void closeBrowser();

	protected abstract void openBrowser(String browserName);

	protected abstract void openApp(String packageName);

	protected abstract void closeApp(String packageName);

	protected abstract void closeSession();

	protected abstract void clickWebElement(By locatorData);
	
	protected abstract void scrollScreen(int pixel);
	
	protected abstract void scrollIntoView(By locator);
	
	protected abstract boolean checkVisibilityOfElement(By locator);
	
	protected abstract void waitForPageToRender();
	
	protected abstract void enterTextInTextBox(By locator, String text);
	
	protected abstract String getTextFromTextBox(By locator);
	
	protected abstract void selectValueFromDropDown(By locator, String text);
	
	protected abstract boolean isButtonEnabled(By locator);

	private void waitForPresenceOfElement(AppiumDriver driver, By loc) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
		wait.until(ExpectedConditions.presenceOfElementLocated(loc));
	}
	
	private void waitForVisibilityOfElement(AppiumDriver driver, By loc) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
		wait.until(ExpectedConditions.presenceOfElementLocated(loc));
		wait.until(ExpectedConditions.visibilityOf(driver.findElement(loc)));
	}
	
	protected void waitForButtonToBeEnabled(AppiumDriver driver, By loc) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
		wait.until(ExpectedConditions.presenceOfElementLocated(loc));
		wait.until(ExpectedConditions.elementToBeClickable(loc));
	}

	protected WebElement getWebElement(AppiumDriver driver, By loc) {
		this.waitForVisibilityOfElement(driver, loc);
		WebElement element = driver.findElement(loc);
		return element;
	}
	
	protected Select getDropDown(AppiumDriver driver, By loc) {
		this.waitForVisibilityOfElement(driver, loc);
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
