package io.springboard.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// @formatter:off
		http.requestMatchers().antMatchers(HttpMethod.OPTIONS, "/**")
			.and()
			.authorizeRequests().anyRequest().permitAll()
//			.authorizeRequests().antMatchers("/oauth/token").permitAll()
			.and()
				.csrf().disable()
				.headers().xssProtection().disable()
				          .frameOptions().sameOrigin();
		// @formatter:on
	}
}
