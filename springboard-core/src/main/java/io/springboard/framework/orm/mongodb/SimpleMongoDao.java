package io.springboard.framework.orm.mongodb;

import io.springboard.framework.orm.Page;
import io.springboard.framework.orm.PropertyFilter;
import io.springboard.framework.orm.PropertyFilter.MatchType;
import io.springboard.framework.utils.reflection.ReflectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.Assert;

import com.mongodb.CommandResult;
import com.mongodb.DBCollection;

public class SimpleMongoDao<T, PK extends Serializable> {
    @Autowired
    protected MongoOperations mongoOp;
    protected Class<T> entityClass;

    public SimpleMongoDao() {
        this.entityClass = ReflectionUtils.getSuperClassGenricType(getClass());
    }

    /**
     * 新增
     */
    public void insert(T entity) {
        mongoOp.insert(entity);
    }

    public void insert(T entity, String collectionName) {
        mongoOp.insert(entity, collectionName);
    }

    /**
     * 保存
     */
    public void save(T entity) {
        mongoOp.save(entity);
    }

    public void save(T entity, String collectionName) {
        mongoOp.save(entity, collectionName);
    }

    /**
     * 批量新增
     */
    public void insertAll(List<T> entitys) {
        mongoOp.insertAll(entitys);
    }

    public void insertAll(List<T> entitys, String collectionName) {
        mongoOp.insert(entitys, collectionName);
    }

    /**
     * 删除,按主键id, 如果主键的值为null,删除会失败
     * 
     * @param id
     */
    public void deleteById(PK id) {
        mongoOp.remove(id);
    }

    public void deleteById(PK id, String collectionName) {
        mongoOp.remove(id, collectionName);
    }

    /**
     * 按条件删除
     */
    public void delete(Query query) {
        mongoOp.remove(query, entityClass);
    }

    public void delete(Query query, String collectionName) {
        mongoOp.remove(query, entityClass, collectionName);
    }

    /**
     * 删除全部
     */
    public void deleteAll() {
        mongoOp.dropCollection(entityClass);
    }

    public void deleteAll(String collectionName) {
        mongoOp.dropCollection(collectionName);
    }

    /**
     * 根据主键查询
     * 
     * @param id
     * @return
     */
    public T findById(String id) {
        return mongoOp.findById(id, entityClass);
    }

    public T findById(String id, String collectionName) {
        return mongoOp.findById(id, entityClass, collectionName);
    }

    /**
     * 查询全部
     */
    public List<T> findAll() {
        return mongoOp.findAll(entityClass);
    }

    public List<T> findAll(String collectionName) {
        return mongoOp.findAll(entityClass, collectionName);
    }

    /**
     * 按条件查询, 分页
     * 
     * @param query
     * @param skip
     * @param limit
     * @return
     */
    public List<T> find(Query query, int skip, int limit) {
        query.skip(skip);
        query.limit(limit);
        return mongoOp.find(query, entityClass);
    }

    public List<T> find(Query query, int skip, int limit, String collectionName) {
        query.skip(skip);
        query.limit(limit);
        return mongoOp.find(query, entityClass, collectionName);
    }

    /**
     * 按条件查询，分页返回page
     * 
     * @param page
     * @param query
     * @return
     */
    public Page<T> findByPage(Page<T> page, Query query) {
        if (page.isAutoCount()) {
            long totalCount = this.count(query);
            page.setTotalCount(totalCount);
        }
        if (page.getPageSize() > 0) {
            query.skip(page.getFirst() - 1);
            query.limit(page.getPageSize());
        }
        List<T> result = this.find(query);
        page.setResult(result);
        return page;
    }

    public Page<T> findByPage(Page<T> page, Query query, String collectionName) {
        if (page.isAutoCount()) {
            long totalCount = this.count(query, collectionName);
            page.setTotalCount(totalCount);
        }
        if (page.getPageSize() > 0) {
            query.skip(page.getFirst() - 1);
            query.limit(page.getPageSize());
        }
        List<T> result = this.find(query, collectionName);
        page.setResult(result);
        return page;
    }

    public Page<T> findByPage(Page<T> page, List<PropertyFilter> filters) {
        Query query = expandFilter(filters);
        long totalCount = mongoOp.count(query, entityClass);
        query.skip(page.getFirst() - 1);
        query.limit(page.getPageSize());
        String[] orderBys = (page.getOrderBy() == null ? "" : page.getOrderBy()).split(",");
        String[] orders = (page.getOrder() == null ? "" : page.getOrder()).split(",");
        List<Order> orderList = new ArrayList<Order>();
        if (orderBys.length > 0) {
            for (int i = 0; i < orderBys.length; i++) {
                Direction direction = Direction.ASC;
                if (orders.length > i) {
                    direction = Page.DESC.equals(orders[i]) ? Direction.DESC : Direction.ASC;
                }
                Order order = new Order(direction, orderBys[i]);
                orderList.add(order);
            }
            query.with(new Sort(orderList));
        }
        List<T> result = mongoOp.find(query, entityClass);
        page.setResult(result);
        page.setTotalCount(totalCount);
        return page;
    }

    /**
     * 按条件查询, 不分页
     * 
     * @param query
     * @return
     */
    public List<T> find(Query query) {
        return mongoOp.find(query, entityClass);
    }

    public List<T> find(Query query, String collectionName) {
        return mongoOp.find(query, entityClass, collectionName);
    }

    /**
     * 按条件查询单个对象
     * 
     * @param query
     * @return
     */
    public T findOne(Query query) {
        return mongoOp.findOne(query, entityClass);
    }

    public T findOne(Query query, String collectionName) {
        return mongoOp.findOne(query, entityClass, collectionName);
    }

    /**
     * 查询出来后 删除
     */
    public T findAndRemove(Query query) {
        return mongoOp.findAndRemove(query, entityClass);
    }

    public T findAndRemove(Query query, String collectionName) {
        return mongoOp.findAndRemove(query, entityClass, collectionName);
    }

    /**
     * 获取结果集记录数
     * 
     * @param query
     * @return
     */
    public long count(Query query) {
        return mongoOp.count(query, entityClass);
    }

    public long count(Query query, String collectionName) {
        return mongoOp.count(query, collectionName);
    }

    /**
     * 根据collectionName获取Collection对象
     * 
     * @param collectionName
     * @return
     */
    public DBCollection getCollection(String collectionName) {
        return mongoOp.getCollection(collectionName);
    }

    /**
     * 创建Collection
     * 
     * @param collectionName
     */
    public void createCollection(String collectionName) {
        mongoOp.createCollection(collectionName);
    }

    /**
     * 判断Collection 是否存在
     * 
     * @return
     */
    public boolean collectionExists(String collectionName) {
        return mongoOp.collectionExists(collectionName);
    }

    /**
     * 直接执行shell命令
     * 
     * @param jsonCommand
     * @return
     */
    public CommandResult executeCommand(String jsonCommand) {
        return mongoOp.executeCommand(jsonCommand);
    }

    // //查询例子
    // Query query = new BasicQuery("{ age : { $lt : 40 }, name : 'cat' }");
    // //等介于，推荐上面用法
    // Query query = new Query();
    // Criteria criteria = Criteria.where("age").lt(40).andOperator(Criteria.where("name").is("cat"));
    // query.addCriteria(criteria);

    public MongoOperations getMongoOp() {
        return mongoOp;
    }

    public Query expandFilter(List<PropertyFilter> filters) {
        Query query = new Query();
        for (PropertyFilter filter : filters) {
            query.addCriteria(buildCriterion(filter.getPropertyName(), filter.getMatchValue(), filter.getMatchType()));
        }

        return query;
    }

    private Criteria buildCriterion(final String propertyName, final Object propertyValue, final MatchType matchType) {
        Assert.hasText(propertyName, "propertyName不能为空");
        Criteria criteria = null;
        // 根据MatchType构造criteria
        switch (matchType) {
        case EQ:
            criteria = new Criteria(propertyName).is(propertyValue);
            break;
        case LIKE:
            criteria = new Criteria(propertyName).regex(".*" + propertyValue + ".*");
            break;
        case LE:
            criteria = new Criteria(propertyName).lte(propertyValue);
            break;
        case LT:
            criteria = new Criteria(propertyName).lt(propertyValue);
            break;
        case GE:
            criteria = new Criteria(propertyName).gte(propertyValue);
            break;
        case GT:
            criteria = new Criteria(propertyName).gt(propertyValue);
            break;
        case IN:
            criteria = new Criteria(propertyName).in((Object[]) propertyValue);
            break;
        }
        return criteria;
    }
}
