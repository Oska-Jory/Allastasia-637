package org.oracle.entity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.oracle.Launcher;
import org.oracle.data.json.user.JsonUserEncoder;
import org.oracle.entity.user.User;
import org.oracle.network.packet.PacketBuilder;
import org.oracle.utilities.Logger;
import org.oracle.utilities.Logger.Type;


/**
 * 
 * @author Oska - <format@allastasia.com>
 * Manages all active entities.
 */
public class EntityManager {

	
	/**
	 * A list of active Users.
	 */
	private static LinkedList<User> users = new LinkedList<User>();
	
	
	/**
	 * Returns the list of active users.
	 * @return
	 */
	public static LinkedList<User> getUsers() {
		return users;
	}
	
	
	/**
	 * Stores a new user into the list of active users'.
	 * @param user
	 */
	public static void storeUser(User user, boolean inWorld) {
		int code = 2;
		
		if (users.contains(user) /*|| Lobby.getUsers().contains(user) ||
				Launcher.getWorld().getUsers().contains(user)*/) {
			code = 7;
		}
		
		
		String filePath = "./data/json/user/" + user.getDefinition().getLoginName() + ".json";
		File file = new File(filePath);
	
		if (file.exists()) {
			try {
				JSONObject decodedObject = (JSONObject) new JSONParser().parse(new FileReader(filePath));
				
				String password = decodedObject.get("password").toString();
				
				if (!user.getDefinition().getPassword().equalsIgnoreCase(password)) 
					code = 3; 
			} catch (ParseException | IOException e) {
				e.printStackTrace();
			}
		} else {
			user.setDataEncoder(new JsonUserEncoder(user));
			try { 
				user.getDataEncoder().encode();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Logger.log(Type.INFO, "Username: " + user.getDefinition().getLoginName() + ", Login Code: " + code);
		new PacketBuilder().writeByte(code).write(user.getConnection().getChannel());
		users.add(user);
		
		if (inWorld) { 
			if (Lobby.getUsers().contains(user))
				Lobby.removeUser(user);
			Launcher.getWorld().storeUser(user);
		} else {
			if (Launcher.getWorld().getUsers().contains(user))
				Launcher.getWorld().removeUser(user);
			Lobby.storeUser(user);
		}
	}
	
	
	/**
	 * Removes a user from the list of active users.
	 * @param user
	 */
	public static void removeUser(User user) {
		if (users.contains(user)) {
			users.remove(user);
		}
	}
	
	
}
