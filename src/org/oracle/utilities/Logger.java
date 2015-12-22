package org.oracle.utilities;

/**
 * 
 * @author Oska - <format@allastasia.com>
 * 
 * Prints informative text in the terminal.
 */
public class Logger {
	
	
	/**
	 * 
	 * @author Oska - <format@allastasia.com>
	 * Represents the type of information being printed.
	 */
	public enum Type {
		
		INFO("Info"),
		ERROR("Error"),
		NETWORK("Network"),
		WORLD("World"),
		USER("User"),
		NPC("Npc"),
		LAUNCHER("Launcher"),
		EXCEPTION("Exception"),
		PACKET("Packet"),
		CONNECTION("Connection"),
		DECODER("Message Decoder"),
		CACHE("Cache");
		
		private String text;
		
		private Type(String text) {
			this.text = text;
		}
	}
	
	
	/**
	 * Prints the information.
	 * @param type
	 * @param text
	 */
	public static void log(Type type, String text) {
		System.err.println("[" + type.text + "] " + text);
	}
	
}
