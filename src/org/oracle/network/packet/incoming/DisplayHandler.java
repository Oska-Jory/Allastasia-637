package org.oracle.network.packet.incoming;

import org.oracle.entity.user.InterfaceSettings;
import org.oracle.entity.user.User;
import org.oracle.network.packet.IncomingPacket;
import org.oracle.network.packet.Packet;
import org.oracle.tickable.Tick;


/**
 * 
 * @author Oska - <format@allastasia.com>
 * Handles the user's display.
 */
public class DisplayHandler extends IncomingPacket {

	@Override
	public void execute(User user, Packet packet) {
		int mode = packet.readByte();
		int width = packet.readShort();
		int height = packet.readShort();
		packet.readByte();
		if (mode < 0 || mode > 3) {
			return;
		}
		
		if (user.getViewDistance() < 1) {
			user.incrementViewDistance();
			user.submitTick("distance_increment", new Tick(4) {
				@Override
				public void execute() {
					setTime(1);
					if (user.getViewDistance() >= 15) {
						stop();
						return;
					}
					user.incrementViewDistance();
				}
			});
		}
		if (mode == 2 && user.getConnection().getDisplayMode() != 2
				|| mode == 3 && user.getConnection().getDisplayMode() != 3) {
			InterfaceSettings.switchWindow(user, mode);
		} else if (mode == 1 && user.getConnection().getDisplayMode() != 1
				|| mode == 0 && user.getConnection().getDisplayMode() != 0) {
			InterfaceSettings.switchWindow(user, mode);
		}
	}

}
