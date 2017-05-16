package io.springboard.framework.utils.reflection;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Maps;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class ConvertUtils {

	static {
		registerDateConverter();
	}

	/**
	 * 提取集合中的对象的属性(通过getter函数), 组合成List.
	 * 
	 * @param collection 来源集合.
	 * @param propertyName 要提取的属性名.
	 */
	public static List convertElementPropertyToList(final Collection collection, final String propertyName) {
		List list = new ArrayList();

		try {
			for (Object obj : collection) {
				list.add(PropertyUtils.getProperty(obj, propertyName));
			}
		} catch (Exception e) {
			throw ReflectionUtils.convertReflectionExceptionToUnchecked(e);
		}

		return list;
	}

	/**
	 * 提取集合中的对象的属性(通过getter函数), 组合成由分割符分隔的字符串.
	 * 
	 * @param collection 来源集合.
	 * @param propertyName 要提取的属性名.
	 * @param separator 分隔符.
	 */
	public static String convertElementPropertyToString(final Collection collection, final String propertyName,
			final String separator) {
		List list = convertElementPropertyToList(collection, propertyName);
		return StringUtils.join(list, separator);
	}

	/**
	 * 转换字符串到相应类型.
	 * 
	 * @param value 待转换的字符串.
	 * @param toType 转换目标类型.
	 */
	public static Object convertStringToObject(String value, Class<?> toType) {
		try {
			return org.apache.commons.beanutils.ConvertUtils.convert(value, toType);
		} catch (Exception e) {
			throw ReflectionUtils.convertReflectionExceptionToUnchecked(e);
		}
	}
	
	/**
	 * 转换字符串到相应类型的数组.
	 * 
	 * @param value 待转换的字符串.
	 * @param toType 转换目标类型.
	 */
	public static Object convertStringToObjectArray(String value, Class<?> toType) {
		try {
			String[] values = value.split(",");
			return org.apache.commons.beanutils.ConvertUtils.convert(values, toType);
		} catch (Exception e) {
			throw ReflectionUtils.convertReflectionExceptionToUnchecked(e);
		}
	}
	
	/**
	 * 转换java Bean为Map（通过getter函数赋值）
	 * @param bean
	 * @return
	 */
	public static Map<String, Object> convertBeanToMap(Object bean) {
		Map<String, Object> result = Maps.newHashMap();
		
		Field[] fields = ReflectionUtils.getAllField(bean.getClass());
		for(Field field : fields){
			try {
	            Object value = ReflectionUtils.invokeGetterMethod(bean, field.getName());
	            result.put(field.getName(), value);
            } catch (Exception e) {}
		}
		return result;
	}
	
	/**
	 * 转换Map为java Bean（绕过setter函数直接赋值给字段）
	 * @param map
	 * @param classType
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static <T> T convertMapToBean(Map<String, Object> map, Class<T> classType) throws InstantiationException, IllegalAccessException{
		T bean = classType.newInstance();
		
		if(map != null && map.size() > 0) {
			for(String key : map.keySet()){
				try {
	                Object value = map.get(key);
	                ReflectionUtils.setFieldValue(bean, key, value);
                } catch (Exception e) {}
			}
		}
		return bean;
	}
	
	/**
	 * 转换Map为java Bean（绕过setter函数直接赋值给字段）
	 * @param map
	 * @param bean
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static void convertMapToBean(Map<String, Object> map, Object bean) throws InstantiationException, IllegalAccessException{		
		if(map != null && map.size() > 0) {
			for(String key : map.keySet()){
				try {
	                Object value = map.get(key);
	                ReflectionUtils.setFieldValue(bean, key, value);
                } catch (Exception e) {}
			}
		}
	}

	/**
	 * 定义日期Converter的格式: yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss
	 */
	private static void registerDateConverter() {
		DateConverter dc = new DateConverter();
		dc.setUseLocaleFormat(true);
		dc.setPatterns(new String[] { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss" });
		org.apache.commons.beanutils.ConvertUtils.register(dc, Date.class);
	}
}
