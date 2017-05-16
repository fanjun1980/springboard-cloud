package io.springboard.account.dto;

import io.springboard.framework.rest.dto.AbstratorDto;

public class PrivilegeDto extends AbstratorDto {

	private String code;
	private String name;
	private String url;
	private Boolean enabled;
	private String ico;
	private Integer type;
	private Long parentId;

	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

	public String getIco() {
		return ico;
	}
	public void setIco(String ico) {
		this.ico = ico;
	}

	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	
	public Boolean getEnabled() {
		return enabled;
	}
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Long getParentId() {
		return parentId;
	}
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
}
