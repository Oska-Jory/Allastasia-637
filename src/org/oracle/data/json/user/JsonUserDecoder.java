package org.oracle.data.json.user;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.oracle.data.security.DataDecryption;
import org.oracle.entity.user.UserDefinition;
import org.oracle.utilities.Misc;

/**
 *
 * @author Oska <format@allastasia.com>
 * Decodes data stored in a json file format.
 */
public class JsonUserDecoder {
	
	
	/**
	 * The path to the file being decoded.
	 */
	private String filePath;
	
	
	/**
	 * The new definition being created with the
	 * decoded data.
	 */
	private UserDefinition definition;
	
	
	/**
	 * An object storing the recorded data from the Json File.
	 */
	private JSONObject storedObject;
	
	
	/**
	 * Constructs a new Json Decoder with a given loginName to work with.
	 * @param loginName
	 */
	public JsonUserDecoder(String fileName) {
		this.filePath = "./data/json/user/" + Misc.formatPlayerNameForDisplay(fileName) + ".json";
	}
	
	
	
	/**
	 * Decodes and stores data into a new User Definition ready to be utilized.
	 */
	public void decode() {
		File file = new File(filePath);
		
		if (!file.exists()) {
			System.out.println("File does not exist!");
			return;
		}
		
		try {
			JSONObject object = (JSONObject) new JSONParser().parse(new FileReader(filePath));
			storedObject = object;
			
			//DataDecryption decryptor = new DataDecryption(null);
			
			definition = new UserDefinition(object.get("loginName").toString(), object.get("password").toString(), (int) object.get("permissionLevel"));
			definition.setDisplayName(object.get("displayName").toString());
			definition.setEmail(object.get("email").toString());
			definition.setLastConnectedIp(object.get("lastIp").toString());
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Returns the newly decoded User Definitions.
	 * @return
	 */
	public UserDefinition getDefinition() {
		return definition;
	}	
	
	
	/**
	 * Returns an array of stored data.
	 * @return
	 */
	public JSONObject getObject() {
		return storedObject;
	}
}

