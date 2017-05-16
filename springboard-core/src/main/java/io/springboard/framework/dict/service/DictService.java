package io.springboard.framework.dict.service;

import io.springboard.framework.dict.dao.DictDao;
import io.springboard.framework.dict.entity.Dict;
import io.springboard.framework.orm.Page;
import io.springboard.framework.orm.PropertyFilter;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 数据字典
 */
@Component
@Transactional
public class DictService {

    private DictDao dao;

    @CacheEvict(value = { "dict" }, allEntries = true)
    public void save(Dict model) {
        dao.save(model);
    }

    @CacheEvict(value = { "dict" }, allEntries = true)
    public void deleteById(Long id) {
        dao.delete(id);
    }

    @CacheEvict(value = { "dict" }, allEntries = true)
    public void deleteByIds(String ids) {
        String[] idArray = ids.split(",");
        for (int i = 0; i < idArray.length; i++) {
            if (!StringUtils.isEmpty(idArray[i])) {
                deleteById(Long.valueOf(idArray[i]));
            }
        }
    }

    @Cacheable(value = "dict", key = "'getDict_' + #id")
    public Dict get(Long id) {
        return dao.get(id);
    }
    
    @Cacheable(value = "dict", key = "'getDict_' + #key + '_' + #type")
    public Dict get(String key, String type) {
        return dao.getDictByValue(key, type);
    }

    /**
     * 分页查询
     * 
     **/
    public Page<Dict> findPage(Page<Dict> page, List<PropertyFilter> filters) {
        return dao.findPage(page, filters, Dict.class);
    }

    @Cacheable(value = "dict", key = "'getDictValue_' + #key + '_' + #type")
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public String getDictValue(String key, String type) {
        return dao.getDictValue(key, type);
    }

    @Cacheable(value = "dict", key = "'getDictMap_' + #type")
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Map<String, String>> getDictMap(String type) {
        return dao.getDictMap(type);
    }

    @Autowired
    public void setDao(DictDao dao) {
        this.dao = dao;
    }

}
