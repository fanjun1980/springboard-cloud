package io.springboard.framework.redis;

import static org.junit.Assert.assertTrue;
import io.springboard.framework.redis.JedisUtils;
import io.springboard.framework.test.AbstratorSpringTestCase;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

@ContextConfiguration(locations = { "/applicationContext.xml", "/cache/applicationContext-redis.xml" })
public class JedisUtilsTest extends AbstratorSpringTestCase{
	private Jedis jedis;
	
	@Before
	public void setUp() throws Exception {
		jedis = JedisUtils.getJedis();
	}

	@After
	public void tearDown() throws Exception {
		jedis.flushDB();
		JedisUtils.closeJedis(jedis);
	}

	@Test
	public void testKey() {
		System.out.println("=============key==========================");  
        // 清空数据  
        System.out.println("jedis.flushDB()     : " + jedis.flushDB());  
        System.out.println("jedis.echo(\"foo\")   : " + jedis.echo("foo"));  
        // 判断key否存在  
        System.out.println("jedis.exists(\"foo\") : " + jedis.exists("foo"));
        assertTrue(jedis.exists("foo")==false);
        jedis.set("key", "values");  
        System.out.println("jedis.exists(\"key\") : " + jedis.exists("key"));  
        assertTrue(jedis.exists("key")==true);
	}
	
    @Test
    public void testString() {
    	System.out.println("=============String======================="); 
        //-----添加数据----------  
        jedis.set("name","xinxin");//向key-->name中放入了value-->xinxin  
        System.out.println(jedis.get("name"));//执行结果：xinxin  
        
        jedis.append("name", " is my lover"); //拼接
        System.out.println(jedis.get("name")); 
        
        jedis.del("name");  //删除某个键
        System.out.println(jedis.get("name"));
        //设置多个键值对
        jedis.mset("name","liuling","age","23","qq","476777389");
        jedis.incr("age"); //进行加1操作
        System.out.println(jedis.get("name") + "-" + jedis.get("age") + "-" + jedis.get("qq"));
        
    }
    
    @Test
    public void testMap() {
    	System.out.println("=============map=========================="); 
        //-----添加数据----------  
        Map<String, String> map = new HashMap<String, String>();
        map.put("name", "xinxin");
        map.put("age", "22");
        map.put("qq", "123456");
        jedis.hmset("user",map);
        //取出user中的name，执行结果:[minxr]-->注意结果是一个泛型的List  
        //第一个参数是存入redis中map对象的key，后面跟的是放入map中的对象的key，后面的key可以跟多个，是可变参数  
        List<String> rsmap = jedis.hmget("user", "name", "age", "qq");
        System.out.println(rsmap);  
  
        //删除map中的某个键值  
        jedis.hdel("user","age");
        System.out.println(jedis.hmget("user", "age")); //因为删除了，所以返回的是null  
        System.out.println(jedis.hlen("user")); //返回key为user的键中存放的值的个数2 
        System.out.println(jedis.exists("user"));//是否存在key为user的记录 返回true  
        System.out.println(jedis.hkeys("user"));//返回map对象中的所有key  
        System.out.println(jedis.hvals("user"));//返回map对象中的所有value 
  
        Iterator<String> iter=jedis.hkeys("user").iterator();  
        while (iter.hasNext()){  
            String key = iter.next();  
            System.out.println(key+":"+jedis.hmget("user",key));  
        }  
    }
    
    @Test  
    public void testList(){  
    	System.out.println("=============list========================="); 
        //开始前，先移除所有的内容  
        jedis.del("java framework");  
        System.out.println(jedis.lrange("java framework",0,-1));  
        //先向key java framework中存放三条数据  
        jedis.lpush("java framework","spring");  
        jedis.lpush("java framework","struts");  
        jedis.lpush("java framework","hibernate");  
        //再取出所有数据jedis.lrange是按范围取出，  
        // 第一个是key，第二个是起始位置，第三个是结束位置，jedis.llen获取长度 -1表示取得所有  
        System.out.println(jedis.lrange("java framework",0,-1));  
        
        jedis.del("java framework");
        jedis.rpush("java framework","spring");  
        jedis.rpush("java framework","struts");  
        jedis.rpush("java framework","hibernate"); 
        System.out.println(jedis.lrange("java framework",0,-1));
    }  
    
    @Test  
    public void testSet(){  
    	System.out.println("=============set=========================="); 
        //添加  
        jedis.sadd("user","liuling");  
        jedis.sadd("user","xinxin");  
        jedis.sadd("user","ling");  
        jedis.sadd("user","zhangxinxin");
        jedis.sadd("user","who");  
        //移除noname  
        jedis.srem("user","who");  
        System.out.println(jedis.smembers("user"));//获取所有加入的value  
        System.out.println(jedis.sismember("user", "who"));//判断 who 是否是user集合的元素  
        System.out.println(jedis.srandmember("user"));  
        System.out.println(jedis.scard("user"));//返回集合的元素个数  
        
        //  
        jedis.sadd("sets1", "HashSet1");  
        jedis.sadd("sets1", "SortedSet1");  
        jedis.sadd("sets1", "TreeSet");  
        jedis.sadd("sets2", "HashSet2");  
        jedis.sadd("sets2", "SortedSet1");  
        jedis.sadd("sets2", "TreeSet1");  
        // 交集  
        System.out.println(jedis.sinter("sets1", "sets2"));  
        // 并集  
        System.out.println(jedis.sunion("sets1", "sets2"));  
        // 差集  
        System.out.println(jedis.sdiff("sets1", "sets2"));  
    }  
    
    @Test
    public void testSortedSet(){
    	System.out.println("==============SoretedSet===================");   
        // 清空数据  
        System.out.println(jedis.flushDB());  
        // 添加数据  
        jedis.zadd("zset", 10.1, "hello");  
        jedis.zadd("zset", 10.0, ":");  
        jedis.zadd("zset", 9.0, "zset");  
        jedis.zadd("zset", 11.0, "zset!");  
        // 元素个数  
        System.out.println(jedis.zcard("zset"));  
        // 元素下标  
        System.out.println(jedis.zscore("zset", "zset"));  
        // 集合子集  
        System.out.println(jedis.zrange("zset", 0, -1));  
        // 删除元素  
        System.out.println(jedis.zrem("zset", "zset!"));  
        System.out.println(jedis.zcount("zset", 9.5, 10.5));  
        // 整个集合值  
        System.out.println(jedis.zrange("zset", 0, -1));  
    }
    
    @Test
    public void testHash(){
    	System.out.println("==============HashSet===================");   
    	// 清空数据  
        System.out.println(jedis.flushDB());  
        // 添加数据  
        jedis.hset("hashs", "entryKey", "entryValue");  
        jedis.hset("hashs", "entryKey1", "entryValue1");  
        jedis.hset("hashs", "entryKey2", "entryValue2");  
        // 判断某个值是否存在  
        System.out.println(jedis.hexists("hashs", "entryKey"));  
        // 获取指定的值  
        System.out.println(jedis.hget("hashs", "entryKey")); // 批量获取指定的值  
        System.out.println(jedis.hmget("hashs", "entryKey", "entryKey1"));  
        // 删除指定的值  
        System.out.println(jedis.hdel("hashs", "entryKey"));  
        // 为key中的域 field 的值加上增量 increment  
        System.out.println(jedis.hincrBy("hashs", "entryKey", 123l));  
        // 获取所有的keys  
        System.out.println(jedis.hkeys("hashs"));  
        // 获取所有的values  
        System.out.println(jedis.hvals("hashs"));  
    }
    
    @Test  
    public void testPipelinePref() {  
        long start = new Date().getTime();  
        for (int i = 0; i < 1000; i++) {  
            jedis.set("age1" + i, i + "");  
            jedis.get("age1" + i);// 每个操作都发送请求给redis-server  
        }  
        long end = new Date().getTime();  
        System.out.println("unuse pipeline cost:" + (end - start) + "ms");
        
        jedis.flushDB();
        start = new Date().getTime();  
        Pipeline p = jedis.pipelined();  
        for (int i = 0; i < 10000; i++) {  
            p.set("age2" + i, i + "");  
            //System.out.println(p.get("age2" + i));  
        }  
        p.syncAndReturnAll();// 这段代码获取所有的response  
        end = new Date().getTime();  
        System.out.println("use pipeline cost:" + (end - start) + "ms"); 
    }  
  
}
