Purpose : Learn framework building from scratch
Requirement,Version / Notes
Java JDK,17 or higher
Maven,3.6+
Git,For cloning and version control
IDE (Optional),IntelliJ IDEA / Eclipse (with Maven support)
Appium Server up and running to run tests locally on Mobile devices.
Excel Support,Apache POI libraries are included via Maven (no manual setup needed)

Follow these steps to set up and run your automation tests:

1. Keyword Dictionary
Locate or create the keyword dictionary at:
src/main/resources/CompilerDictionary/KeywordDictionary/


2. Locator Dictionary
Create your locator dictionary in:
src/main/resources/CompilerDictionary/LocatorDictionary/


3. Feature Files
Write your Gherkin feature files and place them in:
src/main/resources/FeatureFiles/


4. Test Plan Configuration
Define your test execution plan in:
TestPlan.xlsx

You can target specific tests using tags (e.g., @Smoke, @Regression).


5. Test Environment URLs
Configure base URLs in:
TestURLConfig.json

Use {BaseURL} in your feature files to reference configured URLs dynamically.



6. Run Tests via Maven
Execute tests using the following command:
bashmvn clean install exec:java@run-tests \
  -DtestEnv=<Test URL config name> \
  -DdeviceConfig=<Device Configuration name> \
  -DtestCaseTags="<Tags from feature files (default: @Regression)>" \
  -DtestPlanTags="<Tags to filter test plan entries>" \
  -DtargetDevice="<Tag to target web or mobile tests>"
Example:
bash mvn clean install exec:java@run-tests -DtestEnv=staging -DdeviceConfig=chrome-desktop -DtestCaseTags="@Smoke" -DtestPlanTags="@Priority1" -DtargetDevice="Mobile"

Tips:

All default test cases are tagged with @Regression.
Ensure TestPlan.xlsx references valid feature file names and tags.
For mobile Configuration is done for Chrome browser (Android devices) and safari for iOS devices.
