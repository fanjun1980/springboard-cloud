package io.springboard.framework.cache;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class HelloManager {
	private boolean hit = false;
	
	@Cacheable(value="messageCache", key="#name", condition="'Joshua'.equals(#name)")
    public String getMessage(String name) {
		hit = true;
        System.out.println("Executing HelloServiceImpl.getHelloMessage(\"" + name + "\")");
        return "Hello " + name + "!";
    }

	public boolean isHit() {
		return hit;
	}
	public void setHit(boolean hit) {
		this.hit = hit;
	}
}
