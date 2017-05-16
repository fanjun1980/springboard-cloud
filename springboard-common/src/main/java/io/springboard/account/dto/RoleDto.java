package io.springboard.account.dto;

import io.springboard.framework.rest.dto.AbstratorDto;

public class RoleDto extends AbstratorDto {

    private String name;
    private String description;
    private String chineseName;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getChineseName() {
        return chineseName;
    }
    public void setChineseName(String chineseName) {
        this.chineseName = chineseName;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
