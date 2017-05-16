package io.springboard.framework.exception;

/**
 * 参数验证的Exception.
 * 
 * 继承自RuntimeException, 从由Spring管理事务的函数中抛出时会触发事务回滚.
 * 
 * @author fanjun
 */
public class ValidationException extends RuntimeException {

	private static final long serialVersionUID = 3583566093089790862L;
	private int code = 1000;
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}

	public ValidationException() {
		super();
	}

	public ValidationException(String message) {
		super(message);
	}
	public ValidationException(int code, String message) {
		super(message);
		this.code = code;
	}

	public ValidationException(Throwable cause) {
		super(cause);
	}

	public ValidationException(String message, Throwable cause) {
		super(message, cause);
	}
	public ValidationException(int code, String message, Throwable cause) {
		super(message, cause);
		this.code = code;
	}
}
