package util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil
{
	public final static String YYYYMMDD = "yyyyMMdd";
	public final static String HHmmss = "HHmmss";

	public static String getToday()
	{
		return getToday(YYYYMMDD);
	}

	public static String getTime()
	{
		return getTime(HHmmss);
	}

	public static String getToday(String format)
	{
		return toString(new Date(), format);
	}

	public static String getTime(String format)
	{
		return toString(new Date(), format);
	}
	
	public static String toString(Date date)
	{
		return toString(date, YYYYMMDD);
	}

	public static String toString(Date date, String format)
	{
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}

	public static String offset(int offset)
	{
		return offset( new Date(), YYYYMMDD, Calendar.DATE, offset);
	}

	public static String offset(String format, int offset)
	{
		return offset( new Date(), format, Calendar.DATE, offset);
	}

	public static String offset(Date date, String format, int field, int offset)
	{
		Calendar cal = Calendar.getInstance(Locale.KOREA);
		cal.setTime(date);
		cal.add(field, offset);
		return toString( cal.getTime(), format);
	}

	public static Date toDate(String date)
	{
		return toDate(date, YYYYMMDD);
	}

	public static Date toDate(String date, String format)
	{
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			return sdf.parse(date);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
