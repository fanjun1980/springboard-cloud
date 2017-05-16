package io.springboard.framework.dict.entity;

import io.springboard.framework.orm.IdEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;





import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 数据字典实体
 */
@Entity
@Table(name = "tb_dictionary")
public class Dict extends IdEntity {
    private String keycode; // 键
    private String value; // 值
    private Boolean enable; // 是否可用
    private String remark; // 说明
    private Integer sortNo; // 排序号码
    private String type; // 类型

    @Column(name = "KEYCODE", nullable = false)
    public String getKeycode() {
        return keycode;
    }

    public void setKeycode(String keycode) {
        this.keycode = keycode;
    }

    @Column(name = "VALUE")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Column(name = "ENABLE", nullable = false)
    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    @Column(name = "REMARK")
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Column(name = "SORTNO")
    public Integer getSortNo() {
        return sortNo;
    }

    public void setSortNo(Integer sortNo) {
        this.sortNo = sortNo;
    }

    @Column(name = "TYPE", nullable = false)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}
}
