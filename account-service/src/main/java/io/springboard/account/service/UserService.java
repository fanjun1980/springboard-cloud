package io.springboard.account.service;

import io.springboard.account.dao.UserDao;
import io.springboard.account.entity.Privilege;
import io.springboard.account.entity.Role;
import io.springboard.account.entity.User;
import io.springboard.framework.exception.ValidationException;
import io.springboard.framework.orm.Page;
import io.springboard.framework.orm.PropertyFilter;
import io.springboard.framework.rest.BaseService;
import io.springboard.framework.security.SpringSecurityUtils;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * User的管理类.
 * 
 * @author fanjun
 */
@Service
@Transactional
public class UserService extends BaseService<User, Long> {

	@Autowired
	private UserDao dao;
	
	@Transactional(readOnly = true)
	public User get(Long id) {
		return dao.get(id);
	}
	
	@CacheEvict(value={"user"},allEntries = true)
	public void save(User entity) {
		saveValidate(entity);
		dao.save(entity);
	}

	public void saveValidate(User entity) {
		if (!dao.isUnqiue(entity)) throw new ValidationException("此账号已存在,请更换");
	}
	public void deleteValidate(Long id){
//		if(!dao.canDelete(id)) {
//			User entity = get(id);
//			throw new ValidationException(entity.getFullName() + " 正在被引用,不能被删除!");
//		}
	}
	
	@CacheEvict(value={"userRole","userPrivilege","user"},allEntries = true)
	public void deleteById(Long id) {
		deleteValidate(id);
		dao.delete(id);
		dao.deleteUserRoles(id);
	}
	@CacheEvict(value={"userRole","userPrivilege","user"},allEntries = true)
	public void deleteByIds(String ids) {
		String[] idArray = ids.split(",");
		for(int i=0;i<idArray.length;i++){
			if(!StringUtils.isEmpty(idArray[i])){
				deleteById(Long.valueOf(idArray[i]));
			}
		}
	}
	
	/**
	 * 根据用户名称获取对象
	 * @param name
	 * @return User
	 * */
	@Cacheable(value="user",key="#username")
	public User getByUserName(String username) {
		User u = dao.getUserBy(username); 
		return u;
	}
	
	/**
	 * 使用属性过滤条件查询用户.
	 */
	@Transactional(readOnly = true)
	public <D> Page<D> findPage(Page<D> page, final List<PropertyFilter> filters, Class<D> clazz) {
		return dao.findPage(page, filters, clazz);
	}

	/**
	 * 获取用户权限菜单，通过用户ID 
	 * @param userId 用户ID
	 * @return 用户权限菜单集合
	 */
	@Cacheable(value="userPrivilege",key="#userId+'_'+#type")
	@Transactional(readOnly = true)
	public  List<Privilege> getUserPrivileges(Long userId, Long type){
		List<Privilege> result = dao.getPrivileges(userId, type);
		return result;
	}
	
	/**
	 * 获取用户的角色集合，通过用户的ID
	 * @param userId 用户ID
	 * @return 用户角色集合
	 */
	@Cacheable(value="userRole",key="#userId")
	@Transactional(readOnly = true)
	public List<Role> getUserRoles(Long userId){
		return this.dao.getRoles(userId);
	}
	
	/**
	 * 获取当前登录用户
	 * @return
	 */
	@Transactional(readOnly = true)
	public User getCurrentUser(){
		String loginname = SpringSecurityUtils.getCurrentUserName();
		if(loginname == null || loginname.equals("")) return null;
		return getByUserName(loginname);
	}
}
