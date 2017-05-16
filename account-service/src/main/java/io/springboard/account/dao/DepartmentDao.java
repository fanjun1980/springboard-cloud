package io.springboard.account.dao;

import io.springboard.account.entity.Department;
import io.springboard.account.entity.User;
import io.springboard.framework.orm.mybatis.SimpleMybatisDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public class DepartmentDao extends SimpleMybatisDao<Department, Long> {
	
    /**
     * @description 判断部门名称是否唯一
     * @param entity 部门信息
     * @return true表示唯一，false表示不唯一
     */
	public Boolean isNameUnqiue(Department entity){
		Map<String,Object> query = new HashMap<String,Object>();
		query.put("id", entity.getId()==null ? 0L : entity.getId());
		query.put("name", entity.getName());
		Long result = getSqlSession().selectOne(
				"io.springboard.account.dao.DepartmentDao.isNameUnqiue", query);
		
		if(result > 0) 
			return false;
		else 
			return true;
	}
    /**
     * @description 判断部门编号是否唯一
     * @param entity 部门信息
     * @return true表示唯一，false表示不唯一
     */
	public Boolean isCodeUnqiue(Department entity){
		Map<String,Object> query = new HashMap<String,Object>();
		query.put("id", entity.getId()==null ? 0L : entity.getId());
		query.put("code", entity.getCode());
		Long result = getSqlSession().selectOne(
				"io.springboard.account.dao.DepartmentDao.isCodeUnqiue", query);
		
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
	public List<Department> getChildren(Long parentId) {
		List<Department> children = getSqlSession().selectList("io.springboard.account.dao.DepartmentDao.getChildren", parentId);
		return children;
	}
	
	/**
	 * 获取部门所属用户
	 * @param depId
	 * @return
	 */
	public List<User> getUsers(Long depId) {
		List<User> users = getSqlSession().selectList("io.springboard.account.dao.DepartmentDao.getUsers", depId);
		return users;
	}
	
	public List<Department> getAll(){
		List<Department> all = getSqlSession().selectList("io.springboard.account.dao.DepartmentDao.getAll");
		return all;
	}
}