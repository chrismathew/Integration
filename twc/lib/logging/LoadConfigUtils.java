package com.twc.eis.lib.logging;

import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.twc.eis.asb.config.ConfigElement;
import com.twc.eis.asb.config.ConfigManagerFactory;
import com.twc.eis.asb.config.IConfigManager;
import com.twc.eis.lib.xml.Element;
/**
 * <p> Load Config Utils is used to read the logging specific properties file and set the values 
 * to a value object. </p>
 * 
 * @author Chris Mathew
 * @Task 561
 *
 */
public class LoadConfigUtils {

	public static LogProperties loadLoggerCfg() {

		Element config = null;
		ConfigElement ele = new ConfigElement();
		Properties props = null;
		Properties props2 = null;
		Properties props3 = null;
		
		LogProperties logProps = null;
		try {
		    
		    	IConfigManager mgr = ConfigManagerFactory.getConfigManagerInstance();
		    	String _config = mgr.getConfig("logger", "class");
		    	config = Element.parse(_config.toString());
			System.out.println("COMING IN LOAD CONFI UTILS");
			logProps = new LogProperties();

			if (config != null) {
				ele.init(config);
			}

			Element ea1 = ele.getPropertyList("dsblogger");
			props = ele.getAsPropertiesObj(ea1);

			Element ea2 = ele.getPropertyList("loggertype");
			props2 = ele.getAsPropertiesObj(ea2);

			logProps.setLoggerType(props.getProperty("dsblogging.type"));
			logProps.setDateFormat(props.getProperty("dsblogging.datePattern"));
			logProps.setFileName(props.getProperty("dsblogging.filename"));
			logProps.setFolderLocation(props
					.getProperty("dsblogging.folderLocation"));
			logProps.setFileRollingType(props.getProperty("dsblogging.rollingType"));
			String thresholdLevel = props.getProperty("dsblogging.threshold");
			Map<String, Integer> logLevelMap = LoggingLevelEnum.logLevelLookup;
			Set<Map.Entry<String, Integer>> set = logLevelMap.entrySet();

			for (Map.Entry<String, Integer> level : set) {
				if (level.getKey().equals(thresholdLevel)) {
					logProps.setThresholdLevel(level.getValue());
				}
			}
			logProps.setIsDsbLogger(new Boolean(props2
					.getProperty("isDsbLogger")));
			
			Element ea3 = ele.getPropertyList("logqueue");
			props3 = ele.getAsPropertiesObj(ea3);
			logProps.setQueueCapacity(new Integer(props3.getProperty("queue.size")));
			
		} catch (Exception e) {

		}
		return logProps;
	}
	
	public static LogProperties loadLoggerResources() {
		LogProperties logProps = null;
		try {
			logProps = new LogProperties();
			Properties props = new Properties();
			URL url = ClassLoader.getSystemResource("dsblogger.properties");

			props.load(url.openStream());
			logProps.setLoggerType(props.getProperty("dsblogging.type"));
			logProps.setDateFormat(props.getProperty("dsblogging.datePattern"));
			logProps.setFileName(props.getProperty("dsblogging.filename"));
			logProps.setFolderLocation(props
					.getProperty("dsblogging.folderLocation"));
			logProps.setFileRollingType(props.getProperty("dsblogging.rollingType"));
			String thresholdLevel = props.getProperty("dsblogging.threshold");
			Map<String, Integer> logLevelMap = LoggingLevelEnum.logLevelLookup;
			Set<Map.Entry<String, Integer>> set = logLevelMap.entrySet();

			for (Map.Entry<String, Integer> level : set) {
					if (level.getKey().equals(thresholdLevel)) {
					logProps.setThresholdLevel(level.getValue());
				}
			}
			logProps.setIsDsbLogger(new Boolean(props.getProperty("isDsbLogger")));
			logProps.setQueueCapacity(new Integer(props.getProperty("queue.size")));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return logProps;
	}
	
}
