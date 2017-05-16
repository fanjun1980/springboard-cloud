package io.springboard.framework.test;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

/**
 * 在List中保存日志的Appender, 用于测试日志输出.
 * 
 * 在测试开始前,使用addToLogger方法将此appender添加到需要侦听的logger.
 * 
 * @author fanjun
 */
public class MockLogbackAppender extends AppenderBase<ILoggingEvent> {

	private List<ILoggingEvent> logs = new ArrayList<ILoggingEvent>();
	
	/**
	 * 返回之前append的第一个log事件.
	 */
	public ILoggingEvent getFirstLog() {
		if (logs.isEmpty()) {
			return null;
		}
		return logs.get(0);
	}

	/**
	 * 返回之前append的最后一个log事件.
	 */
	public ILoggingEvent getLastLog() {
		if (logs.isEmpty()) {
			return null;
		}
		return logs.get(logs.size() - 1);
	}

	/**
	 * 返回之前append的log事件List.
	 */
	public List<ILoggingEvent> getAllLogs() {
		return logs;
	}

	/**
	 * 清除之前append的log事件List.
	 */
	public void clearLogs() {
		logs.clear();
	}

	/**
	 * 将此appender添加到logger中.
	 */
	public void addToLogger(String loggerName) {
		Logger logger = (Logger) LoggerFactory.getLogger(loggerName);
		logger.addAppender(this);
	}

	/**
	 * 将此appender添加到logger中.
	 */
	public void addToLogger(Class<?> loggerClass) {
		Logger logger = (Logger) LoggerFactory.getLogger(loggerClass);
		logger.addAppender(this);
	}

	/**
	 * 将此appender从logger中清除.
	 */
	public void removeFromLogger(String loggerName) {
		Logger logger = (Logger) LoggerFactory.getLogger(loggerName);
		logger.detachAppender(this);
	}

	/**
	 * 将此appender从logger中清除.
	 */
	public void removeFromLogger(Class<?> loggerClass) {
		Logger logger = (Logger) LoggerFactory.getLogger(loggerClass);
		logger.detachAppender(this);
	}

//	/**
//	 * 实现AppenderSkeleton的append函数, 将log事件加入到内部的List.
//	 */
//	@Override
//	protected void append(LoggingEvent event) {
//		logs.add(event);
//	}
	
	@Override
	protected void append(ILoggingEvent event) {
		logs.add(event);
	}
}
