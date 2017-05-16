package io.springboard.account.dto;

import io.springboard.framework.rest.dto.AbstratorDto;

import javax.persistence.Column;

public class DepartmentDto extends AbstratorDto {

    private String code;
    private String name;
    private String description;
    private Long parentId = null; // parent为null，说明是root节点

	public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    @Column(name = "name", nullable = false, length = 256)
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "description")
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "PARENT_ID")
    public Long getParentId() {
        return parentId;
    }
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
}
