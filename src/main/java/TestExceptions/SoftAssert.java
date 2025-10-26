package TestExceptions;

public class SoftAssert extends Exception{
	public SoftAssert(String message)  {
		super(message);
	}
	
	public SoftAssert(String message, Throwable cause)  {
		super(message, cause);
	}
}
