package io.springboard.framework.redis;

import io.springboard.framework.redis.JedisUtils;
import io.springboard.framework.test.AbstratorSpringTestCase;

import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.SortingParams;

@ContextConfiguration(locations = { "/applicationContext.xml", "/cache/applicationContext-redis.xml" })
public class JedisUseCase extends AbstratorSpringTestCase{
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

	
    /** 
     * 时间复杂度： 
     *   O(N+M*log(M))， N 为要排序的列表或集合内的元素数量， M 为要返回的元素数量。 
     *   如果只是使用 SORT 命令的 GET 选项获取数据而没有进行排序，时间复杂度 O(N)。 
     */
	@Test  
    public void testSort1() {  
        // 排序默认以数字作为对象，值被解释为双精度浮点数，然后进行比较  
        // 一般SORT用法 最简单的SORT使用方法是SORT key。  
        jedis.lpush("mylist", "1");  
        jedis.lpush("mylist", "4");  
        jedis.lpush("mylist", "6");  
        jedis.lpush("mylist", "3");  
        jedis.lpush("mylist", "0");  
        // List<String> list = redis.sort("sort");// 默认是升序  
        SortingParams sortingParameters = new SortingParams();  
        sortingParameters.desc();  
        // sortingParameters.alpha();//当数据集中保存的是字符串值时，你可以用 ALPHA修饰符进行排序。  
        sortingParameters.limit(0, 2);// 可用于分页查询  
        List<String> list = jedis.sort("mylist", sortingParameters);// 默认是升序  
//        Long s = jedis.sort("mylist", sortingParameters, "newlist");// 排序结果另存为一个key，用expire可设置失效时间
        for (int i = 0; i < list.size(); i++) {  
            System.out.println(list.get(i));  
        }  
    }  

    /** 
     * sort list 
     * LIST结合hash的排序 
     */  
	@Test  
    public void testSort2() {  
		//user list
        jedis.lpush("userlist", "33");  
        jedis.lpush("userlist", "22");  
        jedis.lpush("userlist", "55");  
        jedis.lpush("userlist", "11");  
        //user detail
        jedis.hset("user:66", "name", "66");  
        jedis.hset("user:55", "name", "55");  
        jedis.hset("user:33", "name", "33");  
        jedis.hset("user:22", "name", "79");  
        jedis.hset("user:11", "name", "24");  
        jedis.hset("user:11", "addr", "beijing");  
        jedis.hset("user:22", "addr", "shanghai");  
        jedis.hset("user:33", "addr", "guangzhou");  
        jedis.hset("user:55", "addr", "chongqing");  
        jedis.hset("user:66", "addr", "xi'an");  
  
        SortingParams sortingParameters = new SortingParams();  
        // 符号 "->" 用于分割哈希表的键名(key name)和索引域(hash field)，格式为 "key->field" 。  
        sortingParameters.get("user:*->name");  
        sortingParameters.get("user:*->addr");  
//        sortingParameters.by("user:*->name");  
//        sortingParameters.get("#");  
        List<String> result = jedis.sort("userlist", sortingParameters);  
        for (String item : result) {  
            System.out.println("item...." + item);  
        }  
        /** 
         * 对应的redis客户端命令是：
         * sort ml get user*->name sort ml get user:*->name get user:*->addr 
         */  
    }  
  
    /** 
     * sort set 
     * SET结合String的排序 
     */  
	@Test
    public void testSort3() {  
        jedis.sadd("tom:friend:list", "123"); // tom的好友id列表  
        jedis.sadd("tom:friend:list", "456");  
        jedis.sadd("tom:friend:list", "789");  
        jedis.sadd("tom:friend:list", "101");  
  
        jedis.set("score:uid:123", "1000"); // 好友对应的成绩  
        jedis.set("score:uid:456", "6000");  
        jedis.set("score:uid:789", "100");  
        jedis.set("score:uid:101", "5999");  
  
        jedis.set("uid:123", "{'uid':123,'name':'lucy'}"); // 好友的详细信息  
        jedis.set("uid:456", "{'uid':456,'name':'jack'}");  
        jedis.set("uid:789", "{'uid':789,'name':'jay'}");  
        jedis.set("uid:101", "{'uid':101,'name':'jolin'}");  
  
        SortingParams sortingParameters = new SortingParams();  
        sortingParameters.desc();  
        // sortingParameters.limit(0, 2);  
        // 注意GET操作是有序的，GET user_name_* GET user_password_*  
        // 和 GET user_password_* GET user_name_*返回的结果位置不同  
        sortingParameters.get("#");// GET 还有一个特殊的规则—— "GET #"  
                                   // ，用于获取被排序对象(我们这里的例子是 user_id )的当前元素。  
        sortingParameters.get("uid:*");  
        sortingParameters.get("score:uid:*");  
        sortingParameters.by("score:uid:*");  
        // 对应的redis 命令是
        // sort tom:friend:list by score:uid:* get # get uid:* get score:uid:*  
        List<String> result = jedis.sort("tom:friend:list", sortingParameters);  
        for (String item : result) {  
            System.out.println("item..." + item);  
        }  
    }  

	/** 
     * 可以利用lrange对list进行分页操作 
     */  
    @Test  
    public void queryPageBy() {  
        int pageNo = 2;  
        int pageSize = 10;  
        for (int i = 1; i <= 30; i++) {  
            jedis.rpush("a", i + "");  
        }  
  
        int start = pageSize * (pageNo - 1);// 因为redis中list元素位置基数是0  
        int end = start + pageSize - 1;  
  
        List<String> results = jedis.lrange("a", start, end);// 从start算起，start算一个元素，到结束那个元素  
        for (String str : results) {  
            System.out.println(str);  
        }  
    }  
  
    /** 
     * [向Redis list压入ID而不是实际的数据] 
     * 在上面的例子里 ，我们将“对象”（此例中是简单消息）直接压入Redis list，但通常不应这么做， 由于对象可能被多次引用. 
     * 让我们回到reddit.com的例子，将用户提交的链接（新闻）添加到list中，有更可靠的方法如下所示： 
        $ redis-cli incr next.news.id 
        (integer) 1 
        $ redis-cli set news:1:title "Redis is simple" 
        OK 
        $ redis-cli set news:1:url "http://code.google.com/p/redis" 
        OK 
        $ redis-cli lpush submitted.news 1 
        OK 
        我们自增一个key，很容易得到一个独一无二的自增ID，然后通过此ID创建对象–为对象的每个字段设置一个key。最后将新对象的ID压入submitted.news list。 
        这只是牛刀小试。在命令参考文档中可以读到所有和list有关的命令。你可以删除元素，旋转list，根据索引获取和设置元素，当然也可以用LLEN得到list的长度。 
     */  
    @Test  
    public void testListStrUsage() {  
        String title = "太阳能是绿色能源4";  
        String url = "http://javacreazyer.iteye.com";   
  
        long adInfoId = jedis.incr("ad:adinfo:next.id");  
        jedis.set("ad:adinfo:" + adInfoId + ":title", title);  
        jedis.set("ad:adinfo:" + adInfoId + ":url", url);  
        jedis.lpush("ad:adinfo", String.valueOf(adInfoId));  
  
        String resultTitle = jedis.get("ad:adinfo:" + adInfoId + ":title");  
        String resultUrl = jedis.get("ad:adinfo:" + adInfoId + ":url");  
        List<String> ids = jedis.lrange("ad:adinfo", 0, -1);  
        System.out.println(resultTitle);  
        System.out.println(resultUrl);  
        System.out.println(ids);  
  
        /** 
         * dbsize返回的是所有key的数目，包括已经过期的， 而redis-cli keys "*"查询得到的是有效的key数目 
         */  
        System.out.println(jedis.dbSize());  
    }  
  
    /** 
     * 下面是一个简单的方案：对每个想加标签的对象，用一个标签ID集合与之关联，并且对每个已有的标签，一组对象ID与之关联。 例如假设我们的新闻ID 
     * 1000被加了三个标签tag 1,2,5和77，就可以设置下面两个集合： $ redis-cli sadd news:1000:tags 1 
     * (integer) 1 $ redis-cli sadd news:1000:tags 2 (integer) 1 $ redis-cli 
     * sadd news:1000:tags 5 (integer) 1 $ redis-cli sadd news:1000:tags 77 
     * (integer) 1 $ redis-cli sadd tag:1:objects 1000 (integer) 1 $ redis-cli 
     * sadd tag:2:objects 1000 (integer) 1 $ redis-cli sadd tag:5:objects 1000 
     * (integer) 1 $ redis-cli sadd tag:77:objects 1000 (integer) 1 
     * 要获取一个对象的所有标签，如此简单： $ redis-cli smembers news:1000:tags 1. 5 2. 1 3. 77 4. 
     * 2 而有些看上去并不简单的操作仍然能使用相应的Redis命令轻松实现。例如我们也许想获得一份同时拥有标签1, 2, 
     * 10和27的对象列表。这可以用SINTER命令来做，他可以在不同集合之间取出交集。因此为达目的我们只需： $ redis-cli sinter 
     * tag:1:objects tag:2:objects tag:10:objects tag:27:objects ... no result 
     * in our dataset composed of just one object ... 
     * 在命令参考文档中可以找到和集合相关的其他命令，令人感兴趣的一抓一大把。一定要留意SORT命令，Redis集合和list都是可排序的。 
     */  
    @Test  
    public void testSetUsage() {  
        jedis.sadd("zhongsou:news:1000:tags", "1");  
        jedis.sadd("zhongsou:news:1000:tags", "2");  
        jedis.sadd("zhongsou:news:1000:tags", "5");  
        jedis.sadd("zhongsou:news:1000:tags", "77");  
        jedis.sadd("zhongsou:news:2000:tags", "1");  
        jedis.sadd("zhongsou:news:2000:tags", "2");  
        jedis.sadd("zhongsou:news:2000:tags", "5");  
        jedis.sadd("zhongsou:news:2000:tags", "77");  
        jedis.sadd("zhongsou:news:3000:tags", "2");  
        jedis.sadd("zhongsou:news:4000:tags", "77");  
        jedis.sadd("zhongsou:news:5000:tags", "1");  
        jedis.sadd("zhongsou:news:6000:tags", "5");  
  
        jedis.sadd("zhongsou:tag:1:objects", 1000 + "");  
        jedis.sadd("zhongsou:tag:2:objects", 1000 + "");  
        jedis.sadd("zhongsou:tag:5:objects", 1000 + "");  
        jedis.sadd("zhongsou:tag:77:objects", 1000 + "");  
  
        jedis.sadd("zhongsou:tag:1:objects", 2000 + "");  
        jedis.sadd("zhongsou:tag:2:objects", 2000 + "");  
        jedis.sadd("zhongsou:tag:5:objects", 2000 + "");  
        jedis.sadd("zhongsou:tag:77:objects", 2000 + "");  
  
        Set<String> sets = jedis.sinter("zhongsou:tag:1:objects",  
                "zhongsou:tag:2:objects", "zhongsou:tag:5:objects",  
                "zhongsou:tag:77:objects");  
        System.out.println(sets);   
    }  
  
    @Test  
    public void testSortedSetUsage() {    
        jedis.zadd("zhongsou:hackers", 1940, "Alan Kay");  
        jedis.zadd("zhongsou:hackers", 1953, "Richard Stallman");  
        jedis.zadd("zhongsou:hackers", 1943, "Jay");  
        jedis.zadd("zhongsou:hackers", 1920, "Jellon");  
        jedis.zadd("zhongsou:hackers", 1965, "Yukihiro Matsumoto");  
        jedis.zadd("zhongsou:hackers", 1916, "Claude Shannon");  
        jedis.zadd("zhongsou:hackers", 1969, "Linus Torvalds");  
        jedis.zadd("zhongsou:hackers", 1912, "Alan Turing");  
  
        Set<String> hackers = jedis.zrange("zhongsou:hackers", 0, -1);  
        System.out.println(hackers);  
  
        Set<String> hackers2 = jedis.zrevrange("zhongsou:hackers", 0, -1);  
        System.out.println(hackers2);  
  
        // 区间操作,我们请求Redis返回score介于负无穷到1920年之间的元素（两个极值也包含了）。  
        Set<String> hackers3 = jedis.zrangeByScore("zhongsou:hackers", "-inf",  
                "1920");  
        System.out.println(hackers3);  
  
        // ZREMRANGEBYSCORE 这个名字虽然不算好，但他却非常有用，还会返回已删除的元素数量。  
        long num = jedis.zremrangeByScore("zhongsou:hackers", "-inf", "1920");  
        System.out.println(num);   
    }  
}
