package FileManager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonFileManager {
	String dir, pathSep;
	 
	public JsonFileManager() {
		pathSep = File.separator;
		this.dir = System.getProperty("user.dir");
	}
	
	/**
	 * returns Iterator<JsonNode> from the json file read. Will be used against JSON array files.
	 * Doesn't need ".json" extension in file name. 
	 * @param "filename"
	 */	
	public Iterator<JsonNode> getItemsFromJson(String fileName) throws IOException {
		 ObjectMapper objectMapper = new ObjectMapper();
	        JsonNode jsonNode = objectMapper.readTree(new File(fileName+".json"));
	        Iterator<JsonNode> jsonArray = jsonNode.elements();
	    return jsonArray;
	}
	
	/**
	 * returns JsonNode from the json file read. Will be used against JSON files.
	 * Doesn't need ".json" extension in file name. 
	 * @param "filename"
	 */
	public JsonNode getItemFromJson(String fileName)throws IOException {
		 ObjectMapper objectMapper = new ObjectMapper();
	        JsonNode jsonNode = objectMapper.readTree(new File(fileName+".json"));
	    return jsonNode;
	}
	
}
