package com.twc.eis.lib.logging;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Chris Mathew
 * @Task 560
 *
 */
public enum LoggingLevelEnum {


	DEBUG(1), INFO(2), WARN(3), ERROR(4), FATAL(5);

	public static final Map<String, Integer> logLevelLookup = new HashMap<String, Integer>();

	static {
		for (LoggingLevelEnum s : EnumSet.allOf(LoggingLevelEnum.class))
			logLevelLookup.put(s.name(), s.getLoggingLevel());
	}

	private LoggingLevelEnum(Integer logLevel) {
		this.logLevel = logLevel;
	}

	private Integer logLevel;

	public Integer getLoggingLevel() {
		return logLevel;
	}

	/*public static void main(String args[]) {

		System.out.println("value of enums : " + LoggingLevelEnum.FATAL.getLoggingLevel());
		Map<String, Integer>  logLevelMap = LoggingLevelEnum.logLevelLookup;
	      Set<Map.Entry<String, Integer>> set = logLevelMap.entrySet();

	      for (Map.Entry<String, Integer> TypeTo : set)
	      {
	         System.out.print(TypeTo.getKey() + ": ");
	         System.out.println(TypeTo.getValue());
	      }

	}*/

}
