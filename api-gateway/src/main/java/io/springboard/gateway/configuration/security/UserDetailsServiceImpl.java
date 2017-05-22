package io.springboard.gateway.configuration.security;

import io.springboard.account.client.UserClient;
import io.springboard.account.dto.RoleDto;
import io.springboard.account.dto.UserDto;
import io.springboard.framework.rest.Response;
import io.springboard.framework.security.SecUser;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.google.common.collect.Sets;

/**
 * 实现SpringSecurity的UserDetailsService接口,实现获取用户Detail信息的回调函数.
 * 
 * @author fanjun
 */
@Component
public class UserDetailsServiceImpl implements UserDetailsService {
	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private UserClient userClient;

	/**
	 * 获取用户Details信息的回调函数.
	 */
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {

		UserDto user = null;
		Response<UserDto> req = userClient.getByUserName(username);
		if(req.getMeta().isSuccess()) user = req.getData();
		else {
			logger.warn("getByUserName error:" + req.getMeta().getMessage());
		}

		if (user == null) {
			throw new UsernameNotFoundException("用户" + username + " 不存在");
		}

		Collection<GrantedAuthority> grantedAuths = obtainGrantedAuthorities(user);

		boolean enabled = user.getEnabled() == null ? true : user.getEnabled();
		boolean accountNonExpired = true; //user.getAccountNonExpired() == null ? true : user.getAccountNonExpired();
		boolean credentialsNonExpired = true;
		boolean accountNonLocked = true; //user.getAccountNonLocked() == null ? true : user.getAccountNonLocked();

		UserDetails userdetails = new SecUser(user.getId(), user.getUsername(),
				user.getPassword(), user.getFullName(),enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, grantedAuths);

		return userdetails;
	}

	/**
	 * 获得用户所有角色的权限集合.
	 */
	private Collection<GrantedAuthority> obtainGrantedAuthorities(UserDto user) {
		Collection<GrantedAuthority> authSet = Sets.newHashSet();
		List<RoleDto> roles = userClient.getRoles(user.getId()).getData();
		for (RoleDto role : roles) {
			authSet.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
		}
		return authSet;
	}
}
