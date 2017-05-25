package io.springboard.calculate.client;

import io.springboard.framework.rest.Response;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "calculate-api", url = "${springcloud.feignclient.calculate-service:}")
@RequestMapping(value = "/api/calculate")
public interface CalculateClient {
	@RequestMapping(value = "/", method = RequestMethod.POST)
	public Response<String> calculate(@RequestBody String exp); 
}
