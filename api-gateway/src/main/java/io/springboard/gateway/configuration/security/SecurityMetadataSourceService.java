package io.springboard.gateway.configuration.security;

import io.springboard.account.client.RoleClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;

/**
 * 提供某个资源对应的权限定义，即getAttributes方法返回的结果。
 * 此类在初始化时，应该取到所有资源及其对应角色的定义。
 * @author fanjun
 *
 */
@Component
public class SecurityMetadataSourceService implements FilterInvocationSecurityMetadataSource {
	private Logger logger = LoggerFactory.getLogger(getClass());
			
	private static Map<String, Collection<ConfigAttribute>> resourceMap = new ConcurrentSkipListMap<String, Collection<ConfigAttribute>>();
	private static Map<String, Collection<ConfigAttribute>> anonymousMap = Maps.newLinkedHashMap();
	private Collection<ConfigAttribute> denyAll = Arrays.asList(new SecurityConfig("ROLE_DENYALL"));
	
	@Autowired
	private RoleClient roleClient;

	public SecurityMetadataSourceService setAnonymousUrls(String... urls){
		if(urls != null && urls.length > 0)	{
			Collection<ConfigAttribute> anonymousAtts = new ArrayList<ConfigAttribute>();
	        ConfigAttribute anonymousCa = new SecurityConfig("ROLE_ANONYMOUS");
	        anonymousAtts.add(anonymousCa);
	        
	        for(String url : urls){
	        	anonymousMap.put(url, anonymousAtts);
	        }
		}
		
		return this;
	}
	
	public void loadMetadataSource() {
		resourceMap.clear();
		resourceMap.putAll(anonymousMap);
		Map<String,Collection<String>> map = roleClient.getMetaSource().getData();
		resourceMap.putAll(convertMap(map));
		logger.info("loadMetadataSource finish.");
	}

	// 根据URL，找到相关的权限配置。
	@Override
	public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
		if(resourceMap.size() == 0){
			loadMetadataSource();
		}
		
		FilterInvocation filterInvocation = (FilterInvocation) object;		
		
		for(String url : anonymousMap.keySet()) {	// 匿名访问权限
			RequestMatcher requestMatcher = new AntPathRequestMatcher(url);
			HttpServletRequest httpRequest = filterInvocation.getHttpRequest();

			if (requestMatcher.matches(httpRequest)) {
				return anonymousMap.get(url);
			}
		}
		for(String url : resourceMap.keySet()) {	// 授权访问权限
			RequestMatcher requestMatcher = new AntPathRequestMatcher(url);
			HttpServletRequest httpRequest = filterInvocation.getHttpRequest();

			if (requestMatcher.matches(httpRequest)) {
				return resourceMap.get(url);
			}
		}

//		return null;
		return denyAll;
	}
	private Map<String, Collection<ConfigAttribute>> convertMap(Map<String,Collection<String>> map){
		Map<String, Collection<ConfigAttribute>> resultMap = Maps.newLinkedHashMap();
		
		for(String url : map.keySet()){
			Collection<String> roles = map.get(url);

			if(roles != null && roles.size() > 0) {
				Collection<ConfigAttribute> cas = new ArrayList<ConfigAttribute>();
				for(String role : roles){
					ConfigAttribute ca = new SecurityConfig(role);
					cas.add(ca);
				}
				resultMap.put(url, cas);
			}
		}
		
		return resultMap;
	}
	
	@Override
	public Collection<ConfigAttribute> getAllConfigAttributes() {
		return null;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return FilterInvocation.class.isAssignableFrom(clazz);
	}

}
