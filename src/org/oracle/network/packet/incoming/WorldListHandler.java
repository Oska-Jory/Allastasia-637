package org.oracle.network.packet.incoming;

import org.oracle.entity.user.User;
import org.oracle.network.packet.IncomingPacket;
import org.oracle.network.packet.Packet;
import org.oracle.utilities.WorldList;

/**
 * 
 * @author Oska - <format@allastasia.com>
 * Handles the world list packet.
 */
public class WorldListHandler extends IncomingPacket {

	@Override
	public void execute(User user, Packet packet) {
            user.getConnection().getChannel().write(WorldList.getData(true, true));
		
	}
	
}