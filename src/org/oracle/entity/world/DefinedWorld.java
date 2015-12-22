package org.oracle.entity.world;


/**
 * 
 * @author Oska - <format@allastasia.com>
 * A list of pre-made world definitions.
 */
public enum DefinedWorld {
	
	/**
	 * World 1 definitions.
	 */
	WORLD_1(new WorldDefinition(false, "AU", "Trade", 1));
	
	
	/**
	 * The definition of the defined world.
	 */
	private WorldDefinition definition;
	
	
	/**
	 * Constructs a new pre defined world.
	 * @param definition
	 */
	private DefinedWorld(WorldDefinition definition) {
		this.definition = definition;
	}
	
	
	/**
	 * Returns the definition of the defined world.
	 * @return
	 */
	public WorldDefinition getDefinition() {
		return definition;
	}

}
