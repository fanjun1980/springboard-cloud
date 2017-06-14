package io.springboard.account.client;

import io.springboard.account.dto.PrivilegeDto;
import io.springboard.account.dto.RoleDto;
import io.springboard.framework.orm.Page;
import io.springboard.framework.rest.Response;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Component
public class RoleClientFallback implements RoleClient {

	@Override
	public Response<List<PrivilegeDto>> getPrivileges(Long id, Long type) {
		return new Response<List<PrivilegeDto>>().failure(1000, "RoleClient Fallback", Lists.newArrayList());
	}

	@Override
	public Response<List<RoleDto>> getAll() {
		return new Response<List<RoleDto>>().failure(1000, "RoleClient Fallback", Lists.newArrayList());
	}

	@Override
	public Response<Map<String, Collection<String>>> getMetaSource() {
		return new Response<Map<String, Collection<String>>>().failure(1000, "RoleClient Fallback", Maps.newHashMap());
	}

	@Override
	public Response<RoleDto> get(Long id) {
		return new Response<RoleDto>().failure(1000, "RoleClient Fallback");
	}

	@Override
	public Response<String> save(RoleDto dto) {
		return new Response<String>().failure(1000, "RoleClient Fallback");
	}

	@Override
	public Response<String> delete(String ids) {
		return new Response<String>().failure(1000, "RoleClient Fallback");
	}

	@Override
	public Response<Page<RoleDto>> query(Map<String, Object> params) {
		return new Response<Page<RoleDto>>().failure(1000, "RoleClient Fallback");
	}

}
