package org.oracle.entity;

import java.util.LinkedList;

import org.oracle.entity.user.User;
import org.oracle.network.ActionSender;

/**
 * @author Oska - <format@allastasia.com>
 * Represents the lobby user's are waiting in.
 */
public class Lobby {
	
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
	public static void storeUser(User user) {
		if (users.contains(user)) {
			return;
		}
		users.add(user);
		ActionSender.sendLobbyResponse(user);
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
