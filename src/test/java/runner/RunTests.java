package runner;

import java.time.Duration;
import java.time.Instant;
import TestReports.ReportTestEventManager;

public class RunTests {
	ReportTestEventManager[] report;

	public static void main(String args[]) {
		Instant start = Instant.now();
		String targetDevice = System.getProperty("targetDevice", "Web");
		
		if(targetDevice.equalsIgnoreCase("Web")) {
			RunWebTests webTests = new RunWebTests();
			webTests.run();
		}else {
			RunMobileTests mobileTests = new RunMobileTests();
			mobileTests.run();
		}
		
		
		Instant end = Instant.now();
		Duration timeElapsed = Duration.between(start, end);

		long seconds = timeElapsed.toSeconds();
		double minutes = timeElapsed.toMinutes();
		System.out.println("Time taken: " + seconds + " seconds (" + minutes + " minutes)");

		System.exit(0);
	}
}
