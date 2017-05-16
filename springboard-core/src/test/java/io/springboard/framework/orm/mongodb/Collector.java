package io.springboard.framework.orm.mongodb;

import io.springboard.framework.orm.mongodb.AbstractMongoEntity;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="collector")
public class Collector extends AbstractMongoEntity {
	private String name;	// ip+name标识一个collector
	private String ip;
	private int port;
	private int status;		// 在线状态，0-离线；1-在线
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
	@Override
    public String toString() {
	    return "Collector [name=" + name + ", ip=" + ip + ", port=" + port + ", status=" + status + "]";
    }
	
}
