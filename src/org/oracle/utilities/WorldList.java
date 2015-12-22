package org.oracle.utilities;

import java.util.ArrayList;

import org.oracle.Launcher;
import org.oracle.entity.world.World;
import org.oracle.network.packet.Packet;
import org.oracle.network.packet.Packet.Type;
import org.oracle.network.packet.PacketBuilder;

public class WorldList {
    public static final int COUNTRY_AUSTRALIA = 16;

    public static final int COUNTRY_BELGIUM = 22;

    public static final int COUNTRY_BRAZIL = 31;

    public static final int COUNTRY_CANADA = 38;

    public static final int COUNTRY_DENMARK = 58;

    public static final int COUNTRY_FINLAND = 69;

    public static final int COUNTRY_IRELAND = 101;

    public static final int COUNTRY_MEXICO = 152;

    public static final int COUNTRY_NETHERLANDS = 161;

    public static final int COUNTRY_NORWAY = 162;

    public static final int COUNTRY_SWEDEN = 191;

    public static final int COUNTRY_UK = 77;

    public static final int COUNTRY_USA = 225;

    public static final int FLAG_HIGHLIGHT = 16;

    public static final int FLAG_LOOTSHARE = 8;

    public static final int FLAG_MEMBERS = 1;

    public static final int FLAG_NON_MEMBERS = 0;

    public static final int FLAG_PVP = 4;

    private static final ArrayList<WorldDef> worldList = new ArrayList<WorldDef>();

    static {
        worldList.add(new WorldDef(1, 0, FLAG_MEMBERS, "World 1", "184.82.211.134", "USA", COUNTRY_USA));
    }

    public static Packet getData(boolean worldConfiguration, boolean worldStatus) {
        PacketBuilder bldr = new PacketBuilder(98, Type.VAR_SHORT);
        bldr.writeByte((byte) 1);
        bldr.writeByte((byte) 2);
        bldr.writeByte((byte) 1);
        if (worldConfiguration)
            populateConfiguration(bldr);
        if (worldStatus)
            populateStatus(bldr);
        return bldr.toPacket();
    }

    private static void populateConfiguration(PacketBuilder buffer) {
    	buffer.writeSmart(worldList.size());
        setCountry(buffer);
        buffer.writeSmart(0);
        buffer.writeSmart((worldList.size() + 1));
        buffer.writeSmart(worldList.size());
        for (WorldDef w : worldList) {
        	buffer.writeSmart(w.getWorldId());
            buffer.writeByte((byte) w.getLocation());
            buffer.writeInt(w.getFlag());
            buffer.writeJAGString(w.getActivity()); // activity
            buffer.writeJAGString(w.getIp()); // ip writeress
        }
        buffer.writeInt(0x94DA4A87); // != 0
    }

    private static void populateStatus(PacketBuilder buffer) {
        for (WorldDef w : worldList) {
            buffer.writeSmart(w.getWorldId()); // world id
            buffer.writeShort((short) Launcher.getWorld().getUsers().size()); // player count
        }
    }


    private static void setCountry(PacketBuilder buffer) {
        for (WorldDef w : worldList) {
            buffer.writeSmart(w.getCountry());
            buffer.writeJAGString(w.getRegion());
        }
    }
}
