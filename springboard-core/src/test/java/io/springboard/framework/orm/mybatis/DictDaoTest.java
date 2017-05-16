package io.springboard.framework.orm.mybatis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import io.springboard.framework.dict.dao.DictDao;
import io.springboard.framework.dict.entity.Dict;
import io.springboard.framework.orm.Page;
import io.springboard.framework.orm.PropertyFilter;
import io.springboard.framework.test.AbstratorDBTestCase;
import io.springboard.framework.test.DataUtils;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

//@FixMethodOrder(MethodSorters.JVM)
//@Rollback(false)
public class DictDaoTest extends AbstratorDBTestCase {
    @Autowired
    private DictDao dictDao;
    
    private static List<Dict> dictList = new ArrayList<Dict>();
    
    @Test
    public void test() {
    	testSave();
    	testGet();
    	testFindPage();
    	testDelete();
    }
    
    private List<Dict> createDictData() {
    	List<Dict> result = new ArrayList<Dict>();
    	for(int i=0;i < 10;i++){
    		Dict dict = new Dict();
    		dict.setType("test");
    		dict.setSortNo(i+1);
    		dict.setEnable(true);
    		dict.setKeycode(DataUtils.randomName("key_"));
    		dict.setValue(DataUtils.randomName("value_"));
    		result.add(dict);
    	}
    	return result;
    }

    public void testSave() {
        try {
        	List<Dict> dicts = createDictData();
        	for(Dict dict:dicts){
	            dictDao.save(dict);
	            dictList.add(dict);
        	}
        } catch (Exception e) {
            e.printStackTrace();
            fail("error");
        }
    }

    public void testDelete() {
        try {
        	Dict dict = DataUtils.randomOne(dictList);
            dictDao.delete(dict.getId());
        } catch (Exception e) {
            e.printStackTrace();
            fail("error");
        }
    }

    public void testGet() {
        try {
        	Dict dict1 = DataUtils.randomOne(dictList);
            Dict dict2 = dictDao.get(dict1.getId());
            assertEquals(dict1.getValue(), dict2.getValue());
        } catch (Exception e) {
            e.printStackTrace();
            fail("error");
        }
    }

    private void testFindPage() {
        try {
            Page<Dict> page = new Page<Dict>(3) {
            };
            page.setOrderBy("id");
            page.setOrder(Page.DESC);
            List<PropertyFilter> filters = new ArrayList<PropertyFilter>();
            // filter_LIKES_name
            // PropertyFilter filter = new PropertyFilter("LIKES_fullName", "管理");
            // filters.add(filter);
            PropertyFilter filter2 = new PropertyFilter("GEI_sortNo", "6");
            filters.add(filter2);
            Page<Dict> dicts = dictDao.findPage(page, filters, Dict.class);
            System.out.println("listSize " + dicts.getResult().size());
            System.out.println("PageSize " + page.getPageSize());
            System.out.println("TotalCount " + page.getTotalCount());
            
            System.out.println("dict list:");
            printList(dictList);
            System.out.println("query result:");
            printList(page.getResult());
            
            assertEquals(dictList.get(dictList.size()-1).getId(), page.getResult().get(0).getId());
        } catch (Exception e) {
            e.printStackTrace();
            fail("error");
        }
    }
    private void printList(List<Dict> lists){
    	for (Dict dict : lists) {
            System.out.println(dict.toString());
        }
    }

}
