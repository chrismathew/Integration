package org.cms.hios.common.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.codec.binary.Hex;

/**
 * Singleton utility class to encrypt the given text. Uses Triple DES 
 * algorithm and Apache codec implementation for encryption
 * A
 */

public class CryptographyUtil {
	
	public final static String DESEDE_ENCRYPTION_ALGORITHM = "DESede";
	public final static String TRANSFORMATION = "DESede/CBC/PKCS5Padding";
	private Cipher encryptCipher;
	private Cipher decryptCipher;

	private static CryptographyUtil instance;
	private String ENCODING = "UTF-8";
	private final static String DEFAULT_KEY = "asd@#$@#$FdFGDGG#$%#$%23123DSFSDFSDF";
	/**
	 * Factory method which will return the instance
	 * @return
	 * @throws Exception
	 */
	public static CryptographyUtil getInstance() throws Exception {
		if (instance == null) {
			instance = new CryptographyUtil();
		}
		return instance;
	}

	/**
	 * Private Constructor which reads the property file and gets the key. If 
	 * the key is not configured, takes the default key and initializes
	 * the Cipher instance for encryption
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws InvalidKeyException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeySpecException 
	 * @throws NoSuchPaddingException 
	 * @throws InvalidAlgorithmParameterException 
	 * @throws Exception
	 */
	private CryptographyUtil() throws Exception {
		//READ THE PROPERTIES FILE
		Properties prop = new Properties();
		FileInputStream str = new FileInputStream(System.getProperty("KEY_FILE"));
		String keyPhrase= DEFAULT_KEY;
		if(str!=null) {
			prop.load(str);            
			keyPhrase = prop.getProperty("KEY");
			if(keyPhrase==null || keyPhrase.trim().isEmpty()){
				keyPhrase = DEFAULT_KEY;
			}			
		}
		
		DESedeKeySpec keySpec = new DESedeKeySpec(keyPhrase.getBytes());
		SecretKey key = SecretKeyFactory.getInstance(DESEDE_ENCRYPTION_ALGORITHM).generateSecret(keySpec);
		IvParameterSpec ivParameterSpec = new IvParameterSpec(new byte[8]);
		encryptCipher = Cipher.getInstance(TRANSFORMATION);		
		encryptCipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);		
		
		decryptCipher = Cipher.getInstance(TRANSFORMATION);		
		decryptCipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);
		
		
	}
	/**
	 * Encrypts the given string and returns the encrypted string
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public String encrypt(String str) throws Exception {
		byte[] utf8 = str.getBytes(ENCODING);
		byte[] enc = encryptCipher.doFinal(utf8);
		return new String(Hex.encodeHex(enc));
	}

	/**
	 * Decrypts the given string using the DESEDE algorithm and
	 * returns the string
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public String decrypt(String str) throws Exception {		
		byte[] aa = Hex.decodeHex(str.toCharArray());
		byte[] utf8 = decryptCipher.doFinal(aa);		
		return new String(utf8);
	}
}
