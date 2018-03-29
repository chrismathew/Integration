package gov.hhs.cms.base.common.util;

import gov.hhs.cms.base.common.security.util.EncryptionManager;
import gov.hhs.cms.base.common.security.util.EncryptionUtil;
import gov.hhs.cms.base.common.security.util.EncryptionUtil.Key;
import gov.hhs.cms.base.vo.ServiceHeaderVO;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.JarURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.configuration.reloading.VFSFileChangedReloadingStrategy;
import org.apache.log4j.Logger;

public class FFEConfig {
	private static final Logger LOG = Logger.getLogger(FFEConfig.class);

	private static CompositeConfiguration compositeConfiguration =  new CompositeConfiguration();
	private static List<String> moduleAwareTenants = new ArrayList<String>();
	private static List<String> ffeModuleTenants = new ArrayList<String>();
	private static final String ffeTenant = "ffe";
	private static ConcurrentHashMap<String, ConfigCache> configurationFileMap = null;
	private static EncryptionUtil encryptionUtil;

	//properties files to load from the classpath
	private static final String FFEGLOBAL_PROPS_FILE = "ffe.global.properties";
	private static final String FFE_PROPS_FILE = "ffe.properties";
	private static final String FFEKEYS_PROPS_FILE = "ffe_keys.properties";
	private static final String USER_PROPS_FILE = "user.properties";
	private static final String FFEWEB_PROPS_FILE = "ffeweb.properties";
	private static final String ENCRYPTION_RUNTIME_PROPS_FILE = "encryption-runtime.properties";
	private static final String SYSTEM_PROPERTIES = "system.properties";

	private static boolean configInitialized = false;
	
	static {
		configurationFileMap = new ConcurrentHashMap<String, ConfigCache>();
		moduleAwareTenants.add("fm");
		moduleAwareTenants.add("os");
		moduleAwareTenants.add("qm");
		moduleAwareTenants.add("usp");
		moduleAwareTenants.add("ffe");
		moduleAwareTenants.add("urr");
		moduleAwareTenants.add("eds");
		moduleAwareTenants.add("fti");
		moduleAwareTenants.add("refcode");
		moduleAwareTenants.add("dsh");
		moduleAwareTenants.add("pmapi");
		moduleAwareTenants.add("enrollmentgs");
		moduleAwareTenants.add("eligibilitygs");
		moduleAwareTenants.add("emf");
		moduleAwareTenants.add("pixpdq");
	}

	/**
	 * This first loads the properties from System.getProperties. Then it
	 * attempts to load one or more properties files from the classpath.
	 * Finally, if encryption is turned on, it initializes the EncryptionUtil
	 * and decrypts any properties that are to be treated as encrypted (see
	 * the EncryptionManager.ENCRYPTED_PROPS_NAME property).
	 */
	private static void load() throws ConfigurationException{
		synchronized (compositeConfiguration) {
			//Disable list delimiter (comma) as all properties are considered as strings
			AbstractConfiguration.setDefaultListDelimiter('|');
			
			// load all the properties file
			String[] propFiles = { SYSTEM_PROPERTIES, FFEGLOBAL_PROPS_FILE,
					ENCRYPTION_RUNTIME_PROPS_FILE, FFE_PROPS_FILE,
					FFEWEB_PROPS_FILE, FFEKEYS_PROPS_FILE, USER_PROPS_FILE };


			for (String propFile : propFiles) {
				try {
					PropertiesConfiguration con = createPropertiesConfiguration(propFile);
					compositeConfiguration.addConfiguration(con);

				} catch (ConfigurationException e) {
					// do nothing; just means that we couldn't find one of the
					// properties file
					LOG.warn("FFE properties file ("+ propFiles + ") not found. "
							+ "That's OK though if this server doesn't require those properties. "
							+ "You should check to make sure that this server doesn't require this properties file.");
				}
			}
			
			configInitialized = true;
			
			//if encryption is turned on, initialize decrypter 
			if (encryptionOn()) {
				initEncryption();
			}

			if (ffeModuleTenants == null || ffeModuleTenants.size() < 1) {
				String ffeTenantsStr = getProperty("ffe.tenants", "AK,AL,AR,AZ,CA,CO,CT,DC,DE,FL,GA,HI,IA,ID,IL,IN,KS,KY,LA,MA,MD,ME,MI,MN,MO,MS,MT,NC,ND,NE,NH,NJ,NM,NV,NY,OH,OK,OR,PA,RI,SC,SD,TN,TX,UT,VA,VT,WA,WI,WV,WY,northeast,global");
				ffeModuleTenants = Arrays.asList(ffeTenantsStr.toLowerCase().split(","));
			}
		}
	}

	
	private static PropertiesConfiguration createPropertiesConfiguration(
			String propertiesName) throws ConfigurationException {
		//special treatment to System properties as they do not reside in a prop file
		if (propertiesName.equals(SYSTEM_PROPERTIES)) {
			PropertiesConfiguration sysConfig = new PropertiesConfiguration();
			Properties sysProps = System.getProperties();
			Enumeration<String> e = (Enumeration<String>) sysProps.propertyNames();

			if (e == null) {
				throw new ConfigurationException("No system properties found");
			}
			
			while (e.hasMoreElements()) {
				String key = e.nextElement();
				sysConfig.addProperty(key, sysProps.getProperty(key));
			}
			return sysConfig;
		} else {
			PropertiesConfiguration con = new PropertiesConfiguration(propertiesName);
			VFSFileChangedReloadingStrategy f = new VFSFileChangedReloadingStrategy();
			con.setReloadingStrategy(f);
			return con;
		}
	}
	
	/**
	 * Assume encryption is enabled if the private key property is available
	 * @return
	 */
	private static boolean encryptionOn() {
		return compositeConfiguration.containsKey(Key.PRIVATE_KEY.getValue());
	}

	/**
	 * Create the EncryptionUtil object to be used by this class for decrypting property values.
	 */
	private static synchronized void initEncryption() {
		try {
			if (encryptionOn()) {
				encryptionUtil = EncryptionUtil.getInstance(null,
					EncryptionUtil.getPrivateKeyFromPkcs8DerBase64Encoded(compositeConfiguration.getString(Key.PRIVATE_KEY.getValue())),
					compositeConfiguration.getString(Key.SALT.getValue()));
			}
		} catch (NoSuchProviderException ex) {
			LOG.error("Caught NoSuchProviderException initializing EncryptionUtil so we won't be able " +
					"to read encrypted properties: " + ex.getMessage());
		} catch (NoSuchPaddingException ex) {
			LOG.error("Caught NoSuchPaddingException initializing EncryptionUtil so we won't be able " +
					"to read encrypted properties: " + ex.getMessage());
		} catch (NoSuchAlgorithmException ex) {
			LOG.error("Caught NoSuchAlgorithmException initializing EncryptionUtil so we won't be able " +
					"to read encrypted properties: " + ex.getMessage());
		} catch (InvalidKeyException ex) {
			LOG.error("Caught InvalidKeyException initializing EncryptionUtil so we won't be able " +
					"to read encrypted properties: " + ex.getMessage());
		} catch (InvalidKeySpecException ex) {
			LOG.error("Caught InvalidKeySpecException initializing EncryptionUtil so we won't be able " +
					"to read encrypted properties: " + ex.getMessage());
		}
	}

	
	/**
	 * Returns a Properties object containing the same keys as in encryptedProps, but
	 * with the values decrypted. If an individual value cannot be decrypted it is
	 * just copied over from the input properties list to the output one.
	 * @param encryptedProps
	 * @return
	 */
	public static Properties decryptProperties(Properties encryptedProps) {
		Properties decryptedProps = new Properties();

		//go through all encryptedProps key-value pairs and decrypt the value
		//for each, adding to decryptedProps as we go
		for (Entry<Object, Object> item : encryptedProps.entrySet()) {
			decryptedProps.put(
					item.getKey(),
					internalDecrypt((String)item.getKey(), (String)item.getValue()));
		}
		return decryptedProps;
	}

	/**
	 * Returns the decrypted value associated with a single key in the provided properties object.
	 * Returns null if the properties file is null or if it does not contain the key. If there was a
	 * problem decrypting the value then it returns the value as is.
	 * @param props
	 * @param key
	 * @return
	 */
	public static String decryptProperty(Properties props, String key) {
		if (props == null || !props.containsKey(key)) {
			return null;
		}

		return internalDecrypt(key, props.getProperty(key));
	}

	/**
	 * Decrypts a string property value, returning either the decrypted value or
	 * the original value if decryption fails.
	 * @param encryptedVal
	 * @return
	 */
	public static String decryptProperty(String encryptedVal) {
		return internalDecrypt("<no key provided>", encryptedVal);
	}

	/**
	 * Decrypts the provided value, returning either the decrypted value or
	 * the original value if decryption fails.
	 * @param key
	 * @param value
	 * @return
	 */
	private static String internalDecrypt(String key, String value) {
		//make sure we are initialized
		if (encryptionUtil == null) {
			initEncryption();
		}

		if (encryptionUtil!=null) {
			try {
				return encryptionUtil.decrypt(value);
			} catch (IllegalBlockSizeException ex) {
				LOG.error("Caught IllegalBlockSizeException decrypting encrypted property (" + key + "): " + ex.getMessage());
			} catch (BadPaddingException ex) {
				LOG.error("Caught BadPaddingException decrypting encrypted property (" + key + "): " + ex.getMessage());
			}
		}
		
		//return the original value
		return value;
	}

	public static Properties getProperties() {
		try {
			if (!configInitialized) {
				load();
			}
			//recreate config to take care of auto reload
			Properties config = new Properties();
			config = new Properties();
			Iterator<String> itr = compositeConfiguration.getKeys();
			if (itr == null){
				throw new Exception("No property key found");
			}
			while (itr.hasNext()) {
				String key = itr.next();
				config.put(key, compositeConfiguration.getString(key));
			}
			return config;

		} catch (Exception e) {
			throw new RuntimeException("Internal error in Property Manager", e);
		}
	}

	public static String getProperty(String propName) {
		return getProperty(propName, null, null);
	}

	public static String getProperty(String propName, String defaultVal) {
		return getProperty(propName, defaultVal, null);
	}

	public static String getProperty(String propName, ServiceHeaderVO context) {
		return getProperty(propName, null, context);
	}

	public static String getProperty(String propName, String defaultVal,
			ServiceHeaderVO context) {
		try {
			if (!configInitialized) {
				load();
			}

			// add on the suffix (if necessary)
			propName += getPropertyNameSuffix(context);

			String value = null;
			
			value = compositeConfiguration.getString(propName);
			value = (value != null) ? value : defaultVal;
			
			if ((value == null) && LOG.isDebugEnabled()) 
				LOG.debug(propName + " property not set and has a null default value. The property may be optional.");
			
			if (encryptionOn()) {
				return decryptIfEncrypted(propName, value);
			}
			else {
				return value;
			}
		} catch (Exception e) {
			throw new RuntimeException(
					"Internal error in Property Manager", e);
		}
	}

	public static boolean isModuleAwareTenant(String tenantId) {
		return moduleAwareTenants.contains(tenantId);
	}

	private static String getPropertyNameSuffix(ServiceHeaderVO context) {
		// context and tenant id can't be null
		if (context == null || context.getTenantId() == null
				|| "".equals(context.getTenantId())) {
			return "";
		}

		String tenantId = context.getTenantId().toLowerCase();

		if (moduleAwareTenants.contains(tenantId)) {
			// it's a non-ffe tenant
			return "." + tenantId;
		} else if (ffeModuleTenants.contains(tenantId)) {
			// it's an ffe tenant (ie a state) so force it to use the .ffe
			// module
			return "." + ffeTenant;
		}

		// use the default (base) property
		return "";
	}

	/**
	 * Forces a reload of all properties. However, the properties that were loaded via the
	 * JBoss properties service will not be re-loaded from the underlying file (ie it won't
	 * pickup changes in hot-deploy fashion). But for files that it reads from the classpath
	 * or straight from the filesystem, it will pick up changes to those files.
	 */
	@Deprecated
	public static synchronized void flush() {
		// no need to flush as it has auto reload on properties change. The method is here only for backward compliance
		//config.clear();
		//config = new Properties();
		// load();
	}

	public static ConfigCache getConfigurationContents(String filePath)
			throws IOException {
		return updateCacheEntry(filePath);
	}

	private static ConfigCache updateCacheEntry(String filePath)
			throws IOException {
		ConfigCache cacheEntry = configurationFileMap.get(filePath);
		// One minute hardcoded from last-modified to prevent constant-IO.
		if (cacheEntry != null
				&& cacheEntry.getLastModified() + 6000 > (new Date())
						.getTime()) {
			return cacheEntry;
		}
		URL url = FFEConfig.class.getResource(filePath);
		String resourceFilePath;
		if (url == null){
			throw new IOException("Was unable to find resource: " + filePath);
		}
		if ("file".equals(url.getProtocol())) {
			resourceFilePath = url.getFile();
		} else if ("jar".equals(url.getProtocol())) {
			JarURLConnection jarUrl = (JarURLConnection) url.openConnection();
			resourceFilePath = jarUrl.getJarFile().getName();
		} else if ("vfsfile".equals(url.getProtocol())) {
			URL fileReplacement = new URL(url.toString().replace("vfsfile:/", "file://"));
			resourceFilePath = fileReplacement.getFile();
		} else {
			throw new IllegalArgumentException("Not a file");
		}
		File file = new File(resourceFilePath);
		long currentLastModified = file.lastModified();
		if (currentLastModified == 0){
			currentLastModified = 1;
		}
		if (cacheEntry == null
				|| currentLastModified > cacheEntry.getLastModified()) {
			cacheEntry = updateCacheEntry(filePath, currentLastModified);
		}
		return cacheEntry;
	}

	private static ConfigCache updateCacheEntry(String filePath,
			long currentLastModified) throws IOException {
		ConfigCache cacheEntry = new ConfigCache();
		cacheEntry.setContents(getFileAsString(filePath));
		cacheEntry.setLastModified(currentLastModified);
		configurationFileMap.put(filePath, cacheEntry);
		return cacheEntry;
	}

	private static String getFileAsString(String filePath) throws IOException {
		InputStreamReader isr = new InputStreamReader(
				FFEConfig.class.getResourceAsStream(filePath), "UTF-8");
		BufferedReader reader = new BufferedReader(isr);
		String line = null;
		StringBuilder sb = new StringBuilder();
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}
		return sb.toString();
	}
	
	private static String decryptIfEncrypted(String key, String value) {

		String propsToEncryptCSV = compositeConfiguration.getString(Key.ENCRYPTED_PROPS.getValue());

		// see if this property value needs to be decrypted
		if (EncryptionManager.isEncryptedProp(key, propsToEncryptCSV)) {
			value = internalDecrypt(key, value);
		}

		return value;
	}
}
