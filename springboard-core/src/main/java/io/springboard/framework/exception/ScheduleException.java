package io.springboard.framework.exception;

/**
 * Schedule公用的Exception.
 * 
 * 继承自RuntimeException, 从由Spring管理事务的函数中抛出时会触发事务回滚.
 * 
 * @author fanjun
 */
public class ScheduleException extends RuntimeException {

	private static final long serialVersionUID = 3583566093089790852L;
	private int code = 3000;
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}

	public ScheduleException() {
		super();
	}

	public ScheduleException(String message) {
		super(message);
	}
	public ScheduleException(int code, String message) {
		super(message);
		this.code = code;
	}

	public ScheduleException(Throwable cause) {
		super(cause);
	}

	public ScheduleException(String message, Throwable cause) {
		super(message, cause);
	}
	public ScheduleException(int code, String message, Throwable cause) {
		super(message, cause);
		this.code = code;
	}
}
