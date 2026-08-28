package api;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import org.apache.log4j.Logger;
import util.DBUtil;
import util.DateUtil;
import util.PropUtil;
import util.RvmsUtil;

public class ApiUtil
{
	private static Logger logger = Logger.getLogger(ApiUtil.class);
	private static Properties props = PropUtil.load("/api.properties");
	private static final String KEY	= ApiUtil.getProperty("aria.crypto.service_key");
	
	public static String getProperty(String key)
	{
		return props.getProperty(key);
	}

	
	public static boolean isHoliday(String sDate)
	{
		Connection conn = null;
		CallableStatement stmt = null;
		try
		{
			conn = RvmsUtil.getDBConnection();
			stmt = conn.prepareCall("{? = call FNC_CHK_HOLIDAY(?)}");
			stmt.registerOutParameter(1, java.sql.Types.VARCHAR);
			stmt.setString(2, sDate);
			stmt.execute();
			return ( stmt.getString(1) != null );
		}
		catch(Exception e)
		{
			logger.error(e,e);
		}
		finally
		{
			DBUtil.close(stmt);
			DBUtil.close(conn);
		}
		return false;
	}

	public static boolean isWorkingDay(String date)
	{
		String tmp = getWorkingDay(date);
		return date.equals(tmp);
	}

	public static String getWorkingDay()
	{
		return getWorkingDay( new Date() );
	}
	public static String getWorkingDay(String date)
	{
		return getWorkingDay( DateUtil.toDate(date) );
	}
	public static String getWorkingDay(Date date)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return getWorkingDay(cal);
	}
	public static String getWorkingDay(Calendar cal)
	{
		String day = null;
		int week;
		while(true)
		{
			week = cal.get(Calendar.DAY_OF_WEEK);

			// 토,일 Skip
			if(    Calendar.SATURDAY == week ) cal.add(Calendar.DAY_OF_MONTH, 2);	// 토요일
			else if( Calendar.SUNDAY == week ) cal.add(Calendar.DAY_OF_MONTH, 1);	// 일요일

			day = DateUtil.toString(cal.getTime());

			// 공휴일 체크
			if( !isHoliday(day) ) break;

			cal.add(Calendar.DAY_OF_MONTH, 1); // 익일
		}
		return day;
	}
}
