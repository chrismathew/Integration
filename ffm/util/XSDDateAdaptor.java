package gov.hhs.cms.base.common.util;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Calendar;
import java.util.Date;
import javax.xml.bind.DatatypeConverter;

public class XSDDateAdaptor {

        public static Date parseDateTime(String s) {
                if (s == null || s.length() == 0) {
                        return null;
                }
                return DatatypeConverter.parseDateTime(s).getTime();
        }
        
        public static Date parseDate(String s) {
                if (s == null || s.length() == 0) {
                        return null;
                }
                return DatatypeConverter.parseDateTime(s).getTime();
        }

        public static Date parseDateOnly(String s) {
                if (s == null || s.length() == 0) {
                        return null;
                }
                return DatatypeConverter.parseDate(s).getTime();
        }

        public static String printDate(Date dt) {
                Calendar cal = new GregorianCalendar();

                if (dt != null) {
                        cal.setTime(dt);
                        return DatatypeConverter.printDateTime(cal);
                }
                return null;
        }

        public static String printDateTime(Date dt) {
                if (dt != null) {
                        Calendar cal = new GregorianCalendar();
                        cal.setTime(dt);
                        return DatatypeConverter.printDateTime(cal);
                }
                return null;
        }

        public static String outputDateOnly(Date dt) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                if (dt != null) {
                        String date = sdf.format(dt);
                        return date;
                }
                return null;
        }

}
