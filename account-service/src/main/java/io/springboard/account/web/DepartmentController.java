package io.springboard.account.web;

import io.springboard.account.client.DepartmentClient;
import io.springboard.account.dto.DepartmentDto;
import io.springboard.account.entity.Department;
import io.springboard.account.service.DepartmentService;
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
 * DepartmentDto Controller
 * @author fanjun
 */
@Api(value = "Department")
@RestController
@RequestMapping(value = "/api/department")
public class DepartmentController extends BaseController implements DepartmentClient {
	
	@Autowired
	protected DepartmentService service;
	
    /**
     * 获取所有子部门，包括子部门的子部门
     * 
     * @param id
     * @return
     */
    @ApiOperation(value = "获取所有子部门", notes = "获取所有子部门，包括子部门的子部门")
    @RequestMapping(value = "/get_all_children/{id}", method = RequestMethod.GET)
    public Response<List<DepartmentDto>> getAllChildren(@PathVariable("id") Long id){
        if (id == null)
            throw new ValidationException("id不可为空");
        List<Department> deps = service.getAllChildren(id);
        List<DepartmentDto> dds = deps.stream().map(dep -> convert2Dto(dep, DepartmentDto.class)).collect(Collectors.toList());
        return new Response<List<DepartmentDto>>().success(dds);
    }

    /**
     * 获取直接子部门
     * 
     * @param id
     * @return
     */
    @ApiOperation(value = "获取直接子部门", notes = "获取直接子部门")
    @RequestMapping(value = "/get_children/{id}", method = RequestMethod.GET)
    public Response<List<DepartmentDto>> getChildren(@PathVariable("id") Long id) {
        if (id == null)
            throw new ValidationException("id不可为空");
        List<Department> deps = service.getChildren(id);
        List<DepartmentDto> dds = deps.stream().map(dep -> convert2Dto(dep, DepartmentDto.class)).collect(Collectors.toList());
        return new Response<List<DepartmentDto>>().success(dds);
    }

    /**
     * 获取非子部门（可以作为父部门的部门）
     * 
     * @param id
     * @return
     */
    @ApiOperation(value = "获取非子部门", notes = "获取非子部门（可以作为父部门的部门）")
    @RequestMapping(value = "/get_non_children/{id}", method = RequestMethod.GET)
    public Response<List<DepartmentDto>> getNonChildren(@PathVariable("id") Long id) {
        if (id == null)
            throw new ValidationException("id不可为空");
        List<Department> all = service.getAll();
        if (id > 0) {
            List<Department> children = service.getAllChildren(id);
            all.removeIf(p -> p.getId() == id); // 删除自己
            all.removeIf(p -> children.contains(p));// 删除所有下属部门
        }
        List<DepartmentDto> dds = all.stream().map(dep -> convert2Dto(dep, DepartmentDto.class)).collect(Collectors.toList());
        return new Response<List<DepartmentDto>>().success(dds);
    }
    
    //===============================================================================
  	@ApiOperation(value = "获取Department对象", notes = "获取Department对象的方法")
  	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
  	public Response<DepartmentDto> get(@PathVariable("id") Long id) {
  		Department entity = service.get(id);
  		if(entity == null) {
  			logger.info("id={id}的记录不存在", id);
  			return new Response<DepartmentDto>().failure(404, "无此记录");
  		}
  		return new Response<DepartmentDto>().success(convert2Dto(entity, DepartmentDto.class)); 
  	}
  	
  	@ApiOperation(value = "保存Department对象", notes = "保存Department对象的方法")
  	@RequestMapping(value = "/save", method = RequestMethod.POST)
  	public Response<String> save(@RequestBody DepartmentDto dto) {
  		Department entity = convert2Entity(dto, Department.class);
  		service.save(entity);
  		return new Response<String>().success();  
  	}
  	
  	@ApiOperation(value = "删除Department对象", notes = "删除Department对象的方法")
  	@RequestMapping(value = "/{ids}", method = RequestMethod.DELETE)
  	public Response<String> delete(@PathVariable("ids") String ids){
  		if(StringUtils.isNotEmpty(ids)) {
  			service.deleteByIds(ids);
  		}
  		return new Response<String>().success();
  	}
  	
  	@SuppressWarnings("unchecked")
  	@ApiOperation(value = "查询Department对象", notes = "查询Department对象的方式，支持分页、排序、多查询条件")
  	@RequestMapping(value = "/query", method = RequestMethod.POST)
  	public Response<Page<DepartmentDto>> query(@RequestBody Map<String, Object> params){
  		if(params == null || params.size() == 0) throw new ValidationException("参数不可为空");
  		
  		List<PropertyFilter> filters = PropertyFilter.buildFromMap(params);
          Page<DepartmentDto> page = (Page<DepartmentDto>) Page.buildFromMap(params);
  		page = (Page<DepartmentDto>) service.findPage(page, filters, DepartmentDto.class);
  		
  		return new Response<Page<DepartmentDto>>().success(page);
  	}
}
