package io.springboard.framework.orm;

import static org.junit.Assert.*;
import io.springboard.framework.orm.Page;

import org.junit.Before;
import org.junit.Test;

public class PageTest {
	private Page<Object> page;

	@Before
	public void setUp() {
		page = new Page<Object>();
	}

	/**
	 * 检测Page的默认值契约.
	 */
	@Test
	public void defaultParameter() {
		assertEquals(1, page.getPageNo());
		assertEquals(-1, page.getPageSize());
		assertEquals(-1, page.getTotalCount());
		assertEquals(-1, page.getTotalPages());
		assertEquals(true, page.isAutoCount());

		page.setPageNo(-1);
		assertEquals(1, page.getPageNo());

		assertNull(page.getOrder());
		assertNull(page.getOrderBy());

		assertEquals(false, page.isOrderBySetted());
		page.setOrderBy("Id");
		assertEquals(false, page.isOrderBySetted());
		page.setOrder("ASC,desc");
	}

	@Test(expected = IllegalArgumentException.class)
	public void checkInvalidOrderParameter() {
		page.setOrder("asc,abcd");
	}

	@Test
	public void getFirst() {
		page.setPageSize(10);

		page.setPageNo(1);
		assertEquals(1, page.getFirst());
		page.setPageNo(2);
		assertEquals(11, page.getFirst());

	}

	@Test
	public void getTotalPages() {
		page.setPageSize(10);

		page.setTotalCount(1);
		assertEquals(1, page.getTotalPages());

		page.setTotalCount(10);
		assertEquals(1, page.getTotalPages());

		page.setTotalCount(11);
		assertEquals(2, page.getTotalPages());
	}

	@Test
	public void hasNextOrPre() {
		page.setPageSize(10);
		page.setPageNo(1);

		page.setTotalCount(9);
		assertEquals(false, page.isHasNext());

		page.setTotalCount(11);
		assertEquals(true, page.isHasNext());

		page.setPageNo(1);
		assertEquals(false, page.isHasPre());

		page.setPageNo(2);
		assertEquals(true, page.isHasPre());
	}

	@Test
	public void getNextOrPrePage() {
		page.setPageNo(1);
		assertEquals(1, page.getPrePage());

		page.setPageNo(2);
		assertEquals(1, page.getPrePage());

		page.setPageSize(10);
		page.setTotalCount(11);
		page.setPageNo(1);
		assertEquals(2, page.getNextPage());

		page.setPageNo(2);
		assertEquals(2, page.getNextPage());
	}

	@Test
	public void setAllParameterInOneLine() {
		page.pageNo(2).pageSize(10).orderBy("abc").order(Page.ASC).autoCount(true);

		assertEquals(2, page.getPageNo());
		assertEquals(10, page.getPageSize());
		assertEquals("abc", page.getOrderBy());
		assertEquals(Page.ASC, page.getOrder());
		assertEquals(true, page.isAutoCount());
	}
}
