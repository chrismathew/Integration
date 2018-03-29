package org.cms.hios.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	private static final String MM_DD_YY_H_MM_A_FORMAT = "MM/dd/yyyy h:mm a";
	private static final String MM_DD_YYYY_FORMAT="MM/dd/yyyy";
	/**
	 * Converts the date to String format 8/11/2013 5:30 AM
	 * @param date
	 * @return
	 */
	public static String convertDateToMMddyyWithAMPMMarker(Date date) {
		SimpleDateFormat format =new SimpleDateFormat(MM_DD_YY_H_MM_A_FORMAT);						
		return (format.format(date));
	}
	
	/**
	 * Converts the date to String format 8/11/2013
	 * @param date
	 * @return
	 */
	public static String convertDateToMMddyyyy(Date date) {
		if (date==null)
			return null;
		SimpleDateFormat format =new SimpleDateFormat(MM_DD_YYYY_FORMAT);	
		return (format.format(date));
	}
	
	/**
	 * Determines if a given date is before the current, returning false if invalid date is given 
	 * @param date
	 * @return
	 */
	public static boolean isDateBeforeTodaysDate(String date) {
	 	SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT);
	 	Date inputDt;
	 	try {
	 		inputDt = sdf.parse(date);
	 		Date endDt =new Date();
	 		if(inputDt.before(endDt)){
				return true;
				
			}else{
				return false;
			}
	 	} catch (Exception e) {
	 		//Ignore invalid dates
	 		return false;
	 	}
	}
	
	public static boolean isFirstDateBeforeSecondDate(String dateOne, String dateTwo) {
	 	SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT);
	 	Date inputDt;
	 	Date endDt;
	 	try {
	 		inputDt = sdf.parse(dateOne);
	 		endDt = sdf.parse(dateTwo);
	 		if(inputDt.before(endDt)){
				return true;
				
			}else{
				return false;
			}
	 	} catch (Exception e) {
	 		//Ignore invalid dates
	 		return false;
	 	}
	}
	
	public static boolean isValidDateFormat(String input) {
		if (CommonUtilFunctions.isNotNullAndNotEmpty(input)) {
			String formatString = "MM/dd/yyyy";

	        try {
	            SimpleDateFormat format = new SimpleDateFormat(formatString);
	            format.setLenient(false);
	            format.parse(input);
	            return true;
	        }
	        catch (java.text.ParseException e) {
	            return false;
	        }
		}
		else {
			return false;
		}
	}
	
}
