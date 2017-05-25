package io.springboard;

import io.springboard.framework.utils.ThreadUtils;
import io.springboard.framework.utils.spring.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication(scanBasePackages = {"io.springboard"})
@EnableDiscoveryClient
public class AccountApplication {
	
	private static Logger logger = LoggerFactory.getLogger(AccountApplication.class);

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(AccountApplication.class);
        ApplicationContext ctx = application.run(args);
        
        // profile
        List activeProfiles = SpringUtils.getActiveProfiles();
        logger.warn("Use profile:{}" , Arrays.toString(activeProfiles.toArray()));
        
        // 业务逻辑
        final AccountApplication boot = new AccountApplication();
		boot.start();
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				boot.stop();
			}
		});
    }
    
	public void start() {
		logger.info("User.dir:" + System.getProperty("user.dir"));
		logger.info("Begin start Account-Service!");

		try {
			// TODO 
		} catch (Exception e) {
			logger.info("start Account-Service error:" + e.getMessage());
			System.exit(-1);
		}

		ThreadUtils.sleep(1000);
		logger.info("Start Account-Service completed!");
	}
	public void stop() {
		logger.info("Begin stop Account-Service!");

		try {
			// TODO 
		} catch (Exception e) {
			logger.info("stop Account-Service error:" + e.getMessage());
			System.exit(-1);
		}

		ThreadUtils.sleep(1000);
		logger.info("Stop Account-Service completed!");
	}
}
