package org.oracle.data.json.user;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.json.simple.JSONObject;
import org.oracle.data.security.DataEncryption;
import org.oracle.entity.user.User;
import org.oracle.utilities.Misc;


/**
 *
 * @author Oska <format@allastasia.com>
 * Enables objects to be encoded and stored into a
 * JavaScript Object Notation format.
 */
public class JsonUserEncoder {
	
	
	/**
	 * The object being decoded.
	 */
	private User user;
	
	
	/**
	 * Constructs a new Json Encoder.
	 * @param user
	 */
	public JsonUserEncoder(User user) {
		this.user = user;
	}
	
	
	/**
	 * Encodes data into an encrypted json file.
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public void encode() throws IOException {
		
		File file = new File("./data/json/user/" + user.getDefinition().getLoginName() + ".json");
		
		if (!file.exists())
			file.createNewFile();
		
		Writer writer = new FileWriter(file);
		
		JSONObject object = new JSONObject();
		
		//DataEncryption encryptor = new DataEncryption(null);
		
		object.put("loginName", user.getDefinition().getLoginName());
		object.put("displayName", user.getDefinition().getDisplayName());
		object.put("email", user.getDefinition().getEmail());
		object.put("password", user.getDefinition().getPassword());
		object.put("permissionLevel", user.getDefinition().getPermissionLevel());
		object.put("lastIp", user.getDefinition().getLastConnectedIp());
		
		writer.write(object.toString());
		
		writer.close();
	}
}
