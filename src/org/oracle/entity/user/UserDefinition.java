package org.oracle.entity.user;


/**
 *
 * @author Oska - <format@allastasia.com>
 * The definitions of the user.
 */
public class UserDefinition {

	
	/**
	 * An array of character based information, that
	 * define the user as an individual.
	 */
	private String[] definition = new String[5];
	
	
	/**
	 * An array of integer based information, 
	 * that define the user as an individual.
	 */
	private int[] values = new int[1];
	
	
	/**
	 * Creates a new set of user definitions.
	 * @param loginName
	 * @param password
	 */
	public UserDefinition(String loginName, String password, int permissionLevel) {
		setLoginName(loginName);
		setPassword(password);
		setPermissionLevel(permissionLevel);
	}
	
	
	/**
	 * Returns the loginName.
	 * @return
	 */
	public String getLoginName() {
		return definition[0];
	}
	
	
	/**
	 * Sets the loginName.
	 * @param loginName
	 */
	public void setLoginName(String loginName) {
		this.definition[0] = loginName;
	}
	
	
	/**
	 * Returns the password.
	 * @return
	 */
	public String getPassword() {
		return definition[1];
	}
	
	
	/**
	 * Sets the password.
	 * @param password
	 */
	public void setPassword(String password) {
		this.definition[1] = password;
	}
	
	
	/**
	 * Returns the email.
	 * @return
	 */
	public String getEmail() {
		return definition[2];
	}
	
	
	/**
	 * Sets the email.
	 * @param email
	 */
	public void setEmail(String email) {
		this.definition[2] = email; 
	}
	
	
	/**
	 * Returns the displayName.
	 * @return
	 */
	public String getDisplayName() {
		return definition[3];
	}
	
	
	/**
	 * Sets the displayName.
	 * @param displayName
	 */
	public void setDisplayName(String displayName) {
		this.definition[3] = displayName;
	}
	
	
	/**
	 * Returns the last connected Ip address.
	 * @return
	 */
	public String getLastConnectedIp() {
		return definition[4];
	}
	
	
	/**
	 * Sets the last connected Ip Address.
	 * @param ip
	 */
	public void setLastConnectedIp(String ip) {
		this.definition[4] = ip;
	}
	
	
	/**
	 * Returns the permission level.
	 * @return
	 */
	public int getPermissionLevel() {
		return values[0];
	}
	
	
	/**
	 * Sets the permission level.
	 * @param level
	 */
	public void setPermissionLevel(int level) {
		this.values[0] = level;
	}
}
