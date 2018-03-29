package gov.hhs.cms.base.common.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;


/**
 * Age related methods
 * This is different from the age calculation done in DateUtil, since the latter only looks at the year difference
 * while this also takes months and days into account 
 */
public class AgeUtils {
	
	private static Logger logger = Logger.getLogger(AgeUtils.class);

	/**
	 * Returns age in years on a specific date
	 * @param birthdate
	 * @param date
	 * @return age
	 */
	public static int getAgeOnDate(Calendar birthdate, Calendar date) {
		return getAgeInMonthOnDate(birthdate, date) / 12;
	}
	
	/**
	 * Returns age in years on a specific date
	 * @param birthdate
	 * @param date
	 * @return age
	 */
	public static int getAgeOnDate(Date birthdate, Date date) {
		return getAgeInMonthOnDate(toCalendar(birthdate), toCalendar(date)) / 12;
	}

	/**
	 * Returns age on January 1st of effective date
	 * @param birthdate
	 * @param effectiveDate
	 * @return
	 */
	public static int getAgeOnJanuaryFirstOfEffectiveDate(Date birthdate, Date effectiveDate) {
		return getAgeOnJanuaryFirstOfEffectiveDate(toCalendar(birthdate), toCalendar(effectiveDate));
	}
	
	/**
	 * Returns age on January 1st of effective date
	 * @param birthdate
	 * @param effectiveDate
	 * @return
	 */
	public static int getAgeOnJanuaryFirstOfEffectiveDate(Calendar birthdate, Calendar effectiveDate) {
		Calendar c = Calendar.getInstance();
		c.set(effectiveDate.get(Calendar.YEAR), Calendar.JANUARY, 1);
		return getAgeOnDate(birthdate, c);
	}

	/**
	 * Returns age on July 1st of effective date
	 * @param birthdate
	 * @param effectiveDate
	 * @return
	 */
	public static int getAgeOnJulyFirstOfEffectiveDate(Date birthdate, Date effectiveDate) {
		return getAgeOnJulyFirstOfEffectiveDate(toCalendar(birthdate), toCalendar(effectiveDate));
	}

	/**
	 * Returns age on July 1st of effective date
	 * @param birthdate
	 * @param effectiveDate
	 * @return
	 */
	public static int getAgeOnJulyFirstOfEffectiveDate(Calendar birthdate, Calendar effectiveDate) {
		Calendar c = Calendar.getInstance();
		c.set(effectiveDate.get(Calendar.YEAR), Calendar.JULY, 1);
		return getAgeOnDate(birthdate, c);
	}

	/**
	 * age in months on date
	 * @param birthdate
	 * @param date
	 * @return age in months
	 */
	public static int getAgeInMonthOnDate(Calendar birthdate, Calendar date) {
		int result = 0;

		// years
		int ageYears = date.get(Calendar.YEAR) - birthdate.get(Calendar.YEAR);
		int ageMonths = 0;
		
		// months
		if(date.before(new GregorianCalendar(date.get(Calendar.YEAR), birthdate.get(Calendar.MONTH), birthdate.get(Calendar.DATE)))) {
			ageYears--;
			ageMonths = 12 - birthdate.get(Calendar.MONTH) + date.get(Calendar.MONTH);
		}
		else if(date.after(new GregorianCalendar(date.get(Calendar.YEAR), birthdate.get(Calendar.MONTH), birthdate.get(Calendar.DATE)))) {
			ageMonths = date.get(Calendar.MONTH) - birthdate.get(Calendar.MONTH);
		}
		//if birthdate is 2/29 and date is 3/1 of same year then add 1 extra month due to error in GregorianCalendar calender can't defference the two dates
		if(birthdate.get(Calendar.DATE) == 29 && birthdate.get(Calendar.MONTH) == 1 && date.get(Calendar.MONTH) == 2 && date.get(Calendar.DATE) ==1){
			ageMonths++;
		}
		
		// days
		GregorianCalendar tmp = new GregorianCalendar(date.get(Calendar.YEAR), date.get(Calendar.MONTH), birthdate.get(Calendar.DATE));
		if(date.before(tmp)) {
			// account for 31 day in months that have 30 or less days (one month can be 31 or 30 days) 
			if ((tmp.get(Calendar.MONTH) == date.get(Calendar.MONTH)) || (birthdate.get(Calendar.DATE) == 29 && birthdate.get(Calendar.MONTH) == 1 && date.get(Calendar.MONTH) == 1))
				ageMonths--;
		}
		
		result = ageYears * 12 + ageMonths;

		if (logger.isDebugEnabled()) 
			logger.debug("Age in month is: " + result);

		return result;
	}

	
	/**
	 * Returns age closest to a specific date 
	 * (e.g. DOB is January 1, 1980 and date in question is June 2nd 1990, then we calculate the age on January 1, 1991, since that's closer to June 2nd, 1990 than January 1, 1990 is) 
	 * @param birthdate
	 * @param date
	 * @return
	 */
	public static int getAgeClosestToDate(Date birthdate, Date date) {
		return getAgeClosestToDate(toCalendar(birthdate), toCalendar(date));
	}	
	
	/**
	 * Returns age closest to a specific date 
	 * (e.g. DOB is January 1, 1980 and date in question is June 2nd 1990, then we calculate the age on January 1, 1991, since that's closer to June 2nd, 1990 than January 1, 1990 is) 
	 * @param birthdate
	 * @param date
	 * @return
	 */
	public static int getAgeClosestToDate(Calendar birthdate, Calendar date) {
		int result;

		// set year of DOB to year of effective date		
		Calendar tmp = Calendar.getInstance();
		tmp.setTime(date.getTime());
		tmp.set(Calendar.YEAR, date.get(Calendar.YEAR));

		// two birth dates enclosing the effective date
		Calendar birthDate2;

		// birth date is further away than effective date
		if (tmp.getTime().getTime() - date.getTime().getTime() >= 0) {
			birthDate2 = new GregorianCalendar(birthdate.get(Calendar.YEAR) - 1, birthdate.get(Calendar.MONTH), birthdate.get(Calendar.DATE));
		}
		// birth date is before effective date
		else {
			birthDate2 = new GregorianCalendar(birthdate.get(Calendar.YEAR) + 1, birthdate.get(Calendar.MONTH), birthdate.get(Calendar.DATE));			
		}

		long distanceToDOB1 = date.getTime().getTime() - birthdate.getTime().getTime();
		long distanceToDOB2 = date.getTime().getTime() - birthDate2.getTime().getTime();

		if (distanceToDOB1 < 0)
			distanceToDOB1 = -distanceToDOB1;
		if (distanceToDOB2 < 0)
			distanceToDOB2 = -distanceToDOB2;

		// choose birth date nearest to effective date
		if (distanceToDOB1 < distanceToDOB2)
			result = getAgeOnDate(birthdate, date);
		else
			result = getAgeOnDate(birthDate2, date);

		return result;
	}
	
	// for date arguments
	private static Calendar toCalendar(Date d) {
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		// just in case the date comes w/ a time, clear the time
		c.set(Calendar.HOUR, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c;
	}
	
}
