package io.springboard.calculate.service;

import static org.junit.Assert.assertTrue;
import io.springboard.calculate.MultiplyApplication;
import io.springboard.calculate.client.MultiplyServiceClient;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.dubbo.config.annotation.Reference;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = MultiplyApplication.class)
public class MultiplyServiceTest {
	
	@Reference(version = "1.0")
	private MultiplyServiceClient service;
	
	@Test
	public void test() {
		double z = service.multiply(5, 3.2);
		assertTrue(16 == z);
	}

}
