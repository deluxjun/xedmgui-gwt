package com.speno.xedm.gui.common.client.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.googlecode.gwt.crypto.bouncycastle.DataLengthException;
import com.googlecode.gwt.crypto.bouncycastle.InvalidCipherTextException;
import com.googlecode.gwt.crypto.client.TripleDesCipher;

/**
 * Utility class to encrypt/decrypt strings
 * 
 * @author deluxjun
 */
public class StringEncrypter {
	private static Logger logger = Logger.getLogger("");

	// 24 byte
    public static String key_DESede()
    {
    	return "XEDMSUITEKEY012345678901";
    }
    
    
	public static String encryptDESede(String value){
		TripleDesCipher cipher = new TripleDesCipher();
		cipher.setKey(key_DESede().getBytes());
		try {
			String enc = cipher.encrypt(new String(value.getBytes("UTF-8")));
			String base = Base64Utils.toBase64(enc.getBytes());
			
			return base;

		} catch (DataLengthException e1) {
			logger.log(Level.SEVERE, e1.getMessage(), e1);
		} catch (IllegalStateException e1) {
			logger.log(Level.SEVERE, e1.getMessage(), e1);
		} catch (InvalidCipherTextException e1) {
			logger.log(Level.SEVERE, e1.getMessage(), e1);
		} catch (java.io.UnsupportedEncodingException e1) {
			logger.log(Level.SEVERE, e1.getMessage(), e1);
		}
		
		return "";
	}

	
	// java.security emul (crypto-gwt.jar « ø‰«‘)
	public static String encryptSHA256(String original) {

		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] digest = md.digest(original.getBytes());

			StringBuffer copy = new StringBuffer(digest.length * 2);
			for (int i = 0; i < digest.length; i++) {
				copy.append(Integer.toHexString(digest[i] & 0xFF));
			}
			return copy.toString();
			
		} catch (NoSuchAlgorithmException nsae) {
			GWT.log(nsae.getMessage(), nsae);
		}

		return "";
	}
}
