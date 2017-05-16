package io.springboard.gateway.configuration.security;

import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

@Configuration
public class OAuth2ServerConfig {
	private static final String RESOURCE_ID = "restservice";
 
	@Configuration
	@EnableResourceServer
	protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

		@Override
		public void configure(ResourceServerSecurityConfigurer resources) {
			resources.resourceId(RESOURCE_ID);
		}
		
		@Bean
		public SecurityAccessDecisionManager accessDecisionManager() {
		    return new SecurityAccessDecisionManager();
		}
		@Bean
		public SecurityMetadataSourceService securityMetadataSource() {
			SecurityMetadataSourceService metaSource = new SecurityMetadataSourceService();
			metaSource.setAnonymousUrls("/userinfo/**","/oauth/token/revoke","/hystrix*/**");
			return metaSource;
		}

		@Override
		public void configure(HttpSecurity http) throws Exception {
			// @formatter:off
			http.requestMatchers().antMatchers("/**")
				.and()
				.authorizeRequests()
					.anyRequest().authenticated()
						.withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
		                    public <O extends FilterSecurityInterceptor> O postProcess(O fsi) {
		                    	fsi.setAccessDecisionManager(accessDecisionManager());
		                        fsi.setSecurityMetadataSource(securityMetadataSource());
		                        return fsi;
		                    }
		                });
			// @formatter:on
		}
	}

	@Configuration
	@EnableAuthorizationServer
	protected static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

		@Autowired
		@Qualifier("authenticationManagerBean")
		private AuthenticationManager authenticationManager;
		
		@Autowired
	    private DataSource dataSource;
		
	    @Bean
	    public TokenStore tokenStore() {
	        return new JdbcTokenStore(dataSource);
	    }
	    
	    @Bean
	    public ClientDetailsService clientDetailsService() {
	        return new JdbcClientDetailsService(dataSource);
	    }
	    
	    @Bean
        protected AuthorizationCodeServices authorizationCodeServices() {
	    	return new JdbcAuthorizationCodeServices(dataSource);
        }

		@Autowired
		private UserDetailsService userDetailsService;
		
		@Override
		public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
			endpoints.authenticationManager(authenticationManager);
	        endpoints.tokenStore(tokenStore());
	        endpoints.userDetailsService(userDetailsService);

	        // 配置TokenServices参数
//	        DefaultTokenServices tokenServices = new DefaultTokenServices();
//	        tokenServices.setTokenStore(endpoints.getTokenStore());
//	        tokenServices.setSupportRefreshToken(true);
//	        tokenServices.setClientDetailsService(endpoints.getClientDetailsService());
//	        tokenServices.setTokenEnhancer(endpoints.getTokenEnhancer());
//	        tokenServices.setAccessTokenValiditySeconds( (int) TimeUnit.DAYS.toSeconds(30)); // 30天
//	        endpoints.tokenServices(tokenServices);
		}

		@Override
		public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
			clients.jdbc(dataSource).clients(clientDetailsService());
		}

		@Bean
		@Primary
		public DefaultTokenServices tokenServices() {
			DefaultTokenServices tokenServices = new DefaultTokenServices();
	        tokenServices.setTokenStore(tokenStore());
	        tokenServices.setSupportRefreshToken(false);
	        tokenServices.setAccessTokenValiditySeconds( (int) TimeUnit.DAYS.toSeconds(30)); // 30天
			return tokenServices;
		}
	}
}
