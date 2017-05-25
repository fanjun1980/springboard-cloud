package io.springboard.framework.log.service;

import io.springboard.framework.security.SecUser;
import io.springboard.framework.utils.spring.SpringUtils;

public class ALogUtils {

private static ActionLogService logManager = null;
	
	private static ActionLogService getLogManager(){
		if(logManager == null)
			logManager = SpringUtils.getBean(ActionLogService.class);
		return logManager;
	}
	
	public static void addLog(String modelName ,String operatorType ,String url ,Integer result, SecUser user) {
		//getLogManager().addLog(modelName, operatorType, url, result ,user); 
	}
}
