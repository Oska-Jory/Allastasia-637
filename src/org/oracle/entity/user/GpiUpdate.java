package org.oracle.entity.user;

import java.util.LinkedList;

import org.oracle.Launcher;
import org.oracle.network.packet.PacketBuilder;

public class GpiUpdate {

	
	private User user;
	
	
	public GpiUpdate(User user) {
		this.user = user;
	}
	
	
	 private final LinkedList<User> localPlayers = new LinkedList<User>();
	 
	 private final boolean[] added = new boolean[2048];
	 
	 
	 public void loginData(PacketBuilder builder) {
		builder.initBitAccess();
		builder.putBits(30, user.getLocation().getX() << 14 | user.getLocation().getY() & 0x3fff | user.getLocation().getZ() << 28);
		short userIndex = user.getIndex();
		for (int index = 1; index < Launcher.getWorld().getUsers().size()/*2048*/; index++) {
			
			if (index == userIndex)
                continue;
			
            User other = Launcher.getWorld().getUsers().get(index);//World.getWorld().getPlayers().get(index);
            if (other == null) {  //||// !other.isOnline()) {
                builder.putBits(18, 0);
                continue;
            }
            if (!user.getLocation().withinDistance(other.getLocation(), user.getViewDistance())) {
                builder.putBits(18, 0);
                continue;
            }
            builder.putBits(18, other.getLocation().get18BitsHash());
        }
        builder.finishBitAccess();
	 }
	 
	 
	 public void sendUpdate() {
		 if (user.getRegion().isDidMapRegionChange()) {
			 user.updateMap();
		 }
		 
		 
	 }
}
