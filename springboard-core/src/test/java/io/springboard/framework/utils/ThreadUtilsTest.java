package io.springboard.framework.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import io.springboard.framework.test.MockLogbackAppender;
import io.springboard.framework.utils.ThreadUtils;
import io.springboard.framework.utils.ThreadUtils.CustomizableThreadFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadUtilsTest {

	@Test
	public void customizableThreadFactory() {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
			}
		};
		CustomizableThreadFactory factory = new CustomizableThreadFactory("foo");

		Thread thread = factory.newThread(runnable);
		assertEquals("foo-1", thread.getName());

		Thread thread2 = factory.newThread(runnable);
		assertEquals("foo-2", thread2.getName());
	}

	@Test
	public void gracefulShutdown() throws InterruptedException {

		Logger logger = LoggerFactory.getLogger("test");
		MockLogbackAppender appender = new MockLogbackAppender();
		appender.addToLogger("test");
		appender.start();

		//time enough to shutdown
		ExecutorService pool = Executors.newSingleThreadExecutor();
		Runnable task = new Task(logger, 500, 0);
		pool.execute(task);
		ThreadUtils.gracefulShutdown(pool, 1000, 1000, TimeUnit.MILLISECONDS);
		assertTrue(pool.isTerminated());
		assertNull(appender.getFirstLog());

		//time not enough to shutdown,call shutdownNow
		appender.clearLogs();
		pool = Executors.newSingleThreadExecutor();
		task = new Task(logger, 1000, 0);
		pool.execute(task);
		ThreadUtils.gracefulShutdown(pool, 500, 1000, TimeUnit.MILLISECONDS);
		assertTrue(pool.isTerminated());
		assertEquals("InterruptedException", appender.getFirstLog().getMessage());

		//self thread interrupt while calling gracefulShutdown
		appender.clearLogs();

		final ExecutorService self = Executors.newSingleThreadExecutor();
		task = new Task(logger, 100000, 0);
		self.execute(task);

		final CountDownLatch lock = new CountDownLatch(1);
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				lock.countDown();
				ThreadUtils.gracefulShutdown(self, 200000, 200000, TimeUnit.MILLISECONDS);
			}
		});
		thread.start();
		lock.await();
		thread.interrupt();
		ThreadUtils.sleep(500);
		assertEquals("InterruptedException", appender.getFirstLog().getMessage());
	}

	@Test
	public void normalShutdown() throws InterruptedException {

		Logger logger = LoggerFactory.getLogger("test");
		MockLogbackAppender appender = new MockLogbackAppender();
		appender.addToLogger("test");
		appender.start();

		//time not enough to shutdown,write error log.
		appender.clearLogs();
		ExecutorService pool = Executors.newSingleThreadExecutor();
		Runnable task = new Task(logger, 1000, 0);
		pool.execute(task);
		ThreadUtils.normalShutdown(pool, 500, TimeUnit.MILLISECONDS);
		assertTrue(pool.isTerminated());
		assertEquals("InterruptedException", appender.getFirstLog().getMessage());

		//self thread interrupt while calling shutdown
		appender.clearLogs();
		final ExecutorService selfpool = Executors.newSingleThreadExecutor();
		task = new Task(logger, 100000, 1000);
		selfpool.execute(task);

		final CountDownLatch lock = new CountDownLatch(1);
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				lock.countDown();
				ThreadUtils.normalShutdown(selfpool, 200000, TimeUnit.MILLISECONDS);
			}
		});
		thread.start();
		lock.await();
		thread.interrupt();

		ThreadUtils.sleep(1000);

		assertEquals("InterruptedException", appender.getFirstLog().getMessage());
	}

	static class Task implements Runnable {
		private Logger logger;

		private int runTime = 0;

		private int sleepTime;

		Task(Logger logger, int sleepTime, int runTime) {
			this.logger = logger;
			this.sleepTime = sleepTime;
			this.runTime = runTime;
		}

		@Override
		public void run() {
			System.out.println("start task");
			if (runTime > 0) {
				long start = System.currentTimeMillis();
				while (System.currentTimeMillis() - start < runTime) {
				}
			}

			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				logger.warn("InterruptedException");
			}
		}
	}
}
