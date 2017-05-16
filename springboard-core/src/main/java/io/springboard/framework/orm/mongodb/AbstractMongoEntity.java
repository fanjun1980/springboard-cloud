package io.springboard.framework.orm.mongodb;

import org.springframework.data.annotation.Id;

public abstract class AbstractMongoEntity {
	@Id
	protected String id;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
}
