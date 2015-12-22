package org.oracle.entity.world;

/**
 * 
 * @author Oska - <format@allastasia.com>
 * 
 * Gives a world its own unique definition.
 */
public class WorldDefinition {
	
	
	/**
	 * Is the world a member's world?
	 */
	private boolean members;
	
	
	/**
	 * The country of the world.
	 */
	private String country;
	
	
	/**
	 * The worlds main activity.
	 */
	private String activity;
	
	
	/**
	 * The flag id
	 */
	private int flag;
	
	
	/**
	 * The Definition of the World.
	 * @param members
	 * @param country
	 * @param activity
	 * @param flag
	 */
	public WorldDefinition(boolean members, String country, String activity, int flag) {
		this.members = members;
		this.country = country;
		this.activity = activity;
		this.flag = flag;
	}
	
	
	
	/**
	 * Returns the boolean value for members.
	 * @return
	 */
	public boolean isMembers() {
		return members;
	}
	
	
	/**
	 * Returns the country.
	 * @return
	 */
	public String getCountry() {
		return country;
	}
	
	
	/**
	 * Returns the worlds activity.
	 * @return
	 */
	public String getActivity() {
		return activity;
	}
	
	
	/**
	 * Returns the worlds flag.
	 * @return
	 */
	public int getFlag() {
		return flag;
	}

}
