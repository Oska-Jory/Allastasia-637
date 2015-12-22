package org.oracle.network.packet;

import org.oracle.entity.user.User;
import org.oracle.network.packet.incoming.DisplayHandler;
import org.oracle.network.packet.incoming.WorldListHandler;


/**
 * 
 * @author Oska - <format@allastasia.com>
 *
 * Represents an incoming packet, sent from an external 
 * connection.
 */
public abstract class IncomingPacket {

	
	/**
	 * Handles an incoming packet.
	 * @param user
	 * @param packet
	 */
	public abstract void execute(User user, Packet packet);
	
	
	
	/**
	 * Stores the handlers for each individual received packet.
	 */
	public static final IncomingPacket[] HANDLERS = new IncomingPacket[256];
	
	
	/**
	 * The sizes of each individual packet.
	 */
	public static final byte[] PACKET_SIZES = new byte[]{
        8, -1, -1, 16, 6, 2,
        8, 6, 3, -1, 16, 15, 0, 8, 11, 8, -1, -1, 3, 2, -3, -1, 7, 2, -1,
        7, -1, 3, 3, 6, 4, 3, 0, 3, 4, 5, -1, -1, 7, 8, 4, -1, 4, 7, 3, 15,
        8, 3, 2, 4, 18, -1, 1, 3, 7, 7, 4, -1, 8, 2, 7, -1, 1, -1, 3, 2,
        -1, 8, 3, 2, 3, 7, 3, 8, -1, 0, 7, -1, 11, -1, 3, 7, 8, 12, 4, -3,
        -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3,
        -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3};
	
	
	
	static {
		
		HANDLERS[84] = new WorldListHandler();
		HANDLERS[7] = new DisplayHandler();
		
		PACKET_SIZES[35] = 5;
	}
}
