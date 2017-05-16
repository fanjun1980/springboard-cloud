package io.springboard.framework.rest.dto;

import java.util.Date;

/**
 * 业务DTO基类，增加创建（更新）人、创建（更新）时间字段
 * @author fanjun
 *
 */
public class AbstratorDto extends BaseDto{
	private Long createUser;
	private Date createDate;
	private Long updateUser;
	private Date updateDate;
	
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
	public Long getCreateUser() {
		return createUser;
	}
	public void setCreateUser(Long createUser) {
		this.createUser = createUser;
	}
	
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	
	public Long getUpdateUser() {
		return updateUser;
	}
	public void setUpdateUser(Long updateUser) {
		this.updateUser = updateUser;
	}
}
