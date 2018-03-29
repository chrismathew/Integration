package gov.hhs.cms.base.common.util;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.bind.DatatypeConverter;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class DateUtil {

	private static DatatypeFactory datatypeFactory = null;

	static {
		try {
			datatypeFactory = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException dce) {
			throw new IllegalStateException("Exception while obtaining DatatypeFactory instance", dce);
			// TODO Need to analyze the effect on this in BRMS?
		}
	}

	public static Date getLatestDate(List<Date> dates) {
		return Collections.max(dates, new DateComparator());
	}

	public static Date getEarliestDate(List<Date> dates) {
		return Collections.min(dates, new DateComparator());
	}

	public static int compare(Date date1, Date date2) {
		return date1.compareTo(date2);
	}

	public static int compare(XMLGregorianCalendar date1, XMLGregorianCalendar date2) {

		return date1.toGregorianCalendar().compareTo(date2.toGregorianCalendar());
	}

	public static boolean determineEqual(Date clockEndDate) {

		Date date = getDateAfterDays(new Date(), -1);
		boolean result = false;
		Calendar calDate = Calendar.getInstance();
		calDate.setTime(date);
		Calendar calTimerDate = Calendar.getInstance();
		calTimerDate.setTime(clockEndDate);
		if (calTimerDate.get(Calendar.YEAR) == calDate.get(Calendar.YEAR)) {
			if (calTimerDate.get(Calendar.MONTH) == calDate.get(Calendar.MONTH)) {
				if (calTimerDate.get(Calendar.DATE) == calDate.get(Calendar.DATE)) {
					result = true;
				}
			}
		}
		return result;
	}

	public static int calculateAge(Date dob) {
		// this method calculates the real age
		Date date = new Date();
		Calendar calDate = Calendar.getInstance();
		calDate.setTime(date);
		Calendar calDobDate = Calendar.getInstance();
		calDobDate.setTime(dob);
		int age = calDate.get(Calendar.YEAR) - calDobDate.get(Calendar.YEAR);
		int month = calDate.get(Calendar.MONTH) - calDobDate.get(Calendar.MONTH);
		if (month < 0) {
			age = age - 1;
		} else if (month == 0) {
			int day = calDate.get(Calendar.DATE) - calDobDate.get(Calendar.DATE);
			if (day < 0) {
				age = age - 1;
			}
		}
		return age;
	}

	public static short calculateAge(Date dob, Date fromDate) {
		// this method calculates the coverage age for applicant
		Calendar calDobDate = Calendar.getInstance();
		Calendar calFromDate = Calendar.getInstance();
		calFromDate.setTime(fromDate);
		calDobDate.setTime(dob);
		int age = calFromDate.get(Calendar.YEAR) - calDobDate.get(Calendar.YEAR);
		int month = calFromDate.get(Calendar.MONTH) - calDobDate.get(Calendar.MONTH);
		if (month < 0) {
			age = age - 1;
		} else if (month == 0) {
			int day = calFromDate.get(Calendar.DATE) - calDobDate.get(Calendar.DATE);
			if (day < 0) {
				age = age - 1;
			}
		}
		Integer ageObj = Integer.valueOf(age);
		return ageObj.shortValue();
	}

	public static short calculateShortAge(Date dob) {

		Date date = new Date();
		Calendar calDate = Calendar.getInstance();
		calDate.setTime(date);
		Calendar calDobDate = Calendar.getInstance();
		calDobDate.setTime(dob);
		int age = calDate.get(Calendar.YEAR) - calDobDate.get(Calendar.YEAR);
		int month = calDate.get(Calendar.MONTH) - calDobDate.get(Calendar.MONTH);
		if (month < 0) {
			age = age - 1;
		} else if (month == 0) {
			int day = calDate.get(Calendar.DATE) - calDobDate.get(Calendar.DATE);
			if (day < 0) {
				age = age - 1;
			}
		}
		Integer ageObj = Integer.valueOf(age);
		return ageObj.shortValue();
	}

	public static Date getDateAfterMonthes(Date startDate, int month) {
		Date dateAfterMonthes = null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		cal.add(Calendar.MONTH, month);
		dateAfterMonthes = cal.getTime();
		return dateAfterMonthes;
	}

	public static XMLGregorianCalendar toXMLGregorianCalendar(Date date) {
		if (date == null) {
			return null;
		} else {
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.setTimeInMillis(date.getTime());
			return datatypeFactory.newXMLGregorianCalendar(calendar);
		}
	}

	public static java.util.Date toDate(XMLGregorianCalendar xgc) {
		if (xgc == null) {
			return null;
		} else {
			return xgc.toGregorianCalendar().getTime();
		}
	}

	public static Date getApplicantPostpartumEndDate(Date deliveryDate) {
		return getApplicantPostpartumEndDate (deliveryDate, 60);
	}

	public static Date getApplicantPostpartumEndDate(Date deliveryDate, BigInteger period) {
		return getApplicantPostpartumEndDate (deliveryDate, period.intValue());
	}
	
	public static Date getApplicantPostpartumEndDate(Date deliveryDate, int postPartumPeriod) {

		Date applicantPostpartumEndDate = null;

		// Convert Date to Calendar
		Calendar cal = Calendar.getInstance();
		cal.setTime(deliveryDate);

		// Find the last day of the month and set the new date
		int lastDayofMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		cal.set(Calendar.DATE, lastDayofMonth);

		cal.add(Calendar.DATE, postPartumPeriod);

		// convert Calendar to Date
		applicantPostpartumEndDate = cal.getTime();

		return applicantPostpartumEndDate;
	}
	
	public static Date getDateAfterDays(Date startDate, int day) {
		Date dateAfterDays = null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		cal.add(Calendar.DATE, day);
		dateAfterDays = cal.getTime();
		return dateAfterDays;
	}

	public static Date getDateAfterDays(Date startDate, int day1, int day2) {
		Date dateAfterDays = null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		cal.add(Calendar.DATE, day1);
		cal.add(Calendar.DATE, day2);
		dateAfterDays = cal.getTime();
		return dateAfterDays;
	}

	public static void writeToFile(String writeThis, String inThisLocation) {
		java.io.BufferedWriter bufferW = null;
		try {
			bufferW = new java.io.BufferedWriter(new java.io.FileWriter(inThisLocation));
			bufferW.write(writeThis);
			bufferW.close();
		} catch (Exception ex) {
		} finally {
			if(bufferW != null) {
				try {
					bufferW.close();
				} catch (Exception e) {
				}
			}
		}
	}

	public static Date getFirstDayOfFollowingMonth(Date date) {

		Date firstDayOfFollowingMonth = null;

		// Convert Date to Calendar
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		int firstDay = 1;
		int currentMonth = cal.get(Calendar.MONTH);
		cal.set(Calendar.DATE, firstDay);
		cal.set(Calendar.MONTH, currentMonth + 1);

		// convert Calendar to Date
		firstDayOfFollowingMonth = cal.getTime();

		return firstDayOfFollowingMonth;
	}

	public static Date getFirstDayOfFollowingYear(Date date) {

		Date firstDayOfFollowingYear = null;

		// Convert Date to Calendar
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		int firstDay = 1;
		int firstMonth = 0;
		int currentYear = cal.get(Calendar.YEAR);
		cal.set(Calendar.DATE, firstDay);
		cal.set(Calendar.MONTH, firstMonth);
		cal.set(Calendar.YEAR, currentYear + 1);

		// convert Calendar to Date
		firstDayOfFollowingYear = cal.getTime();

		return firstDayOfFollowingYear;
	}

	public static Date getFirstDayOfSecondFollowingMonth(Date date) {

		Date firstDayOfSecondFollowingMonth = null;

		// Convert Date to Calendar
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		int firstDay = 1;
		int currentMonth = cal.get(Calendar.MONTH);
		cal.set(Calendar.DATE, firstDay);
		cal.set(Calendar.MONTH, currentMonth + 2);

		// convert Calendar to Date
		firstDayOfSecondFollowingMonth = cal.getTime();

		return firstDayOfSecondFollowingMonth;
	}


	public static Date getLastDayOfCurrentMonth() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		setTimeToEndofDay(cal);
		return cal.getTime();
	}

	private static void setTimeToEndofDay(Calendar calendar) {
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
	}

	public static Date getDateAfterBigDays(Date startDate, BigInteger bigDay, boolean negate) {
		Date dateAfterDays = null;
		int day = bigDay.intValue();
		if (negate == true) {
			dateAfterDays = getDateAfterDays(startDate, -day);
		} else {
			dateAfterDays = getDateAfterDays(startDate, day);
		}

		return dateAfterDays;
	}

    // input date is in format "dd-MMM-yyyy"
    public static Date getDateFromString(String date) {

          Date myDate = null;
          SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
          try {
                myDate = sdf.parse(date);
          } catch(ParseException expt) {
        	  try {
	        	  Calendar calendar = DatatypeConverter.parseDateTime(date);
	        	  myDate = calendar.getTime();
        	  }
        	  catch (IllegalArgumentException iaEx) {
        		  expt.printStackTrace();
        		  iaEx.printStackTrace();
        	  }
          }
          return myDate;
    }
    
 /**
  * returns the date based on a given format string
  * @param date as string
  * @param format formatter
  * @return Date
  */
    public static Date getDateFromString(String date, String format) {
    	Date myDate = null;
    	SimpleDateFormat sdf = new SimpleDateFormat(format);
    	try {
    		myDate = sdf.parse(date);
    	} catch(ParseException expt) {
    		try {
    			Calendar calendar = DatatypeConverter.parseDateTime(date);
    			myDate = calendar.getTime();
    		}
    		catch (IllegalArgumentException iaEx) {
    			expt.printStackTrace();
    			iaEx.printStackTrace();
    		}
    	}
    	return myDate;
    }
    
    // convert date to a string of input format
    public static String getStringFromDate(Date date, String format) {
    	
    	String stringDate = null;
    	if(!UtilFunctions.isEmptyObject(date) && UtilFunctions.isNotNullAndNotEmpty(format)) {
    		DateFormat formatter = new SimpleDateFormat(format);
    		stringDate = formatter.format(date);
    	}
    	
    	return stringDate;
    }
    
    // change format of date according to input format
    public static Date changeDateFormat(Date date, String format) {
    	
    	Date newDate = null;
    	if(!UtilFunctions.isEmptyObject(date) && UtilFunctions.isNotNullAndNotEmpty(format)) {
    		DateFormat formatter = new SimpleDateFormat(format);
    		try {
				newDate = formatter.parse(formatter.format(date));
			} catch (ParseException expt) {
				expt.printStackTrace();
			}
    	}
    	
    	return newDate;
    }
    
    public static Date getFifteenthDayOfCurrentMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 15);
        setTimeToEndofDay(cal);
        return cal.getTime();
    }

    public static Date getLastDayOfYear(Date date) {

        Date lastDayOfYear = null;

        // Convert Date to Calendar
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.MONTH, cal.getActualMaximum(Calendar.MONTH));

        setTimeToEndofDay(cal);

        // convert Calendar to Date
        lastDayOfYear = cal.getTime();

        return lastDayOfYear;
    }

    public static Date getFifteenthDayOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        cal.set(Calendar.DAY_OF_MONTH, 15);
        setTimeToEndofDay(cal);
        return cal.getTime();
    }

    // to get the first day of this month
    public static Date getFirstDayOfThisMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
		setTimeToStartOfDay(cal);
		return cal.getTime();
	}

    // to get the last day of this month
    public static Date getLastDayOfThisMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		setTimeToEndofDay(cal);
		return cal.getTime();
	}

    // to get the last day of previous month
    public static Date getLastDayOfPreviousMonth(Date date) {
    	Calendar cal = Calendar.getInstance();
		cal.setTime(date);
    	int currentMonth = cal.get(Calendar.MONTH);
		cal.set(Calendar.MONTH, currentMonth - 1);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		setTimeToEndofDay(cal);
		return cal.getTime();
	}

    // set time to start of day
	private static void setTimeToStartOfDay(Calendar calendar) {
		calendar.set(Calendar.HOUR_OF_DAY, 00);
		calendar.set(Calendar.MINUTE, 00);
		calendar.set(Calendar.SECOND, 00);
		calendar.set(Calendar.MILLISECOND, 000);
	}

	// give a date and get the year from it
	public static int getYear(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int year = cal.get(Calendar.YEAR);
		return year;
	}
	
	// calculate gap in months between two dates
	public static int getGapInMonths(Date start, Date end) {
		Calendar calStart = Calendar.getInstance();
		Calendar calEnd = Calendar.getInstance();
		calStart.setTime(start);
		calEnd.setTime(end);
		int gap = 0;
		if(calEnd.get(Calendar.YEAR) == calStart.get(Calendar.YEAR)) {
			gap = calEnd.get(Calendar.MONTH) - calStart.get(Calendar.MONTH);
		} else if(calEnd.get(Calendar.YEAR) > calStart.get(Calendar.YEAR)){
			int temp = 0;
			temp = (calEnd.get(Calendar.YEAR) - calStart.get(Calendar.YEAR))*12;
			gap = calEnd.get(Calendar.MONTH) - calStart.get(Calendar.MONTH) + temp;
		}
		if(calEnd.get(Calendar.DAY_OF_MONTH) == calEnd.getActualMaximum(Calendar.DAY_OF_MONTH)) {
			gap = gap + 1;
		}
		return gap;
	}
	
	// set date to first day of year
    public static Date getFirstDayOfYear(Date date) {

        Date firstDayOfYear = null;

        // Convert Date to Calendar
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.MONTH, cal.getActualMinimum(Calendar.MONTH));

        setTimeToStartOfDay(cal);

        // convert Calendar to Date
        firstDayOfYear = cal.getTime();

        return firstDayOfYear;
    }

	public static Date getCurrentDate() {
		return new Date();
	}
    
	private static Date dateTimeToDate(Date in) {
		if(in == null) {
			return null;
		}
        Calendar cal = Calendar.getInstance();
        cal.setTime(in);
        cal.set(Calendar.HOUR_OF_DAY, 00);
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 00);
        cal.set(Calendar.MILLISECOND, 000);
        
        return cal.getTime();
	}	
}