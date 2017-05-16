package io.springboard.account.dao;


import io.springboard.account.entity.Privilege;
import io.springboard.account.entity.Role;
import io.springboard.account.entity.User;
import io.springboard.framework.orm.mybatis.SimpleMybatisDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public class UserDao extends SimpleMybatisDao<User, Long> {

	/**
	 * 判断账号是否唯一
	 * @param entity
	 * @return
	 */
	public Boolean isUnqiue(User entity){
		Map<String,Object> query = new HashMap<String,Object>();
		query.put("id", entity.getId()==null ? 0L : entity.getId());
		query.put("username", entity.getUsername());
		Long result = getSqlSession().selectOne("io.springboard.account.dao.UserDao.isUnqiue", query);
		
		if(result > 0) 
			return false;
		else 
			return true;
	}
	
	/**
	 * 判断用户能否被删除。若有引用，则不能删除
	 * @param entity
	 * @return
	 */
	public Boolean canDelete(Long id){
		return true;
	}
	
	/**
	 * 根据用户id 获取该用户所有角色
	 * @param id
	 * @return
	 */
	public List<Role> getRoles(Long id){
		List<Role> result = getSqlSession().selectList("io.springboard.account.dao.UserDao.getRoles", id);
		return result;
	}

	/**
	 * 根据用户id 获取该用户所有权限
	 * @param id
	 * @return
	 */
	public List<Privilege> getPrivileges(Long id, Long type){
		Map<String,Long> query = new HashMap<String,Long>();
		query.put("id", id);
		query.put("type", type);
		List<Privilege> result = getSqlSession().selectList("io.springboard.account.dao.UserDao.getPrivileges", query);
		return result;
	}

	/**
	 * 根据用户名 获取该用户
	 * @param username
	 * @return
	 */
	public User getUserBy(String username){
		User result = getSqlSession().selectOne(
				"io.springboard.account.dao.UserDao.getUserBy", username);
		return result;
	}
	
	/**
	 * 根据用户id 删除该用户所有用户-角色关系
	 * @param id
	 */
	public void deleteUserRoles(Long id){
		getSqlSession().delete("io.springboard.account.dao.UserDao.deleteUserRoles", id);
	}
	/**
	 * 添加用户-角色关系
	 * @param userId
	 * @param roleId
	 */
	public void saveUserRole(Long userId ,Long roleId){
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("userId", userId);
		params.put("roleId", roleId);
		getSqlSession().insert("io.springboard.account.dao.UserDao.saveUserRole", params);
		
	}
}
