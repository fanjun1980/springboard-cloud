package io.springboard.account.client;

import io.springboard.account.dto.PrivilegeDto;
import io.springboard.account.dto.RoleDto;
import io.springboard.account.dto.UserDto;
import io.springboard.framework.exception.ValidationException;
import io.springboard.framework.orm.Page;
import io.springboard.framework.rest.Response;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@FeignClient(name = "account-service", url = "${springcloud.feignclient.account-service:}", fallback = UserClientFallback.class)
public interface UserClient {
	
	@RequestMapping(value = "/api/user/get_by_name/{username}", method = RequestMethod.GET)
    public Response<UserDto> getByUserName(@PathVariable("username") String username);
	
	@HystrixCommand(ignoreExceptions = {ValidationException.class})
	@RequestMapping(value = "/api/user/get_roles/{id}", method = RequestMethod.GET)
    public Response<List<RoleDto>> getRoles(@PathVariable("id") Long id);
	
	@RequestMapping(value = "/api/user/get_curr_user", method = RequestMethod.GET)
    public Response<UserDto> getCurrUser();
	
	@RequestMapping(value = "/api/user/reset_pass", method = RequestMethod.POST)
    public Response<String> resetPass(@RequestBody Map<String, Object> params);
	
	@RequestMapping(value = "/api/user/get_privileges", method = RequestMethod.GET)
	public Response<List<PrivilegeDto>> getPrivileges(@PathVariable("id") Long id, @RequestParam(name="type",required=false) Long type);
	
	//=======================================================================
	@RequestMapping(value = "/api/user/{id}", method = RequestMethod.GET)
	public Response<UserDto> get(@PathVariable("id") Long id);
	
	@RequestMapping(value = "/api/user/save", method = RequestMethod.POST)
	public Response<String> save(@RequestBody UserDto dto);
	
	@RequestMapping(value = "/api/user/{ids}", method = RequestMethod.DELETE)
	public Response<String> delete(@PathVariable("ids") String ids);
	
	@RequestMapping(value = "/api/user/query", method = RequestMethod.POST)
	public Response<Page<UserDto>> query(@RequestBody Map<String, Object> params);
}
