package io.springboard.gateway.configuration;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import com.alibaba.druid.pool.DruidDataSource;

@Configuration
@EnableCaching
@EnableAspectJAutoProxy
public class BaseConfig{
	
	@Value("${spring.datasource.driver-class-name}") private String driver;
	@Value("${spring.datasource.url}") private String url;
	@Value("${spring.datasource.password}") private String password;
	@Value("${spring.datasource.username}") private String username;
//	@Value("${db.initialSize}") private int initialSize;
	@Value("${spring.datasource.min-idle}") private int minIdle;
	@Value("${spring.datasource.max-active}") private int maxActive;
	@Value("${spring.datasource.max-wait}") private int maxWait;
	@Value("${spring.datasource.time-between-eviction-runs-millis}") private int timeBetweenEvictionRunsMillis;
	@Value("${spring.datasource.min-evictable-idle-time-millis}") private int minEvictableIdleTimeMillis;
	@Value("${spring.datasource.validation-query}") private String validationQuery;
	@Value("${spring.datasource.test-while-idle}") private boolean testWhileIdle;
	@Value("${spring.datasource.test-on-borrow}") private boolean testOnBorrow;
	@Value("${spring.datasource.test-on-return}") private boolean testOnReturn;
//	@Value("${db.filters}") private String filters;
//	@Value("${db.poolPreparedStatements}") private boolean poolPreparedStatements;
//	@Value("${db.maxOpenPreparedStatements}") private int maxOpenPreparedStatements;

	@Bean(destroyMethod="close")
	public DataSource dataSource() {
		DruidDataSource dataSource = new DruidDataSource();
		
		dataSource.setDriverClassName(driver);
		dataSource.setUrl(url);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		
//		dataSource.setInitialSize(initialSize);
		dataSource.setMinIdle(minIdle);
		dataSource.setMaxActive(maxActive);
		dataSource.setMaxWait(maxWait);
		dataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
		dataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
		dataSource.setValidationQuery(validationQuery);
		dataSource.setTestWhileIdle(testWhileIdle);
		dataSource.setTestOnBorrow(testOnBorrow);
		dataSource.setTestOnReturn(testOnReturn);
//		try {
//	        dataSource.setFilters(filters);
//        } catch (SQLException e) {
//	        logger.error("DruidDataSource.setFilters error : " + e.getMessage(), e);
//        }
//		dataSource.setPoolPreparedStatements(poolPreparedStatements);
//		dataSource.setMaxOpenPreparedStatements(maxOpenPreparedStatements);
		dataSource.setConnectionProperties("druid.stat.slowSqlMillis=10000");
		
		return dataSource;
	}
}
