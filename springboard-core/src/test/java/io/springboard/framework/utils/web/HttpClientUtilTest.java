package io.springboard.framework.utils.web;

import static org.junit.Assert.fail;
import io.springboard.framework.utils.web.HttpClientUtil;

import org.junit.Test;


public class HttpClientUtilTest {

	private HttpClientUtil hc = new HttpClientUtil();
	
	@Test
	public void testDoGetRequest(){
		String result;
		
		try {
			long time = System.currentTimeMillis();
			result = hc.doGetRequest("http://www.sina.com.cn/");
			System.out.println("size:" + result.length() + ";time:" + (System.currentTimeMillis() - time));
			
			time = System.currentTimeMillis();
			result = hc.doGetRequest("http://www.baidu.com/");
			System.out.println("size:" + result.length() + ";time:" + (System.currentTimeMillis() - time));
			
//			time = System.currentTimeMillis();
//			result = hc.doGetRequest("https://bitly.com/");
//			System.out.println("size:" + result.length() + ";time:" + (System.currentTimeMillis() - time));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		} finally {
			hc.shutdown();
		}
	}
}
