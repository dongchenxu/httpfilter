package com.googlecode.httpfilter.domain;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.googlecode.httpfilter.util.ResultUtils;

/**
 * 返回结果基类
 * @author vlinux
 *
 */
public class BaseResultDO extends BaseDO {

	private static final long serialVersionUID = 1978227428619439353L;

	private boolean success = true; // 默认成功

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	/**
	 * 错误信息
	 * @author luanjia
	 *
	 */
	public static class ErrMsg extends HashMap<String/*errorcode*/,String/*errormessage*/>{

		private static final long serialVersionUID = -8627688749426802046L;
		
		/**
		 * 放置错误信息
		 * @param errorCode
		 * @param errorMessage
		 */
		public void putError(String errorCode, Object... args) {
			this.put(errorCode, ResultUtils.getErrorMsg(errorCode, args));
		}
		
		@SuppressWarnings("rawtypes")
		public String getErrorCode(){
			Iterator it = this.entrySet().iterator();
			String str = "";
			if( it.hasNext() ){
				Map.Entry entry = (java.util.Map.Entry) it.next();
				str += (String) entry.getValue();
			}
			return str;
		}
		
	}
	
}
