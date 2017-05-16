package io.springboard.framework.rest.dto;

import io.springboard.framework.orm.IdEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

public class DtoHelper {
	static Logger logger = LoggerFactory.getLogger(DtoHelper.class);
	
	public static <T extends BaseDto> T convert2Dto(IdEntity source, Class<T> targetClass){
		T target = null;
        try {
	        target = targetClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
	        logger.error("convert2Dto error : " + e);
        }
        copyProperties(source, target);
		return target;
	}
	
	
	public static <T extends IdEntity> T convert2Entity(BaseDto source, Class<T> targetClass){
		T target = null;
        try {
	        target = targetClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
	        logger.error("convert2Entity error : " + e);
        }
        copyProperties(source, target);
		return target;
	}
	
	private static void copyProperties(Object source, Object target) {
		BeanUtils.copyProperties(source, target);
	}
}
