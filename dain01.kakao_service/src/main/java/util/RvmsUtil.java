package util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import util.DBUtil;
import util.PropUtil;

public class RvmsUtil
{
	private static Properties props = PropUtil.load("/rvms.properties");

	public static String getProperty(String key)
	{
		return props.getProperty(key);
	}

	public static Connection getDBConnection() throws Exception
	{
		return getDBConnection("db");
	}

	public synchronized static Connection getDBConnection(String id) throws Exception
	{
		String db_driver = getProperty(id+".driver");
		String db_url = getProperty(id+".url");
		String db_user = getProperty(id+".user");
		String db_password = getProperty(id+".password");

		return DBUtil.getDBPConnection(id, db_driver, db_url, db_user, db_password);
	}

	public static String join(String delim, String[] elements)
	{
//		return String.join(delim, elements);	// at 1.8
		StringBuffer buf = new StringBuffer(elements[0]);
		for( int i=1; i<elements.length; i++ )
			buf.append(delim).append(elements[i]);
		return buf.toString();
	}

	public static String toJson(List list)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("[ ");

		Object obj;
		for(int i=0; i<list.size(); i++)
		{
			if( i > 0 ) sb.append(", ");
			obj = list.get(i);
			sb.append( toJson(obj) );
		}
		sb.append(" ]");
		return sb.toString();
	}

	public static String toJson(Map map)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("{ ");

		Object obj;
		Map.Entry entry;
		Iterator iter = map.entrySet().iterator();

		int i = 0;
		while( iter.hasNext() )
		{
			if( i++ > 0 ) sb.append(", ");
			entry = (Map.Entry) iter.next();
			sb.append( toJson( (String)entry.getKey(), entry.getValue()) );
		}

		sb.append(" }");
		return sb.toString();
	}

	public static String toJson(ResultSet rs) throws Exception
	{
		StringBuilder sb = new StringBuilder();
		sb.append("[ ");

		ResultSetMetaData rsmd = rs.getMetaData();
		int cnt = rsmd.getColumnCount();
		String name;
		Object obj;

		while( rs.next() )
		{
			sb.append("{ ");
			for( int i=1; i<=cnt; i++ )
			{
				if( i > 1 ) sb.append(", ");
				name = rsmd.getColumnLabel(i);
				obj = rs.getObject(i);
				sb.append( toJson(name, obj) );
			}
			sb.append(" }");
		}

		sb.append(" ]");
		return sb.toString();
	}

	public static String toJson(String key, Object val)
	{
		return key +": "+ toJson(val);
	}

	public static String toJson(Object obj)
	{
		if( obj == null ) return "null";
		else if( obj instanceof String) return "\""+ obj +"\"";
		else if( obj instanceof List  ) return toJson((List) obj);
		else if( obj instanceof Map   ) return toJson((Map) obj);
//		else if( obj instanceof Number) return (String) obj;
		else return (String) obj;
	}


	private static String[] DUMMY = new String[0];
	public static String[] split(String sLine, String sDelim)
	{
//		System.out.println( sLine );
		ArrayList<String> list = new ArrayList<String>();
		String val;
		int begin=0, mid, end, cnt, len = sDelim.length();
		while( (end = sLine.indexOf(sDelim, begin)) != -1 )
		{
			if( sLine.charAt(begin) == '"' ) // startsWith("\"")
			{
				cnt = 1;
				mid = begin+1;
				while(true)
				{
					for(int i=mid; i<end; i++ )
					{
						if( sLine.charAt(i) == '"' ) ++cnt;
					}
					if( cnt%2 == 0 ) break;	// '"' 이 짝수개이면

					mid = end+len; 
					end = sLine.indexOf(sDelim, mid);
					if( end == -1 ) break;
				}
				if( end == -1 ) break;
			}

			val = sLine.substring(begin, end);
			list.add( trim(val) );
			begin = end+len;
		}
		// last
		if( begin <= sLine.length() )
		{
			val = sLine.substring(begin);
			list.add( trim(val) );
		}
//		System.out.println(list);
		return list.toArray(DUMMY);
	}

	private static String trim(String val)
	{
//		System.out.println("{"+ val +"}");
		if( val.startsWith("\"") && val.endsWith("\"") )
			val = val.substring(1,val.length()-1);
		val = val.replaceAll("\"\"", "\"");
//		System.out.println("["+ val +"]");
		return val;
//		return val.trim();
	}

	public static int indexOf(String src, String find, int nth)
	{
		int idx = -1;
		for( int i=0; i<nth; i++ )
		{
			idx = src.indexOf(find, idx+1);
			if( idx < 0 ) break;
		}
		return idx;
	}

	public static boolean isEquals(String src, String tar)
	{
		if( src == null )
		{
			if( tar == null ) return true;
			return (tar.length() == 0);
		}

		if( tar == null )
		{
			return (src.length() == 0);
		}

		return src.equals(tar);
	}
}
