package io.springboard.calculate.web;

import io.springboard.calculate.client.CalculateClient;
import io.springboard.calculate.service.CalculateService;
import io.springboard.framework.rest.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "Calculate")
@RestController
@RequestMapping(value = "/api/calculate")
public class CalculateController implements CalculateClient{
	@Autowired
	CalculateService service;

	@ApiOperation(value = "计算", notes = "根据四则运算表达式进行计算")
	@RequestMapping(value = "/", method = RequestMethod.POST)
	public Response<String> calculate(@RequestBody String exp) {
	    if(StringUtils.isEmpty(exp)) return null;
		String result = service.calculate(exp);
	    return new Response<String>().success(result);
    }
	

}
