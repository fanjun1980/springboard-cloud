package io.springboard.framework.test;

import org.junit.runner.RunWith;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用于service测试的基类
 * 默认支持事务，事务类型为自动rollback；
 * 调试多线程且有事务时，慎用
 * 
 * @author fanjun
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext.xml" })
@Rollback(true)
@Transactional
public abstract class AbstratorDBTestCase {
}
