package io.springboard.calculate.service;

import static org.junit.Assert.*;
import io.springboard.calculate.CalculateApplication;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = CalculateApplication.class)
public class CalculateServiceTest {
	
	@Autowired
	CalculateService service;
	
	@Test
	public void test() {
		String z = service.calculate("( ( ( 15 / 3 ) + ( 1.5 * 2 ) + ( 20 - 12 ))  - 3.2 +2.3 + 5 ) ");
		assertEquals("20.1", z);
	}

}
