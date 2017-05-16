package io.springboard.framework.log.service;

import io.springboard.framework.log.dao.ActionLogDao;
import io.springboard.framework.log.entity.ActionLog;
import io.springboard.framework.orm.Page;
import io.springboard.framework.orm.PropertyFilter;
import io.springboard.framework.security.SecUser;
import io.springboard.framework.security.SpringSecurityUtils;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 行为日志业务处理类
 * @author liuwenwei
 * 
 * */
@Service
@Transactional
public class ActionLogService {

	private ActionLogDao dao;

	private static Logger logger = LoggerFactory.getLogger(ActionLogService.class);
	
	public void addLog(String modelName ,String operatorType ,String url ,Integer result ,SecUser user) {
		SecUser u  = user;
		try {
			if(u == null) u = (SecUser)SpringSecurityUtils.getCurrentUser();
		} catch (Exception e) {
			logger.info("log中获取SecUser数据失败：" + e.getMessage());
		}
		
		ActionLog log = new ActionLog();
		
		ServletRequestAttributes sra = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
		if(sra != null) {
			if(StringUtils.isEmpty(url)) url = sra.getRequest().getRequestURL().toString();
			log.setDestIp(sra.getRequest().getLocalAddr());
			log.setSourceIp(sra.getRequest().getRemoteAddr());
		}
		if(u != null) {
			log.setUserId(u.getUserID());
			log.setUsername(u.getUsername());
			log.setRealName(u.getFullName());
			
			String roleName = "";
			for(GrantedAuthority ga : u.getAuthorities()){
				if(roleName.equals("")) roleName = ga.getAuthority().replace("ROLE_", "");
				else roleName = roleName + "," + ga.getAuthority().replace("ROLE_", "");
			}
			log.setRoleName(roleName);
			
			log.setContent("{user:\"" + u.getFullName() + "\", oper:\"" + operatorType + "\"}");
		} else {
			log.setContent(operatorType);
		}
		
		log.setCreateTime(new Date());
		log.setModelName(modelName);
		log.setOperatorType(operatorType);
		log.setUrl(url);
		log.setResult(result);
		
//		System.out.println(log);
		dao.save(log); 
	}
	
	public Page<ActionLog> findPage(Page<ActionLog> page ,List<PropertyFilter> filters) {
		return dao.findPage(page, filters, ActionLog.class);
	}
	
	public ActionLog get(Long id) {
		return dao.get(id);
	}
	
	public void deleteById(Long id){
		dao.delete(id);
	}

	public void deleteByIds(String ids) {
		String[] idArray = ids.split(",");
		for(int i=0;i<idArray.length;i++){
			if(!StringUtils.isEmpty(idArray[i])){
				deleteById(Long.valueOf(idArray[i]));
			}
		}
		
	}
	
	@Autowired
	public void setDao(ActionLogDao dao) {
		this.dao = dao;
	}

}
