package io.springboard.framework.utils.reflection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import io.springboard.framework.utils.reflection.ConvertUtils;
import io.springboard.framework.utils.reflection.ReflectionUtilsTest.TestBean3;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class CovertUtilsTest {

	@Test
	public void convertElementPropertyToString() {
		TestBean3 bean1 = new TestBean3();
		bean1.setId(1);
		TestBean3 bean2 = new TestBean3();
		bean2.setId(2);

		List list = Lists.newArrayList(bean1, bean2);
		assertEquals("1,2", ConvertUtils.convertElementPropertyToString(list, "id", ","));
	}

	@Test
	public void convertElementPropertyToList() {
		TestBean3 bean1 = new TestBean3();
		bean1.setId(1);
		TestBean3 bean2 = new TestBean3();
		bean2.setId(2);

		List list = Lists.newArrayList(bean1, bean2);
		List<String> result = ConvertUtils.convertElementPropertyToList(list, "id");
		assertEquals(2, result.size());
		assertEquals(1, result.get(0));
	}

	@Test
	public void convertStringToObject() {
		assertEquals(1, ConvertUtils.convertStringToObject("1", Integer.class));

		Calendar calendar = Calendar.getInstance(); 
		Date date = (Date) ConvertUtils.convertStringToObject("2010-06-01", Date.class);
		calendar.setTime(date);  
		assertEquals(2010, calendar.get(Calendar.YEAR));

		Date dateTime = (Date) ConvertUtils.convertStringToObject("2010-06-01 12:00:04", Date.class);
		calendar.setTime(dateTime);  
		assertEquals(12, calendar.get(Calendar.HOUR_OF_DAY));
	}
	
	@Test
	public void convertBeanToMap(){
		TestBean bean = new TestBean();
		Map<String, Object> map = ConvertUtils.convertBeanToMap(bean);
		assertEquals(map.get("publicField"), bean.getPublicField());
		assertEquals(map.get("privateField"), null);
	}
	
	@Test
	public void convertMapToBean(){
		Map<String, Object> map = Maps.newHashMap();
		map.put("privateField", 2);
		map.put("publicField", 2);
		
		try {
	        TestBean bean1 = ConvertUtils.convertMapToBean(map, TestBean.class);
	        assertEquals(bean1.getPublicField(), 2);
//	        assertEquals(bean1.showPrivateField(), 2);
	        
	        TestBean bean2 = new TestBean();
	        bean2.setPublicField(10);
	        ConvertUtils.convertMapToBean(map, bean2);
	        assertEquals(bean2.getPublicField(), 2);
//	        assertEquals(bean2.showPrivateField(), 2);
        } catch (Exception e) {
	        fail("error");
        }
	}
	
	public static class TestBean {
		/** 没有getter/setter的field */
		private int privateField = 1;
		/** 有getter/setter的field */
		private int publicField = 1;
		
		public int getPublicField() {
			return publicField;
		}
		public void setPublicField(int publicField) {
			this.publicField = publicField;
		}
		
		public int showPrivateField(){
			return privateField;
		}
	}
}


