package io.springboard.framework.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

/**
 * SpringSecurity的工具类.
 * 
 * 注意. 本类只支持SpringSecurity 4.0.x.
 * 
 * @author fanjun
 */
public class SpringSecurityUtils {
	/**
	 * 取得当前用户, 返回值为SpringSecurity的User类或其子类, 如果当前用户未登录则返回null.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends User> T getCurrentUser() {
		Authentication authentication = getAuthentication();

		if (authentication == null) {
			return null;
		}

		Object principal = authentication.getPrincipal();
		if (!(principal instanceof SecUser)) {
			return null;
		}

		return (T) principal;
	}

	/**
	 * 取得当前用户的登录名, 如果当前用户未登录则返回空字符串.
	 */
	public static String getCurrentUserName() {
		Authentication authentication = getAuthentication();

		if (authentication == null || authentication.getPrincipal() == null) {
			return "";
		}

		return authentication.getName();
	}

	/**
	 * 取得当前用户登录IP, 如果当前用户未登录则返回空字符串.
	 */
	public static String getCurrentUserIp() {
		Authentication authentication = getAuthentication();

		if (authentication == null) {
			return "";
		}

		Object details = authentication.getDetails();
		if (!(details instanceof WebAuthenticationDetails)) {
			return "";
		}

		WebAuthenticationDetails webDetails = (WebAuthenticationDetails) details;
		return webDetails.getRemoteAddress();
	}

	/**
	 * 判断用户是否拥有角色, 如果用户拥有参数中的任意一个角色则返回true.
	 */
	public static boolean hasAnyRole(String... roles) {
		Authentication authentication = getAuthentication();

		if (authentication == null) {
			return false;
		}

		Collection<? extends GrantedAuthority> grantedAuthorityList = authentication.getAuthorities();
		for (String role : roles) {
			for (GrantedAuthority authority : grantedAuthorityList) {
				if (role.equals(authority.getAuthority())) {
					return true;
				}
			}
		}

		return false;
	}
	
	/**
	 * 获取当前用户的角色列表
	 * @return
	 */
	public static List<String> getCurrentUserRoles(){
		Authentication authentication = getAuthentication();
		if(authentication == null) return null;
		
		List<String> result = new ArrayList<String>();
		Collection<? extends GrantedAuthority> grantedAuthorityList = authentication.getAuthorities();
		for (GrantedAuthority authority : grantedAuthorityList) {
			result.add(authority.getAuthority());
		}

		return result;
	}

	/**
	 * 将UserDetails保存到Security Context.
	 * 
	 * @param userDetails 已初始化好的用户信息.
	 * @param request 用于获取用户IP地址信息,可为Null.
	 */
	public static void saveUserDetailsToContext(UserDetails userDetails, HttpServletRequest request) {
		PreAuthenticatedAuthenticationToken authentication = new PreAuthenticatedAuthenticationToken(userDetails,
				userDetails.getPassword(), userDetails.getAuthorities());

		if (request != null) {
			authentication.setDetails(new WebAuthenticationDetails(request));
		}

		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	/**
	 * 取得Authentication, 如当前SecurityContext为空时返回null.
	 */
	private static Authentication getAuthentication() {
		SecurityContext context = SecurityContextHolder.getContext();

		if (context == null) {
			return null;
		}

		return context.getAuthentication();
	}
	
	/**
	 * 获取用户ID
	 * @return
	 */
	public static Long getUserId(){
		Long result=null;
		try {
			SecUser secUser = SpringSecurityUtils.getCurrentUser();
			if (secUser!=null) {
				result = secUser.getUserID();	
			}
		} catch (Exception e) {
		}
		return result;
	}
	
	/**
	 * 获取当前用户的OAuth2AuthenticationDetails，其中可取到sessionId、remoteAddress、token
	 * @return
	 */
	public static OAuth2AuthenticationDetails getOauth2Details() {
		Authentication authentication = getAuthentication();

		if (authentication == null || authentication.getDetails() == null) {
			return null;
		}
		
		Object value = authentication.getDetails();
		if(value instanceof WebAuthenticationDetails) 
			return (OAuth2AuthenticationDetails)authentication.getDetails();
		else 
			return null;
	}
	/**
	 * 获取当前用户的WebAuthenticationDetails，其中可取到sessionId、remoteAddress
	 * @return
	 */
	public static WebAuthenticationDetails getWebDetails() {
		Authentication authentication = getAuthentication();

		if (authentication == null || authentication.getDetails() == null) {
			return null;
		}
		
		Object value = authentication.getDetails();
		if(value instanceof WebAuthenticationDetails) 
			return (WebAuthenticationDetails)authentication.getDetails();
		else 
			return null;
	}
}
