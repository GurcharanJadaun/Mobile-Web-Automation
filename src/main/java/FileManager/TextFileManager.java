package FileManager;

import java.io.File;
import java.io.IOException;

public class TextFileManager {
	File file;
	String dir,pathSep;
	
	public TextFileManager(){
		this.dir = System.getProperty("user.dir");
	}
	
	public TextFileManager(String directory){
		this.pathSep = "\\";
		this.dir = System.getProperty("user.dir"+this.pathSep+directory);
	}
	
	public void createNewFile(String fileName) {
		file = new File(dir + pathSep + fileName);
		try {
			file.createNewFile();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	
}
