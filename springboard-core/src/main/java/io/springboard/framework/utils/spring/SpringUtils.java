package io.springboard.framework.utils.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * Spring工具类
 * 以静态变量保存Spring ApplicationContext, 可在任何代码任何地方任何时候中取出ApplicaitonContext.
 */
@Service
@Lazy(false)
public class SpringUtils implements ApplicationContextAware, DisposableBean {

	private static ApplicationContext applicationContext = null;

	private static Logger logger = LoggerFactory.getLogger(SpringUtils.class);

	/**
	 * 实现ApplicationContextAware接口, 注入Context到静态变量中.
	 */
	public void setApplicationContext(ApplicationContext applicationContext) {
		logger.debug("注入ApplicationContext到SpringContextHolder:" + applicationContext);

		if (SpringUtils.applicationContext != null) {
			logger.warn("SpringContextHolder中的ApplicationContext被覆盖, 原有ApplicationContext为:"
					+ SpringUtils.applicationContext);
		}

		SpringUtils.applicationContext = applicationContext; //NOSONAR
	}

	/**
	 * 实现DisposableBean接口,在Context关闭时清理静态变量.
	 */
	@Override
	public void destroy() throws Exception {
		SpringUtils.clear();
	}

	/**
	 * 取得存储在静态变量中的ApplicationContext.
	 */
	public static ApplicationContext getApplicationContext() {
		assertContextInjected();
		return applicationContext;
	}

	/**
	 * 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name) {
		assertContextInjected();
		return (T) applicationContext.getBean(name);
	}

	/**
	 * 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
	 */
	public static <T> T getBean(Class<T> requiredType) {
		assertContextInjected();
		return applicationContext.getBean(requiredType);
	}

	/**
	 * 清除SpringContextHolder中的ApplicationContext为Null.
	 */
	public static void clear() {
		logger.debug("清除SpringContextHolder中的ApplicationContext:" + applicationContext);
		applicationContext = null;
	}

	/**
	 * 获取当前profiles
	 */
	public static List<String> getActiveProfiles(){
		assertContextInjected();
		String[] activeProfiles = getApplicationContext().getEnvironment().getActiveProfiles();
		return Arrays.asList(activeProfiles);
	}

	/**
	 * 获取配置文件数据
	 */
	public static String getProperty(String key) {
		assertContextInjected();
		return getApplicationContext().getEnvironment().getProperty(key);
	}

	/**
	 * 获取配置文件数据
	 * @param key
	 * @param defaultValue 默认值
	 * @return
	 */
	public static String getProperty(String key, String defaultValue) {
		assertContextInjected();
		return getApplicationContext().getEnvironment().getProperty(key, defaultValue);
	}

	/**
	 * 检查ApplicationContext不为空.
	 */
	protected static void assertContextInjected() {
		if (applicationContext == null) {
			throw new IllegalStateException("applicaitonContext未注入,请在applicationContext.xml中定义SpringContextHolder");
		}
	}
}
