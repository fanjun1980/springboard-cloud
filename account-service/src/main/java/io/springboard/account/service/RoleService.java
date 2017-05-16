package io.springboard.account.service;

import io.springboard.account.dao.RoleDao;
import io.springboard.account.entity.Privilege;
import io.springboard.account.entity.Role;
import io.springboard.framework.exception.ValidationException;
import io.springboard.framework.orm.Page;
import io.springboard.framework.orm.PropertyFilter;
import io.springboard.framework.rest.BaseService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;

/**
 * Role的管理类
 */
@Service
@Transactional
public class RoleService extends BaseService<Role, Long> {

	@Autowired
    private RoleDao roleDao;

    @CacheEvict(value={"userRole","userPrivilege"},allEntries = true)
    public void save(Role entity) {
    	saveValidate(entity);
        roleDao.save(entity);
//        roleDao.deleteRolePris(entity.getId());
    }

    @Transactional(readOnly = true)
	public void saveValidate(Role entity) {
		if (!roleDao.isUnqiue(entity)) throw new ValidationException("此名称已存在,请更换");
	}
    @Transactional(readOnly = true)
	public void deleteValidate(Long id){
		if(!roleDao.canDelete(id)) {
			Role entity = get(id);
			throw new ValidationException(entity.getName() + " 正在被引用,不能被删除!");
		}
	}
	
    @CacheEvict(value={"userRole","userPrivilege"},allEntries = true)
    public void deleteById(Long id) {
    	deleteValidate(id);
        roleDao.delete(id);
        roleDao.deleteRolePris(id); 
    }
    @CacheEvict(value={"userRole","userPrivilege"},allEntries = true)
    public void deleteByIds(String ids) {
        String[] idArray = ids.split(",");
        for (int i = 0; i < idArray.length; i++) {
            if (!StringUtils.isEmpty(idArray[i])) {
                deleteById(Long.valueOf(idArray[i]));
            }
        }
    }

    @Transactional(readOnly = true)
    public Role get(Long id) {
        return roleDao.get(id);
    }

    @Transactional(readOnly = true)
    public List<Role> getAll(){
    	return roleDao.getAll();
    }
    
    /**
     * 分页查询
     **/
    @Transactional(readOnly = true)
    public <D> Page<D> findPage(Page<D> page, List<PropertyFilter> filters, Class<D> clazz) {
        return roleDao.findPage(page, filters, clazz);
    }

    /**
     * 根据角色id 查询该角色权限
     * @param id
     * @param type 权限类型
     * @return
     */
    @Transactional(readOnly = true)
    public List<Privilege> getPrivileges(Long id, Long type){
    	return roleDao.getPrivileges(id, type);
    }
    
    /**
     * 获取权限url与角色的对照关系，用于spring security中的FilterInvocationSecurityMetadataSource
     * @return url->角色名集合，默认url按照权限的code排序
     */
    @Transactional(readOnly = true)
    public Map<String, Collection<String>> getMetaSource() {
    	Map<String, Collection<String>> resourceMap = Maps.newLinkedHashMap();
		List<Role> roles = getAll();
    	
		for (Role role : roles) {
			String roleName = role.getName();
			List<Privilege> resources = getPrivileges(role.getId(), 1L); // 获取所有api权限
			for (Privilege privilege : resources) {
				String url = privilege.getUrl();
				if(StringUtils.isEmpty(url)) continue;	// url为空的权限跳过
				
				/*
				 * 判断资源url和权限的对应关系，如果已经存在相关的资源url，则要通过该url为key提取出权限集合，将权限增加到权限集合中。
				 */
				if (resourceMap.containsKey(url)) {
					Collection<String> value = resourceMap.get(url);
					value.add(roleName);
					resourceMap.put(url, value);
				} else {
					Collection<String> value = new ArrayList<>();
					value.add(roleName);
					resourceMap.put(url, value);
				}
			}
		}
		
		return resourceMap;
    }

}
