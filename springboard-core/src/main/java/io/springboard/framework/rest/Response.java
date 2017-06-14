package io.springboard.framework.rest;

/**
 * REST返回结果封装类
 * code取值:	0		   - success
 * 			[1~600)	   - http返回码
 * 			[900~999]  - ValidationException
 *          [1000~1999]- ServiceException
 *          [2000~2999]- ScheduleException
 *          [3000~3999]- WorkerException
 * @author fanjun
 *
 */
public class Response<T> {

	private static final String OK = "ok";
	private static final String ERROR = "error";

	private Meta meta;
	private T data;
	
	public Response(){}

	public Response<T> success() {
		this.meta = new Meta(true, 0, OK);
		return this;
	}
	public Response<T> success(T data) {
		this.meta = new Meta(true, 0, OK);
		this.data = data;
		return this;
	}

	public Response<T> failure() {
		this.meta = new Meta(false, 1000, ERROR);
		return this;
	}
	public Response<T> failure(int code, String message) {
		this.meta = new Meta(false, code, message);
		return this;
	}
	public Response<T> failure(int code, String message, T data) {
		this.meta = new Meta(false, code, message);
		this.data = data;
		return this;
	}

	public Meta getMeta() {
		return meta;
	}
	
	public T getData() {
		return data;
	}

	public class Meta {
		private boolean success;
		private int code = 0;
		private String message;
		
		public Meta(){}
		
		public Meta(boolean success, int code, String message) {
			this.success = success;
			this.code = code;
			this.message = message;
		}

		public boolean isSuccess() {
			return success;
		}
		public int getCode(){
			return code;
		}
		public String getMessage() {
			return message;
		}
	}
}
