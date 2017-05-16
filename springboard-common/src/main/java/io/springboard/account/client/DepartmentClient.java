package io.springboard.account.client;

import io.springboard.account.dto.DepartmentDto;
import io.springboard.framework.orm.Page;
import io.springboard.framework.rest.Response;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("account-service")
@RequestMapping(value = "/api/Department")
public interface DepartmentClient{
	
    @RequestMapping(value = "/get_all_children/{id}", method = RequestMethod.GET)
    public Response<List<DepartmentDto>> getAllChildren(@PathVariable("id") Long id);

    @RequestMapping(value = "/get_children/{id}", method = RequestMethod.GET)
    public Response<List<DepartmentDto>> getChildren(@PathVariable("id") Long id);

    @RequestMapping(value = "/get_non_children/{id}", method = RequestMethod.GET)
    public Response<List<DepartmentDto>> getNonChildren(@PathVariable("id") Long id);
    
	//=======================================================================
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Response<DepartmentDto> get(@PathVariable("id") Long id);
	
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public Response<String> save(@RequestBody DepartmentDto dto);
	
	@RequestMapping(value = "/{ids}", method = RequestMethod.DELETE)
	public Response<String> delete(@PathVariable("ids") String ids);
	
	@RequestMapping(value = "/query", method = RequestMethod.POST)
	public Response<Page<DepartmentDto>> query(@RequestBody Map<String, Object> params);
}
