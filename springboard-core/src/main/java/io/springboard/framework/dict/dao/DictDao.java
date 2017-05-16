package io.springboard.framework.dict.dao;

import io.springboard.framework.dict.entity.Dict;
import io.springboard.framework.orm.mybatis.SimpleMybatisDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

/**
 * 数据字典
 */
@Component
public class DictDao extends SimpleMybatisDao<Dict, Long> {

    /**
     * 根据键取值
     * 
     * @return String
     */
    public String getDictValue(String key, String type) {
    	Map<String, Object> params = new HashMap<String, Object>();
        params.put("type", type);
        params.put("keycode", key);
        String rs = getSqlSession().<String> selectOne("io.springboard.framework.dict.dao.DictDao.getDictValue", params);
        return rs;
    }
    
    /**
     * 根据键取值
     * 
     * @return Dict
     */
    public Dict getDictByValue(String key, String type) {
    	Map<String, Object> params = new HashMap<String, Object>();
        params.put("type", type);
        params.put("keycode", key);
        Dict dict = getSqlSession().<Dict> selectOne("io.springboard.framework.dict.dao.DictDao.getDictByValue", params);
        return dict;
    }

    /**
     * 根据类型取值
     * 
     * @return List<map>
     */
    public List<Map<String, String>> getDictMap(String type) {
    	Map<String, Object> params = new HashMap<String, Object>();
        params.put("type", type);
        List<Map<String, String>> rs = getSqlSession().<Map<String, String>> selectList("io.springboard.framework.dict.dao.DictDao.getDictMap", params);
        return rs;
    }
}
