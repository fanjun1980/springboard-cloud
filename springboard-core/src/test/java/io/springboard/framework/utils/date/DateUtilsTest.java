package io.springboard.framework.utils.date;

import static org.junit.Assert.assertEquals;
import io.springboard.framework.utils.date.DateUtils;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DateUtilsTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void isWeekEnd() {
		Date test1 = DateUtils.formateStringToDate("2015-02-08");
		assertEquals(true, DateUtils.isWeekEnd(test1));
		Date test2 = DateUtils.formateStringToDate("2015-02-28");
		assertEquals(false, DateUtils.isWeekEnd(test2));
	}
	
	@Test
	public void isMonthEnd() {
		Date test1 = DateUtils.formateStringToDate("2015-02-08");
		assertEquals(false, DateUtils.isMonthEnd(test1));
		Date test2 = DateUtils.formateStringToDate("2015-02-28");
		assertEquals(true, DateUtils.isMonthEnd(test2));
	}

}
