package utilities;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.model.Media;

import automationUtilities.webAutomation.KeywordDictionary;
import automationUtilities.mobileAutomation.MobileKeywordDictionary;
import deviceConfiguration.AppConfig;
import deviceConfiguration.BrowserConfig;
import testManager.TestStatus;

public class ExecuteStep {
	Class<?> keywordDictionaryClass;
	KeywordSet keywordSet;
	public TestStatus result;
	public String reason;
	private MediaEntityBuilder screenshotBuilder;
	public Media screenshot;
	BrowserConfig browserConfig;
	AppConfig appConfig;

	public ExecuteStep(BrowserConfig browserConfig) {
		this.browserConfig = browserConfig;
		KeywordDictionary dictionary = new KeywordDictionary(browserConfig);
		keywordDictionaryClass = dictionary.getClass();
		keywordSet = new KeywordSet(dictionary);
		this.flush();
	}
	
	public ExecuteStep(AppConfig appConfig) {
		this.appConfig = appConfig;
		MobileKeywordDictionary dictionary = new MobileKeywordDictionary(appConfig);
		keywordDictionaryClass = dictionary.getClass();
		keywordSet = new KeywordSet(dictionary);
		this.flush();
	}

	public void flush() {
		result = TestStatus.PASSED;
		reason = "";
		screenshot = null;
		
	}

	public void executeStep(String methodName, String locator, String testData) {
		Method method;
		try {
			method = keywordDictionaryClass.getMethod(methodName, String.class, String.class);
			try {
				method.invoke(keywordSet.getDictionary(keywordDictionaryClass), locator, testData);
			} catch (IllegalAccessException | IllegalArgumentException e) {
				// add logs here for failing to execute located method from Keyword class.
				this.setResultForException(e);
			} catch (InvocationTargetException e) {
				// add logs here for exception to execute the
				this.setResultForException(e);
			} catch (Exception e) {
				// add logs here for exception to execute the
				this.setResultForException(e);
			}
			// Thread.sleep(250);
		} catch (Exception e) {
			// add logs here for failing to locate method in Keyword Class.
			this.setResultForException(e);
		}
	}

	/**
	 * Execute functions with one Parameter. Expects user to determine parameter
	 * passed is correct
	 * 
	 * @param "oneParameter" can either be locator or be test data depending on the
	 *                       function
	 */
	public void executeStep(String methodName, String oneParameter) {
		Method method;
		try {
			method = keywordDictionaryClass.getMethod(methodName, String.class);
			try {
				method.invoke(keywordSet.getDictionary(keywordDictionaryClass), oneParameter);
			} catch (IllegalAccessException | IllegalArgumentException e) {
				// add logs here for failing to execute located method from Keyword class.
				this.setResultForException(e);
			} catch (InvocationTargetException e) {
				// add logs here for exception to execute the
				this.setResultForException(e);
			}
			// Thread.sleep(250);
		} catch (NoSuchMethodException | SecurityException e) {
			// add logs here for failing to locate method in Keyword Class.
			this.setResultForException(e);
		} catch (Exception e) {
			// add logs here for exception to execute the
			this.setResultForException(e);
		}
	}

	public void executeStep(String methodName) {
		Method method;
		try {
			method = keywordDictionaryClass.getMethod(methodName);
			try {
				method.invoke(keywordSet.getDictionary(keywordDictionaryClass));
			} catch (IllegalAccessException | IllegalArgumentException e) {
				// add logs here for failing to execute located method from Keyword class.
				this.setResultForException(e);
			} catch (InvocationTargetException e) {
				// add logs here for exception to execute the
				this.setResultForException(e);
			}
			// Thread.sleep(250);
		} catch (NoSuchMethodException | SecurityException e) {
			// add logs here for failing to locate method in Keyword Class.
			this.setResultForException(e);
		} catch (Exception e) {
			// add logs here for exception to execute the
			this.setResultForException(e);
		}
	}
	
	public BrowserConfig getBrowserConfig() {
		return this.browserConfig;
	}
	
	public AppConfig getAppConfig() {
		return this.appConfig;
		}

	@SuppressWarnings("static-access")
	void takeScreenshot() {
		try {
			byte[] screenshotBytes;
			
			if(keywordDictionaryClass == KeywordDictionary.class) {
				KeywordDictionary obj = (KeywordDictionary) this.keywordSet.getDictionary(keywordDictionaryClass);
				screenshotBytes = obj.takeScreenshot();
					this.screenshot = this.screenshotBuilder
							.createScreenCaptureFromBase64String(java.util.Base64.getEncoder().encodeToString(screenshotBytes))
							.build();
			}else if(keywordDictionaryClass == MobileKeywordDictionary.class) {
				MobileKeywordDictionary obj = (MobileKeywordDictionary) this.keywordSet.getDictionary(keywordDictionaryClass);
				screenshotBytes = obj.takeScreenshot();
					this.screenshot = this.screenshotBuilder
							.createScreenCaptureFromBase64String(java.util.Base64.getEncoder().encodeToString(screenshotBytes))
							.build();
			}
			
			
		} catch (Exception ex) {
			System.out.print("<<<<<<< Screenshot Not Taken >>>>>");
			this.screenshot = null;
		}
	}

	void setResultForException(Exception ex) {

		this.takeScreenshot();

		if (ex.getCause() == null) {
			this.reason = ex.toString();
			this.result = TestStatus.STOP_EXECUTION;
		} else {
			this.reason = ex.getCause().toString();
			if (reason.contains("SoftAssert")) {
				this.result = TestStatus.FAILED;
			} else {
				result = TestStatus.STOP_EXECUTION;
			}
		}
		ex.printStackTrace();
	}
}
