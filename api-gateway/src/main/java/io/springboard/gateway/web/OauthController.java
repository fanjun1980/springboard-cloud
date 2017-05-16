package io.springboard.gateway.web;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "授权校验")
@RestController
public class OauthController {
	@Autowired
	private ConsumerTokenServices defaultTokenServices;

	@ApiOperation(value = "登出", notes = "登出")
	@RequestMapping(value = "/oauth/token/revoke", method = RequestMethod.POST)
	public @ResponseBody void logout(Principal principal) throws Exception {
		
		if(principal instanceof Authentication){
			OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails)((Authentication) principal).getDetails();
			defaultTokenServices.revokeToken(details.getTokenValue());
		}
	}
	
	@ApiOperation(value = "检验当前用户信息的URI", notes = "检验当前用户信息的URI")
	@RequestMapping("/userinfo")
	public Principal me(Principal user) {
		return user;
	}

}
