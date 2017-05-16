package io.springboard.framework.rest;

import io.springboard.framework.orm.IdEntity;
import io.springboard.framework.rest.dto.BaseDto;
import io.springboard.framework.rest.dto.DtoHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Spring MVC Controller 基类, 提供辅助方法
 * @author fanjun
 */
public abstract class BaseController {
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
//	@Autowired
//	protected Service service;
//	
//	@ApiOperation(value = "获取对象", notes = "获取对象的公用方法")
//	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
//	public Response<Entity> get(@PathVariable("id") PK id) {
//		Entity entity = service.get(id);
//		if(entity == null) {
//			logger.info("id={id}的记录不存在", id);
//			return new Response<Entity>().failure(404, "无此记录");
//		}
//		return new Response<Entity>().success(entity); 
//	}
//	
//	@ApiOperation(value = "保存对象", notes = "保存对象的公用方式")
//	@RequestMapping(value = "/save", method = RequestMethod.POST)
//	public Response<String> save(@RequestBody Entity entity) {
//		service.save(entity);
//		return new Response<String>().success();  
//	}
//	
//	@ApiOperation(value = "删除对象", notes = "删除对象的公用方法")
//	@RequestMapping(value = "/{ids}", method = RequestMethod.DELETE)
//	public Response<String> delete(@PathVariable("ids") String ids){
//		if(StringUtils.isNotEmpty(ids)) {
//			service.deleteByIds(ids);
//		}
//		return new Response<String>().success();
//	}
//	
//	@SuppressWarnings("unchecked")
//	@ApiOperation(value = "查询对象", notes = "查询对象的公用方式，支持分页、排序、多查询条件")
//	@RequestMapping(value = "/query", method = RequestMethod.POST)
//	public Response<Page<Entity>> query(@RequestBody Map<String, Object> params){
//		if(params == null || params.size() == 0) throw new ValidationException("参数不可为空");
//		
//		List<PropertyFilter> filters = PropertyFilter.buildFromMap(params);
//        Page<Entity> page = (Page<Entity>) Page.buildFromMap(params);
//		page = service.findPage(page, filters);
//		
//		return new Response<Page<Entity>>().success(page);
//	}

	protected <T extends BaseDto> T convert2Dto(IdEntity source, Class<T> targetClass){
		return DtoHelper.convert2Dto(source, targetClass);
	}
	
	protected <T extends IdEntity> T convert2Entity(BaseDto source, Class<T> targetClass){
		return DtoHelper.convert2Entity(source, targetClass);
	}

}
