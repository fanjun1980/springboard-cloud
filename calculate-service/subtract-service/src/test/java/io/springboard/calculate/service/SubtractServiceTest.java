package io.springboard.calculate.service;

import static org.junit.Assert.*;
import io.springboard.calculate.SubtractApplication;
import io.springboard.calculate.client.SubtractServiceClient;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.dubbo.config.annotation.Reference;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SubtractApplication.class)
public class SubtractServiceTest {
	
	@Reference(version = "1.0")
	private SubtractServiceClient service;
	
	@Test
	public void test() {
		double z = service.subtract(5, 3.2);
		assertTrue(1.8 == z);
	}

}
