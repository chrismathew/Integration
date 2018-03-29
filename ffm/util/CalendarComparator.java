package gov.hhs.cms.base.common.util;

import java.util.Comparator;

import javax.xml.datatype.XMLGregorianCalendar;

public class CalendarComparator implements Comparator<XMLGregorianCalendar> {

	public int compare(XMLGregorianCalendar calendarOne, XMLGregorianCalendar calendarTwo) {
		return DateUtil.compare(calendarOne, calendarTwo);
	}
}
