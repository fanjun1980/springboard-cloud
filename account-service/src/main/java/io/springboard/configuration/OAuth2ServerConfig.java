package io.springboard.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

@Configuration
public class OAuth2ServerConfig {
	private static final String RESOURCE_ID = "restservice";
 
	@Profile("dev")
	@Configuration
	@EnableResourceServer
	protected static class DevResourceServerConfiguration extends ResourceServerConfigurerAdapter {

		@Override
		public void configure(ResourceServerSecurityConfigurer resources) {
			resources.resourceId(RESOURCE_ID);
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

		@Override
		public void configure(ResourceServerSecurityConfigurer resources) {
			resources.resourceId(RESOURCE_ID);
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
