package io.springboard.account.web;

import io.springboard.account.client.UserClient;
import io.springboard.account.dto.PrivilegeDto;
import io.springboard.account.dto.RoleDto;
import io.springboard.account.dto.UserDto;
import io.springboard.account.entity.Privilege;
import io.springboard.account.entity.Role;
import io.springboard.account.entity.User;
import io.springboard.account.service.UserService;
import io.springboard.framework.exception.ValidationException;
import io.springboard.framework.orm.Page;
import io.springboard.framework.orm.PropertyFilter;
import io.springboard.framework.rest.BaseController;
import io.springboard.framework.rest.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * User Controller
 * @author fanjun
 */
@Api(value = "User")
@RestController
@RequestMapping(value = "/api/user")
public class UserController extends BaseController implements UserClient {
	
	@Autowired
	protected UserService service;

    @ApiOperation(value = "根据用户名，获取用户", notes = "根据用户名，获取用户")
    @RequestMapping(value = "/get_by_name/{username}", method = RequestMethod.GET)
    public Response<UserDto> getByUserName(@PathVariable("username") String username) {
        User entity = service.getByUserName(username);
        UserDto dto = convert2Dto(entity, UserDto.class);
        if (entity == null) {
            logger.info("username={username}的记录不存在", username);
            return new Response<UserDto>().failure(404, "无此记录");
        }
        return new Response<UserDto>().success(dto);
    }

    @ApiOperation(value = "获取用户对应角色", notes = "根据userid获取用户对应角色")
    @RequestMapping(value = "/get_roles/{id}", method = RequestMethod.GET)
    public Response<List<RoleDto>> getRoles(@PathVariable("id") Long id) {
        if (id == null)
            throw new ValidationException("id不可为空");
        List<Role> roles = service.getUserRoles(id);
        List<RoleDto> rds = roles.stream().map(role -> convert2Dto(role, RoleDto.class)).collect(Collectors.toList());
        return new Response<List<RoleDto>>().success(rds);
    }
    
    @ApiOperation(value = "获取用户对应权限", notes = "获取用户对应权限,可选择权限类型")
    @RequestMapping(value = "/get_privileges/{id}", method = RequestMethod.GET)
    public Response<List<PrivilegeDto>> getPrivileges(@PathVariable("id") Long id, @RequestParam(name="type",required=false) Long type) {
        List<Privilege> pris = service.getUserPrivileges(id, type);
        List<PrivilegeDto> pds = pris.stream().map(pri -> convert2Dto(pri, PrivilegeDto.class)).collect(Collectors.toList());
		return new Response<List<PrivilegeDto>>().success(pds); 
    }

    @ApiOperation(value = "获取当前登录用户", notes = "获取当前登录用户")
    @RequestMapping(value = "/get_curr_user", method = RequestMethod.GET)
    public Response<UserDto> getCurrUser() {
        User u = service.getCurrentUser();
        if(u == null) throw new ValidationException("当前未登录");
        else {
        	UserDto dto = convert2Dto(u, UserDto.class);
        	return new Response<UserDto>().success(dto);
        }
    }

    @ApiOperation(value = "重置密码", notes = "重置密码")
    @RequestMapping(value = "/reset_pass", method = RequestMethod.POST)
    public Response<String> resetPass(@RequestBody Map<String, Object> params) {
        if (params == null || params.size() == 0)
            throw new ValidationException("参数不可为空");
        if (params.get("password_old") == null)
            throw new ValidationException("原密码不可为空");
        if (params.get("password_new") == null)
            throw new ValidationException("新密码不可为空");
        User u = service.getCurrentUser();
        if(u == null)
        	throw new ValidationException("当前未登录");
        
        String oldPass = params.get("password_old").toString();
        String newPass = params.get("password_new").toString();
        BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
        if (bcrypt.matches(oldPass, u.getPassword())) {
            u.setPassword(bcrypt.encode(newPass));
            service.save(u);	// TODO 为了修改密码，保存这个User不妥
        } else
            throw new ValidationException("原密码输入错误");

        return new Response<String>().success();
    }
    
    //===============================================================================
  	@ApiOperation(value = "获取User对象", notes = "获取User对象的方法")
  	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
  	public Response<UserDto> get(@PathVariable("id") Long id) {
  		User entity = service.get(id);
  		if(entity == null) {
  			logger.info("id={id}的记录不存在", id);
  			return new Response<UserDto>().failure(404, "无此记录");
  		}
  		return new Response<UserDto>().success(convert2Dto(entity, UserDto.class)); 
  	}
  	
  	@ApiOperation(value = "保存User对象", notes = "保存User对象的方法")
  	@RequestMapping(value = "/save", method = RequestMethod.POST)
  	public Response<String> save(@RequestBody UserDto dto) {
  		User entity = convert2Entity(dto, User.class);
  		service.save(entity);
  		return new Response<String>().success();  
  	}
  	
  	@ApiOperation(value = "删除User对象", notes = "删除User对象的方法")
  	@RequestMapping(value = "/{ids}", method = RequestMethod.DELETE)
  	public Response<String> delete(@PathVariable("ids") String ids){
  		if(StringUtils.isNotEmpty(ids)) {
  			service.deleteByIds(ids);
  		}
  		return new Response<String>().success();
  	}
  	
  	@SuppressWarnings("unchecked")
  	@ApiOperation(value = "查询User对象", notes = "查询User对象的方式，支持分页、排序、多查询条件")
  	@RequestMapping(value = "/query", method = RequestMethod.POST)
  	public Response<Page<UserDto>> query(@RequestBody Map<String, Object> params){
  		if(params == null || params.size() == 0) throw new ValidationException("参数不可为空");
  		
  		List<PropertyFilter> filters = PropertyFilter.buildFromMap(params);
          Page<UserDto> page = (Page<UserDto>) Page.buildFromMap(params);
  		page = (Page<UserDto>) service.findPage(page, filters, UserDto.class);
  		
  		return new Response<Page<UserDto>>().success(page);
  	}
}
