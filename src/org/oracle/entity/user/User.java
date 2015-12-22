package org.oracle.entity.user;

import java.util.ArrayList;
import java.util.List;

import org.oracle.data.json.user.JsonUserDecoder;
import org.oracle.data.json.user.JsonUserEncoder;
import org.oracle.entity.Entity;
import org.oracle.entity.Location;
import org.oracle.entity.object.region.RegionBuilder;
import org.oracle.network.ActionSender;
import org.oracle.network.external.Connection;
import org.oracle.network.packet.Packet;

/**
 * 
 * @author Oska - <format@allastasia.com>
 *
 * Represents a connected and successful connection,
 * currently receiving and sending reliable information to and from the server.
 */
public class User extends Entity {
	
	
	/**
	 * The connection of the user.
	 */
	private Connection connection;
	
	
	/**
	 * The definitions belonging to the user.
	 */
	private UserDefinition definition;
	
	
	/**
	 * Creates a new user, using a connection
	 * and a set of new or already defined definitions.
	 * @param connection
	 */
	public User(Connection connection, UserDefinition definition) {
		this.connection = connection;
		this.definition = definition;
	}
	
	
	/**
	 * Returns the connection of the user.
	 * @return
	 */
	public Connection getConnection() {
		return connection;
	}
	
	
	/**
	 * Sets the connection of the user.
	 * @param connection
	 */
	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	
	
	/**
	 * Returns the user's definitions.
	 * @return
	 */
	public UserDefinition getDefinition() {
		return definition;
	}
	
	
	/**
	 * Sets the User's definition.
	 * @param definition
	 */
	public void setDefinition(UserDefinition definition) {
		this.definition = definition;
	}
	
	
	
	/**
	 * The Gathered Player Information being updated.
	 */
	private final GpiUpdate gpi = new GpiUpdate(this);
	
	
	
	/**
	 * Returns the gathered player information that is being updated.
	 * @return
	 */
	public GpiUpdate getGpi() {
		return gpi;
	}
	
	
	private int viewportDepth;
	
	
	public void setViewportDepth(int depth) {
		if (depth < 0 || depth > 3) {
			return;
		}
		this.viewportDepth = depth;
	}

	
	public int getViewportDepth() {
		return viewportDepth;
	}
	
	
	private List<Integer> mapRegionIds;
	
	
	public List<Integer> getMapRegionIds() {
		return mapRegionIds;
	}
	
	
	private boolean isAtDynamicRegion;
	
	
	private RegionData region = new RegionData(this);
	
	
	public RegionData getRegion() {
		return region;
	}
	
	
	public void updateRegionArea() {
		mapRegionIds = new ArrayList<Integer>();
		int regionX = getLocation().getRegionX();
		int regionY = getLocation().getRegionY();
		int mapHash = Location.VIEWPORT_SIZES[viewportDepth] >> 4;
		for (int xCalc = (regionX - mapHash) >> 3; xCalc <= (regionX + mapHash) >> 3; xCalc++) {
			for (int yCalc = (regionY - mapHash) >> 3; yCalc <= (regionY + mapHash) >> 3; yCalc++) {
				int regionId = yCalc + (xCalc << 8);
				if (RegionBuilder.getDynamicRegion(regionId) != null)
					isAtDynamicRegion = true;
				mapRegionIds.add(yCalc + (xCalc << 8));
			}
		}
	}
	
	
	public void updateMap() {
		updateRegionArea();
		if (isAtDynamicRegion) {
			ActionSender.sendDynamicRegion(this);
		} else {
			ActionSender.updateMapRegion(this, true);
		}
	}
	
	
	
	public void teleport(int x, int y, int z) {
	    
		//getRegion().teleport(x, y, z);
	}
	
	
	private int viewDistance = 0;
	
	
	public int getViewDistance() {
		return viewDistance;
	}
	
	public void setViewDistance(int distance) {
		this.viewDistance = distance;
	}
	
	public void incrementViewDistance() {
		this.viewDistance++;
	}
	
	
	
	public void write(Packet packet) {
		getConnection().getChannel().write(packet);
	}
	
	
	/**
	 * The decodes data from the json file.
	 */
	private JsonUserDecoder dataDecoder;
	
	
	/**
	 * Encodes data to a json file.
	 */
	private JsonUserEncoder dataEncoder;
	
	
	/**
	 * Returns the Json Decoder.
	 * @return
	 */
	public JsonUserDecoder getDataDecoder() {
		return dataDecoder;
	}
	
	
	/**
	 * Returns the Json Encoder.
	 * @return
	 */
	public JsonUserEncoder getDataEncoder() {
		return dataEncoder;
	}
	
	
	/**
	 * Sets the User's data Json Decoder.
	 * @param decoder
	 */
	public void setDataDecoder(JsonUserDecoder decoder) {
		this.dataDecoder = decoder;
	}
	
	
	/**
	 * Sets the User's data Json Encoder.
	 * @param encoder
	 */
	public void setDataEncoder(JsonUserEncoder encoder) {
		this.dataEncoder = encoder;
	}
}
