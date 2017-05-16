package io.springboard.framework.rest;

import io.springboard.framework.orm.IdEntity;
import io.springboard.framework.orm.Page;
import io.springboard.framework.orm.PropertyFilter;

import java.io.Serializable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service 基类, 用于对实体进行操作
 * @author fanjun
 *
 * @param <Entity>
 * @param <PK>
 */
public abstract class BaseService<Entity extends IdEntity, PK extends Serializable> {
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	public abstract void save(Entity entity);
	public abstract void deleteById(PK id);
	public abstract void deleteByIds(String ids);
	public abstract Entity get(PK id);
	public abstract <D> Page<D> findPage(Page<D> page, List<PropertyFilter> filters, Class<D> clazz);
	
	/**
	 * 保存验证，验证错误通过ValidationException抛出
	 * @param entity
	 */
	public abstract void saveValidate(Entity entity);
	/**
	 * 删除验证，验证错误通过ValidationException抛出
	 * @param id
	 */
	public abstract void deleteValidate(PK id);
}
