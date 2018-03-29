package com.twc.eis.lib.logging;

/**
 * 
 * <p> A simple logging interface abstracting logging APIs. </p>
 * <p>
 * The five logging levels used by <code>Log</code> are (in order):
 * <ol>
 * <li>debug</li>
 * <li>info</li>
 * <li>warn</li>
 * <li>error</li>
 * <li>fatal</li>
 * </ol>
 * </p>
 * 
 * @author Chris Mathew
 * @Task 560
 * 
 */
public interface ILogService {
	/**
	 * <p>
	 * Is info logging currently enabled?
	 * </p>
	 * @return true if info is enabled in the underlying logger.
	 */
	public boolean isInfoEnabled();

	/**
	 * <p>
	 * Is debug logging currently enabled?
	 * </p>
	 * @return true if debug is enabled in the underlying logger.
	 */
	public boolean isDebugEnabled();

	/**
	 * <p>
	 * Is error logging currently enabled?
	 * </p>
	 * @return true if error is enabled in the underlying logger.
	 */
	public boolean isErrorEnabled();

	/**
	 * <p>
	 * Is warn logging currently enabled?
	 * </p>
	 * @return true if warn is enabled in the underlying logger.
	 */
	public boolean isWarnEnabled();

	/**
	 * <p>
	 * Is fatal logging currently enabled?
	 * </p>
	 * 
	 * @return true if fatal is enabled in the underlying logger.
	 */
	public boolean isFatalEnabled();

	/**
	 * <p>
	 * Log a message with info log level.
	 * </p>
	 * 
	 * @param message
	 *            log this message
	 */
	public void info(Object message);

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
	public void info(Object message, Throwable t);

	/**
	 * <p>
	 * Log a message with debug log level.
	 * </p>
	 * 
	 * @param message
	 *            log this message
	 */
	public void debug(Object message);

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
	public void debug(Object message, Throwable t);

	/**
	 * 
	 * @param message
	 */
	public void error(Object message);

	/**
	 * 
	 * @param message
	 * @param t
	 */
	public void error(Object message, Throwable t);

	/**
	 * <p>
	 * Log a message with warn log level.
	 * </p>
	 * 
	 * @param message
	 *            log this message
	 */
	public void warn(Object message);

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
	public void warn(Object message, Throwable t);

	/**
	 * <p>
	 * Log a message with fatal log level.
	 * </p>
	 * 
	 * @param message
	 *            log this message
	 */
	public void fatal(Object message);

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
	public void fatal(Object message, Throwable t);

}
