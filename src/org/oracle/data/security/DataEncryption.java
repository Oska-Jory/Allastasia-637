package org.oracle.data.security;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


/**
 * 
 * @author Oska - <format@allastasia.com>
 * Encrypts data with replacement characters.
 */
public class DataEncryption {
	
	
	/**
	 * Represents the raw data.
	 */
	private Object rawData;


	/**
	 * Creates a new system to encrypt given raw data.
	 * @param rawData
	 */
	public DataEncryption(Object rawData) {
		this.setRawData(rawData);
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
		

	public String encrypt(String rawData, int process) {
		String keys = "";
		String encryptedData = "";
		try {
			BufferedReader reader = new BufferedReader(new FileReader("./data/EncryptionKeys.txt"));
			if (process > 0) {
				for (int i = 0; i < process; i++) {
					keys = reader.readLine();
				}
			} else {
				keys = reader.readLine();
			}
			reader.close();
			
			char[] encryptionKeys = new char[keys.length() - 1];
			
			for (int i = 0; i < encryptionKeys.length; i++)  
				encryptionKeys[i] = keys.charAt(i);
			
			for (int i = 0; i < rawData.length(); i++) {
				for (int rawChar = 0; rawChar < rawChars.length; rawChar++) {
					if (rawChars[rawChar].charAt(0) == rawData.charAt(i)) 
						encryptedData += "" + encryptionKeys[rawChar];
				}
			}			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return encryptedData;
	}


	/**
	 * @return the rawData
	 */
	public Object getRawData() {
		return rawData;
	}


	/**
	 * @param rawData the rawData to set
	 */
	public void setRawData(Object rawData) {
		this.rawData = rawData;
	}
}
