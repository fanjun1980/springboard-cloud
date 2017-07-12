package io.springboard.framework.security.oauth2.error;

import io.springboard.framework.rest.Response;
import org.codehaus.jackson.map.JsonSerializer;

import java.io.IOException;

class CustomOAuth2ExceptionJackson1Serializer extends JsonSerializer<CustomOAuth2Exception> {
	
	@Override
	public void serialize(CustomOAuth2Exception value, org.codehaus.jackson.JsonGenerator jgen, org.codehaus.jackson.map.SerializerProvider provider) throws IOException {
		Response res = new Response().failure(value.getHttpErrorCode(), value.getMessage());
		jgen.writeObject(res);
	}
	
}
