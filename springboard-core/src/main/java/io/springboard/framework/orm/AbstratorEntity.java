package io.springboard.framework.orm;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * 业务对象基类，增加创建（更新）人、创建（更新）时间字段
 * @author fanjun
 *
 */
@MappedSuperclass
public class AbstratorEntity extends IdEntity {
	private Long createUser;
	private Date createDate;
	private Long updateUser;
	private Date updateDate;
	
	@Column(name = "CREATEDATE")
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
	@Column(name = "CREATEUSER")
	public Long getCreateUser() {
		return createUser;
	}
	public void setCreateUser(Long createUser) {
		this.createUser = createUser;
	}
	
	@Column(name = "UPDATEDATE")
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	
	@Column(name = "UPDATEUSER")
	public Long getUpdateUser() {
		return updateUser;
	}
	public void setUpdateUser(Long updateUser) {
		this.updateUser = updateUser;
	}
}
