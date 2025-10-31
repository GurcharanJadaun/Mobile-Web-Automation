package utilities;

import automationUtilities.mobileAutomation.MobileKeywordDictionary;
import automationUtilities.webAutomation.KeywordDictionary;
import deviceConfiguration.AppConfig;
import deviceConfiguration.BrowserConfig;

public class KeywordSet {
	KeywordDictionary webDictionary;
	MobileKeywordDictionary mobileDictionary;
	
	public KeywordSet(KeywordDictionary dictionary) {
		this.webDictionary = dictionary;
	}
	
	public KeywordSet(MobileKeywordDictionary dictionary) {
		this.mobileDictionary = dictionary;
	}
		
	public <T> T getDictionary(Class<T> type) {
		if(type == KeywordDictionary.class)
	        return type.cast(this.webDictionary);
		else {
			return type.cast(this.mobileDictionary);
		}
	    
	}

}
