package io.springboard.framework.rest.error;

import io.springboard.framework.exception.ServiceException;
import io.springboard.framework.exception.ValidationException;
import io.springboard.framework.rest.Response;
import io.springboard.framework.utils.spring.SpringUtils;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartException;

@ControllerAdvice  
@ResponseBody  
public class ExceptionAdvice { 
	private Logger logger = LoggerFactory.getLogger(getClass());

    /** 
     * 400 - Bad Request 
     */  
    @ResponseStatus(HttpStatus.BAD_REQUEST)  
    @ExceptionHandler(HttpMessageNotReadableException.class)  
    public Response<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {  
        logger.error("参数解析失败", e);  
        return new Response<String>().failure(HttpStatus.BAD_REQUEST.value(), getResponseMessage(e.getMessage(), "参数解析失败"));  
    }  
    
    /** 
     * 405 - Method Not Allowed 
     */  
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)  
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)  
    public Response<String> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {  
        logger.error("不支持当前请求方法", e);  
        return new Response<String>().failure(HttpStatus.METHOD_NOT_ALLOWED.value(), getResponseMessage(e.getMessage(), "不支持当前请求方法"));  
    }  

    /** 
     * 415 - Unsupported Media Type 
     */  
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)  
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)  
    public Response<String> handleHttpMediaTypeNotSupportedException(Exception e) {  
        logger.error("不支持当前媒体类型", e);  
        return new Response<String>().failure(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), getResponseMessage(e.getMessage(), "不支持当前媒体类型"));  
    }  

    /** 
     *  MultipartException
     */
    @ResponseStatus(value = HttpStatus.PAYLOAD_TOO_LARGE)
    @ExceptionHandler(MultipartException.class)  
    public Response<String> handleMultipartException(Exception e) {  
        logger.error("文件上传错误", e);  
        return new Response<String>().failure(HttpStatus.INTERNAL_SERVER_ERROR.value(), getResponseMessage(e.getMessage(), "上传文件太大"));  
    }  
    
    /** 
     * ValidationException 
     */  
    @ResponseStatus(HttpStatus.OK)  
    @ExceptionHandler(ValidationException.class)  
    public Response<String> handleValidationException(ValidationException e, HttpServletRequest request) {
        logger.error("业务检验异常", e);  
        return new Response<String>().failure(e.getCode(), getResponseMessage(e.getMessage(), e.getMessage()));  
    } 

    /** 
     * ServiceException 
     */  
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ServiceException.class)  
    public Response<String> handleServiceException(ServiceException e, HttpServletRequest request) {
        logger.error("运行时异常", e);  
        return new Response<String>().failure(e.getCode(), getResponseMessage(e.getMessage(), e.getMessage()));  
    } 
    
    /** 
     * 500 - Internal Server Error 
     */  
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)  
    @ExceptionHandler(Throwable.class)  
    public Response<String> handleAnyException(Throwable e, HttpServletRequest request) {
        logger.error("服务运行异常", e);  
        return new Response<String>().failure(HttpStatus.INTERNAL_SERVER_ERROR.value(), getResponseMessage(e.getMessage(), "服务运行异常"));  
    } 
    
    
    private String getResponseMessage(String devMessage, String prodMessage) {
    	String[] activeProfiles = SpringUtils.getApplicationContext().getEnvironment().getActiveProfiles();
		boolean isDev = Arrays.asList(activeProfiles).contains("dev");
		if (isDev) { // 打印调试信息
			return devMessage;
		} else { 	 // 打印简要信息
			return prodMessage;
		}
    }
}  
