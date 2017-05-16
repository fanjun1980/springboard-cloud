package io.springboard.account.dao;


import io.springboard.account.entity.Privilege;
import io.springboard.framework.orm.mybatis.SimpleMybatisDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public class PrivilegeDao extends SimpleMybatisDao<Privilege ,Long> {	
	
	/**
	 * 判断权限编号是否唯一
	 * @param entity
	 * @return
	 */
	public Boolean isUnqiue(Privilege entity){
		Map<String,Object> query = new HashMap<String,Object>();
		query.put("id", entity.getId()==null ? 0L : entity.getId());
		query.put("code", entity.getCode());
		Long result = getSqlSession().selectOne("io.springboard.account.dao.PrivilegeDao.isUnqiue", query);
		
		if(result > 0) 
			return false;
		else 
			return true;
	}

	/**
	 * 获取直接子节点
	 * @param parentId
	 * @return
	 */
	public List<Privilege> getChildren(Long parentId) {
		List<Privilege> children = getSqlSession().selectList("io.springboard.account.dao.PrivilegeDao.getChildren", parentId);
		return children;
	}
	
	/**
	 * 删除权限时级联删除权限-角色关联
	 * @param id
	 */
	public void deletePriRoles(Long id){
		Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", id);
        getSqlSession().delete("io.springboard.account.dao.PrivilegeDao.deletePriRoles", params);
	}
	
	/**
	 * 判断权限能否被删除。若有引用，则不能删除
	 * @param id
	 * @return
	 */
	public Boolean canDelete(Long id){
		Long result = getSqlSession().selectOne(
				"io.springboard.account.dao.PrivilegeDao.canDelete", id);
		
		if(result > 0) 
			return false;
		else 
			return true;
	}

	public List<Privilege> getAll(){
		List<Privilege> all = getSqlSession().selectList("io.springboard.account.dao.PrivilegeDao.getAll");
		return all;
	}
}
