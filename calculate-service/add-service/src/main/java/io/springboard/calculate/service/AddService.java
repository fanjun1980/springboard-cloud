package io.springboard.calculate.service;

import java.math.BigDecimal;

import com.alibaba.dubbo.config.annotation.Service;

import io.springboard.calculate.client.AddServiceClient;

@Service(version = "1.0")
public class AddService implements AddServiceClient {

	@Override
    public double add(double x, double y) {
		BigDecimal a = new BigDecimal(Double.toString(x));  
        BigDecimal b = new BigDecimal(Double.toString(y));  
		return a.add(b).doubleValue();
    }
	
}
