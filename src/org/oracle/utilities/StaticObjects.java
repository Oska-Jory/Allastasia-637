package org.oracle.utilities;

/**
 * 
 * @author Oska - <format@allastasia.com>
 *
 * Stores static values.
 */
public class StaticObjects {

	/**
	 * The Update Build of the Application. (What Connections are compatible?)
	 */
	public static final int BUILD = 637;
	
	
	/**
	 * The name of the Application. e.g. Welcome to Oracle
	 */
	public static final String APPLICATION_NAME = "Oracle";
	
	
	/**
	 * The name of the original developer of the application.
	 */
	public static final String AUTHOR_NAME = "Format";
	
	
	/**
	 * The port this server binds to.
	 */
	public static final int PORT = 43594;
	
	
	/**
	 * The maximum amount of User's able to connect.
	 */
	public static final int MAX_ENTITY_AMOUNT = 2048;
	
	
	/**
	 * The Cache element sizes.
	 */
	public static final int[] ELEMENT_SIZES = { 56, 79325, 55568, 46770, 24563, 299978, 44375,
		0, 4176, 3589, 109125, 604031, 176138, 292288, 350498, 686783, 18008, 20836, 16339, 
		1244, 8142, 743, 119, 699632, 932831, 3931, 2974,};
	
	
    public final static byte DISCONNECT = -1;
    public final static byte GET_CONNECTION_ID = 0;
    public final static byte LOGIN_START = 1;
    public final static byte LOGIN_CYPTION = 2;
    public final static byte CHECK_ACC_NAME = 3;
    public final static byte CHECK_ACC_COUNTRY = 4;
    public final static byte UPDATE_SERVER = 6;

    public final static byte MAKE_ACC = 5;
    public final static byte REMOVE_ID = 100;
    public static final byte LOGIN_OK = 2;
    public static final byte INVALID_PASSWORD = 3;
    public static final byte BANNED = 4;
    public static final byte ALREADY_ONLINE = 5;
    public static final byte WORLD_FULL = 7;
    public static final byte TRY_AGAIN = 11;
    public static final byte ERROR_LOADING_PROFILE = 24;

	
	
}
