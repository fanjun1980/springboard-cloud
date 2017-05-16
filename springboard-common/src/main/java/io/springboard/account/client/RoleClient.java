package io.springboard.account.client;


import io.springboard.account.dto.PrivilegeDto;
import io.springboard.account.dto.RoleDto;
import io.springboard.framework.orm.Page;
import io.springboard.framework.rest.Response;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("account-service")
@RequestMapping(value = "/api/role")
public interface RoleClient {
	
	@RequestMapping(value = "/get_privileges/{id}", method = RequestMethod.GET)
	public Response<List<PrivilegeDto>> getPrivileges(@PathVariable("id") Long id, @RequestParam(name="type",required=false) Long type);
	
	@RequestMapping(value = "/get_all", method = RequestMethod.GET)
	public Response<List<RoleDto>> getAll();
	
	@RequestMapping(value = "/get_meta_source", method = RequestMethod.GET)
	public Response<Map<String, Collection<String>>> getMetaSource();
	
	//=======================================================================
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Response<RoleDto> get(@PathVariable("id") Long id);
	
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public Response<String> save(@RequestBody RoleDto dto);
	
	@RequestMapping(value = "/{ids}", method = RequestMethod.DELETE)
	public Response<String> delete(@PathVariable("ids") String ids);
	
	@RequestMapping(value = "/query", method = RequestMethod.POST)
	public Response<Page<RoleDto>> query(@RequestBody Map<String, Object> params);
}
