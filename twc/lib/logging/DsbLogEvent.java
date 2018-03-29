package com.twc.eis.lib.logging;

import com.twc.eis.asb.core.eventnotifier.event.AsbEvent;

/**
 * <p>
 * Dsb Log Event API to capture the log message and related data.
 * </p>
 * @author Chris Mathew
 * @Task 560
 *
 */
public class DsbLogEvent extends AsbEvent {
	
	private static final long serialVersionUID = 1L;
	
	private LoggingLevelEnum logLevel = LoggingLevelEnum.ERROR;
	
	private String msgs;
	private String timeTaken;
	
	private String logDateAndTime;
	  /**
     * @serial Class that issued logging call
     */
    private String sourceClassName;

    /**
     * @serial Method that issued logging call
     */
    private String sourceMethodName;
    
    /**
     * @serial The Throwable (if any) associated with log message
     */
    private Throwable thrown;
    
    private static DsbLogEvent dsblogevent = null;

	static public synchronized DsbLogEvent getInstance() {
		
		if (dsblogevent == null)
		{
			dsblogevent = new DsbLogEvent();			
		}
		
		return dsblogevent;
	}  
    public DsbLogEvent() {
    	
    }
	public DsbLogEvent(LoggingLevelEnum logLevel) {
		super();
		this.logLevel = logLevel;
	}
	public DsbLogEvent(LoggingLevelEnum logLevel,String message) {
		super();
		this.setMessage(message);
		this.logLevel = logLevel;
	}
	

	public DsbLogEvent(LoggingLevelEnum logLevel, String logDateAndTime,
			String sourceClassName, String sourceMethodName, Throwable thrown,String message) {
		super();
		this.logLevel = logLevel;
		this.logDateAndTime = logDateAndTime;
		this.sourceClassName = sourceClassName;
		this.sourceMethodName = sourceMethodName;
		this.thrown = thrown;
		this.setMessage(message);
	}
	
	public LoggingLevelEnum getLogLevel() {
		return logLevel;
	}
	public void setLogLevel(LoggingLevelEnum logLevel) {
		this.logLevel = logLevel;
	}

	public String getSourceClassName() {
		return sourceClassName;
	}

	public void setSourceClassName(String sourceClassName) {
		this.sourceClassName = sourceClassName;
	}
	public String getSourceMethodName() {
		return sourceMethodName;
	}

	public void setSourceMethodName(String sourceMethodName) {
		this.sourceMethodName = sourceMethodName;
	}
	
	public Throwable getThrown() {
		return thrown;
	}
	public void setThrown(Throwable thrown) {
		this.thrown = thrown;
	}
	
	public String getLogDateAndTime() {
		return logDateAndTime;
	}
	public void setLogDateAndTime(String logDateAndTime) {
		this.logDateAndTime = logDateAndTime;
	}
	
	public String getMsgs() {
		return msgs;
	}
	public void setMsgs(String msgs) {
		this.msgs = msgs;
	}
	
	public String getTimeTaken() {
		return timeTaken;
	}
	public void setTimeTaken(String timeTaken) {
		this.timeTaken = timeTaken;
	}
	public String getLogMessage() {
		return this.formatLogMessage();
	}
	private String formatLogMessage() {
		StringBuffer buf = null;
			try {
			buf = new StringBuffer();
			buf.append(this.logDateAndTime);
			buf.append(" ");
			switch (getLogLevel()) {
			case DEBUG:
				buf.append("[DEBUG] ");
				break;
			case INFO:
				buf.append("[INFO] ");
				break;
			case WARN:
				buf.append("[WARN] ");
				break;
			case ERROR:
				buf.append("[ERROR] ");
				break;
			case FATAL:
				buf.append("[FATAL] ");
				break;
			}
			//buf.append("");
			buf.append(this.sourceClassName);
			buf.append(".{");
			buf.append(this.sourceMethodName);
			buf.append("}");
			buf.append(" ");
			
			
			buf.append(String.valueOf(this.getMessage()));
			} catch (Exception ex) {

			// ex.printStackTrace();
		}
		
		return buf.toString();
	}
	


}
