package com.twc.eis.lib.logging;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Date utility
 * 
 * @author Chris Mathew
 * @Task 560
 * 
 */
public class DateUtils {

	static protected final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss:SSS zzz";
	/** The date and time format to use in the log message */
	static protected String dateTimeFormat = DEFAULT_DATE_TIME_FORMAT;
	static protected DateFormat dateFormatter = null;

	/**
	 * 
	 * @return the formatted date.
	 * 
	 */
	public static String getDate(String dateValue) {
		String logDate = null;
		try {
			Date now = new Date();
			// LogProperties props = LoadConfigUtils.loadLoggerCfg();
			String dateFormatFromProps = dateValue; 
			dateFormatter = new SimpleDateFormat(dateFormatFromProps);
			synchronized (dateFormatFromProps) {
				logDate = dateFormatter.format(now);
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			// If the format pattern is invalid - use the default format
			dateTimeFormat = DEFAULT_DATE_TIME_FORMAT;
			dateFormatter = new SimpleDateFormat(dateTimeFormat);
		}
		return logDate;
	}

	/*public static void main(String args[]){
		
		Calendar now = Calendar.getInstance();
		String dateValue = DateUtils.getDateAndTime(""); //CalendarToYMDT(now);
		System.out.println("value of date :" + dateValue);
	}*/
	public static String getDateAndTime(String dateValue) {

		Calendar now = Calendar.getInstance();

		String dateandtime = (now.get(Calendar.MONTH) + 1) + ":"
				+ now.get(Calendar.DATE) + ":" + now.get(Calendar.YEAR) + " "
				+ now.get(Calendar.HOUR_OF_DAY) + ":"
				+ now.get(Calendar.MINUTE) + ":" + now.get(Calendar.SECOND)
				+ "." + now.get(Calendar.MILLISECOND);
		// System.out.println("value of date and time : " + dateandtime);
		return dateandtime;

	}

	public static String getTimeDiff(double startTime, double endTime) {
		return getTimeDiff(startTime, endTime, 0);

	}

	public static String getTimeDiff(double startTime, double endTime,
			int timeType) {
		if (timeType == 0) {
			return getTimeDiffInSecs(startTime, endTime);
		} else if (timeType == 1) {
			return getTimeDiffInMinutes(startTime, endTime);
		} else if (timeType == 2) {
			return getTimeDiffInMilliSecs(startTime, endTime);
		} else if (timeType == 3) {
			return getTimeDiffInMicroSecs(startTime, endTime);
		} else {
			return getTimeDiffInSecs(startTime, endTime);
		}

	}

	private static String getTimeDiffInSecs(double startTime, double endTime) {
		double elapsedTime = endTime - startTime;
		double elapsedTimeInSec = (double) elapsedTime / 1000000000.0;

		return elapsedTimeInSec + " secs ";
	}

	private static String getTimeDiffInMilliSecs(double startTime,
			double endTime) {
		double elapsedTime = endTime - startTime;
		double elapsedTimeInMilliSec = (double) elapsedTime / 1000000.0;

		return elapsedTimeInMilliSec + " ms ";
	}
	
	public static String getTimeDiffInMilliSecsCalc(double startTime,
			double endTime) {
		double elapsedTime = endTime - startTime;
		double elapsedTimeInMilliSec = (double) elapsedTime / 1000000.0;

		return elapsedTimeInMilliSec + " ms ";
	}
	
	private static String getTimeInMilliSecs(double totalTime) {
		
		double totalTimeInMilliSec = (double) totalTime / 1000000.0;

		return totalTimeInMilliSec + " ms ";
	}


	private static String getTimeDiffInMicroSecs(double startTime,
			double endTime) {
		double elapsedTime = endTime - startTime;
		double elapsedTimeInMicroSec = (double) elapsedTime / 1000.0;

		return elapsedTimeInMicroSec + " microsec ";
	}

	private static String getTimeDiffInMinutes(double startTime, double endTime) {
		double elapsedTime = endTime - startTime;
		double elapsedTimeInMinutes = (double) elapsedTime * 1.66666667 / 100000000000.0;

		return elapsedTimeInMinutes + " mins ";
	}

}
