package io.springboard.framework.redis.spring.serializer;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException; 

/**
 * Longde的redis序列化类
 * @author fanjun
 *
 */
public class LongSerializer implements RedisSerializer<Long> {  
 
    @Override  
    public byte[] serialize(Long aLong) throws SerializationException {  
        if (null != aLong) {  
            return aLong.toString().getBytes();  
        } else {  
            return new byte[0];  
        }  
    }  

    @Override  
    public Long deserialize(byte[] bytes) throws SerializationException {  
        if (bytes.length > 0) {  
            return Long.parseLong(new String(bytes));  
        } else {  
            return null;  
        }  
    }  
}  
