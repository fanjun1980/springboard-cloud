package io.springboard.framework.orm.mongodb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import io.springboard.framework.orm.Page;
import io.springboard.framework.orm.PropertyFilter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public class CollectorDaoTest {
    @Autowired
    private CollectorDao dao;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() {
        try {
            Collector entity = new Collector();
            String ip = "127.0.0.1";
            String name = "test_" + (new Date()).toString();
            entity.setIp(ip);
            entity.setName(name);
            entity.setPort(80);
            entity.setStatus(1);
            dao.insert(entity);

            Collector c = dao.getCollectorEntity(ip, name);
            assertTrue(c != null);
            assertEquals(c.getName(), name);
            System.out.println(c.toString());
        } catch (Exception e) {
            e.printStackTrace();
            fail("error : " + e.getMessage());
        }
    }

    @Test
    public void testFindByPage() {
        Page<Collector> page = new Page<Collector>(3);
        page.setOrderBy("id");
        page.setOrder(Page.DESC);
        List<PropertyFilter> filters = new ArrayList<PropertyFilter>();
        // filter_LIKES_name
        PropertyFilter filter = new PropertyFilter("LIKES_name", "Wed");
        filters.add(filter);

        page = dao.findByPage(page, filters);
        List<Collector> result = page.getResult();
        System.out.println("listSite " + result.size());
        System.out.println("PageSize " + page.getPageSize());
        System.out.println("TotalCount " + page.getTotalCount());
        for (Collector collector : result) {
            System.out.println(collector.toString());
        }
    }

}
