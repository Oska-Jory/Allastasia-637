package org.oracle.data.security;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * 
 * @author Oska - <format@allastasia.com>
 * Decrypts data into its original form.
 */
public class DataDecryption {
	
	
	/**
	 * The newly encrypted data.
	 */
	//private Object decrypted_data;
	
	
	/**
	 * Represents the decrypted data.
	 */
	private Object encryptedData;


	/**
	 * Creates a new system to decrypt given encrypted data.
	 * @param encryptedData
	 */
	public DataDecryption(Object encryptedData) {
		this.setEncryptedData(encryptedData);
	}
	
	public static final String[] EncryptionChars = {
		"æ", "©", "ç", ".", "'", "#", "$", "_", "-",
		"|", ":", ";", "*", "?", "&", "!", ">", "<", 
		"~", "`", "¢", "Þ", "Ó", "Ö", "É", "é", "ó", "ä",
		"Ä", "ñ", "Ñ", "µ", "ß", "§", "Ð","ð", "ø", "Ø", 
		"´", "¨", "á", "’", "½", "€", "%", "³", "£", "²", 
		"¹", "¡", "¥", "×", "÷", "¬", "¦", "»", "«"};
	
	private static final String[] rawChars = {
		"1", "2", "3", "4", "5", "6", "7", "8", "9", "0", 
		"q", "w", "e", "r", "t", "y", "u", "i", "o", "p", "a",
		"s", "d", "f", "g", "h", "j", "k", "l", "z", "x", "c",
		"v", "b", "n", "m"
	};
		

	public String decrypt(Object encryptedData, int process) {
		String decryptedData = "";
		String keys = "";
		try {
			BufferedReader reader = new BufferedReader(new FileReader("./data/EncryptionKeys.txt"));
			
			if (process != 0) {
				for (int i = 0; i < process; i++) {
					keys = reader.readLine();
				}
			} else {
				keys = reader.readLine();
			}
			reader.close();
			
			char[] decryptionKeys = new char[rawChars.length];
			
			for (int i = 0; i < rawChars.length; i++) 
				decryptionKeys[i] = keys.charAt(i);
			
			for (int i = 0; i < ((String) encryptedData).length(); i++) {
				for (int encryptedChar = 0; encryptedChar < decryptionKeys.length; encryptedChar++) {
					if (((String)encryptedData).charAt(i) == decryptionKeys[encryptedChar]) {
						decryptedData += "" + rawChars[encryptedChar]; 
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return decryptedData;
	}


	/**
	 * @return the encryptedData
	 */
	public Object getEncryptedData() {
		return encryptedData;
	}


	/**
	 * @param encryptedData the encryptedData to set
	 */
	public void setEncryptedData(Object encryptedData) {
		this.encryptedData = encryptedData;
	}
}
