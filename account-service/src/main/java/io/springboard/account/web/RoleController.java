package io.springboard.account.web;

import io.springboard.account.client.RoleClient;
import io.springboard.account.dto.PrivilegeDto;
import io.springboard.account.dto.RoleDto;
import io.springboard.account.entity.Privilege;
import io.springboard.account.entity.Role;
import io.springboard.account.service.RoleService;
import io.springboard.framework.exception.ValidationException;
import io.springboard.framework.orm.Page;
import io.springboard.framework.orm.PropertyFilter;
import io.springboard.framework.rest.BaseController;
import io.springboard.framework.rest.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * RoleDto Controller
 * @author fanjun
 */
@Api(value = "Role")
@RestController
@RequestMapping(value = "/api/role")
public class RoleController extends BaseController implements RoleClient {
	
	@Autowired
	protected RoleService service;
	
	@ApiOperation(value = "获取角色对应的权限", notes = "获取角色对应的权限，可选择权限类型")
	@RequestMapping(value = "/get_privileges/{id}", method = RequestMethod.GET)
	public Response<List<PrivilegeDto>> getPrivileges(@PathVariable("id") Long id, @RequestParam(name="type",required=false) Long type) {
		if(id == null ) throw new ValidationException("id不可为空");
		List<Privilege> pris = service.getPrivileges(id, type);
		List<PrivilegeDto> pds = pris.stream().map(pri -> convert2Dto(pri, PrivilegeDto.class)).collect(Collectors.toList());
		return new Response<List<PrivilegeDto>>().success(pds);
	}

	@ApiOperation(value = "获取所有角色", notes = "获取所有角色对象")
	@RequestMapping(value = "/get_all", method = RequestMethod.GET)
	public Response<List<RoleDto>> getAll() {
		List<Role> rs = service.getAll();
		List<RoleDto> rds = rs.stream().map(role -> convert2Dto(role, RoleDto.class)).collect(Collectors.toList());
		return new Response<List<RoleDto>>().success(rds);
	}
	
	@ApiOperation(value = "获取url-角色关系", notes = "获取权限url与角色的对照关系，用于spring security中的FilterInvocationSecurityMetadataSource")
	@RequestMapping(value = "/get_meta_source", method = RequestMethod.GET)
	public Response<Map<String, Collection<String>>> getMetaSource() {
		Map<String, Collection<String>> resourceMap = service.getMetaSource();
		return new Response<Map<String, Collection<String>>>().success(resourceMap);
	}

	//===============================================================================
	@ApiOperation(value = "获取Role对象", notes = "获取Role对象的方法")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Response<RoleDto> get(@PathVariable("id") Long id) {
		Role entity = service.get(id);
		if(entity == null) {
			logger.info("id={id}的记录不存在", id);
			return new Response<RoleDto>().failure(404, "无此记录");
		}
		return new Response<RoleDto>().success(convert2Dto(entity, RoleDto.class)); 
	}
	
	@ApiOperation(value = "保存Role对象", notes = "保存Role对象的方法")
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public Response<String> save(@RequestBody RoleDto dto) {
		Role entity = convert2Entity(dto, Role.class);
		service.save(entity);
		return new Response<String>().success();  
	}
	
	@ApiOperation(value = "删除Role对象", notes = "删除Role对象的方法")
	@RequestMapping(value = "/{ids}", method = RequestMethod.DELETE)
	public Response<String> delete(@PathVariable("ids") String ids){
		if(StringUtils.isNotEmpty(ids)) {
			service.deleteByIds(ids);
		}
		return new Response<String>().success();
	}
	
	@SuppressWarnings("unchecked")
	@ApiOperation(value = "查询Role对象", notes = "查询Role对象的方式，支持分页、排序、多查询条件")
	@RequestMapping(value = "/query", method = RequestMethod.POST)
	public Response<Page<RoleDto>> query(@RequestBody Map<String, Object> params){
		if(params == null || params.size() == 0) throw new ValidationException("参数不可为空");
		
		List<PropertyFilter> filters = PropertyFilter.buildFromMap(params);
        Page<RoleDto> page = (Page<RoleDto>) Page.buildFromMap(params);
		page = (Page<RoleDto>) service.findPage(page, filters, RoleDto.class);
		
		return new Response<Page<RoleDto>>().success(page);
	}
	
}
