package io.springboard.account.service;


import io.springboard.account.dao.PrivilegeDao;
import io.springboard.account.entity.Privilege;
import io.springboard.framework.exception.ValidationException;
import io.springboard.framework.orm.Page;
import io.springboard.framework.orm.PropertyFilter;
import io.springboard.framework.rest.BaseService;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author fanjun
 * @description 权限的管理类
 */
@Service
@Transactional
public class PrivilegeService extends BaseService<Privilege, Long> {
	
	@Autowired
	private PrivilegeDao dao;
	
	@Cacheable(value = "privilege", key = "#id")
	@Transactional(readOnly = true)
	public Privilege get(Long id) {
		return dao.get(id);
	}
	
	@CacheEvict(value={"userPrivilege","privilege"},allEntries = true)
	public void save(Privilege entity){
		saveValidate(entity);
		dao.save(entity);
	}

	public void saveValidate(Privilege entity) {
		if (!dao.isUnqiue(entity)) throw new ValidationException("此编号已存在,请更换");
	}
	public void deleteValidate(Long id){
		if(!dao.canDelete(id)) {
			Privilege entity = get(id);
			throw new ValidationException(entity.getName() + " 正在被引用,不能被删除!");
		}
	}
	
	@CacheEvict(value={"userPrivilege","privilege"},allEntries = true)
	public void deleteById(Long id){
		deleteValidate(id);
		dao.delete(id);
		dao.deletePriRoles(id); 
	}
	@CacheEvict(value={"userPrivilege","privilege"},allEntries = true)
	public void deleteByIds(String ids) {
		String[] idArray = ids.split(",");
		for(int i=0;i<idArray.length;i++){
			if(!StringUtils.isEmpty(idArray[i])){
				deleteById(Long.valueOf(idArray[i]));
			}
		}
	}

    /**
     * 获取全部权限
     * 
     * @return
     */
    @Transactional(readOnly = true)
    public List<Privilege> getAll() {
        return this.dao.getAll();
    }
    
    /**
     * 递归查询所有子权限
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public List<Privilege> getAllChildren(Long id) {
        List<Privilege> all = new LinkedList<Privilege>(dao.getChildren(id));
        for(Privilege pri : all){
        	List<Privilege> sub = getAllChildren(pri.getId());
        	all.addAll(sub);
        }
        return all;
    }
    
    /**
     * 获取直接子权限
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public List<Privilege> getChildren(Long id) {
        List<Privilege> children = dao.getChildren(id);
        return children;
    }

	/**
	 * 使用属性过滤条件查询权限.
	 */
	@Transactional(readOnly = true)
	public <D> Page<D> findPage(Page<D> page, final List<PropertyFilter> filters, Class<D> clazz) {
		return dao.findPage(page, filters, clazz);
	}	
	
}
