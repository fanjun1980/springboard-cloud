package io.springboard.framework.orm.mybatis;

import io.springboard.framework.orm.AbstratorEntity;
import io.springboard.framework.orm.IdEntity;
import io.springboard.framework.orm.Page;
import io.springboard.framework.orm.PropertyFilter;
import io.springboard.framework.orm.PropertyFilter.MatchType;
import io.springboard.framework.security.SpringSecurityUtils;
import io.springboard.framework.utils.FieldNameUtil;
import io.springboard.framework.utils.reflection.ReflectionUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class SimpleMybatisDao<T extends IdEntity, PK extends Serializable> {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private SqlSession sqlSession;

    protected Class<T> entityClass;
    private String tableName = "";
    @SuppressWarnings("unchecked")
    private Map<String, String> dbField = new CaseInsensitiveMap();
    @SuppressWarnings("unchecked")
    private Map<String, String> beanProp = new CaseInsensitiveMap();

    /**
     * 新增/修改
     * 
     * @param entity
     */
    public void save(final T entity) {
        String dbType = DbType.MySQL;
        try {
            dbType = this.getSqlSession().getConnection().getMetaData().getDatabaseProductName();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Long id = entity.getId();
        String sql = "";
        Map<String, Object> params = new HashMap<String, Object>();
        if (id == null || id < 1) {
            if (entity instanceof AbstratorEntity) {
                Long userId = SpringSecurityUtils.getUserId();
                ((AbstratorEntity) entity).setCreateDate(new Date());
                ((AbstratorEntity) entity).setCreateUser(userId);
            }
            if (DbType.PostgreSQL.equals(dbType)) {
                sql = buildInsertSqlPostgreSQL(sql, entity, params);
            } else {
                sql = buildInsertSqlMySQL(sql, entity, params);
            }
            params.put("sql", sql);
            params.put("id", id);
            getSqlSession().insert("io.springboard.framework.orm.mybatis.SimpleMybatisDao.insert", params);
            entity.setId(Long.valueOf(params.get("id") + ""));
        } else {
            if (entity instanceof AbstratorEntity) {
                Long userId = SpringSecurityUtils.getUserId();
                ((AbstratorEntity) entity).setUpdateDate(new Date());
                ((AbstratorEntity) entity).setUpdateUser(userId);
            }
            sql = buildUpdateSql(sql, entity, params);
            params.put("sql", sql);

            getSqlSession().update("io.springboard.framework.orm.mybatis.SimpleMybatisDao.update", params);
        }
    }

    /**
     * 根据主键删除
     * 
     * @param id
     */
    public void delete(final PK id) {
        String sql = "delete from " + tableName + " where id = #{id} ";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("sql", sql);
        params.put("id", id);
        getSqlSession().delete("io.springboard.framework.orm.mybatis.SimpleMybatisDao.delete", params);
    }

    /**
     * 根据主键获取
     * 
     * @param id
     * @return
     */
    public T get(final PK id) {
        String sql = "select * from (select * from " + tableName + ") as _temp where id = #{id} ";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("sql", sql);
        params.put("id", id);
        Map<String, Object> map = getSqlSession().<Map<String, Object>> selectOne("io.springboard.framework.orm.mybatis.SimpleMybatisDao.get", params);
        try {
            if (map != null) {
                T t = entityClass.newInstance();
                for (String key : map.keySet()) {
                    try {
                        ReflectionUtils.setFieldValue(t, getBeanProp(key), map.get(key));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return t;
            } else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 分页查询
     * 
     * @param page
     * @param filters
     * @param clazz
     * @return
     */
    public <D> Page<D> findPage(Page<D> page, List<PropertyFilter> filters, Class<D> clazz) {
        String sql = "select * from " + tableName + " ";
        Map<String, Object> params = new HashMap<String, Object>();
        sql = buildFilterSql(sql, filters, entityClass, params);
        String sqlCount = "select count(0) from (" + sql + ") as total";
        sql = buildOrderSql(sql, page, entityClass);
        params.put("first", (page.getFirst() - 1));
        params.put("pageSize", page.getPageSize());

        String dbType = DbType.MySQL;
        try {
            dbType = this.getSqlSession().getConnection().getMetaData().getDatabaseProductName();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (DbType.PostgreSQL.equals(dbType)) {
            sql = sql + " limit #{pageSize} offset #{first} ";
        } else {
            sql = sql + " limit #{first}, #{pageSize} ";
        }
        params.put("sql", sql);
        List<Map<String, Object>> maps = getSqlSession().<Map<String, Object>> selectList("io.springboard.framework.orm.mybatis.SimpleMybatisDao.findPage", params);

        params.put("sql", sqlCount);
        Long totalCount = getSqlSession().<Long> selectOne("io.springboard.framework.orm.mybatis.SimpleMybatisDao.totalCount", params);
        if (totalCount != null) {
            page.setTotalCount(totalCount);
        } else {
            page.setTotalCount(0L);
        }
        List<D> result = new ArrayList<D>();
        for (Map<String, Object> map : maps) {
            try {
            	D t = clazz.newInstance();
            	if(t instanceof Map){	// MAP
            		for (String key : map.keySet()) {
	                    try {
	                        ((Map)t).put(getBeanProp(key), map.get(key));
	                    } catch (Exception e) {
	                        e.printStackTrace();
	                    }
	                }
            	} else {				// Pojo
	                for (String key : map.keySet()) {
	                    try {
	                        ReflectionUtils.setFieldValue(t, getBeanProp(key), map.get(key));
	                    } catch (Exception e) {
	                        e.printStackTrace();
	                    }
	                }
            	}
                result.add(t);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        page.setResult(result);
        return page;
    }

    public SimpleMybatisDao() {
        this.entityClass = ReflectionUtils.getSuperClassGenricType(getClass());
        tableName = entityClass.getAnnotation(Table.class) != null ? entityClass.getAnnotation(Table.class).name() : entityClass.getName().substring(entityClass.getName().lastIndexOf(".") + 1);
        Field[] fields = ReflectionUtils.getAllField(entityClass);
        for (Field field : fields) {
            String propName = field.getName();
            if (!ifFieldIgnore(entityClass, propName)) {
                String fieldName = FieldNameUtil.camelToLine(propName);
                try {
                    Column column = null;
                    if (field != null) {
                        column = field.getAnnotation(Column.class);
                    }
                    if (column == null) {
                        String getter = "get" + StringUtils.capitalize(propName);
                        Method method = ReflectionUtils.getAccessibleMethod(entityClass, getter);
                        if (method != null) {
                            column = method.getAnnotation(Column.class);
                        }
                    }
                    if (column == null) {
                        String setter = "set" + StringUtils.capitalize(propName);
                        Method method = ReflectionUtils.getAccessibleMethod(entityClass, setter);
                        if (method != null) {
                            column = method.getAnnotation(Column.class);
                        }
                    }
                    if (column != null) {
                        fieldName = column.name();
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
                setBeanProp(fieldName, propName);
                setDbField(propName, fieldName);
            }
        }
    }

    private String buildInsertSqlMySQL(String sql, T entity, Map<String, Object> params) {
        StringBuffer insertSql = new StringBuffer("insert into " + tableName + " (");
        String values = "";
        Object[] keys = beanProp.keySet().toArray();
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i].toString();
            if (i == keys.length - 1) {
                insertSql.append(key + ") values (");
                values += getPropVal(entity, beanProp.get(key), params) + ")";
            } else {
                insertSql.append(key + ", ");
                values += getPropVal(entity, beanProp.get(key), params) + ", ";
            }
        }
        insertSql.append(values);

        return insertSql.toString();
    }

    private String buildInsertSqlPostgreSQL(String sql, T entity, Map<String, Object> params) {
        StringBuffer insertSql = new StringBuffer("insert into " + tableName + " (");
        String values = "";
        @SuppressWarnings("unchecked")
        Map<String, String> beanPropCopy = new CaseInsensitiveMap();
        beanPropCopy.putAll(beanProp);
        beanPropCopy.keySet().remove("ID");
        Object[] keys = beanPropCopy.keySet().toArray();
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i].toString();
            if (i == keys.length - 1) {
                insertSql.append(key + ") values (");
                values += getPropVal(entity, beanProp.get(key), params) + ")";
            } else {
                insertSql.append(key + ", ");
                values += getPropVal(entity, beanProp.get(key), params) + ", ";
            }
        }
        insertSql.append(values);

        return insertSql.toString();
    }

    private String buildUpdateSql(String sql, T entity, Map<String, Object> params) {
        StringBuffer updateSql = new StringBuffer("update " + tableName + " set ");
        @SuppressWarnings("unchecked")
        Map<String, String> beanPropCopy = new CaseInsensitiveMap();
        beanPropCopy.putAll(beanProp);
        beanPropCopy.keySet().remove("ID");
        beanPropCopy.keySet().remove("CREATEUSER");
        beanPropCopy.keySet().remove("CREATEDATE");
        Object[] keys = beanPropCopy.keySet().toArray();
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i].toString();
            if (i == keys.length - 1) {
                updateSql.append(key + " = " + getPropVal(entity, beanProp.get(key), params) + "");
            } else {
                updateSql.append(key + " = " + getPropVal(entity, beanProp.get(key), params) + ", ");
            }
        }
        updateSql.append(" where id = '" + entity.getId() + "'");

        return updateSql.toString();
    }

    private String getPropVal(T entity, String propName, Map<String, Object> params) {
        String value = "null";
        if (ReflectionUtils.getFieldValue(entity, propName) != null) {
            value = "#{" + propName + "}";
            Field prop = ReflectionUtils.getAccessibleField(entityClass, propName);
            if (prop.getType().equals(Boolean.class)) {
                params.put(propName, ((Boolean) ReflectionUtils.getFieldValue(entity, propName) ? 1 : 0));
            } else if (prop.getType().equals(Date.class)) {
                params.put(propName, (Date) ReflectionUtils.getFieldValue(entity, propName));
            } else {
                params.put(propName, ReflectionUtils.getFieldValue(entity, propName));
            }
        }

        return value;
    }

    @SuppressWarnings("rawtypes")
    private String buildFilterSql(String sql, List<PropertyFilter> filters, Class clazz, Map<String, Object> params) {
        StringBuffer filterSql = new StringBuffer("select * from (" + sql + ") as _temp where 1 = 1 ");
        for (PropertyFilter filter : filters) {
            if (!filter.hasMultiProperties()) {// 只有一个属性需要比较的情况.
                String propertyName = filter.getPropertyName();
                String dbFieldName = getDbFieldName(clazz, propertyName);
                if (dbFieldName != null && !dbFieldName.isEmpty()) {
                    propertyName = dbFieldName;
                } else {
                    if (ifFieldIgnore(clazz, propertyName)) {
                        continue;
                    }
                    propertyName = FieldNameUtil.camelToLine(propertyName);
                }
                Object propertyValue = filter.getMatchValue();
                MatchType matchType = filter.getMatchType();
                Assert.hasText(propertyName, "propertyName不能为空");
                switch (matchType) {
                case EQ:
                    filterSql.append(" and " + propertyName + " = #{" + propertyName + "} ");
                    params.put(propertyName, propertyValue);
                    break;
                case LIKE:
                    filterSql.append(" and " + propertyName + " like concat('%', #{" + propertyName + "}, '%') ");
                    params.put(propertyName, propertyValue);
                    break;
                case LE:
                    filterSql.append(" and " + propertyName + " <= #{" + propertyName + "} ");
                    params.put(propertyName, propertyValue);
                    break;
                case LT:
                    filterSql.append(" and " + propertyName + " < #{" + propertyName + "} ");
                    params.put(propertyName, propertyValue);
                    break;
                case GE:
                    filterSql.append(" and " + propertyName + " >= #{" + propertyName + "} ");
                    params.put(propertyName, propertyValue);
                    break;
                case GT:
                    filterSql.append(" and " + propertyName + " > #{" + propertyName + "} ");
                    params.put(propertyName, propertyValue);
                    break;
                case IN:
                    filterSql.append(" and ( ");
                    Object[] vs = (Object[]) propertyValue;
                    for (int i = 0; i < vs.length; i++) {
                        Object av = vs[i];
                        if (i == vs.length - 1) {
                            filterSql.append(" " + propertyName + " = #{" + propertyName + i + "} ");
                            params.put(propertyName + i, av);
                        } else {
                            filterSql.append(" " + propertyName + " = #{" + propertyName + i + "} or ");
                            params.put(propertyName + i, av);
                        }
                    }
                    filterSql.append(" ) ");
                    break;
                }
            } else {// 包含多个属性需要比较的情况,进行or处理.
                String[] propertyNames = filter.getPropertyNames();
                MatchType matchType = filter.getMatchType();
                if (MatchType.LIKE.equals(matchType)) {
                    filterSql.append(" and ( ");
                    Object propertyValue = filter.getMatchValue();
                    for (int i = 0; i < propertyNames.length; i++) {
                        String propertyName = propertyNames[i];
                        String dbFieldName = getDbFieldName(clazz, propertyName);
                        if (dbFieldName != null && !dbFieldName.isEmpty()) {
                            propertyName = dbFieldName;
                        } else {
                            if (ifFieldIgnore(clazz, propertyName)) {
                                continue;
                            }
                            propertyName = FieldNameUtil.camelToLine(propertyName);
                        }
                        if (i == propertyNames.length - 1) {
                            filterSql.append(" concat(" + propertyName + ", '') like concat('%', #{" + propertyName + i + "}, '%') ");
                            params.put(propertyName + i, propertyValue);
                        } else {
                            filterSql.append(" concat(" + propertyName + ", '') like concat('%', #{" + propertyName + i + "}, '%') or ");
                            params.put(propertyName + i, propertyValue);
                        }
                    }
                    filterSql.append(" ) ");
                }
            }
        }

        return filterSql.toString();
    }

    @SuppressWarnings("rawtypes")
    private String buildOrderSql(String filterSql, Page page, Class clazz) {
        StringBuffer orderSql = new StringBuffer("");
        String[] orderBys = page.getOrderBy() == null ? new String[] {} : page.getOrderBy().split(",");
        String[] orders = page.getOrder() == null ? new String[] {} : page.getOrder().split(",");
        if (orderBys.length > 0) {
            for (int i = 0; i < orderBys.length; i++) {
                String orderBy = orderBys[i];
                String dbFieldName = getDbFieldName(clazz, orderBy);
                if (dbFieldName != null && !dbFieldName.isEmpty()) {
                    orderBy = dbFieldName;
                } else {
                    if (ifFieldIgnore(clazz, orderBy)) {
                        continue;
                    }
                    orderBy = FieldNameUtil.camelToLine(orderBy);
                }
                String orderByVal = orders.length > i ? (Page.DESC.equals(orders[i]) ? Page.DESC : Page.ASC) : Page.ASC;
                if (i == orderBys.length - 1) {
                    orderSql.append(" " + orderBy + " " + orderByVal + " ");
                } else {
                    orderSql.append(" " + orderBy + " " + orderByVal + ", ");
                }
            }
        }
        return filterSql + (orderSql.toString().isEmpty() ? "" : " order by " + orderSql.toString());
    }

    @SuppressWarnings("rawtypes")
    private String getDbFieldName(Class clazz, String propertyName) {
        try {
            Column column = null;
            Field field = ReflectionUtils.getAccessibleField(clazz, propertyName);
            if (field != null) {
                column = field.getAnnotation(Column.class);
            }
            if (column == null) {
                String getter = "get" + StringUtils.capitalize(propertyName);
                Method method = ReflectionUtils.getAccessibleMethod(clazz, getter);
                if (method != null) {
                    column = method.getAnnotation(Column.class);
                }
            }
            if (column == null) {
                String setter = "set" + StringUtils.capitalize(propertyName);
                Method method = ReflectionUtils.getAccessibleMethod(clazz, setter);
                if (method != null) {
                    column = method.getAnnotation(Column.class);
                }
            }
            if (column != null) {
                return column.name();
            } else {
                return null;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    @SuppressWarnings("rawtypes")
    private boolean ifFieldIgnore(Class clazz, String propertyName) {
        try {
            Transient column = null;
            Field field = ReflectionUtils.getAccessibleField(clazz, propertyName);
            if (field != null) {
                column = field.getAnnotation(Transient.class);
            }
            if (column == null) {
                String getter = "get" + StringUtils.capitalize(propertyName);
                Method method = ReflectionUtils.getAccessibleMethod(clazz, getter);
                if (method != null) {
                    column = method.getAnnotation(Transient.class);
                }
            }
            if (column == null) {
                String setter = "set" + StringUtils.capitalize(propertyName);
                Method method = ReflectionUtils.getAccessibleMethod(clazz, setter);
                if (method != null) {
                    column = method.getAnnotation(Transient.class);
                }
            }
            if (column != null) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return true;
        }
    }

    @Autowired(required = false)
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        this.sqlSession = new SqlSessionTemplate(sqlSessionFactory);
    }

    public SqlSession getSqlSession() {
        return this.sqlSession;
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public String getDbField(String beanProp) {
        return dbField.get(beanProp);
    }

    public void setDbField(String beanProp, String dbField) {
        this.dbField.put(beanProp, dbField);
    }

    public String getBeanProp(String dbField) {
        return beanProp.get(dbField);
    }

    public void setBeanProp(String dbField, String beanProp) {
        this.beanProp.put(dbField, beanProp);
    }
}
