package io.springboard.framework.orm.mybatis;

import io.springboard.framework.orm.Page;
import io.springboard.framework.orm.PropertyFilter;
import io.springboard.framework.orm.PropertyFilter.MatchType;
import io.springboard.framework.utils.FieldNameUtil;
import io.springboard.framework.utils.reflection.ReflectionUtils;
import io.springboard.framework.utils.spring.SpringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.resultset.DefaultResultSetHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

@Intercepts({ @Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class }), @Signature(type = ResultSetHandler.class, method = "handleResultSets", args = { Statement.class }) })
public class PageInterceptor implements Interceptor {
    private static final Logger logger = LoggerFactory.getLogger(PageInterceptor.class);

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (invocation.getTarget() instanceof StatementHandler) {
            StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
            MetaObject metaStatementHandler = MetaObject.forObject(statementHandler, new DefaultObjectFactory(), new DefaultObjectWrapperFactory(), new DefaultReflectorFactory());
            MappedStatement mappedStatement = (MappedStatement) metaStatementHandler.getValue("delegate.mappedStatement");
            // if (mappedStatement.getId().matches(".*ByPage$")) {
            BoundSql boundSql = (BoundSql) metaStatementHandler.getValue("delegate.boundSql");
            Object parameterObject = boundSql.getParameterObject();
            if (parameterObject == null) {
                logger.error("parameterObject is null!");
                throw new NullPointerException("parameterObject is null!");
            } else {
                Page<?> page = null;
                try {
                    page = (Page<?>) metaStatementHandler.getValue("delegate.boundSql.parameterObject.page");
                } catch (Exception e) {
                    logger.warn("分页查询必须包含page参数：" + e.getMessage());
                }
                List<PropertyFilter> filters = null;
                try {
                    filters = (List<PropertyFilter>) metaStatementHandler.getValue("delegate.boundSql.parameterObject.filters");
                } catch (Exception e) {
                    logger.warn("自动匹配必须包含filters参数：" + e.getMessage());
                }
                String sql = boundSql.getSql();
                // Class clazz = ReflectionUtils.getSuperClassGenricType(page.getClass());
                String daoName = mappedStatement.getId().substring(0, mappedStatement.getId().lastIndexOf("."));
                SimpleMybatisDao daoInst = (SimpleMybatisDao) SpringUtils.getBean(Class.forName(daoName));
                // Class clazz = ReflectionUtils.getSuperClassGenricType(Class.forName(daoName));
                Class clazz = daoInst.getEntityClass();
                if (sql == null || sql.trim().isEmpty()) {
                    sql = "select * from " + (clazz.getAnnotation(Table.class) == null ? clazz.getAnnotation(Table.class) : clazz.getName().substring(clazz.getName().lastIndexOf("."))) + " ";
                }
                // 自动匹配
                if (filters != null) {
                    sql = buildFilterSql(sql, filters, clazz);
                    logger.debug("filterSql = " + sql);
                }
                // 分页查询
                if (page != null && page instanceof Page) {
                    // 记录总记录数
                    String countSql = "select count(0) from (" + sql + ") as total";
                    logger.debug("countSql = " + countSql);
                    PreparedStatement countStmt = null;
                    ResultSet rs = null;
                    try {
                        Connection connection = (Connection) invocation.getArgs()[0];
                        countStmt = connection.prepareStatement(countSql);
                        BoundSql countBS = new BoundSql(mappedStatement.getConfiguration(), countSql, boundSql.getParameterMappings(), boundSql.getParameterObject());
                        ParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, parameterObject, countBS);
                        parameterHandler.setParameters(countStmt);
                        rs = countStmt.executeQuery();
                        int totalCount = 0;
                        if (rs.next()) {
                            totalCount = rs.getInt(1);
                        }
                        page.setTotalCount(totalCount);
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                    sql = buildOrderSql(sql, page, clazz);
                    sql = sql + " limit " + (page.getFirst() - 1) + ", " + page.getPageSize() + " ";
                    logger.debug("pageSql = " + sql);
                    metaStatementHandler.setValue("delegate.rowBounds.offset", RowBounds.NO_ROW_OFFSET);
                    metaStatementHandler.setValue("delegate.rowBounds.limit", RowBounds.NO_ROW_LIMIT);
                    metaStatementHandler.setValue("delegate.boundSql.parameterObject.page", page);
                }
                metaStatementHandler.setValue("delegate.boundSql.sql", sql);
                // }
            }
        } else if (invocation.getTarget() instanceof ResultSetHandler) {
            DefaultResultSetHandler resultSetHandler = (DefaultResultSetHandler) invocation.getTarget();
            ParameterHandler parameterHandler = (ParameterHandler) ReflectionUtils.getFieldValue(resultSetHandler, "parameterHandler");
            // MappedStatement mappedStatement = (MappedStatement) ReflectionUtils.getFieldValue(parameterHandler, "mappedStatement");
            // if (mappedStatement.getId().matches(".*ByPage$")) {
            Map<String, Object> parameterObj = (Map<String, Object>) parameterHandler.getParameterObject();
            Page<?> page = null;
            try {
                page = (parameterObj.get("page") != null && parameterObj.get("page") instanceof Page) ? (Page<?>) parameterObj.get("page") : null;
            } catch (Exception e) {
                logger.warn("分页查询必须包含page参数：" + e.getMessage());
            }
            if (page == null) {
                return invocation.proceed();
            }

            Object result = invocation.proceed();
            page.setResult((List) result);
            return page;
        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof StatementHandler || target instanceof ResultSetHandler) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }

    @Override
    public void setProperties(Properties properties) {
    }

    @SuppressWarnings("rawtypes")
    public String buildFilterSql(String sql, List<PropertyFilter> filters, Class clazz) {
        StringBuffer filterSql = new StringBuffer("select * from (" + sql + ") as _temp where 1 = 1 ");
        for (PropertyFilter filter : filters) {
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
                filterSql.append(" and " + propertyName + " = '" + propertyValue + "' ");
                break;
            case LIKE:
                filterSql.append(" and " + propertyName + " like '%" + propertyValue + "%' ");
                break;
            case LE:
                filterSql.append(" and " + propertyName + " <= '" + propertyValue + "' ");
                break;
            case LT:
                filterSql.append(" and " + propertyName + " < '" + propertyValue + "' ");
                break;
            case GE:
                filterSql.append(" and " + propertyName + " >= '" + propertyValue + "' ");
                break;
            case GT:
                filterSql.append(" and " + propertyName + " > '" + propertyValue + "' ");
                break;
            case IN:
                filterSql.append(" and ( ");
                Object[] vs = (Object[]) propertyValue;
                for (int i = 0; i < vs.length; i++) {
                    Object av = vs[i];
                    if (i == vs.length - 1) {
                        filterSql.append(" " + propertyName + " = '" + av + "' ");
                    } else {
                        filterSql.append(" " + propertyName + " = '" + av + "' or ");
                    }
                }
                filterSql.append(" ) ");
                break;
            }
        }

        return filterSql.toString();
    }

    @SuppressWarnings("rawtypes")
    private String buildOrderSql(String filterSql, Page page, Class clazz) {
        StringBuffer orderSql = new StringBuffer("");
        String[] orderBys = (page.getOrderBy() == null ? "" : page.getOrderBy()).split(",");
        String[] orders = (page.getOrder() == null ? "" : page.getOrder()).split(",");
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
                if (i == orderBys.length - 1) {
                    orderSql.append(" " + orderBy + " " + (orders.length > i ? orders[i] : Page.ASC) + " ");
                } else {
                    orderSql.append(" " + orderBy + " " + (orders.length > i ? orders[i] : Page.ASC) + ", ");
                }
            }
        }
        return filterSql + (orderSql.toString().isEmpty() ? "" : " order by " + orderSql.toString());
    }

    @SuppressWarnings("rawtypes")
    private String getDbFieldName(Class clazz, String propertyName) {
        try {
            Column column = null;
            Field field = clazz.getDeclaredField(propertyName);
            if (field != null) {
                column = field.getAnnotation(Column.class);
            }
            if (column == null) {
                String getter = "get" + StringUtils.capitalize(propertyName);
                @SuppressWarnings("unchecked")
                Method method = clazz.getDeclaredMethod(getter);
                if (method != null) {
                    column = method.getAnnotation(Column.class);
                }
            }
            if (column == null) {
                String setter = "set" + StringUtils.capitalize(propertyName);
                @SuppressWarnings("unchecked")
                Method method = clazz.getDeclaredMethod(setter);
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
            Field field = clazz.getDeclaredField(propertyName);
            if (field != null) {
                column = field.getAnnotation(Transient.class);
            }
            if (column == null) {
                String getter = "get" + StringUtils.capitalize(propertyName);
                @SuppressWarnings("unchecked")
                Method method = clazz.getDeclaredMethod(getter);
                if (method != null) {
                    column = method.getAnnotation(Transient.class);
                }
            }
            if (column == null) {
                String setter = "set" + StringUtils.capitalize(propertyName);
                @SuppressWarnings("unchecked")
                Method method = clazz.getDeclaredMethod(setter);
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
}
