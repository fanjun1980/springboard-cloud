package io.springboard.framework.orm.mongodb;

import io.springboard.framework.orm.mongodb.SimpleMongoDao;

import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class CollectorDao extends SimpleMongoDao<Collector, String> {

    public Collector getCollectorEntity(String ip, String name) {
        Query query = new BasicQuery("{ ip : '" + ip + "', name : '" + name + "' }");
        Collector col = findOne(query);
        return col;
    }

}
