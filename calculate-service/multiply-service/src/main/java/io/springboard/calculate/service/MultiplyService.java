package io.springboard.calculate.service;

import java.math.BigDecimal;

import io.springboard.calculate.client.MultiplyServiceClient;

import com.alibaba.dubbo.config.annotation.Service;

@Service(version = "1.0")
public class MultiplyService implements MultiplyServiceClient {

	@Override
    public double multiply(double x, double y) {
		BigDecimal a = new BigDecimal(Double.toString(x));  
        BigDecimal b = new BigDecimal(Double.toString(y));  
		return a.multiply(b).doubleValue(); 
    }
	
}
