package org.oracle;

import java.io.IOException;

import org.oracle.cache.Cache;
import org.oracle.entity.world.DefinedWorld;
import org.oracle.entity.world.World;
import org.oracle.network.Bootstrap;
import org.oracle.utilities.Logger;
import org.oracle.utilities.Logger.Type;
import org.oracle.utilities.StaticObjects;
import org.oracle.utilities.XTEA;

/**
 * 
 * @author Oska - <format@allastasia.com>
 *
 * Launches the application.
 */
public class Launcher {

	
	/**
	 * The game world of the application.
	 */
	private static World world;
	
	
	/**
	 * The main method.
	 * Compiles and loads the server.
	 * @param arguments
	 * @throws IOException 
	 */
	public static void main(String[] arguments) throws IOException {
		long time = System.currentTimeMillis();
		Logger.log(Type.LAUNCHER, "Welcome to " + StaticObjects.APPLICATION_NAME + " #" + StaticObjects.BUILD + "!");
		Cache.init();
		world = new World(1, DefinedWorld.WORLD_1.getDefinition());
	
		new Bootstrap(StaticObjects.PORT).bind();
		Logger.log(Type.LAUNCHER,  StaticObjects.APPLICATION_NAME + " successfully loaded in: " + (System.currentTimeMillis() - time) + " milliseconds!");
	}
	
	
	/**
	 * Returns the main game world of the application.
	 * @return
	 */
	public static World getWorld() {
		return world;
	}
	 
	
}
