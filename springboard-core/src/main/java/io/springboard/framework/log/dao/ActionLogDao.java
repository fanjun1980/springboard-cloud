package io.springboard.framework.log.dao;

import io.springboard.framework.log.entity.ActionLog;
import io.springboard.framework.orm.mybatis.SimpleMybatisDao;

import org.springframework.stereotype.Repository;

@Repository
public class ActionLogDao extends SimpleMybatisDao<ActionLog, Long>{

}
