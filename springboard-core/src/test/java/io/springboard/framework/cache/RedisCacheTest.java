package io.springboard.framework.cache;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import io.springboard.framework.test.AbstratorSpringTestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(locations = { "/applicationContext.xml", "/cache/applicationContext-redis.xml" })
public class RedisCacheTest extends AbstratorSpringTestCase{
	@Autowired
	private HelloManager helloManager;
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		try {
	        //First method execution using key="Josh", not cached
	        System.out.println("message: " + helloManager.getMessage("Josh"));
	        assertTrue(helloManager.isHit());
	        helloManager.setHit(false);

	        //Second method execution using key="Josh", still not cached
	        System.out.println("message: " + helloManager.getMessage("Josh"));
	        assertTrue(helloManager.isHit());
	        helloManager.setHit(false);

	        //First method execution using key="Joshua", not cached
	        System.out.println("message: " + helloManager.getMessage("Joshua"));
	        assertTrue(helloManager.isHit());
	        helloManager.setHit(false);

	        //Second method execution using key="Joshua", cached
	        System.out.println("message: " + helloManager.getMessage("Joshua"));
	        assertTrue(!helloManager.isHit());
	        helloManager.setHit(false);

        } catch (Exception e) {
	        e.printStackTrace();
	        fail("error : " + e.getMessage());
        }
	}
}
