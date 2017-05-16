package io.springboard.framework.redis;

import io.springboard.framework.utils.spring.SpringContextHolder;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

import redis.clients.jedis.Jedis;

/**
 * redis工具类，返回jedis实例，可直接操作redis
 * 申请的jedis实例必须调用closeJedis方法释放
 * @author fanjun
 *
 */
public class JedisUtils {
	protected static Logger logger = LoggerFactory.getLogger(JedisUtils.class);
	
	private JedisConnectionFactory jedisFactory;
	private static Map<Jedis, JedisConnection> map = new HashMap<Jedis, JedisConnection>();
	private static JedisUtils instance = new JedisUtils();
    
	private JedisUtils() {
		jedisFactory = SpringContextHolder.getBean(JedisConnectionFactory.class);
	}  
    
    public static JedisUtils getInstance() {  
        return instance;  
    }  
      
    /** 
     * 获取Jedis实例. 
     * @return 
     */  
	public static Jedis getJedis() {
		Jedis jedis = null;
		try {
			JedisConnection jedisConnnection = (JedisConnection) getInstance().jedisFactory.getConnection();
			jedis = jedisConnnection.getNativeConnection();
			map.put(jedis, jedisConnnection);
		} catch (Exception e) {
			logger.error("get jedis failed! ", e);
		}
		return jedis;
	}
	
	/**
	 * 关闭Jedis实例
	 * @param jedis
	 */
	public static void closeJedis(Jedis jedis) {
		JedisConnection jedisConnnection = map.get(jedis);
		if(jedisConnnection != null) jedisConnnection.close();
	}

}
