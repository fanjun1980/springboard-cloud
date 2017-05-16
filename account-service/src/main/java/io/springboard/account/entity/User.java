package io.springboard.account.entity;

import io.springboard.framework.orm.AbstratorEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "tb_user")
public class User extends AbstratorEntity {
	
	protected String username;
	protected String password;
	protected String fullName;
	protected String description;

	//拓展字段
	protected String email;
	protected String mobile;
	protected Boolean enabled = Boolean.TRUE;
	protected Long depId;
	
	@Column(name ="dep_id")
	public Long getDepId() {
		return this.depId;
	}
	public void setDepId(Long depId) {
		this.depId = depId;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	@Column(name = "PASSWORD")
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	@Column(name = "FULLNAME")
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Boolean getEnabled() {
		return enabled;
	}
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
}
