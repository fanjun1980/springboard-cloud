package io.springboard.calculate.service;

import static org.junit.Assert.*;
import io.springboard.calculate.AddApplication;
import io.springboard.calculate.client.AddServiceClient;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.dubbo.config.annotation.Reference;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AddApplication.class)
public class AddServiceTest {
	
	@Reference(version = "1.0")
	private AddServiceClient service;
	
	@Test
	public void test() {
		double z = service.add(5, 3.2);
		assertTrue(8.2 == z);
	}

}
