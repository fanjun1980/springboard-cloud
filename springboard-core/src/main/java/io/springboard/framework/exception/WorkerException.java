package io.springboard.framework.exception;

/**
 * Worker公用的Exception.
 * 
 * 继承自RuntimeException, 从由Spring管理事务的函数中抛出时会触发事务回滚.
 * 
 * @author fanjun
 */
public class WorkerException extends RuntimeException {

	private static final long serialVersionUID = 3583566093089790852L;
	private int code = 4000;
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	
	public WorkerException() {
		super();
	}

	public WorkerException(String message) {
		super(message);
	}
	public WorkerException(int code, String message) {
		super(message);
		this.code = code;
	}

	public WorkerException(Throwable cause) {
		super(cause);
	}

	public WorkerException(String message, Throwable cause) {
		super(message, cause);
	}
	public WorkerException(int code, String message, Throwable cause) {
		super(message, cause);
		this.code = code;
	}
}
