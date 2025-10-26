package utilities;

import java.io.File;

public class CleanupManager {
	
	String dir,pathSep;
	
	public CleanupManager() {
		this.pathSep = File.separator.toString();
		this.dir = System.getProperty("user.dir");
	}

	private void cleanTestRunReports() {
		String directory = this.dir + this.pathSep + "reports" + this.pathSep + "Test Run Report"; 
	
		File file = new File(directory);
		try {
		for(String report : file.list()) {
			System.out.println("Deleting --> "+report);
			File reportFile = new File(file + this.pathSep + report);
			reportFile.delete();
		}}catch(Exception ex) {
			System.out.println("--No Test Run Reports to Delete--");
		}
	}
	
	private void cleanTestCompilerReports(){
		String directory = this.dir + this.pathSep + "reports" + this.pathSep + "Test Compiler Report"; 
		
		File file = new File(directory);
		try {
		for(String report : file.list()) {
			System.out.println("Deleting --> "+report);
			File reportFile = new File(file + this.pathSep + report);
			reportFile.delete();
		}}catch(Exception ex) {
			System.out.println("--No Compiler Reports to Delete--");
		}
	}
	
	public void flush() {
		this.cleanTestCompilerReports();
		this.cleanTestRunReports();
	}
	
	
}
