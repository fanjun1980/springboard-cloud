package io.springboard.account.client;

import io.springboard.account.dto.PrivilegeDto;
import io.springboard.account.dto.RoleDto;
import io.springboard.account.dto.UserDto;
import io.springboard.framework.orm.Page;
import io.springboard.framework.rest.Response;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

@Component
public class UserClientFallback implements UserClient {

	@Override
	public Response<UserDto> getByUserName(String username) {
		return new Response<UserDto>().failure(1000, "UserClient Fallback");
	}

	@Override
	public Response<List<RoleDto>> getRoles(Long id) {
		return new Response<List<RoleDto>>().failure(1000, "UserClient Fallback", Lists.newArrayList());
	}

	@Override
	public Response<UserDto> getCurrUser() {
		return new Response<UserDto>().failure(1000, "UserClient Fallback");
	}

	@Override
	public Response<String> resetPass(Map<String, Object> params) {
		return new Response<String>().failure(1000, "UserClient Fallback");
	}

	@Override
	public Response<List<PrivilegeDto>> getPrivileges(Long id, Long type) {
		return new Response<List<PrivilegeDto>>().failure(1000, "UserClient Fallback", Lists.newArrayList());
	}

	@Override
	public Response<UserDto> get(Long id) {
		return new Response<UserDto>().failure(1000, "UserClient Fallback");
	}

	@Override
	public Response<String> save(UserDto dto) {
		return new Response<String>().failure(1000, "UserClient Fallback");
	}

	@Override
	public Response<String> delete(String ids) {
		return new Response<String>().failure(1000, "UserClient Fallback");
	}

	@Override
	public Response<Page<UserDto>> query(Map<String, Object> params) {
		return new Response<Page<UserDto>>().failure(1000, "UserClient Fallback");
	}

}
