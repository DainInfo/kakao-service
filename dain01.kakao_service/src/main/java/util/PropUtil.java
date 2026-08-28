package util;

import java.util.Properties;

public class PropUtil {

	private Properties props;

	
	public PropUtil(String path)
	{
		load(path);
	}
	
	public static Properties load(String path) {
		Properties properties = new Properties();

		try {
//			properties.load(
//					new InputStreamReader(properties.getClass().getClassLoader().getResourceAsStream(path), "UTF-8"));
			properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(path));
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return properties;
	}
	
	public String get(String key)
	{
		return props.getProperty(key);
	}

	public Object set(String key, String val)
	{
		return props.setProperty(key, val);
	}
}
