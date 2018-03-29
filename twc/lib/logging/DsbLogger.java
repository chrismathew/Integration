package com.twc.eis.lib.logging;

import com.twc.eis.asb.core.eventnotifier.EventNotificationService;


/**
 * <p> Logger class to log the messages. The Logger will send the messages to
 * to Event Notification Services where the messages will be send to a registered topic. The dsb logger event
 * handler will listen to the topic, receive the message and process it to write to a file. </p>
 * 
 * @author Chris Mathew
 * @Task 561
 *
 */
public class DsbLogger implements ILogService {
	protected int currentLogLevel = 1;
	private String fileLocation;
	private String fileName;
	private String fileRollingType;
	private boolean isDsbLogger;
	private Integer queueCapacity;
	private String formattedDate;

	private static final String DSB_LOGGER_TOPIC_NAME = "dsblogger";
	private static EventNotificationService ens = null;
	private DsbLogEventHandler loggingEventHandler = null;
	private static DsbLogger dsblogger = null;
	protected String name;


	static public synchronized DsbLogger getInstance() {
		
		if (dsblogger == null)
		{
			dsblogger = new DsbLogger();			
		}
		
		return dsblogger;
	}  
	public DsbLogger() {

		init();

	}
	public DsbLogger(String name) {
		this.name = name;
		init();
	}
	/**
	 * <p> Initialize the Event Notification Service, load cofig properties file 
	 * and initialize the event handler and log writer. </p>
	 * 
	 * @return true
	 */
	private boolean init() {
		System.out.println("IN INIT()");
		// Get reference to ENS
		ens = EventNotificationService.getInstance();
		loadConfigProperties();
		registerEventLogListener();

		return true;
	}
	

	/**
	 * <p> Check if dsb logger or other logger implementation and 
	 * subscribe the event handler to the Topic. </p>
	 * 
	 */
	private void registerEventLogListener() {
		System.out.println("Subscribe the dsblogger to topic...");
		if (isDsbLogger) {
			DsbLogWriter logWriter = new DsbLogWriter(fileName, fileLocation,fileRollingType,true);
			loggingEventHandler = new DsbLogEventHandler(logWriter,queueCapacity);
		} else {
			DsbLogWriter logWriter = new DsbLogWriter(false);
			loggingEventHandler = new DsbLogEventHandler(logWriter,queueCapacity);
		}

		ens.subscribeToEventsFromTopic(DSB_LOGGER_TOPIC_NAME, loggingEventHandler);

	}
	
	/**
	 *<p> Add the log level, source class name, method name, and message 
	 * to the Log Event object and publish the event to dsb logger topic. </p>
	 * 
	 * @param level
	 * @param sourceClass
	 * @param sourceMethod
	 * @param msg
	 */
	public void logMsgsEvent(LoggingLevelEnum level, String sourceClass,
			String sourceMethod, String msg) {
		DsbLogEvent dsbLogEvent = new DsbLogEvent(level,DateUtils.getDateAndTime(formattedDate), sourceClass, sourceMethod, null,msg);
		ens.publishEventToTopic(dsbLogEvent, DSB_LOGGER_TOPIC_NAME);
	
	}
	
	/**
	 * <p> Add the log level, source class name, method name, message and error message 
	 * to the Log Event object and publish the event to dsb logger topic. <p>
	 * 
	 * @param level
	 * @param sourceClass
	 * @param sourceMethod
	 * @param msg
	 * @param t
	 */
	public void logMsgsEventWithException(LoggingLevelEnum level,
			String sourceClass,String sourceMethod, String msg, Throwable t) {
		DsbLogEvent dsbLogEvent = new DsbLogEvent(level,DateUtils.getDateAndTime(formattedDate),sourceClass,sourceMethod,t,msg);
		ens.publishEventToTopic(dsbLogEvent, DSB_LOGGER_TOPIC_NAME);

	}

	/**
	 * <p> Checks the logging level to determine if the logger needs to be called for the message
	 * and gets the class name, method name for the calling class. <p>
	 * 
	 * @param level
	 * @param msg
	 * @param ex
	 */
	public void log(LoggingLevelEnum level, String msg, Throwable ex) {
		
		if (isLevelEnabled(level)) {
			String className = null;
			String methodName = null;
			if(name != null) {
				className = name;
			}else {
				Throwable t = new Throwable();
				StackTraceElement methodCaller = t.getStackTrace()[2];
				className = methodCaller.getClassName();
				methodName = methodCaller.getMethodName();
			}
			if (ex == null) {
				logMsgsEvent(level, className,methodName, msg);
			} else {
				logMsgsEventWithException(level, className,methodName, msg, ex);
			}
		}

	}
	
	/**
	 * <p> Get the properties data in the dsb logger class. </p>
	 * 
	 * @return LogProperties.
	 */
	private LogProperties loadConfigProperties() {
		System.out.println("IN LOAD CONFIG PROPERTIES");
		LogProperties logProps = LoadConfigUtils.loadLoggerCfg();
		currentLogLevel = logProps.getThresholdLevel();
		fileName = logProps.getFileName();
		fileLocation = logProps.getFolderLocation();
		fileRollingType = logProps.getFileRollingType();
		isDsbLogger = logProps.getIsDsbLogger();
		queueCapacity = logProps.getQueueCapacity();
		formattedDate = logProps.getDateFormat();
		return logProps;
	}
	
	/**
	 * <p> Check the logging level. <p>
	 * 
	 * @param level
	 * @return true or false
	 */
	public boolean isLevelEnabled(LoggingLevelEnum level) {
		// System.out.println("LEVELS level.ordinal()>= currentLogLevel : " +
		if (level.getLoggingLevel() >= currentLogLevel) {
			return true;
		}
		return false;
	}
	
	/**
	 * <p>
	 * Log a message with info log level.
	 * </p>
	 * 
	 * @param message
	 *            log this message
	 */
	public void info(Object message) {
		log(LoggingLevelEnum.INFO, String.valueOf(message), null);
	}
	/**
	 * <p>
	 * Log an error with info log level.
	 * </p>
	 * 
	 * @param message
	 *            log this message
	 * @param t
	 *            log this cause
	 */
	public void info(Object message, Throwable exception) {
		log(LoggingLevelEnum.INFO, String.valueOf(message), exception);
	}
	/**
	 * <p>
	 * Log a message with debug log level.
	 * </p>
	 * 
	 * @param message
	 *            log this message
	 */
	public void debug(Object message) {
		log(LoggingLevelEnum.DEBUG, String.valueOf(message), null);
	}
	/**
	 * <p>
	 * Log an error with debug log level.
	 * </p>
	 * 
	 * @param message
	 *            log this message
	 * @param t
	 *            log this cause
	 */
	public void debug(Object message, Throwable exception) {
		log(LoggingLevelEnum.DEBUG, String.valueOf(message), exception);
	}
	/**
	 * <p>
	 * Log a message with error log level.
	 * </p>
	 * 
	 * @param message
	 *            log this message
	 */
	public void error(Object message) {
		log(LoggingLevelEnum.ERROR, String.valueOf(message), null);
	}
	/**
	 * <p>
	 * Log an error with error log level.
	 * </p>
	 * 
	 * @param message
	 *            log this message
	 * @param t
	 *            log this cause
	 */
	public void error(Object message, Throwable exception) {
		log(LoggingLevelEnum.ERROR, String.valueOf(message), null);
	}
	/**
	 * <p>
	 * Log a message with warn log level.
	 * </p>
	 * 
	 * @param message
	 *            log this message
	 */
	public void warn(Object message) {
		log(LoggingLevelEnum.WARN, String.valueOf(message), null);
	}
	/**
	 * <p>
	 * Log an error with warn log level.
	 * </p>
	 * 
	 * @param message
	 *            log this message
	 * @param t
	 *            log this cause
	 */
	public void warn(Object message, Throwable exception) {
		log(LoggingLevelEnum.WARN, String.valueOf(message), null);
	}
	/**
	 * <p>
	 * Log a message with fatal log level.
	 * </p>
	 * 
	 * @param message
	 *            log this message
	 */
	public void fatal(Object message) {
		log(LoggingLevelEnum.FATAL, String.valueOf(message), null);
	}
	/**
	 * <p>
	 * Log an error with fatal log level.
	 * </p>
	 * 
	 * @param message
	 *            log this message
	 * @param t
	 *            log this cause
	 */
	public void fatal(Object message, Throwable exception) {
		log(LoggingLevelEnum.FATAL, String.valueOf(message), null);
	}
	/**
	 * <p>
	 * Is info logging currently enabled?
	 * </p>
	 * @return true if info is enabled in the underlying logger.
	 */
	public final boolean isInfoEnabled() {
		return this.isLevelEnabled(LoggingLevelEnum.INFO);
	}
	/**
	 * <p>
	 * Is debug logging currently enabled?
	 * </p>
	 * @return true if debug is enabled in the underlying logger.
	 */
	public final boolean isDebugEnabled() {
		return this.isLevelEnabled(LoggingLevelEnum.DEBUG);
	}
	/**
	 * <p>
	 * Is error logging currently enabled?
	 * </p>
	 * @return true if error is enabled in the underlying logger.
	 */
	public final boolean isErrorEnabled() {
		return this.isLevelEnabled(LoggingLevelEnum.ERROR);
	}
	/**
	 * <p>
	 * Is warn logging currently enabled?
	 * </p>
	 * @return true if warn is enabled in the underlying logger.
	 */
	public final boolean isWarnEnabled() {
		return this.isLevelEnabled(LoggingLevelEnum.WARN);
	}
	/**
	 * <p>
	 * Is fatal logging currently enabled?
	 * </p>
	 * 
	 * @return true if fatal is enabled in the underlying logger.
	 */
	public final boolean isFatalEnabled() {
		return this.isLevelEnabled(LoggingLevelEnum.FATAL);
	}

}
