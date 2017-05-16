package io.springboard.account.web;

import io.springboard.account.client.PrivilegeClient;
import io.springboard.account.dto.PrivilegeDto;
import io.springboard.account.entity.Privilege;
import io.springboard.account.service.PrivilegeService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Privilege Controller
 * @author fanjun
 */
@Api(value = "Privilege")
@RestController
@RequestMapping(value = "/api/privilege")
public class PrivilegeController extends BaseController implements PrivilegeClient{
	
	@Autowired
	protected PrivilegeService service;
	
	/**
	 * 获取所有子权限，包括子权限的子权限
	 * @param id
	 * @return
	 */
	@ApiOperation(value = "获取所有子权限", notes = "获取所有子权限，包括子权限的子权限")
	@RequestMapping(value = "/get_all_children/{id}", method = RequestMethod.GET)
	public Response<List<PrivilegeDto>> getAllChildren(@PathVariable("id") Long id) {
		if(id == null) throw new ValidationException("id不可为空");
		List<Privilege> pris = service.getAllChildren(id);
		List<PrivilegeDto> pds = pris.stream().map(pri -> convert2Dto(pri, PrivilegeDto.class)).collect(Collectors.toList());
		return new Response<List<PrivilegeDto>>().success(pds); 
	}

	/**
	 * 获取直接子权限
	 * @param id
	 * @return
	 */
	@ApiOperation(value = "获取直接子权限", notes = "获取直接子权限")
	@RequestMapping(value = "/get_children/{id}", method = RequestMethod.GET)
	public Response<List<PrivilegeDto>> getChildren(@PathVariable("id") Long id) {
		if(id == null) throw new ValidationException("id不可为空");
		List<Privilege> pris = service.getChildren(id);
		List<PrivilegeDto> pds = pris.stream().map(pri -> convert2Dto(pri, PrivilegeDto.class)).collect(Collectors.toList());
		return new Response<List<PrivilegeDto>>().success(pds); 
	}
	
	/**
	 * 获取非子权限（可以作为父权限的权限）
	 * @param id
	 * @return
	 */
	@ApiOperation(value = "获取非子权限（可以作为父权限的权限）", notes = "获取非子权限（可以作为父权限的权限）")
	@RequestMapping(value = "/get_non_children/{id}", method = RequestMethod.GET)
	public Response<List<PrivilegeDto>> getNonChildren(@PathVariable("id") Long id) {
		if(id == null) throw new ValidationException("id不可为空");
		List<Privilege> all = service.getAll();
		if (id > 0) {
			List<Privilege> children = service.getAllChildren(id);
			all.removeIf(p -> p.getId() == id);		// 删除自己
			all.removeIf(p -> children.contains(p));// 删除所有下级权限	
		}
		List<PrivilegeDto> pds = all.stream().map(pri -> convert2Dto(pri, PrivilegeDto.class)).collect(Collectors.toList());
		return new Response<List<PrivilegeDto>>().success(pds); 
	}
	
    //===============================================================================
  	@ApiOperation(value = "获取Privilege对象", notes = "获取Privilege对象的方法")
  	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
  	public Response<PrivilegeDto> get(@PathVariable("id") Long id) {
  		Privilege entity = service.get(id);
  		if(entity == null) {
  			logger.info("id={id}的记录不存在", id);
  			return new Response<PrivilegeDto>().failure(404, "无此记录");
  		}
  		return new Response<PrivilegeDto>().success(convert2Dto(entity, PrivilegeDto.class)); 
  	}
  	
  	@ApiOperation(value = "保存Privilege对象", notes = "保存Privilege对象的方法")
  	@RequestMapping(value = "/save", method = RequestMethod.POST)
  	public Response<String> save(@RequestBody PrivilegeDto dto) {
  		Privilege entity = convert2Entity(dto, Privilege.class);
  		service.save(entity);
  		return new Response<String>().success();  
  	}
  	
  	@ApiOperation(value = "删除Privilege对象", notes = "删除Privilege对象的方法")
  	@RequestMapping(value = "/{ids}", method = RequestMethod.DELETE)
  	public Response<String> delete(@PathVariable("ids") String ids){
  		if(StringUtils.isNotEmpty(ids)) {
  			service.deleteByIds(ids);
  		}
  		return new Response<String>().success();
  	}
  	
  	@SuppressWarnings("unchecked")
  	@ApiOperation(value = "查询Privilege对象", notes = "查询Privilege对象的方式，支持分页、排序、多查询条件")
  	@RequestMapping(value = "/query", method = RequestMethod.POST)
  	public Response<Page<PrivilegeDto>> query(@RequestBody Map<String, Object> params){
  		if(params == null || params.size() == 0) throw new ValidationException("参数不可为空");
  		
  		List<PropertyFilter> filters = PropertyFilter.buildFromMap(params);
          Page<PrivilegeDto> page = (Page<PrivilegeDto>) Page.buildFromMap(params);
  		page = (Page<PrivilegeDto>) service.findPage(page, filters, PrivilegeDto.class);
  		
  		return new Response<Page<PrivilegeDto>>().success(page);
  	}
}
