package dain.web.service;

import java.util.Properties;

public class ProUtil {

	public Properties Load(String path) {
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
}
