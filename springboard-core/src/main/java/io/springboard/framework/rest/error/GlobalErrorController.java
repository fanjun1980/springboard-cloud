package io.springboard.framework.rest.error;

import io.springboard.framework.rest.Response;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.AbstractErrorController;
import org.springframework.boot.autoconfigure.web.DefaultErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ErrorProperties.IncludeStacktrace;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class GlobalErrorController extends AbstractErrorController {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private final ErrorProperties errorProperties;
	
	public GlobalErrorController(){
		this(new DefaultErrorAttributes());
	}
	public GlobalErrorController(ErrorAttributes errorAttributes) {
		this(errorAttributes, new ErrorProperties());
    }
	public GlobalErrorController(ErrorAttributes errorAttributes,
			ErrorProperties errorProperties) {
		super(errorAttributes);
		Assert.notNull(errorProperties, "ErrorProperties must not be null");
		this.errorProperties = errorProperties;
	}

	private static final String PATH = "/error";

	@Override
    public String getErrorPath() {
	    return PATH;
    }

	@RequestMapping("${server.error.path:${error.path:/error}}")
	@ResponseBody
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public Response<String> error(HttpServletRequest request) {
//		Map<String, Object> body = getErrorAttributes(request,isIncludeStackTrace(request, MediaType.ALL));
		HttpStatus status = getStatus(request);
		logger.error(status.getReasonPhrase());
		return new Response<String>().failure(status.value(),status.getReasonPhrase());
	}
	
	protected boolean isIncludeStackTrace(HttpServletRequest request,
			MediaType produces) {
		IncludeStacktrace include = getErrorProperties().getIncludeStacktrace();
		if (include == IncludeStacktrace.ALWAYS) {
			return true;
		}
		if (include == IncludeStacktrace.ON_TRACE_PARAM) {
			return getTraceParameter(request);
		}
		return false;
	}
	
	protected ErrorProperties getErrorProperties() {
		return this.errorProperties;
	}
}
