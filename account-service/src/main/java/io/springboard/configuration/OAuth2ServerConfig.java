package io.springboard.configuration;

import io.springboard.framework.security.SecUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class OAuth2ServerConfig {
	private static final String RESOURCE_ID = "restservice";
 
	@Profile("dev")
	@Configuration
	@EnableResourceServer
	protected static class DevResourceServerConfiguration extends ResourceServerConfigurerAdapter {

		@Qualifier("userInfoTokenServices")
		@Autowired
		UserInfoTokenServices userInfoTokenServices;

		@Override
		public void configure(ResourceServerSecurityConfigurer resources) {
			resources.resourceId(RESOURCE_ID);
			// 根据userinfo生成Principle
			userInfoTokenServices.setPrincipalExtractor(new CustomPrincipalExtractor());
		}

		@Override
		public void configure(HttpSecurity http) throws Exception {
			// @formatter:off
			http.requestMatchers().antMatchers("/**")
				.and()
				.authorizeRequests()
					.anyRequest().permitAll()
				.and()
					.csrf().disable()
					.headers().xssProtection().disable()
					          .frameOptions().sameOrigin();
			// @formatter:on
		}
	}
	
	@Profile("prod")
	@Configuration
	@EnableResourceServer
	protected static class ProdResourceServerConfiguration extends ResourceServerConfigurerAdapter {

		@Qualifier("userInfoTokenServices")
		@Autowired
		UserInfoTokenServices userInfoTokenServices;

		@Override
		public void configure(ResourceServerSecurityConfigurer resources) {
			resources.resourceId(RESOURCE_ID);
			// 根据userinfo生成Principle
			userInfoTokenServices.setPrincipalExtractor(new CustomPrincipalExtractor());
		}

		@Override
		public void configure(HttpSecurity http) throws Exception {
			// @formatter:off
			http.requestMatchers().antMatchers("/**")
				.and()
				.authorizeRequests()
					.antMatchers("/api/user/get_by_name/*","/api/user/get_roles/*","/api/role/get_meta_source","/hystrix*/**").permitAll()
					.anyRequest().authenticated()
				.and()
					.csrf().disable()
					.headers().xssProtection().disable()
					          .frameOptions().sameOrigin();
			// @formatter:on
		}
	}
}

class CustomPrincipalExtractor implements PrincipalExtractor {
	@Override
	public Object extractPrincipal(Map<String, Object> map) {
		Map<String, Object> principal = (Map<String, Object>)map.get("principal");
		Long userID = Long.valueOf(principal.get("userID").toString());
		String fullName = (String)principal.get("fullName");
		String username = (String)principal.get("username");
		boolean enabled = (boolean)principal.get("enabled");
		boolean credentialsNonExpired = (boolean)principal.get("credentialsNonExpired");
		boolean accountNonLocked = (boolean)principal.get("accountNonLocked");
		boolean accountNonExpired = (boolean)principal.get("accountNonExpired");
		List<Map<String,String>> authorities = (List<Map<String,String>>)principal.get("authorities");
		Collection<GrantedAuthority> grantedAuths = authorities.stream().map(item -> new SimpleGrantedAuthority(item.get("authority")))
				.collect(Collectors.toList());

		SecUser user = new SecUser(userID, username, "", fullName,
				enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, grantedAuths);
		return user;
	}

}
