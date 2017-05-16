package io.springboard.gateway.configuration.security;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public class SecurityAccessDecisionManager implements AccessDecisionManager {

	public void decide(Authentication authentication, Object object, 
			Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, InsufficientAuthenticationException {
		
		if (configAttributes != null) {
			for (ConfigAttribute ca : configAttributes) {
				String needRole = ca.getAttribute();
				
				//匿名用户可以访问
				if(needRole.toUpperCase().equals("ROLE_ANONYMOUS")) return;
				
				//authority 为用户所被赋予的权限。 needRole 为访问相应的资源应该具有的权限。
				for (GrantedAuthority authority : authentication.getAuthorities()) {
					if (StringUtils.equals(authority.getAuthority(), "ROLE_" + needRole.toUpperCase())) {
						return;
					}
				}
			}
		}

		throw new AccessDeniedException("没有权限进行操作！");
	}

	public boolean supports(ConfigAttribute attribute) {
		return true;
	}
	public boolean supports(Class<?> clazz) {
		return true;
	}
}
