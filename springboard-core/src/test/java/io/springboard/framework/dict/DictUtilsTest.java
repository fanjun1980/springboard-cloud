package io.springboard.framework.dict;

import static org.junit.Assert.assertEquals;
import io.springboard.framework.dict.entity.Dict;
import io.springboard.framework.dict.service.DictUtils;
import io.springboard.framework.test.AbstratorDBTestCase;

import org.junit.Test;

public class DictUtilsTest extends AbstratorDBTestCase {
	
	@Test
	public void testSave(){
		Dict d = new Dict();
		d.setKeycode("1111");
		d.setValue("ssss");
		d.setEnable(true);
		d.setType("1111");
		
		DictUtils.saveDict(d);
		
		String value = DictUtils.getDictValue("1111","1111");
		assertEquals(value,"ssss");
		System.out.println(value);
	}
}
