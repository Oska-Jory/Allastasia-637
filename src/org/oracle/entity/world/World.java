package org.oracle.entity.world;

import java.util.LinkedList;

import org.oracle.entity.user.User;

/**
 * 
 * @author Oska - <format@allastasia.com>
 * Represents the game world.
 */
public class World {
	
	
	/**
	 * The Identification number of the world.
	 * Gives the world a unique id, to distinguish it,
	 * from other created worlds.
	 */
	private int id;
	
	
	/**
	 * The definition of the world. Gives the world
	 * its very own unique information, used to distinguish
	 * itself from other worlds.
	 */
	private WorldDefinition definition;
	
	
	/**
	 * Creates a new world with its own unique
	 * id.
	 * @param id
	 */
	public World(int id, WorldDefinition definition) {
		this.id = id;
	}
	
	
	/**
	 * Returns the identification number
	 * of the world.
	 * @return
	 */
	public int getId() {
		return id;
	}
	
	
	/**
	 * Returns the worlds definition.
	 * @return
	 */
	public WorldDefinition getDefinition() {
		return definition;
	}
	
	
	public World load() {
		return this;
	}
	
	
	/**
	 * A list of active Users.
	 */
	private LinkedList<User> users = new LinkedList<User>();
	
	
	/**
	 * Returns the list of active users.
	 * @return
	 */
	public LinkedList<User> getUsers() {
		return users;
	}
	
	
	/**
	 * Stores a new user into the list of active users'.
	 * @param user
	 */
	public void storeUser(User user) {
		if (users.contains(user)) {
			return;
		}
		users.add(user);
	}
	
	
	/**
	 * Removes a user from the list of active users.
	 * @param user
	 */
	public void removeUser(User user) {
		if (users.contains(user)) {
			users.remove(user);
		}
	}
}
