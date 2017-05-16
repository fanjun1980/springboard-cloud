package io.springboard.account.dao;

import io.springboard.account.entity.Privilege;
import io.springboard.account.entity.Role;
import io.springboard.framework.orm.mybatis.SimpleMybatisDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public class RoleDao extends SimpleMybatisDao<Role, Long> {
	
	/**
	 * 判断角色名称是否唯一
	 * @param entity
	 * @return
	 */
	public Boolean isUnqiue(Role entity){
		Map<String,Object> query = new HashMap<String,Object>();
		query.put("id", entity.getId()==null ? 0L : entity.getId());
		query.put("name", entity.getName());
		Long result = getSqlSession().selectOne("io.springboard.account.dao.RoleDao.isUnqiue", query);
		
		if(result > 0) 
			return false;
		else 
			return true;
	}
	
	/**
	 * 判断角色 能否被删除。若有引用，则不能删除
	 * @param id
	 * @return
	 */
	public Boolean canDelete(Long id){
		Long result = getSqlSession().selectOne("io.springboard.account.dao.RoleDao.canDelete", id);
		
		if(result > 0) 
			return false;
		else 
			return true;
	}

	/**
	 * 根据角色id 查询该角色权限
	 * @param id
	 * @return
	 */
	public List<Privilege> getPrivileges(Long id, Long type){
		Map<String,Long> query = new HashMap<String,Long>();
		query.put("id", id);
		query.put("type", type);
		List<Privilege> result = getSqlSession().selectList("io.springboard.account.dao.RoleDao.getPrivileges", query);
		return result;
	}
	
	/**
	 * 根据角色id 删除该角色所有角色-权限关系
	 * @param id
	 */
	public void deleteRolePris(Long id){
		getSqlSession().delete("io.springboard.account.dao.RoleDao.deleteRolePris", id);
	}
	/**
	 * 添加角色-权限关系
	 * @param roleId
	 * @param priId
	 */
	public void saveRolePris(Long roleId ,Long priId){
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("roleId", roleId);
		params.put("privilegeId", priId);
		getSqlSession().insert("io.springboard.account.dao.RoleDao.saveRolePri", params);
		
	}
	
	public List<Role> getAll(){
		List<Role> all = getSqlSession().selectList("io.springboard.account.dao.RoleDao.getAll");
		return all;
	}
}