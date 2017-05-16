package io.springboard;

import io.springboard.framework.utils.spring.SpringContextHolder;

import java.util.Arrays;
import java.util.List;

public class RuntimeContext extends SpringContextHolder {

	public static List<String> getActiveProfiles(){
		assertContextInjected();
		String[] activeProfiles = getApplicationContext().getEnvironment().getActiveProfiles();
		return Arrays.asList(activeProfiles);
	}
	
	public static String getProperty(String key) {
		assertContextInjected();
		return getApplicationContext().getEnvironment().getProperty(key);
	}
	public static String getProperty(String key, String defaultValue) {
		assertContextInjected();
		return getApplicationContext().getEnvironment().getProperty(key, defaultValue);
	}
}
