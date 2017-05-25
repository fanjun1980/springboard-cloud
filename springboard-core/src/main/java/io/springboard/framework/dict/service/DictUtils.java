package io.springboard.framework.dict.service;

import io.springboard.framework.dict.entity.Dict;
import io.springboard.framework.utils.spring.SpringUtils;

import java.util.List;
import java.util.Map;

public class DictUtils {
    private static DictService dictManager = null;

    private static DictService getDictManager() {
        if (dictManager == null)
            dictManager = SpringUtils.getBean(DictService.class);

        return dictManager;
    }

    public static void saveDict(Dict d) {
        getDictManager().save(d);
    }

    public static void deleteDict(Long id) {
        getDictManager().deleteById(id);
    }

    public static Dict getDict(Long id) {
        return getDictManager().get(id);
    }
    
    public static void updateDict(String key, String type, String value){
    	Dict dict = getDictManager().get(key, type);
    	if(dict == null) {
    		Dict d = new Dict();
    		d.setKeycode(key);
    		d.setType(type);
    		d.setValue(value);
    		d.setEnable(true);
    		getDictManager().save(d);
    	} else {
    		dict.setValue(value);
    		getDictManager().save(dict);
    	}
    }
    

    /**
     * 根据key、type，获取数据字典中对应键的值
     * 
     * @param key
     * @param type
     * @return
     */
    public static String getDictValue(String key, String type) {
        return getDictManager().getDictValue(key, type);
    }

    /**
     * 根据type，获取一组键的值，以Map的列表形式返回
     * 
     * @param type
     * @return
     */
    public static List<Map<String, String>> getDictMap(String type) {
        return getDictManager().getDictMap(type);
    }

}
