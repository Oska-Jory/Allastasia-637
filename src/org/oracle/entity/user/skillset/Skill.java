package org.oracle.entity.user.skillset;

/**
 * Oska - <format@allastasia.com>
 * 
 * An abstract class representing a user's specific skill.
 */
public class Skill {
	
	
	private int id, level, xp;
	
	
	public Skill(int id, int level, int xp) {
		this.id = id;
		this.level = level;
		this.xp = xp;
	}
	
	
	public int getId() {
		return id;
	}
	
	
	public int getLevel() {
		return level;
	}
	
	
	public int getXp() {
		return xp;
	}
	
	
	public void setLevel(int level) {
		this.level = level;
	}
	
	
	public void setXp(int xp) {
		this.xp = xp;
	}

}
