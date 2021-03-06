package org.oracle.network;

import java.util.Random;

import org.oracle.cache.format.LandscapeParser;
import org.oracle.entity.Location;
import org.oracle.entity.object.GameObject;
import org.oracle.entity.object.ObjectManager;
import org.oracle.entity.object.region.DynamicRegion;
import org.oracle.entity.object.region.RegionBuilder;
import org.oracle.entity.user.InterfaceSettings;
import org.oracle.entity.user.User;
import org.oracle.network.packet.Packet.Type;
import org.oracle.network.packet.PacketBuilder;
import org.oracle.utilities.MapXTEA;
import org.oracle.utilities.Misc;
import org.oracle.utilities.TextUtils;

public class ActionSender { // 2370 -( 2380, 9360+, 9570+

	public static void packet108(User player, int i1, int i2) {
		PacketBuilder bldr = new PacketBuilder(108);
		bldr.writeByteA(i1);
		bldr.writeByteS(i2);
		player.getConnection().getChannel().write(bldr.toPacket());
	}

	public static int NO_BLACKOUT = 0, BLACKOUT_ORB = 1, BLACKOUT_MAP = 2,
			BLACKOUT_ORB_AND_MAP = 5;

	public static int messageCounter = 1;
	public static final Random r = new Random();

	public static void sendAnimateObject(User player, GameObject obj, int anim) { // its
																					// wrong
																					// but
																					// I
																					// dont
																					// need
																					// it
																					// nvm.
		/*
		 * int i_119_ =
		 * Class48_Sub1_Sub2.aClass98_Sub22_Sub1_5514.readByteC((byte) 102); int
		 * i_120_ = Class53.anInt430 + (i_119_ >> 2113611716 & 0x7); int i_121_
		 * = Class335.anInt2819 + (0x7 & i_119_); int i_122_ =
		 * Class48_Sub1_Sub2.aClass98_Sub22_Sub1_5514.readShort1((byte) 49); int
		 * i_123_ =
		 * Class48_Sub1_Sub2.aClass98_Sub22_Sub1_5514.readUnsignedByte((byte)
		 * 45);
		 */
		int localX = obj.getLocation().getX()
				- (player.getRegion().getLastMapRegion().getRegionX() - 6) * 8;
		int localY = obj.getLocation().getY()
				- (player.getRegion().getLastMapRegion().getRegionY() - 6) * 8;
		if (!updateTilePosition(player, obj.getLocation()))
			return;
		PacketBuilder bldr = new PacketBuilder(81);
		bldr.writeByteS(((localX - ((localX >> 3) << 3)) << 4)
				| ((localY - ((localY >> 3) << 3)) & 0x7));
		bldr.writeLEShortA(anim);
		bldr.writeByteA((obj.getType() << 2) + (obj.getRotation() & 3));
		player.getConnection().getChannel().write(bldr.toPacket());
	}

	public static void packet66(User player) {
		// PacketBuilder bldr = new PacketBuilder(5);
		// bldr.writeLEShortA(1043);
		// bldr.writeLEInt(4771);
		// player.getConnection().getChannel().write(bldr.toPacket());
		// player.getConnection().getChannel().write(new PacketBuilder(106)
		// .writeLEInt(4771)
		// .writeLEShortA(1043)
		// .toPacket());
	}

	public static void sendSound(User player, int soundId, int volume,
			int soundSpeed, boolean voice) {
		player.getConnection().getChannel().write(new PacketBuilder(voice ? 7 : 69).writeShort(soundId) // sound
																			// id
				.writeByte(1) // times played
				.writeShort(0) // delay before starting
				.writeByte(volume).writeShort(soundSpeed) // speed of the sound,
															// 255 seems normal
				.toPacket());
	}

	public static void spawnPositionedGraphic(Location location, int graphicId) {
		for (User player : World.getWorld().getPlayers()) {
			if (player.getLocation().distance(location) <= 16) {
				sendPositionedGraphic(player, location, graphicId);
			}
		}
	}

	public static void sendPositionedGraphic(User player, Location location,
			int graphicId) {
		if (!updateTilePosition(player, location))
			return;
		int regionX = location.getX()
				- (player.getLocation().getRegionX() << 3);
		int regionY = location.getY()
				- (player.getLocation().getRegionY() << 3);
		player.getConnection().getChannel().write(new PacketBuilder(87)
				.writeByte((regionX & 0x7) << 4 | regionY & 0x7)
				.writeShort(graphicId).writeByte(0).writeShort(0).writeByte(0)
				.toPacket());
	}

	public static void sendObject(User player, GameObject object) {
		sendObject(player, object.getId(), object.getLocation().getX(), object
				.getLocation().getY(), object.getLocation().getZ(),
				object.getType(), object.getRotation());
	}

	public static void sendObject(User player, int objectId, int x, int y,
			int z, int type, int rotation) {
		if (player == null)
			return;
		int viewportX = x
				- (player.getRegion().getLastMapRegion().getRegionX() - 6) * 8;
		int viewportY = y
				- (player.getRegion().getLastMapRegion().getRegionY() - 6) * 8;
		if (!updateTilePosition(player, Location.locate(x, y, z)))
			return;
		PacketBuilder bldr = new PacketBuilder(78);
		bldr.writeByteC(((viewportX - ((viewportX >> 3) << 3)) << 4)
				| ((viewportY - ((viewportY >> 3) << 3)) & 0x7));
		bldr.writeLEShort(objectId);
		bldr.writeByte((type << 2) + (rotation & 3));
		player.getConnection().getChannel().write(bldr.toPacket());
	}

	public static void deleteObject(User player, int objectId, int x, int y,
			int z, int type, int rotation) {
		int localX = x
				- (player.getRegion().getLastMapRegion().getRegionX() - 6) * 8;
		int localY = y
				- (player.getRegion().getLastMapRegion().getRegionY() - 6) * 8;
		if (!updateTilePosition(player, Location.locate(x, y, z)))
			return;
		player.getConnection().getChannel().write(new PacketBuilder(19)
				.writeByteC((type << 2) + (rotation & 3))
				.writeByteS(
						((localX - ((localX >> 3) << 3)) << 4)
								| ((localY - ((localY >> 3) << 3)) & 0x7))
				.toPacket());
	}

	/*
	 * public static void sendIcon(User p, Icon icon) { PacketBuilder bldr =
	 * new PacketBuilder(0); bldr.writeByte(icon.getSlot() << 5 |
	 * icon.getTargetType()); if (icon.getTargetType() > 0) {
	 * bldr.writeByte(icon.getArrowId()); if (icon.getTargetType() == 1 ||
	 * icon.getTargetType() == 10) { bldr.writeShort(icon.getIndex());
	 * bldr.skip(6); } bldr.writeShort(icon.getModelId()); } else {
	 * bldr.skip(11); } p.write(bldr.toPacket()); }
	 */

	public static void sendHintIcon(User p, Icon icon) {
		PacketBuilder bldr = new PacketBuilder(0);
		bldr.writeByte(icon.getSlot() << 5 | icon.getTargetType());
		bldr.writeByte(icon.getArrowId());
		if (icon.getIndex() > -1) {
			bldr.writeShort(icon.getIndex());
			bldr.writeShort(icon.getDistance());
			bldr.skip(4);
		} else if (icon.getLocation() != null) {
			bldr.writeByte(1); // TODO: Identify.
			bldr.writeShort(icon.getLocation().getX());
			bldr.writeShort(icon.getLocation().getY());
			bldr.writeByte(icon.getLocation().getZ());
			bldr.writeShort(icon.getDistance());
		} else {
			bldr.skip(11);
		}
		bldr.writeShort(icon.getModelId());
		p.write(bldr.toPacket());
	}

	public static void sendSystemUpdate(User p, double seconds) {
		long l = Math.round(seconds);
		int value = Math.round(l);
		PacketBuilder bldr = new PacketBuilder(77);
		if (value > 0) {
			bldr.writeShort(value);
		}
		p.write(bldr.toPacket());
	}

	public static void rotateCamera(User p, int x, int y, int speed, int angle) {
		PacketBuilder bldr = new PacketBuilder(93);
		bldr.writeByteA(0); // idk
		bldr.writeByteS(x); // y coord to look at
		bldr.writeByte(y); // x coord to look at?
		bldr.writeByteC(speed);
		bldr.writeShort(0); // idk
		p.write(bldr.toPacket());

	}

	public static void moveCamera(User p, int speed,
			int backAndForthDistance, int sideToSideDistance, int floorHeight,
			int cameraHeight) {
		PacketBuilder bldr = new PacketBuilder(100);
		bldr.writeByteS(floorHeight); // speed to move
		bldr.writeByteA(sideToSideDistance);
		bldr.writeShort(cameraHeight); // sizeX (dont go over like 10 or so)
		bldr.writeByteA(backAndForthDistance);
		bldr.writeByteA(speed); // nothing?
		p.write(bldr.toPacket());
	}

	public static void resetCamera(User player) {
		player.getConnection().getChannel().write(new PacketBuilder(79).toPacket());
	}

	public static void shakeCamera(User p, int speed) {
		PacketBuilder bldr = new PacketBuilder(26);
		bldr.writeLEShortA(14); // idk
		bldr.writeByteA(speed); // speed
		bldr.writeByteS(1);// nooo idea
		bldr.writeByteS(0); // idk lol
		bldr.writeByteC(0); // idk lol
		p.write(bldr.toPacket());
	}

	public static void sendItems(User player, int interfaceId, int childId,
			int type, Container items) {
		int main = interfaceId * 65536 + childId;
		PacketBuilder bldr = new PacketBuilder(120, Type.VAR_SHORT);
		bldr.writeInt(main);
		bldr.writeShort(type);
		bldr.writeShort(items.getSize());
		for (int i = 0; i < items.getSize(); i++) {
			Item item = items.get(i);
			int id, amt;
			if (item == null) {
				id = -1;
				amt = 0;
			} else {
				id = item.getDefinition().getId();
				amt = item.getAmount();
			}
			if (amt > 254) {
				bldr.writeByteC((byte) 255);
				bldr.writeInt1(amt);
			} else {
				bldr.writeByteC(amt);
			}
			bldr.writeLEShort(id + 1);
		}
		player.getConnection().getChannel().write(bldr.toPacket());
	}

	public static void closeInventoryInterface(User p) {
		if (p == null || p.getConnection() == null)
			return;
		boolean fullscreen = p.getConnection().getDisplayMode() == 2;
		sendCloseInterface(p, fullscreen ? 746 : 548, fullscreen ? 84 : 197);
	}

	public static void closeSideInterface(User p) {
		boolean fullscreen = p.getConnection().getDisplayMode() == 2;
		sendCloseInterface(p, fullscreen ? 746 : 548, 195);
	}

	// 55, 53, 58, 53, 995, 15, 184, 140, 15, 37, 16, 64 Magic shortbow.

	/**
	 * Sends a projectile.
	 * 
	 * @param p
	 *            The player.
	 * @param projectile
	 *            The projectile to send.
	 */
	public static void sendProjectile(User player, Projectile projectile) {
		if (player == null || projectile == null
				|| player.getRegion().getLastMapRegion() == null) {
			return;
		}
		PacketBuilder bldr = new PacketBuilder(15, Type.VAR_SHORT);
		Location end = projectile.isLocationBased() ? projectile
				.getEndLocation() : projectile.getVictim().getLocation();
		Location start = projectile.getSourceLocation();
		int x = start.getX()
				- (player.getRegion().getLastMapRegion().getRegionX() - 6) * 8;
		int y = start.getY()
				- (player.getRegion().getLastMapRegion().getRegionY() - 6) * 8;
		bldr.writeByte(end.getZ()).writeByteC(y >> 3).writeByteA(x >> 3);
		bldr.writeByte(13);
		x = start.getX() - (start.getRegionX() << 3);
		y = start.getY() - (start.getRegionY() << 3);
		bldr.writeByte((x & 0x7) << 3 | y & 0x7)
				.writeByte((start.getX() - end.getX()) * -1)
				.writeByte((start.getY() - end.getY()) * -1)
				.writeShort(
						projectile.getVictim() != null ? (projectile
								.getVictim().isPlayer() ? -(projectile
								.getVictim().getIndex() + 1) : (projectile
								.getVictim().getIndex() + 1)) : -1)
				.writeShort(projectile.getProjectileId())
				.writeByte(projectile.getStartHeight())
				.writeByte(projectile.getEndHeight())
				.writeShort(projectile.getType())
				.writeShort(projectile.getSpeed())
				.writeByte(projectile.getAngle())
				.writeShort(projectile.getDistance());
		player.getConnection().getChannel().write(bldr.toPacket());
	}

	/**
	 * @author Maxime Meire
	 */
	public static void sendProjectile(User player, Mob mob, int projectileId,
			Location start, Location end, int startHeight, int endHeight,
			int speed, int delay, int curve, int startDistanceOffset,
			int creatorSize) {
		if (player == null || start == null || end == null
				|| player.getRegion().getLastMapRegion() == null) {
			return;
		}
		PacketBuilder bldr = new PacketBuilder(15, Type.VAR_SHORT);
		int x = start.getX()
				- (player.getRegion().getLastMapRegion().getRegionX() - 6) * 8;
		int y = start.getY()
				- (player.getRegion().getLastMapRegion().getRegionY() - 6) * 8;
		int distance = start.getDistance(end);
		int duration = (distance * 30 / ((speed / 10) < 1 ? 1 : (speed / 10)))
				+ delay;
		bldr.writeByte(player.getLocation().getZ()).writeByteC(y >> 3)
				.writeByteA(x >> 3);
		bldr.writeByte(13); // projectile subopcode
		x = start.getX() - (start.getRegionX() << 3);
		y = start.getY() - (start.getRegionY() << 3);
		bldr.writeByte((x & 0x7) << 3 | y & 0x7)
				.writeByte((start.getX() - end.getX()) * -1)
				.writeByte((start.getY() - end.getY()) * -1)
				.writeShort(
						mob != null ? (mob.isPlayer() ? -(mob.getIndex() + 1)
								: (mob.getIndex() + 1)) : -1)
				.writeShort(projectileId).writeByte(startHeight)
				.writeByte(endHeight).writeShort(delay).writeShort(duration)
				.writeByte(curve);
		if (creatorSize == 11) {
			bldr.writeShort(11);
		} else {
			bldr.writeShort(64 + startDistanceOffset * 64);
		}
		player.getConnection().getChannel().write(bldr.toPacket());
	}

	/*
	 * @Override public Protocol sendProjectile(User player, Entity receiver,
	 * int projectileId, Tile start, Tile end, int startHeight, int endHeight,
	 * int speed, int delay, int curve, int startDistanceOffset, int
	 * creatorSize) { FrameBuilder fb = new FrameBuilder(94,
	 * Frame.FrameType.VAR_SHORT); int distance = start.distance(end); int
	 * duration = (distance * 30 / ((speed / 10) < 1 ? 1 : (speed / 10))) +
	 * delay; int x = start.getPartX() -
	 * (player.getMapRegionUpdatePosition().getPartX() - 6); int y =
	 * start.getPartY() - (player.getMapRegionUpdatePosition().getPartY() - 6);
	 * fb.writeByteS(x) .writeByteC(y) .writeByteS(player.getZ());
	 * fb.writeByte(2); // projectile subopcode x = start.getX() -
	 * (start.getPartX() << 3); y = start.getY() - (start.getPartY() << 3);
	 * fb.writeByte((x & 0x7) << 3 | y & 0x7); fb.writeByte(end.getX() -
	 * start.getX()) .writeByte(end.getY() - start.getY());
	 * fb.writeShort(receiver != null ? (receiver instanceof Player ?
	 * -(receiver.getIndex() + 1) : receiver.getIndex() + 1) : 0);
	 * fb.writeShort(projectileId); fb.writeByte(startHeight);
	 * fb.writeByte(endHeight); fb.writeShort(delay); fb.writeShort(duration);
	 * fb.writeByte(curve); fb.writeShort(creatorSize * 64 + startDistanceOffset
	 * * 64); fb.write(player.getSession()); return this; }
	 */

	public static void sendBlankClientScript(User player, int id) {
		PacketBuilder bldr = new PacketBuilder(98, Type.VAR_SHORT);
		bldr.writeShort(0);
		bldr.writeRS2String("");
		bldr.writeInt(id);
		player.getConnection().getChannel().write(bldr.toPacket());
	}

	public static void sendGroundItem(User player, Location tile, int itemId,
			boolean uniqueDrop) {
		int deltaX = tile.getX() - (tile.getRegionX() << 3);
		int deltaY = tile.getY() - (tile.getRegionY() << 3);
		PacketBuilder bldr = new PacketBuilder(29);
		if (!updateTilePosition(player, tile))
			return;
		bldr.writeLEShortA(itemId);
		bldr.writeByteS((deltaX & 0x7) << 4 | deltaY & 0x7);
		bldr.writeLEShortA(1);
		player.getConnection().getChannel().write(bldr.toPacket());
	}

	private static boolean updateTilePosition(User player, Location loc) {
		if (player.getLocation().distance(loc) >= 50) {
			return false;
		}
		int x = loc.getRegionX()
				- (player.getRegion().getLastMapRegion().getRegionX() - 6);
		int y = loc.getRegionY()
				- (player.getRegion().getLastMapRegion().getRegionY() - 6);
		player.getConnection().getChannel().write(new PacketBuilder(114).writeByteA(x)
				.writeByteC(loc.getZ()).writeByteA(y).toPacket());
		return true;
	}

	public static void sendPublicChatMessage(User player, int playerIndex,
			int rights, ChatMessage chat) {
		if (chat == null) {
			return;
		}
		PacketBuilder bldr = new PacketBuilder(62, Type.VAR_BYTE);
		bldr.writeShort(playerIndex);
		bldr.writeShort(chat.getEffects());
		bldr.writeByte(rights);
		byte[] chatStr = new byte[256];
		chatStr[0] = (byte) chat.getChatText().length();
		int offset = 1 + TextUtils.huffmanCompress(chat.getChatText(), chatStr,
				1);
		bldr.writeBytes(chatStr, 0, offset);
		player.getConnection().getChannel().write(bldr.toPacket());
	}

	public static void sendChatMessage(User player, int TextType, String Text) {
		if (Text == null || Text.length() < 1) {
			return;
		}
		PacketBuilder bldr = new PacketBuilder(53, Type.VAR_BYTE);
		bldr.writeByte(TextType);
		bldr.writeInt(0);
		bldr.writeByte(0);
		bldr.writeRS2String(Text);
		player.getConnection().getChannel().write(bldr.toPacket());
	}

	public static void sendTradeReq(User player, String user, String message) {
		PacketBuilder bldr = new PacketBuilder(53, Type.VAR_BYTE);
		bldr.writeByte(100);
		bldr.writeInt(0);
		bldr.writeByte(0x1);
		bldr.writeRS2String(Misc.formatPlayerNameForDisplay(user));
		bldr.writeRS2String(message);
		player.getConnection().getChannel().write(bldr.toPacket());
	}

	public static void sendDuelReq(User player, String user, String message) {
		PacketBuilder bldr = new PacketBuilder(53, Type.VAR_BYTE);
		bldr.writeByte(101);
		bldr.writeInt(0);
		bldr.writeByte(0x1);
		bldr.writeRS2String(Misc.formatPlayerNameForDisplay(user));
		bldr.writeRS2String(message);
		player.getConnection().getChannel().write(bldr.toPacket());
	}

	public static void sendMessage(User player, String text) {
		sendChatMessage(player, 0, text);
	}

	public static void sendFriend(User player, String Username,
			String displayName, int world, boolean writeOnline,
			boolean WarnMessage, boolean isLobby) {
		if (Username.length() > 16 || displayName.length() > 16) {
			Username = "null";
			displayName = "null";
		}
		short WorldId = 1;
		PacketBuilder bldr = new PacketBuilder(10, Type.VAR_SHORT);
		bldr.writeByte(0);
		bldr.writeRS2String(displayName);
		bldr.writeRS2String(Username);
		bldr.writeShort(writeOnline ? (world == WorldId ? 1 : 2) : 0);
		Clan clan = World.getWorld().getClanManager()
				.getClan(player.getUsername());
		bldr.writeByte(clan != null ? clan.getRank(Username) : 0);
		if (writeOnline) {
			bldr.writeRS2String(isLobby ? "<col=00FF00>Lobby"
					: "<col=00FF00>Dementhium");
			bldr.writeByte(0);
		}
		player.getConnection().getChannel().write(bldr.toPacket());
	}

	public static void sendIgnore(User player, String name, String displayName) {
		PacketBuilder bldr = new PacketBuilder(85, Type.VAR_BYTE);
		bldr.writeByte(0);
		if (displayName.equalsIgnoreCase(name))
			name = "";
		bldr.writeRS2String(displayName);
		bldr.writeRS2String(displayName);
		bldr.writeRS2String(name);
		bldr.writeRS2String(displayName);
		player.getConnection().getChannel().write(bldr.toPacket());
	}

	public static void sendPrivateMessage(User player, String username,
			String message) {
		byte[] bytes = new byte[256];
		int length = TextUtils.huffmanCompress(message, bytes, 0);
		PacketBuilder bldr = new PacketBuilder(76, Type.VAR_BYTE);
		bldr.writeRS2String(Misc.formatPlayerNameForDisplay(username));
		bldr.writeByte(message.length());
		bldr.writeBytes(bytes, 0, length);
		player.getConnection().getChannel().write(bldr.toPacket());
	}

	public static void receivePrivateMessage(User player, String username,
			String displayName, int rights, String message) {
		long id = (long) (++messageCounter + ((Math.random() * Long.MAX_VALUE) + (Math
				.random() * Long.MIN_VALUE)));
		byte[] bytes = new byte[256];
		bytes[0] = (byte) message.length();
		int length = 1 + TextUtils.huffmanCompress(message, bytes, 1);
		PacketBuilder bldr = new PacketBuilder(30, Type.VAR_BYTE);
		bldr.writeByte(0); // has a previous name?
		bldr.writeRS2String(Misc.formatPlayerNameForDisplay(username));
		bldr.writeShort((int) (id >> 32));
		bldr.writeTriByte((int) (id - ((id >> 32) << 32)));
		bldr.writeByte(rights);
		bldr.writeBytes(bytes, 0, length);
		player.getConnection().getChannel().write(bldr.toPacket());
	}

	public static void sendUnlockIgnoreList(User player) {
		player.getConnection().getChannel().write(new PacketBuilder(12).toPacket());
	}

	public static void sendMusic(User player, int music) {
		PacketBuilder bldr = new PacketBuilder(76);
		bldr.writeByte(255);
		bldr.writeShortA(music);
		bldr.writeByte(50);
		player.getConnection().getChannel().write(bldr.toPacket());
	}

	public static void sendLoginInterfaces(User player) {
		InterfaceSettings.sendInterfaces(player);
		// ActionSender.sendWindowsPane(player, 1028, 0);
		// ActionSender.sendAMask(player, 2, 1028, 45, 0, 204);
		// ActionSender.sendAMask(player, 2, 1028, 111, 0, 204);
		// ActionSender.sendAMask(player, 2, 1028, 107, 0, 204);
	}

	public static void sendHideAllTabs(User player) {
		/*
		 * Access Mask start
		 */
		switch (player.getConnection().getDisplayMode()) {
		case 0:
		case 1:
			sendInterfaceConfig(player, 548, 80, false);
			sendInterfaceConfig(player, 548, 127, false);
			break;
		case 2:
		case 3:
			sendInterfaceConfig(player, 746, 19, false);
			sendInterfaceConfig(player, 746, 20, false);
			break;
		}
	}

	public static void sendShowAllTabs(User player) {
		/*
		 * Access Mask start
		 */
		switch (player.getConnection().getDisplayMode()) {
		case 0:
		case 1:
			sendInterfaceConfig(player, 548, 80, true);
			sendInterfaceConfig(player, 548, 127, true);
			break;
		case 2:
		case 3:
			sendInterfaceConfig(player, 746, 19, true);
			sendInterfaceConfig(player, 746, 20, true);
			break;
		}
	}

	public static void sendLoginConfigurations(User player) {
		sendRunEnergy(player);
		sendConfig(player, 1240, player.getSkills().getHitPoints() * 2);
		/*
		 * Access Mask start
		 */
		switch (player.getConnection().getDisplayMode()) {
		case 0:
		case 1:
			InterfaceSettings.sendFixedAMasks(player);
			break;
		case 2:
		case 3:
			InterfaceSettings.sendFullScreenAMasks(player);
			break;
		}
		/*
		 * Configuration start
		 */
		// sendConfig(player, 2159, 0);
		sendConfig(player, 173, 0);
		sendConfig(player, 101, 100);// Number of completed Quests
		sendConfig(player, 904, 100);// Total number of quests available.
		sendConfig(player, 313, -1);// Emotes
		sendConfig(player, 465, -1);
		sendConfig(player, 802, -1);
		sendConfig(player, 1085, 12);// Zombie Hand enable
		sendConfig(player, 2032, 7341);// Seal of approval
		sendConfig(player, 2033, 1043648799);// Seal of approval req?
		sendConfig(player, 1921, -893736236);// Puppet master emote
		// sendConfig(player, 1404, 67108864);//Around the world in Eggety days
		// and dramatic point emote
		// sendConfig(player, 1405, 1720);
		// sendConfig(player, 1407, 5370);
		sendConfig(player, 1160, -1);
		sendConfig(player, 1583, 511305630);
		sendConfig(player, 281, 1000);// Unlock questlist and chat sort tabs
		sendConfig(player, 1384, 512);// Clear quest sort options
		// TODO sendConfig(player, 43, player.getAttackStyle());
		// sendConfig(player, 1584, 1);
		sendConfig(player, 1584, player.getPrayer().isAncientCurses() ? 1 : 0);
		sendConfig(player, 172, player.isAutoRetaliating() ? 1 : 0);
		organizeSpells(player);

		sendConfig(player, 1960, 1);
		sendConfig(player, 1961, 524160);// Task list
		sendConfig(player, 1962, 8384512);// Task list
		sendConfig(player, 1963, 299354);// Task list
		sendConfig(player, 1964, 1499501);// Task list
		sendConfig(player, 1965, 1470822);// Task list
		sendConfig(player, 1178, 28893);// Wolf Whistle Completed
		sendConfig(player, 20, -1);// Unlock Music
		sendConfig(player, 21, -1);// Unlock Music
		sendConfig(player, 22, -1);// Unlock Music
		sendConfig(player, 23, -1);// Unlock Music
		sendConfig(player, 24, -1);// Unlock Music
		sendConfig(player, 25, -1);// Unlock Music
		sendConfig(player, 311, -1);// Unlock Music
		sendConfig(player, 346, -1);// Unlock Music
		sendConfig(player, 414, -1);// Unlock Music
		sendConfig(player, 598, -1);// Unlock Music
		sendConfig(player, 662, -1);// Unlock Music
		sendConfig(player, 906, -1);// Unlock Music
		sendConfig(player, 1009, -1);// Unlock Music
		/*
		 * Free Quests Completed Start
		 */
		sendConfig(player, 130, 4);// Black Knight's Fortress
		sendConfig(player, 29, 2);// Cook's Assistant
		sendConfig(player, 222, 3);// Demon Slayer
		sendConfig(player, 31, 100);// Doric's Quest
		sendConfig(player, 176, 10);// Dragon Slayer
		sendConfig(player, 32, 3);// Ernest The Chicken
		sendConfig(player, 62, 6);// Goblin Diplomacy
		sendConfig(player, 160, 2);// Imp Catcher
		sendConfig(player, 122, 7);// The Knight's Sword
		sendConfig(player, 71, 4);// Pirate's Treasure
		sendConfig(player, 273, 110);// Prince Ali Rescue
		sendConfig(player, 107, 5);// The Restless Ghost
		sendConfig(player, 144, 100);// Romeo & Juliet
		sendConfig(player, 63, 6);// Rune Mysteries
		sendConfig(player, 179, 21);// Sheep Shearer
		sendConfig(player, 145, 7);// Shield of Arrav
		sendConfig(player, 178, 3);// Vampire Slayer
		sendConfig(player, 67, 3);// Witch's Potion
		sendConfig(player, 293, 100);// Big Chompy Bird Hunting
		sendConfig(player, 68, 100);// Biohazard
		sendConfig(player, 655, 200);// Cabin Fever
		sendConfig(player, 10, 100);// Clocktower
		sendConfig(player, 399, 100);// Creature of Fenkenstrain
		sendConfig(player, 314, 100);// Death Plateau
		sendConfig(player, 131, 100);// The Dig Site
		sendConfig(player, 80, 100);// Drudric Ritual
		sendConfig(player, 0, 100);// Dwarf Cannon
		sendConfig(player, 355, 200);// Eadgars Ruse
		sendConfig(player, 299, -1);// Elemental Workshop 1
		sendConfig(player, 148, 100);// Family Crest
		sendConfig(player, 17, 100);// Fight Arena
		sendConfig(player, 11, 100);// Fishing Contest
		sendConfig(player, 347, 100);// The Fremennik Trails
		sendConfig(player, 65, 100);// The waterfall quest
		sendConfig(player, 180, 100);// Gertrude's Cat
		sendConfig(player, 150, 200);// The Grand Tree
		sendConfig(player, 382, 100);// Haunted Mine
		sendConfig(player, 223, 100);// Hazeel Cult
		sendConfig(player, 188, 100);// Heros Quest
		sendConfig(player, 5, 100);// The Holy Grail
		sendConfig(player, 287, 200);// In Search of the Myreque
		sendConfig(player, 175, 100);// Jungle Potion
		sendConfig(player, 139, 100);// Legands Quest
		sendConfig(player, 147, 100);// Lost City
		sendConfig(player, 14, 100);// Merlin's Crystal
		sendConfig(player, 365, 100);// Monkey Madness
		sendConfig(player, 30, 100);// Monks friend
		sendConfig(player, 517, 100);// Mourning's Ends Part 1
		sendConfig(player, 192, 100);// Murder mystery
		sendConfig(player, 307, 200);// Nature Spirit
		sendConfig(player, 112, 100);// Observatory Quest
		sendConfig(player, 416, 300);// One Small Favour
		sendConfig(player, 165, 100);// Plague City
		sendConfig(player, 302, 100);// Priest in Peril
		sendConfig(player, 328, 100);// Regicide
		sendConfig(player, 402, 100);// Roving Elves
		sendConfig(player, 600, 100);// Rum deal
		sendConfig(player, 76, 100);// Scorpian Catcher
		sendConfig(player, 159, 100);// Sea Slug
		sendConfig(player, 339, 100);// Shades of Mort'ton
		sendConfig(player, 60, 100);// Sheep Herder
		sendConfig(player, 116, 100);// Shilo Village
		sendConfig(player, 320, 100);// Tai Bwo Wannai Trio
		sendConfig(player, 26, 100);// Temple of Ikov
		sendConfig(player, 359, 100);// Throne of Miscellania
		sendConfig(player, 197, 100);// The Tourist Trap
		sendConfig(player, 226, 100);// Witch's House
		sendConfig(player, 111, 100);// Tree Gnome Village
		sendConfig(player, 200, 100);// Tribal Totem
		sendConfig(player, 385, 100);// Troll Romance
		sendConfig(player, 317, 100);// Troll Stronghold
		sendConfig(player, 212, 100);// Watchtower
		sendConfig(player, 980, 200);// The Great Brain Robbery
		sendConfig(player, 939, -1);// Animal Magnetism
		sendConfig(player, 433, 200);// Between a Rock...
		sendConfig(player, 964, 100);// Contact!
		sendConfig(player, 455, 400);// Zogre Flesh Eaters
		sendConfig(player, 869, 400);// Darkness of Hallowvale
		sendConfig(player, 794, 100);// Death to the Dorgeshuun
		sendConfig(player, 1801, player.getSkills().getExperienceCounter() * 10);
		/*
		 * Bottom Configuration start
		 */
		sendBConfig(player, 768, 3);
		sendBConfig(player, 234, 0);
		sendBConfig(player, 181, 0);
		sendBConfig(player, 168, 4);
		sendBConfig(player, 695, 0);
		sendInterfaceConfig(player, 34, 13, false);
		sendInterfaceConfig(player, 34, 13, false);
		sendInterfaceConfig(player, 34, 3, false);
		sendConfig(player, 172, player.isAutoRetaliating() ? 0 : 1);
		sendConfig(player, 1493, player.getSettings().getSummoningOption());
		//Disabled so Mystic Flow doesn't leech again.. player.getFamiliar().init();
	}

	public static void organizeSpells(User player) {
		if (player.getSettings().getSpellBook() == 192) {
			sendConfig(player, 439, 0);
			MagicBookListener.refreshModern(player);
		} else if (player.getSettings().getSpellBook() == 193) {
			sendConfig(player, 439, 1);
			MagicBookListener.refreshAncient(player);
		} else {
			sendConfig(player, 439, 2);
			MagicBookListener.refreshLunar(player);
		}
	}

	public static void sendOtherLoginPackets(User player) {
		player.getInventory().refresh();
		player.getEquipment().refresh();
		player.getSkills().refresh();
		ActionSender.sendPlayerOption(player, "Follow", 2, false);
		ActionSender.sendPlayerOption(player, "Trade with", 3, false);
		sendMessage(player, "Welcome to Dementhium.");
		if (player.isDoubleXP()) {
			sendMessage(player, "<col=ff0000>You are currently on +50% XP");
			sendMessage(player, "<col=ff0000>You have " + player.getTimeLeft()
					+ " bonus experience minutes left.");
		}
		sendConfig(player, 287, player.getSettings().getPrivateTextColor());
		sendConfig(player, 170, 0);
		sendConfig(player, 300, player.getSettings().getSpecialAmount());
	}

	public static void sendRunEnergy(User player) {
		PacketBuilder bldr = new PacketBuilder(13);
		bldr.writeByte(player.getWalkingQueue().getRunEnergy());
		player.getConnection().getChannel().write(bldr.toPacket());
	}

	public static void sendSkillLevel(User player, int skill) {
		ActionSender.sendConfig(player, 1801, player.getSkills()
				.getExperienceCounter() * 10);
		PacketBuilder bldr = new PacketBuilder(8);
		bldr.writeInt1((int) player.getSkills().getXp(skill));
		bldr.writeByteS(skill);
		if (skill == Skills.PRAYER) {
			bldr.writeByteS((int) Math.ceil(player.getSkills()
					.getPrayerPoints()));
		} else {
			bldr.writeByteS((byte) player.getSkills().getLevel(skill));
		}
		player.getConnection().getChannel().write(bldr.toPacket());
	}

	public static void updateMinimap(User player, int state) {
		player.getConnection().getChannel().write(new PacketBuilder(32).writeByte(state).toPacket());
	}

	public static void sendPlayerOption(User player, String option, int slot,
			boolean top) {
		if (option.equals(player.getSettings().getPlayerOptions()[slot])) {
			return;
		}
		PacketBuilder bldr = new PacketBuilder(120, Type.VAR_BYTE);
		bldr.writeShortA(slot == 1 && top ? 42 : 65535); // sprite id for the
															// option
		bldr.writeByteS(top ? 1 : 0);
		bldr.writeRS2String(option);
		bldr.writeByteS(slot);
		player.getConnection().getChannel().write(bldr.toPacket());
		player.getSettings().getPlayerOptions()[slot] = option;
	}

	public static void sendTradeOptions(User player) {
		Object[] tparams1 = new Object[] { "", "", "", "Value<col=FF9040>",
				"Remove-X", "Remove-All", "Remove-10", "Remove-5", "Remove",
				-1, 0, 7, 4, 90, 335 << 16 | 31 };
		sendClientScript(player, 150, tparams1, "IviiiIsssssssss");
		sendAMask(player, 1150, 335, 31, 0, 27);
		Object[] tparams3 = new Object[] { "", "", "", "", "", "", "", "",
				"Value<col=FF9040>", -1, 0, 7, 4, 90, 335 << 16 | 34 };
		sendClientScript(player, 695, tparams3, "IviiiIsssssssss");
		sendAMask(player, 1026, 335, 34, 0, 27);
		Object[] tparams2 = new Object[] { "", "", "Lend", "Value<col=FF9040>",
				"Offer-X", "Offer-All", "Offer-10", "Offer-5", "Offer", -1, 0,
				7, 4, 93, 336 << 16 };
		sendClientScript(player, 150, tparams2, "IviiiIsssssssss");
		sendAMask(player, 1278, 336, 0, 0, 27);
	}

	public static void sendAMask(User player, int set1, int set2,
			int interfaceId1, int childId1, int interfaceId2, int childId2) {
		PacketBuilder bldr = new PacketBuilder(119);
		bldr.writeInt2(interfaceId2 << 16 | childId2);
		bldr.writeShortA(set2);
		bldr.writeShortA(set1);
		bldr.writeLEInt(interfaceId1 << 16 | childId1);
		player.getConnection().getChannel().write(bldr.toPacket());
	}

	public static void sendAMask(User p, int set, int interfaceId,
			int childId, int off, int len) {
		p.write(new PacketBuilder(119).writeInt2(set).writeShortA(len)
				.writeShortA(off).writeLEInt(interfaceId << 16 | childId)
				.toPacket());
		/*
		 * int i_259_ =
		 * Class48_Sub1_Sub2.aClass98_Sub22_Sub1_5514.readInt2(-108); int i_260_
		 * = Class48_Sub1_Sub2.aClass98_Sub22_Sub1_5514.readShortA(i +
		 * -525200459); if (i_260_ == 65535) i_260_ = -1; int i_261_ =
		 * Class48_Sub1_Sub2.aClass98_Sub22_Sub1_5514.readShortA(74); if
		 * ((i_261_ ^ 0xffffffff) == -65536) i_261_ = -1; int i_262_ =
		 * Class48_Sub1_Sub2.aClass98_Sub22_Sub1_5514.readIntReverse(true);
		 */
	}

	public static void sendInterfaceConfig(User player, int interfaceId,
			int childId, boolean hidden) {
		player.getConnection().getChannel().write(new PacketBuilder(3)
				.writeInt2((interfaceId << 16) | childId)
				.writeByteC(hidden ? 0 : 1).toPacket());
	}

	public static void sendBConfig(User player, int id, int value) {
		PacketBuilder bldr;
		if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) {
			bldr = new PacketBuilder(5).writeLEShortA(id).writeLEInt(value);
		} else {
			bldr = new PacketBuilder(51).writeLEShort(id).writeByte(value);
		}
		player.getConnection().getChannel().write(bldr.toPacket());
	}

	public static void sendConfig(User player, int id, int value) {
		PacketBuilder bldr;
		if (value < 0 || value >= 128) {
			bldr = new PacketBuilder(27).writeInt1(value).writeShort(id);
		} else {
			bldr = new PacketBuilder(21).writeShortA(id).writeByteS(value);
		}
		player.getConnection().getChannel().write(bldr.toPacket());
	}

	public static void sendEntityOnInterface(User player, boolean isPlayer,
			int entityId, int interId, int childId) {
		if (isPlayer)
			ActionSender.sendPlayerOnInterface(player, interId, childId); // ...
																			// oh
																			// sorry
																			// :p
		else
			ActionSender.sendNpcOnInterface(player, interId, childId, entityId);
	}

	public static void sendPlayerOnInterface(User player, int interId,
			int childId) {
		PacketBuilder bldr = new PacketBuilder(54);
		bldr.writeInt1(interId << 16 | childId);
		player.getConnection().getChannel().write(bldr.toPacket());
	}

	public static void sendNpcOnInterface(User player, int interId,
			int childId, int npcId) {
		PacketBuilder bldr = new PacketBuilder(71);
		bldr.writeInt2(interId << 16 | childId);
		bldr.writeLEShort(npcId);
		player.getConnection().getChannel().write(bldr.toPacket());
		// PacketBuilder bldr = new PacketBuilder(4);
		// bldr.writeLEShort(205);
		// bldr.writeInt(interId << 16 | childId); idk//this could be send
		// sprite yeah
		// player.getConnection().getChannel().write(bldr.toPacket());
	}

	public static void sendSprite(User player, int interId, int childId,
			int spriteId) {
		PacketBuilder bldr = new PacketBuilder(4);
		bldr.writeLEShort(spriteId);
		bldr.writeInt(interId << 16 | childId); // idk//this could be send
												// sprite yeah
		player.getConnection().getChannel().write(bldr.toPacket());
	}

	public static void sendInterAnimation(User player, int emoteId,
			int interId, int childId) {
		PacketBuilder bldr = new PacketBuilder(86);
		bldr.writeInt1(interId << 16 | childId);
		bldr.writeLEShortA(emoteId);
		player.getConnection().getChannel().write(bldr.toPacket());

		// PacketBuilder bldr = new PacketBuilder(84); Whole player on
		// interface
		// bldr.writeInt2(interId << 16 | childId);
		// player.getConnection().getChannel().write(bldr.toPacket());
		// PacketBuilder bldr = new PacketBuilder(96);//Weird ass shit
		// bldr.writeShort(1);
		// bldr.writeLEInt(interId << 16 | childId);
		// bldr.writeLEShortA(interId);
		// bldr.writeShortA(childId);
		// player.getConnection().getChannel().write(bldr.toPacket());
		// some type of quick chat packet
		// PacketBuilder bldr = new PacketBuilder(74);
		// bldr.writeInt2(interId << 16 | childId);
		// bldr.writeByteS(0); // frame index
		// bldr.writeShort(emoteId);
		// player.getConnection().getChannel().write(bldr.toPacket());
	}

	public static void sendQuickChat(User player) {
		PacketBuilder bldr = new PacketBuilder(74);
		bldr.writeShort(1);
		bldr.writeShort(1);
		bldr.writeByteS(1);
		player.getConnection().getChannel().write(bldr.toPacket());
	}

	public static void sendString(User player, String string,
			int interfaceId, int childId) {
		PacketBuilder bldr = new PacketBuilder(33, Type.VAR_SHORT);
		bldr.writeRS2String(string);
		bldr.writeLEInt(interfaceId << 16 | childId);
		player.getConnection().getChannel().write(bldr.toPacket());
	}

	public static void sendString(User player, int interfaceId, int childId,
			String string) {
		PacketBuilder bldr = new PacketBuilder(33, Type.VAR_SHORT);
		bldr.writeRS2String(string);
		bldr.writeLEInt(interfaceId << 16 | childId);
		player.getConnection().getChannel().write(bldr.toPacket());
	}

	public static void sendOverlay(User p, int childId) {
		switch (p.getConnection().getDisplayMode()) {
		case 0:
		case 1:
			sendInterface(p, 1, 548, 7, childId);
			break;
		case 2:
		case 3:
			sendInterface(p, 1, 746, 7, childId);
			break;
		}
	}

	public static void sendInterface(User player, int childId) {
		if (player.getPriceCheck().isOpen()) {
			player.getPriceCheck().close();
		}
		switch (player.getConnection().getDisplayMode()) {
		case 0:
		case 1:
			ActionSender.sendInterface(player, 0, 548, 18, childId);
			break;
		case 2:
		case 3:
			ActionSender.sendInterface(player, 0, 746, 9, childId);
			break;
		}
	}

	public static void sendTab(User player, int tabId, int childId) {
		ActionSender.sendInterface(player, 1, childId == 137 ? 752 : 548,
				tabId, childId);
	}

	public static void sendChatboxInterface(User player, int childId) {
		ActionSender.sendInterface(player, 1, 752, 13, childId);
	}

	public static void sendCloseChatBox(User player) {
		ActionSender.sendCloseInterface(player, 752, 13); // 752, 7,
		// ActionSender.sendCloseInterface(player, 752, 7);
		for (int i = 0; i < ProduceAction.CONFIG_IDS.length; i++) {
			ActionSender.sendBConfig(player, ProduceAction.CONFIG_IDS[i], -1);
			ActionSender.sendSpecialString(player, ProduceAction.NAME_IDS[i],
					"");
		}
	}

	public static void sendCloseInterface(User player, int window, int tab) {
		if (window != 752 && tab != 13
				&& player.getAttribute("inBank", Boolean.FALSE) == Boolean.TRUE) {
			player.removeAttribute("inBank");
		}
		PacketBuilder bldr = new PacketBuilder(61);
		bldr.writeLEInt(window << 16 | tab);
		player.getConnection().getChannel().write(bldr.toPacket());
	}

	public static void zoom(User p) {
		PacketBuilder bldr = new PacketBuilder(71);
		bldr.writeInt2(155);
		bldr.writeShort(0);
		p.write(bldr.toPacket());
	}

	public static void sendCloseInterface(User player) {
		int winId = player.getConnection().getDisplayMode() < 2 ? 548 : 746;
		int slotId = player.getConnection().getDisplayMode() < 2 ? 18 : 9;
		sendCloseInterface(player, winId, slotId);
	}

	public static void sendCloseOverlay(User player) {
		int winId = player.getConnection().getDisplayMode() < 2 ? 548 : 746;
		int slotId = player.getConnection().getDisplayMode() < 2 ? 7 : 6;
		sendCloseInterface(player, winId, slotId);
	}

	public static void sendInventoryInterface(User player, int childId) {
		switch (player.getConnection().getDisplayMode()) {
		case 0:
		case 1:
			ActionSender.sendInterface(player, 0, 548, 197, childId);
			break;
		case 2:
			ActionSender.sendInterface(player, 0, 746, 84, childId);
			break;
		}
	}

	public static void sendCloseInventoryInterface(User player) {
		switch (player.getConnection().getDisplayMode()) {
		case 0:
		case 1:
			sendCloseInterface(player, 548, 193);
			break;
		case 2:
		case 3:
			sendCloseInterface(player, 746, 83);
			break;
		}
	}

	public static void sendInterface(User player, int showId, int windowId,
			int interfaceId, int childId) {
		PacketBuilder bldr = new PacketBuilder(50);
		bldr.writeInt2(windowId * 65536 + interfaceId);
		bldr.writeLEShortA(interfaceId >> 16 | childId);
		bldr.writeByteC(showId);
		player.getConnection().getChannel().write(bldr.toPacket());
	}

	public static void sendInterfaceOnChild(User player, int interfaceId,
			int childId) {
		switch (player.getConnection().getDisplayMode()) {
		case 0:
		case 1:
			ActionSender.sendInterface(player, 0, 548, interfaceId, childId);
			break;
		case 2:
			ActionSender.sendInterface(player, 0, 746, interfaceId, childId);
			break;
		}
	}

	public static void sendWindowsPane(User player, int pane, int subWindowsId) {
		PacketBuilder bldr = new PacketBuilder(36);
		bldr.writeByteA(subWindowsId);
		bldr.writeShortA(pane);
		player.getConnection().getChannel().write(bldr.toPacket());
	}

	public static void sendDynamicRegion(final User player) {
		PacketBuilder bldr = new PacketBuilder(31, Type.VAR_SHORT);
		int regionX = player.getLocation().getRegionX();
		int regionY = player.getLocation().getRegionY();
		bldr.writeByteA(1); // loading type
		bldr.writeByteA(player.getViewportDepth()); // map size
		bldr.writeShortA(regionY);
		bldr.writeLEShort(regionX);
		bldr.writeByteA(1); // force reload
		int mapHash = Location.VIEWPORT_SIZES[player.getViewportDepth()] >> 4;
		int[] realRegionIds = new int[4 * mapHash * mapHash];
		int realRegionIdsCount = 0;
		bldr.initBitAccess();
		for (int plane = 0; plane < 4; plane++) {
			for (int thisRegionX = (regionX - mapHash); thisRegionX <= ((regionX + mapHash)); thisRegionX++) { // real
																												// x
																												// calcs
				for (int thisRegionY = (regionY - mapHash); thisRegionY <= ((regionY + mapHash)); thisRegionY++) { // real
																													// y
																													// calcs
					int regionId = (((thisRegionX / 8) << 8) + (thisRegionY / 8));
					DynamicRegion dynamicRegion = RegionBuilder
							.getDynamicRegion(regionId);
					int realRegionX;
					int realRegionY;
					int realPlane;
					int rotation;
					if (dynamicRegion != null) { // generated map
						int[] regionCoords = dynamicRegion.getRegionCoords()[plane][thisRegionX
								- ((thisRegionX / 8) * 8)][thisRegionY
								- ((thisRegionY / 8) * 8)];
						realRegionX = regionCoords[0];
						realRegionY = regionCoords[1];
						realPlane = regionCoords[2];
						rotation = regionCoords[3];
					} else { // real map
						// base region + difference * 8 so gets real region
						// coords
						realRegionX = thisRegionX;
						realRegionY = thisRegionY;
						realPlane = plane;
						rotation = 0;// no rotation
					}
					// invalid region, not built region
					if (realRegionX == 0 || realRegionY == 0)
						bldr.putBits(1, 0);
					else {
						bldr.putBits(1, 1);
						bldr.putBits(26, (rotation << 1) | (realPlane << 24)
								| (realRegionX << 14) | (realRegionY << 3));
						int realRegionId = (((realRegionX / 8) << 8) + (realRegionY / 8));
						boolean found = false;
						for (int index = 0; index < realRegionIdsCount; index++)
							if (realRegionIds[index] == realRegionId) {
								found = true;
								break;
							}
						if (!found)
							realRegionIds[realRegionIdsCount++] = realRegionId;
					}

				}
			}
		}
		bldr.finishBitAccess();
		for (int index = 0; index < realRegionIdsCount; index++) {
			int regionId = realRegionIds[index];
			int[] xteas = MapXTEA.getKey(regionId);
			if (xteas == null)
				xteas = new int[4];
			for (int keyIndex = 0; keyIndex < 4; keyIndex++)
				bldr.writeInt(xteas[keyIndex]);
			if (!LandscapeParser.parseLandscape(regionId, xteas)) {
				player.teleport(Mob.DEFAULT);
			}
		}
		player.getRegion().setLastMapRegion(player.getLocation());
		player.getRegion().setDidMapRegionChange(false);
		player.getConnection().getChannel().write(bldr.toPacket());
		//player.getActionManager().stopNonWalkableActions();
		ObjectManager.refresh(player);
		//GroundItemManager.refresh(player);
	}

	public static void updateMapRegion(final User player, boolean loggedin) {
		PacketBuilder bldr = new PacketBuilder(80, Type.VAR_SHORT);
		if (!loggedin) {
			player.getGpi().loginData(bldr);
		}
		bldr.writeLEShortA(player.getLocation().getRegionY());
		bldr.writeShortA(player.getLocation().getRegionX());
		bldr.writeByte(player.getViewportDepth());
		bldr.writeByteA(1);
		for (int region : player.getMapRegionIds()) {
			int[] keys = MapXTEA.getKey(region);
			if (keys == null) {
				keys = new int[4];
			}
			for (int i = 0; i < 4; i++) {
				bldr.writeInt(keys[i]);
			}
			if (!LandscapeParser.parseLandscape(region, keys)) {
				// System.out.println("II " + region);
				//player.teleport(Location.locate(3222, 3218, 0));
			}
		}
		player.getRegion().setLastMapRegion(player.getLocation());
		player.getRegion().setDidMapRegionChange(false);
		player.getConnection().getChannel().write(bldr.toPacket());
	//	player.getActionManager().stopNonWalkableActions();
		ObjectManager.refresh(player);
		//GroundItemManager.refresh(player);
	}

	public static void sendItems(User player, int type, Container inventory,
			boolean split) {
		PacketBuilder bldr = new PacketBuilder(113, Type.VAR_SHORT);
		bldr.writeShort(type);
		bldr.writeByte((split ? 1 : 0));
		bldr.writeShort(inventory.getSize());
		for (int i = 0; i < inventory.getSize(); i++) {
			Item item = inventory.get(i);
			int id, amt;
			if (item == null) {
				id = -1;
				amt = 0;
			} else {
				id = item.getDefinition().getId();
				amt = item.getAmount();
			}
			bldr.writeShortA(id + 1);
			bldr.writeByteC(amt > 254 ? 0xff : amt);
			if (amt > 0xfe)
				bldr.writeInt1(amt);
		}
		player.getConnection().getChannel().write(bldr.toPacket());
	}

	public static void loginResponse(User player) {
		//player.getDefinition().setPermissionLevel(2);
		
		PacketBuilder bldr = new PacketBuilder();
		bldr.writeByte(13); // length
		bldr.writeByte(player.getDefinition().getPermissionLevel());
		bldr.writeByte(0);
		bldr.writeByte(0);
		bldr.writeByte(0);
		bldr.writeByte(1);
		bldr.writeByte(0);
		bldr.writeShort(player.getIndex());
		bldr.writeByte(1);
		bldr.writeTriByte(0);
		bldr.writeByte(1); // members
		player.getConnection().getChannel().write(bldr.toPacket());
		player.updateRegionArea();
		ActionSender.updateMapRegion(player, false);
		ActionSender.sendLoginInterfaces(player);
	}

	public static void sendLogout(User player, int button) {
		if (player.getCombatExecutor().getLastAttacker() != null) {
			player.sendMessage("You have to be 10 seconds out of combat before logging out of the game.");
			return;
		}
		if (!player.getActivity().canLogout(player, true)) {
			return;
		}
		PacketBuilder bldr = new PacketBuilder(button == 5 ? 45 : 23);
		player.getConnection().getChannel().write(bldr.toPacket());
		World.getWorld().unregister(player);
	}

	public static void sendClientScript(User player, int id, Object[] params,
			String types) {
		if (params.length != types.length())
			throw new IllegalArgumentException(
					"params size should be the same as types length");
		PacketBuilder bldr = new PacketBuilder(16, Type.VAR_SHORT);
		// bldr.writeShort(ActionSender.frameIndex++);
		bldr.writeRS2String(types);
		int idx = 0;
		for (int i = types.length() - 1; i >= 0; i--) {
			if (types.charAt(i) == 's')
				bldr.writeRS2String((String) params[idx]);
			else
				bldr.writeInt((Integer) params[idx]);
			idx++;
		}
		bldr.writeInt(id);
		player.getConnection().getChannel().write(bldr.toPacket());
	}

	public static void sendItemKeptOnDeath(User player) {
		sendAMask(player, 211, 0, 2, 102, 18, 4);
		sendAMask(player, 212, 0, 2, 102, 21, 42);
		Object[] params = new Object[] { 11510, 12749, "", 0, 0, -1, 4151,
				15441, 15443, 3, 0 };
		sendClientScript(player, 118, params, "Iviiiiiiiiiiiissssssssssss");
		sendBConfig(player, 199, 442);
	}

	public static void sendSpecialString(User p, int id, String name) {
		PacketBuilder bldr = new PacketBuilder(88, Type.VAR_SHORT);
		bldr.writeRS2String(name);
		bldr.writeLEShortA(id);
		p.write(bldr.toPacket());
	}

	public static void sendGESell(User player) {
		sendConfig(player, 1112, -1);
		sendConfig(player, 1113, -1);
		sendConfig(player, 1111, 1);
		sendConfig(player, 1109, -1);
		sendAMask(player, 6, 209, 105, -1, -1);
		sendAMask(player, 6, 211, 105, -1, -1);
		sendInterface(player, 105);
		sendInventoryInterface(player, 107);
	}

	public static void sendGrandExchange(User player, int slot, int progress,
			int item, int price, int amount, int currentAmount) {
		PacketBuilder bldr = new PacketBuilder(22);
		bldr.writeByte((byte) slot);
		bldr.writeByte((byte) progress);
		bldr.writeShort(item);
		bldr.writeInt(price);
		bldr.writeInt(amount);
		bldr.writeInt(currentAmount);
		bldr.writeInt(price * currentAmount);
		player.getConnection().getChannel().write(bldr.toPacket());
	}

	public static void switchPanes(User player, int paneFrom,
			int windowPosFrom, int paneTo, int windowPosTo) {
		PacketBuilder bldr = new PacketBuilder(72);
		bldr.writeInt(paneTo << 16 | windowPosTo);
		bldr.writeInt(paneFrom << 16 | windowPosFrom);
		bldr.writeLEShort(0); // frame index
		player.getConnection().getChannel().write(bldr.toPacket());
	}

	public static void resetGe(User p, int i) {
		PacketBuilder bldr = new PacketBuilder(22);
		bldr.writeByte((byte) i);
		bldr.writeByte((byte) 0);
		bldr.writeShort(0);
		bldr.writeInt(0);
		bldr.writeInt(0);
		bldr.writeInt(0);
		bldr.writeInt(0);
		p.getConnection().getChannel().write(bldr.toPacket());
	}

	public static void removeGroundItem(User player, GroundItem item) {
		removeGroundItem(player, item.getLocation(), item.getItem());
	}

	public static void sendLocation(User player, Location location) {
		int x = location.getRegionX()
				- (player.getRegion().getLastMapRegion().getRegionX() - 6);
		int y = location.getRegionY()
				- (player.getRegion().getLastMapRegion().getRegionX() - 6);
		PacketBuilder pb = new PacketBuilder(91);
		pb.writeByteS((byte) location.getZ()).writeByteA(y)
				.writeByte((byte) (byte) x);
		player.getConnection().getChannel().write(pb.toPacket());
	}

	public static void sendUnlockFriendList(User player) {
		PacketBuilder bldr = new PacketBuilder(10, Type.VAR_SHORT);
		player.getConnection().getChannel().write(bldr.toPacket());
	}

	public static void sendLobbyResponse(User player) {
		PacketBuilder bldr = new PacketBuilder();
		bldr.writeByte((byte) 0);
		bldr.writeByte((byte) 0);
		bldr.writeByte((byte) 0);
		bldr.writeByte((byte) 0);
		bldr.writeByte((byte) 0);
		bldr.writeShort(30); // member days left
		bldr.writeShort(1); // recovery questions
		bldr.writeShort(0); // unread messages
		bldr.writeShort(3229); // 3229 - lastDays
		int ipHash = Misc.IPAddressToNumber("127.0.0.1");
		bldr.writeInt(ipHash); // last ip
		bldr.writeByte((byte) 3); // email status (0 - no email, 1 - pending
		// parental confirmation, 2 - pending
		// confirmation, 3 - registered)
		bldr.writeShort(0);
		bldr.writeShort(0);
		bldr.writeByte((byte) 0);
		bldr.writeJAGString(player.getDefinition().getLoginName());
		bldr.writeByte((byte) 0);
		bldr.writeInt(1);
		bldr.writeShort(1); // current world id
		bldr.writeJAGString("127.0.0.1");
		PacketBuilder lobbyResponse = new PacketBuilder();
		lobbyResponse.writeByte((byte) bldr.getPosition());
		lobbyResponse.writeBytes(bldr.getBuffer());
		player.getConnection().getChannel().write(lobbyResponse.toPacket());
	}

	public static void removeGroundItem(User player, Location tile, Item item) {
		if (tile == null || item == null || player == null
				|| player.getRegion().getLastMapRegion() == null) {
			return;
		}
		int deltaX = tile.getX() - (tile.getRegionX() << 3);
		int deltaY = tile.getY() - (tile.getRegionY() << 3);
		PacketBuilder bldr = new PacketBuilder(59);
		if (!updateTilePosition(player, tile))
			return;
		bldr.writeByteS((0x7 & deltaX) << 4 | deltaY & 0x7);
		bldr.writeShortA(item.getId());
		player.getConnection().getChannel().write(bldr.toPacket());
	}

	public static void sendAccessMask(User player, int range1, int range2,
			int interfaceId1, int childId1, int interfaceId2, int childId2) {
		PacketBuilder pb = new PacketBuilder(35);
		pb.writeLEShortA(range2).writeLEInt(interfaceId1 << 16 | childId1)
				.writeLEShortA(0).writeShortA(range1)
				.writeInt2(interfaceId2 << 16 | childId2);
		player.getConnection().getChannel().write(pb.toPacket());
	}

	public static void sendDuelOptions(User p) {
		sendAMask(p, 1278, 631, 94, 0, 27); // brb food
		sendAMask(p, 1278, 628, 0, 0, 27);
		Object[] tparams1 = new Object[] { "", "", "", "", "Remove X",
				"Remove All", "Remove 10", "Remove 5", "Remove", 1, 0, 2, 2,
				134, 631 << 16 | 94 };
		sendClientScript(p, 150, tparams1, "IviiiIsssssssss");
		Object[] tparams2 = new Object[] { "", "", "", "", "Stake X",
				"Stake All", "Stake 10", "Stake 5", "Stake", -1, 0, 7, 4, 93,
				628 << 16 };
		sendClientScript(p, 150, tparams2, "IviiiIsssssssss");
	}

	public static void sendItemOnInterface(User player, int interfaceId,
			int child, int size, int id) {
		player.getConnection().getChannel().write(new PacketBuilder(91).writeLEShortA(id).writeInt(size)
				.writeInt2(interfaceId << 16 | child).toPacket());
	}

	public static void sendVariableItemOnInterface(User player,
			int interfaceId, int child, int size, int id) {
		player.getConnection().getChannel().write(new PacketBuilder(56)
				.writeLEInt(interfaceId << 16 | child).writeShortA(0)
				.writeShort(0).writeShort(1300).toPacket());
	}

	public static void sendItemSearch(User player, String string) {
		sendConfig(player, 1111, 1);
		sendConfig(player, 1112, -1);
		sendConfig(player, 1113, -1);
		sendConfig(player, 1112, 0);
		sendConfig(player, 1113, 0);
		sendInterface(player, 1, 752, 7, 389);
		sendClientScript(player, 570, new Object[] { string }, "s");
	}

	public static void sendGroundItem(User player, GroundItem groundItem) {
		if (player == null || groundItem.getLocation() == null) {
			return;
		}
		sendGroundItem(player, groundItem.getLocation(), groundItem);
	}

	public static void sendGroundItem(User player, Location tile,
			GroundItem item) {
		int deltaX = tile.getX() - (tile.getRegionX() << 3);
		int deltaY = tile.getY() - (tile.getRegionY() << 3);
		PacketBuilder bldr = new PacketBuilder(29);
		if (!updateTilePosition(player, tile))
			return;
		bldr.writeLEShortA(item.getItem().getId());
		bldr.writeByteS((deltaX & 0x7) << 4 | deltaY & 0x7);
		bldr.writeLEShortA(item.getItem().getAmount());
		player.getConnection().getChannel().write(bldr.toPacket());
	}
}
