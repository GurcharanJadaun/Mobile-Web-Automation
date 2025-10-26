package TestReports;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.ViewName;

import testManager.TestCase;
import testManager.TestStep;
import testManager.TestSuite;

public class TestReports {
	String baseDir;
	String pathSep;
	List<TestSuite> listOfSuites;
	
	public TestReports(){
		this.pathSep = File.separator.toString();
		baseDir = System.getProperty("user.dir")+this.pathSep+"reports" ;
	}
	
	public TestReports(List<TestSuite> listOfSuites){
		this.pathSep = File.separator.toString();
		baseDir = System.getProperty("user.dir")+this.pathSep+"reports" ;
		this.listOfSuites = listOfSuites;
	}
	
	public TestReports(TestSuite suite){
		this.pathSep = File.separator.toString();
		baseDir = System.getProperty("user.dir")+this.pathSep+"reports" ;
		listOfSuites.add(suite);
	}
	
    public void createCompilerReport() {
    	String subDir =  this.baseDir + this.pathSep + "Test Compiler Report"+this.pathSep;
    	String reportName =  "Compiler Report "+LocalDate.now()+" "+
    						LocalTime.now().format(DateTimeFormatter.ofPattern("HHmmss"))+".html";
    	
    	this.createReport(subDir +reportName);
    	this.postProcessorForReport(subDir +reportName);
    }
    
    public void createTestRunReport() {
    	String reportName = "Test Run Report "+LocalDate.now()+" "+LocalTime.now().format(DateTimeFormatter.ofPattern("HHmmss"))+".html";
    	String subDir = this.baseDir +  this.pathSep + "Test Run Report"+this.pathSep;   
    	
    	this.createReport(subDir + reportName);
    }
    
    void createReport(String reportDir) {
    	 // 1. Create reporter (HTML file)
        ExtentSparkReporter spark = new ExtentSparkReporter(reportDir);
        spark.config().setReportName("CouchBase Test Compiler Report");
        spark.config().setReportName("CouchBase Test Compiler Report");
        spark.viewConfigurer().viewOrder().as(new ViewName[] {ViewName.DASHBOARD,ViewName.TEST}).apply();
      
        
        // 2. Create ExtentReports and attach reporter
        ExtentReports extent = new ExtentReports();
        extent.attachReporter(spark);
        // 3. Create test entries
        for(TestSuite suite : listOfSuites) {
        	 ExtentTest suiteNode = extent.createTest(suite.getSuiteName());
        	 for(TestCase testCase: suite.getTestCases()) {
        		 ExtentTest caseNode = suiteNode.createNode(testCase.getTestCaseId());
        		 for(TestStep step : testCase.getSteps()) {
        			 ExtentTest caseStep = caseNode.createNode(step.getStepDescription());
        			 if(step.getResult().isFailed()) {
        				 caseStep.fail("Step : "+step.getTestStepReason());
        			 }else {
        				 caseStep.pass("Step : "+step.getTestStepReason());
        			 }
        		 }
        	 }
        }
        
        extent.flush();
        
		
    }
    
    public void createTestReport(ExtentReports extent, Optional<String> browserName) {
    	
    	String suffix = browserName.orElse("");
    			
    	String reportName = suffix + "Test Run Report "+LocalDate.now()+" "+LocalTime.now().format(DateTimeFormatter.ofPattern("HHmmss"))+".html";
    	String subDir = this.baseDir +  this.pathSep + "Test Run Report"+this.pathSep;   
    	
    	
    	  ExtentSparkReporter spark = new ExtentSparkReporter(subDir +reportName);
          spark.config().setReportName("CouchBase Test Run Report");
          spark.config().setReportName("CouchBase Test Run Report");
          spark.viewConfigurer().viewOrder().as(new ViewName[] {ViewName.DASHBOARD,ViewName.TEST}).apply();
        
          extent.attachReporter(spark);
          extent.flush();
          
          this.postProcessorForReport(subDir +reportName);
    }
    
    
    void postProcessorForReport(String reportDir) {
    	try {
			String html;
			html = Files.readString(Path.of(reportDir), StandardCharsets.UTF_8);
			// Replace "Tests" -> "Test Suites"
	    	
			html = html.replaceAll(">(\\s*)Tests(\\s*)<", ">Test Suites<");

		     // Replace "Steps" -> "Test Cases"
		     html = html.replaceAll(">(\\s*)Steps(\\s*)<", ">Test Cases<");
		     
		     html = html.replaceAll("Tests Passed", "Test Suites Passed");

		        // 2. Rename Tests Failed → Test Suites Failed
		        html = html.replaceAll("Tests Failed", "Test Suites Failed");

		        // 3. Insert heading before 3rd chart (nth occurrence of div.chart)
		        html = html.replaceAll("<h6 class=\"card-title\"></h6>", 
		            "<h6 class=\"card-title\">Test Steps</h6>");
		        
		        html = html.replaceAll("</b> tests", "</b> Test Suites");

		        html = html.replaceAll("</b> steps", "</b> Test Cases");
		        
		        html = html.replaceAll("</b> passed", "</b> Steps Passed");
		        
		        html = html.replaceAll("</b> failed", "</b> Steps Failed");
		        
		        html = html.replaceAll("nav-link active", "nav-link");
		        
		        html = html.replaceAll("class=\"nav-link\" id=\"nav-dashboard\"", "class=\"nav-link active\" id=\"nav-dashboard\"");

		     Files.writeString(Path.of(reportDir), html, StandardCharsets.UTF_8);
		        System.out.println("Report generated: " + reportDir);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
    
}
