package org.oracle.network.external;

import java.util.HashMap;
import java.util.Map;

import org.jboss.netty.channel.Channel;
import org.oracle.utilities.Logger;
import org.oracle.utilities.Logger.Type;


/**
 * @author Oska - <format@allastasia.com>
 * Manages the external connections.
 */
public class ConnectionManager {
	
	
	/**
	 * Stores a list of current connections.
	 */
	private static Map<Channel, Connection> connections = new HashMap<Channel, Connection>();
	
	
	/**
	 * Returns the list of current connections.
	 * @return
	 */
	public static Map<Channel, Connection> getConnections() {
		return connections;
	}
	
	
	/**
	 * Returns a connection from the list using a given channel.
	 * @param channel
	 * @return
	 */
	public static Connection getConnection(Channel channel) {
		return connections.get(channel);
	}
	
	
	/**
	 * Checks if the list contains the connection.
	 * @param connection
	 * @return
	 */
	public static boolean hasConnection(Connection connection) {
		if (connections.containsKey(connection)) 
			return true;
		return false;
	}
	
	
	
	/**
	 * Stores a connection to the list.
	 * @param channel
	 */
	public static void store(Channel channel) {
		connections.put(channel, new Connection(channel));
		Logger.log(Type.CONNECTION, "Successful connection from: " + channel.getLocalAddress());
	}
	
	
	
	/**
	 * Drops a connection from the list.
	 * @param channel
	 */
	public static void drop(Channel channel) {
		connections.remove(channel);
		Logger.log(Type.CONNECTION, "Connection lost from: " + channel.getLocalAddress());
	}
}
