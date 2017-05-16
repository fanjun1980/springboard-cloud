package io.springboard.framework.utils.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class SpringMvcUtils {
	
	public HttpServletRequest getRequest() {
		ServletRequestAttributes sra = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
		if(sra == null) return null;
		return sra.getRequest();
	}
	
	public HttpServletResponse getResponse() {
		ServletRequestAttributes sra = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
		if(sra == null) return null;
		return sra.getResponse();
	}
	
	public String getSessionId() {
		ServletRequestAttributes sra = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
		if(sra == null) return null;
		return sra.getSessionId();
	}
}
