package gov.hhs.cms.base.common.util;

import java.util.Date;
import java.util.Calendar;

/**
 * The  utility class. it is used to get fiscal years based on the date period.
 * 
 * @author juzhang
 *
 */
public class FiscalCalendarUtils 
{
	private static final int FISCAL_YEAR_START_MONTH = Calendar.OCTOBER;
	private static final int FISCAL_YEAR_START_DATE = 1;
	
	/**
	 * Get fiscal years based on the date period.
	 * @param startDate the date start
	 * @param endDate the date end
	 * @return fiscal years
	 */
	public static int[] getFiscalYears(Date startDate, Date endDate)
	{		
		if (startDate == null || endDate == null)
		{
			throw new IllegalArgumentException("The start date and end date must be provided.");
		}
		
		if (startDate.after(endDate))
		{
			throw new IllegalArgumentException("The start date must be before end date.");
		}	
		
		Calendar start = Calendar.getInstance();
		start.setTime(startDate);
		
		Calendar end = Calendar.getInstance();
		end.setTime(endDate);
		
		return getFiscalYears(start, end);
	}
	
	/**
	 * Get fiscal years based on the date period. the start date is from today
	 * @param endDate the end date
	 * @return fiscal years
	 */
	public static int[] getFiscalYears(Date endDate)
	{
		
		if (endDate == null)
		{
			throw new IllegalArgumentException("The end date must be provided.");
		}
				
		Calendar startDate = Calendar.getInstance();
		return getFiscalYears(startDate.getTime(), endDate);
	}
	
	/**
	 * Get fiscal years based on the date period.
	 * @param startDate the date start
	 * @param endDate the date end
	 * @return fiscal years
	 */
	public static int[] getFiscalYears(Calendar startDate, Calendar endDate)
	{
		if (startDate == null || endDate == null)
		{
			throw new IllegalArgumentException("The start date and end date must be provided.");
		}
		
		if (startDate.after(endDate))
		{
			throw new IllegalArgumentException("The start date must be before end date.");
		}	
		
		int periodYearStart = getFiscalYear(startDate);
		int periodYearEnd = getFiscalYear(endDate);
		int[] years = new int[periodYearEnd-periodYearStart+1];
		int index =0;
		for (int i=periodYearStart; i<periodYearEnd+1; i++)
		{
			years[index++] = i;
		}
		
		return years;
	}
	

	/**
	 * Get fiscal years based on the date period. the start date is from today
	 * @param endDate the end date
	 * @return fiscal years
	 */
	public static int[] getFiscalYears(Calendar endDate)
	{
		
		if (endDate == null)
		{
			throw new IllegalArgumentException("The end date must be provided.");
		}
				
		Calendar startDate = Calendar.getInstance();
		return getFiscalYears(startDate, endDate);
	}
	

	/**
	 * Get fiscal year for this date
	 * @param date the date
	 * @return fiscal year
	 */
	public static int getFiscalYear(Date date)
	{
		if (date == null)
		{
			throw new IllegalArgumentException("The date must be provided.");
		}
		Calendar aDate = Calendar.getInstance();
		aDate.setTime(date);
		return getFiscalYear(aDate);
	}
	
	
	/**
	 * Get fiscal year for this date
	 * @param date the date
	 * @return fiscal year
	 */
	public static int getFiscalYear(Calendar date) 
	{		
		if (date == null)
		{
			throw new IllegalArgumentException("The date must be provided.");
		}
		
		int year = date.get(Calendar.YEAR);
		int mon = date.get(Calendar.MONTH);
		
		Calendar fiscalCalendar = getFiscalCalendar(year,mon);
		
		int fiscalYear=fiscalCalendar.get(Calendar.YEAR);
		
		/*int compareResult = fiscalCalendar.compareTo(date);
		if (compareResult < 0)
		{
			return year + 1;
		}
		else
		{
			return year;
		}*/
		
		return fiscalYear;
	}

	private static Calendar getFiscalCalendar(int fiscalYear, int mon) {
		Calendar startFiscalCalendar = Calendar.getInstance();
		//startFiscalCalendar.set(fiscalYear, FISCAL_YEAR_START_MONTH, FISCAL_YEAR_START_DATE);
		

		if(mon>=FISCAL_YEAR_START_MONTH) {
			
			startFiscalCalendar.set(fiscalYear+1, FISCAL_YEAR_START_MONTH,FISCAL_YEAR_START_DATE);
		}
		
		else {
			
			startFiscalCalendar.set(fiscalYear, FISCAL_YEAR_START_MONTH,FISCAL_YEAR_START_DATE);
		}
		
		return startFiscalCalendar;
	}
	
	
	
}
