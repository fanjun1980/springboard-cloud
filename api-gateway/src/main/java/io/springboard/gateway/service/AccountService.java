package io.springboard.gateway.service;

import io.springboard.account.client.RoleClient;
import io.springboard.account.client.UserClient;
import io.springboard.account.dto.RoleDto;
import io.springboard.account.dto.UserDto;
import io.springboard.framework.exception.ValidationException;
import io.springboard.framework.rest.Response;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.assertj.core.util.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import com.google.common.collect.Maps;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@Service
public class AccountService {
	Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private UserClient userClient;
	@Autowired
	private RoleClient roleClient;
	
	@HystrixCommand(fallbackMethod = "stubMetaSource")
	public Map<String, Collection<String>> getMetaSource() {
		return roleClient.getMetaSource().getData();
	}
	@SuppressWarnings("unused")
    private Map<String, Collection<String>> stubMetaSource() {
		return Maps.newHashMap();
	}
	
	@HystrixCommand(fallbackMethod = "stubRoles")
	public List<RoleDto> getRoles(Long id){
		return userClient.getRoles(id).getData();
	}
	@SuppressWarnings("unused")
    private List<RoleDto> stubRoles(Long id){
		return Lists.newArrayList();
	}
	
	@HystrixCommand(ignoreExceptions = {ValidationException.class})
	public UserDto getByUserName(@PathVariable("username") String username){
		Response<UserDto> req = userClient.getByUserName(username);
		if(req.getMeta().isSuccess()) return req.getData();
		else {
			logger.warn("getByUserName error:" + req.getMeta().getMessage());
			return null;
		}
	}
}
