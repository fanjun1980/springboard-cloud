package io.springboard.gateway.configuration;

import io.springboard.framework.utils.date.DateUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Configuration
@EnableWebMvc
@EnableConfigurationProperties
public class WebConfig extends WebMvcConfigurerAdapter {
	
	@Bean
	public MappingJackson2HttpMessageConverter customJackson2HttpMessageConverter() {
		MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
		
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);	// 排除值为空属性
	//	objectMapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);// 排除初始值未被改变的属性
	//	objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
		// 进行日期格式化
		DateFormat dateFormat = new SimpleDateFormat(DateUtils.DateAndTimeFormat);
		objectMapper.setDateFormat(dateFormat);
		// 设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		
		jsonConverter.setObjectMapper(objectMapper);
		return jsonConverter;
	}

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(customJackson2HttpMessageConverter());
	}

//	@Override
//	public void addViewControllers(ViewControllerRegistry registry) {
//		super.addViewControllers(registry);
//		registry.addViewController("/").setViewName("forward:/index.html");
//	}
//	@Bean 
//	public ViewResolver viewResolver() 
//	{ 
//		UrlBasedViewResolver viewResolver = new UrlBasedViewResolver();
//		viewResolver.setViewClass(JstlView.class);
//		viewResolver.setPrefix("WEB-INF/view/");
//		viewResolver.setSuffix(".jsp");
//		return viewResolver;
//	} 

}
