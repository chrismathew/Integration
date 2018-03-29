package gov.hhs.cms.base.common.util;

import java.util.Comparator;
import java.util.Date;

public class DateComparator implements Comparator<Date> {

	public int compare(Date dateOne, Date dateTwo) {
		return DateUtil.compare(dateOne, dateTwo);
	}
}
