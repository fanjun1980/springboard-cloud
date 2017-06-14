package io.springboard.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.ApplicationContext;

@SpringBootApplication(scanBasePackages = {"io.springboard"})
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"io.springboard"})
@EnableZuulProxy
@EnableHystrix
@EnableHystrixDashboard
public class ApiGatewayApplication  {

    public static void main(String[] args) {
    	ApplicationContext ctx = SpringApplication.run(ApiGatewayApplication.class, args);
        
//        String[] beanNames = ctx.getBeanDefinitionNames();
//        Arrays.sort(beanNames);
//        for (String beanName : beanNames) {
//            System.out.println(beanName);
//        }
    }
}
