package io.springboard.account.service;

import io.springboard.account.dao.DepartmentDao;
import io.springboard.account.entity.Department;
import io.springboard.framework.exception.ValidationException;
import io.springboard.framework.orm.Page;
import io.springboard.framework.orm.PropertyFilter;
import io.springboard.framework.rest.BaseService;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 部门的管理类
 */
@Service
@Transactional
public class DepartmentService extends BaseService<Department, Long> {
    
	@Autowired
    private DepartmentDao dao;

    public void saveValidate(Department entity) {
        if (!dao.isNameUnqiue(entity)) {
            throw new ValidationException("此部门名称已存在,请更换");
        }
        if (!dao.isCodeUnqiue(entity)) {
            throw new ValidationException("此部门编号已存在,请更换");
        }
        if (entity.getParentId() != null) {
            Department de = get(entity.getParentId());
            if (de != null)
                entity.setParentId(de.getId());
        } else {
            entity.setParentId(null);
        }
    }

    public void save(Department entity) {
        saveValidate(entity);
        dao.save(entity);
    }

    public void deleteValidate(Long id) {
        if (dao.getChildren(id).size() > 0) {
            Department entity = get(id);
            throw new ValidationException(entity.getName() + " 对象正在被引用,不能被删除!");
        }
        if (dao.getUsers(id).size() > 0) {
            Department entity = get(id);
            throw new ValidationException(entity.getName() + " 对象正在被引用,不能被删除!");
        }
    }

    public void deleteById(Long id) {
        deleteValidate(id);
        dao.delete(id);
    }
    public void deleteByIds(String ids) {
        String[] idArray = ids.split(",");
        for (int i = 0; i < idArray.length; i++) {
            if (!StringUtils.isEmpty(idArray[i])) {
                deleteById(Long.valueOf(idArray[i]));
            }
        }
    }

    @Transactional(readOnly = true)
    public Department get(Long id) {
        if (id == null)
            throw new ValidationException("id不可为空");
        return dao.get(id);
    }

    /**
     * 分页查询
     * 
     **/
    @Transactional(readOnly = true)
    public <D> Page<D> findPage(Page<D> page, List<PropertyFilter> filters, Class<D> clazz) {
        return dao.findPage(page, filters, clazz);
    }

    /**
     * 获取全部部门
     * 
     * @return
     */
    @Transactional(readOnly = true)
    public List<Department> getAll() {
        return this.dao.getAll();
    }

    /**
     * 递归查询所有子部门
     * 
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public List<Department> getAllChildren(Long id) {
        List<Department> all = dao.getChildren(id);
        List<Department> rs = new ArrayList<>();
        rs.addAll(all);
        for (Department dep : all) {
            List<Department> sub = getAllChildren(dep.getId());
            rs.addAll(sub);
        }
        return rs;
    }

    /**
     * 获取直接子部门
     * 
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public List<Department> getChildren(Long id) {
        List<Department> children = dao.getChildren(id);
        return children;
    }

}
