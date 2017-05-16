package io.springboard.framework.utils.security;

import io.springboard.framework.utils.security.DigestUtils;

import java.io.IOException;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * 支持SHA-1/MD5消息摘要工具类的测试用例兼DEMO.
 * 
 * 支持Hex与Base64两种编码方式.
 * 
 * @author calvin
 */
public class DigestUtilsTest {

	@Test
	public void digestString() {
		String input = "123";

		System.out.println("md5 in hex result              :" + DigestUtils.md5ToHex(input));
		System.out.println("md5 in base64 result           :" + DigestUtils.md5ToBase64(input));
		System.out.println("md5 in base64 url result       :" + DigestUtils.md5ToBase64UrlSafe(input));
		System.out.println("sha1 in hex result              :" + DigestUtils.sha1ToHex(input));
		System.out.println("sha1 in base64 result           :" + DigestUtils.sha1ToBase64(input));
		System.out.println("sha1 in base64 url result       :" + DigestUtils.sha1ToBase64UrlSafe(input));
	}

	@Test
	public void digestFile() throws IOException {
		Resource resource = new ClassPathResource("/mybatis-config.xml");

		System.out.println("md5: " + DigestUtils.md5ToHex(resource.getInputStream()));
		System.out.println("sha1:" + DigestUtils.sha1ToHex(resource.getInputStream()));
	}

}
