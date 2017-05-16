package io.springboard.framework.utils.security;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.alibaba.druid.filter.config.ConfigTools;

/**
 * 生成密码的工具类（security salt）
 * @author fanjun
 *
 */
public class SecurityPasswdCreator {

	/**
	 * 生成系统用户的密码，基于BCryptPasswordEncoder
	 */
	@Test
	public void testUserPassword() {
		BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
		String passwd = "!@#EDCvfr4";
		System.out.println("===========UserPassword====================");
		System.out.println(bcrypt.matches("!@#EDCvfr4", "$2a$10$TGab4A8/mqll0tZxSkDwGuh2k64tOk5L1IrMa7Zxhy/.2sAs26IW6"));
		System.out.println(bcrypt.matches("!@#EDCvfr4", "$2a$10$CU1fDrGg5/kEKU4Xf9uMC.xkfkuECyZBAUX5Hf4WY9rUwOiTnIgF6"));
	    System.out.println("password=" + passwd + "\nresult=\n" + bcrypt.encode(passwd)); 
	}

	/**
	 * 生成properties中的密码字段的加密结果，基于druid的加密算法
	 */
	@Test
	public void testPropertiesPassword() {
		try {
			String pw = "test";
	        String epw = ConfigTools.encrypt(pw);
	        System.out.println("===========PropertiesPassword====================");
	        System.out.println("passwd=" + pw + "\nresult\n" + epw);
	        String tmp = ConfigTools.decrypt(epw);
	        assertTrue(pw.equals(tmp));
        } catch (Exception e) {
	        e.printStackTrace();
        }
	}
}
