package io.springboard.calculate.service;

import static org.junit.Assert.*;
import io.springboard.calculate.DivideApplication;
import io.springboard.calculate.client.DivideServiceClient;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.dubbo.config.annotation.Reference;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DivideApplication.class)
public class DivideServiceTest {
	
	@Reference(version = "1.0")
	private DivideServiceClient service;
	
	@Test
	public void test() {
		double z = service.divide(5, 2);
		assertTrue(2.5 == z);
	}

}
