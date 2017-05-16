package io.springboard.calculate.service;

import java.math.BigDecimal;

import io.springboard.calculate.client.SubtractServiceClient;

import com.alibaba.dubbo.config.annotation.Service;

@Service(version = "1.0")
public class SubtractService implements SubtractServiceClient {

	@Override
    public double subtract(double x, double y) {
		BigDecimal a = new BigDecimal(Double.toString(x));  
        BigDecimal b = new BigDecimal(Double.toString(y));  
		return a.subtract(b).doubleValue(); 
    }
	
}
