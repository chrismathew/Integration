
package com.twc.eis.lib.logging;

import org.apache.log4j.Logger;

import com.twc.eis.asb.core.management.metrics.util.DailyRollingFileLogger;
import com.twc.eis.asb.core.management.metrics.util.HourlyRollingFileLogger;

/**
 * 
 * <p> Class to get the messages from the dsb log queue and write it to a log file. </p>
 * 
 * @author Chris Mathew
 * @Task 560
 * 
 */
public class DsbLogWriter implements Runnable {

	private static final String DSB_LOGGER = "com.twc";
	private static final String DSB_LOG_DEBUG = "DEBUG";
	private static final String DSB_LOG_INFO = "INFO";
	private static final String DSB_LOG_WARN = "WARN";
	private static final String DSB_LOG_ERROR = "ERROR";
	private static final String DSB_LOG_FATAL = "FATAL";
	
	private static final Logger LOG4J = Logger.getLogger(DsbLogWriter.class);
	private static final String ROLLING_TYPE_HOURLY = "hourly";
	private HourlyRollingFileLogger hourlyLogger = null;
	private DailyRollingFileLogger dailyLogger = null;
	private String fileName = null;
	private String folderLoc = null;
	private Boolean isDsbLogger;
	private DsbLogQueue queue = null;
	//TODO
	private String fileRollingType = null;

	public DsbLogWriter(Boolean isDsbLogger) {
		this.isDsbLogger = isDsbLogger;
	}

	public DsbLogWriter(String fileName, String folderLocation,String fileRollingType,
			Boolean isDsbLogger) {
		this.fileName = fileName;
		this.folderLoc = folderLocation;
		this.fileRollingType = fileRollingType;
		this.isDsbLogger = isDsbLogger;
		
	}

	

	public DsbLogQueue getQueue() {
		return queue;
	}

	public void setQueue(DsbLogQueue queue) {
		this.queue = queue;
	}

	/**
	 * Writes the log messages to a Log4j or DSB implementation of the writer to a file.
	 */
	
	public void run() {
		while (true) {
			try {
				
				DsbLogEvent asbEvent = (DsbLogEvent) queue.getAsbEvent();
				if (isDsbLogger) {
					if(fileRollingType.equals(ROLLING_TYPE_HOURLY)){
						hourlyLogger = new HourlyRollingFileLogger(fileName, folderLoc);
						hourlyLogger.log(asbEvent.getLogMessage());
					}else {
						dailyLogger = new DailyRollingFileLogger(fileName, folderLoc);
						dailyLogger.log(asbEvent.getLogMessage());
					}
					
				} else {
					
					if(asbEvent.getLogLevel().toString().equals(DSB_LOG_DEBUG) ) {
						  LOG4J.debug(asbEvent.getLogMessage());
					  }
					  if(asbEvent.getLogLevel().toString().equals(DSB_LOG_INFO) ) {
						  LOG4J.info(asbEvent.getLogMessage());
					  }
					  if(asbEvent.getLogLevel().toString().equals(DSB_LOG_WARN) ) {
						  LOG4J.warn(asbEvent.getLogMessage());
					  }
					  if(asbEvent.getLogLevel().toString().equals(DSB_LOG_ERROR) ) {
						  LOG4J.error(asbEvent.getLogMessage());
					  }
					  if(asbEvent.getLogLevel().toString().equals(DSB_LOG_FATAL) ) {
						  LOG4J.fatal(asbEvent.getLogMessage());
					  }
					

				}
				asbEvent = null;
			} catch (Exception ex) {
				ex.printStackTrace();
				// LOG.fatal("Exception while persisting event.");
			}
		}
	}
	

}
