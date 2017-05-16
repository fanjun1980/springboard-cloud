package io.springboard.calculate.service;

import java.math.BigDecimal;

import io.springboard.calculate.client.DivideServiceClient;

import com.alibaba.dubbo.config.annotation.Service;

@Service(version = "1.0")
public class DivideService implements DivideServiceClient {

	@Override
    public double divide(double x, double y) {
		BigDecimal a = new BigDecimal(Double.toString(x));  
        BigDecimal b = new BigDecimal(Double.toString(y));  
		return a.divide(b).doubleValue(); 
    }
	
}
