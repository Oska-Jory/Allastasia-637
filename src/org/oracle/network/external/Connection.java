package org.oracle.network.external;

import org.jboss.netty.channel.Channel;
import org.oracle.entity.user.User;


/**
 * 
 * @author Oska - <format@allastasia.com>
 * Represents an external connection.
 */
public class Connection {
	
	
	/**
	 * The channel of the connection.
	 */
	private Channel channel;
	
	
	/**
	 * The user of the connection.
	 */
	private User user;
	
	
	/**
	 * Is the connection in the lobby or world? 
	 */
	private boolean inLobby = false, inWorld = false;
	
	
	/**
	 * The display mode of the connection
	 */
	private int displayMode;
	
	
	/**
	 * Creates a new connection, with using the clients channel.
	 * @param channel
	 */
	public Connection(Channel channel) {
		this.channel = channel;
	}
	
	
	/**
	 * Returns the connection's channel.
	 * @return
	 */
	public Channel getChannel() {
		return channel;
	}
	
	
	/**
	 * Sets the connection's channel.
	 * @param channel
	 */
	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	
	
	/**
	 * Returns the User of the connection.
	 * @return
	 */
	public User getUser() {
		return user;
	}
	
	
	/**
	 * Sets the user of the connection.
	 * @param user
	 */
	public void setUser(User user) {
		this.user = user;
	}
	
	
	/**
	 * Is the connection in the lobby?
	 * @return
	 */
	public boolean inLobby() {
		return inLobby;
	}
	
	
	/**
	 * Sets if the connection is in the lobby or not.
	 * @param lobby
	 */
	public void setInLobby(boolean lobby) {
		this.inLobby = lobby;
		this.inWorld = lobby ? false : true;
	}
	
	
	/**
	 * Is the connection in the world?
	 * @return
	 */
	public boolean inWorld() {
		return inWorld;
	}
	
	
	/**
	 * Sets if the connection is in the world or not.
	 * @param world
	 */
	public void setInWorld(boolean world) {
		this.inWorld = world;
		this.inLobby = inWorld ? false : true;
	}
	
	
	/**
	 * Returns the display mode of the connection.
	 * @return
	 */
	public int getDisplayMode() {
		return displayMode;
	}
	
	
	/**
	 * Sets the display mode of the connection.
	 * @param display
	 */
	public void setDisplayMode(int display) {
		this.displayMode = display;
	}
}
