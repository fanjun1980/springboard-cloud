package io.springboard.framework.redis.spring.serializer;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

/**
 * Integer的redis序列化类
 * @author fanjun
 *
 */
public class IntSerializer implements RedisSerializer<Integer> {  
    @Override  
    public byte[] serialize(Integer i) throws SerializationException {  
        if (null != i) {  
            return i.toString().getBytes();  
        } else {  
            return new byte[0];  
        }  
    }  

    @Override  
    public Integer deserialize(byte[] bytes) throws SerializationException {  
        if (bytes.length > 0) {  
            return Integer.parseInt(new String(bytes));  
        } else {  
            return null;  
        }  
    }  
}  
