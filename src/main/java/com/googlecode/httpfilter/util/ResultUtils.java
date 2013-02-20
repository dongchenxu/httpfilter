package com.googlecode.httpfilter.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.googlecode.httpfilter.constant.ErrorCodeConstants;

/**
 * 返回结果处理工具类
 * @author jiangyi.ctd
 *
 */
public class ResultUtils {

	/**
	 * 错误文案信息注解
	 * @author jiangyi.ctd
	 *
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface ErrorMessage {

		/**
		 * 错误对应文案信息
		 * @return
		 */
		String errorMessage() default StringUtils.EMPTY;
		
	}
	
	private static final Map<String, String> errorMsgMap = new HashMap<String, String>();

	static{
		Field[] fields = ErrorCodeConstants.class.getFields();
		if(fields != null){
			for(Field field: fields){
				String errorCode;
				try {
					errorCode = (String) field.get(null);
				} catch (Exception e) {
					//不写错字段定义不会异常
					continue;
				}
				String errorMsg = null;
				//从字段注解上获取错误信息
				if(field.isAnnotationPresent(ErrorMessage.class)){
					errorMsg = field.getAnnotation(ErrorMessage.class).errorMessage();
				}else{
					errorMsg = errorCode;
				}
				errorMsgMap.put(errorCode, errorMsg);
			}
		}
	}

	/**
	 * 根据错误码和参数获取错误文案
	 * @param errorCode
	 * @param args
	 * @return
	 */
	public static String getErrorMsg(String errorCode, Object... args){
		//如果错误码对应文案未定义，则直接返回本身
		if(StringUtils.isEmpty(errorCode) || !errorMsgMap.containsKey(errorCode)){
			return errorCode;
		}
		return String.format(errorMsgMap.get(errorCode), args);
	}
	
}
