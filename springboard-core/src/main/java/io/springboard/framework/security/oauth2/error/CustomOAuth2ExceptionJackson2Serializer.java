package io.springboard.framework.security.oauth2.error;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.springboard.framework.rest.Response;

import java.io.IOException;

class CustomOAuth2ExceptionJackson2Serializer extends StdSerializer<CustomOAuth2Exception> {
	
	public CustomOAuth2ExceptionJackson2Serializer() {
		super(CustomOAuth2Exception.class);
	}
	
	@Override
	public void serialize(CustomOAuth2Exception value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
		Response res = new Response().failure(value.getHttpErrorCode(), value.getMessage());
		jgen.writeObject(res);
		
	}
	
}