package io.springboard.framework.security.oauth2.error;

import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

import java.util.Map;

@org.codehaus.jackson.map.annotate.JsonSerialize(using = CustomOAuth2ExceptionJackson1Serializer.class)
@com.fasterxml.jackson.databind.annotation.JsonSerialize(using = CustomOAuth2ExceptionJackson2Serializer.class)
public class CustomOAuth2Exception extends OAuth2Exception {
	private OAuth2Exception exception;
	public CustomOAuth2Exception(OAuth2Exception oe) {
		super(oe.getMessage(), oe);
		this.exception = oe;
	}
	public String getOAuth2ErrorCode() {
		return exception.getOAuth2ErrorCode();
	}
	
	public int getHttpErrorCode() {
		return exception.getHttpErrorCode();
	}
	
	public Map<String, String> getAdditionalInformation() {
		return exception.getAdditionalInformation();
	}
	
	public void addAdditionalInformation(String key, String value) {
		exception.addAdditionalInformation(key, value);
	}
}