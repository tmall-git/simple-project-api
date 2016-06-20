package com.simple.admin.util;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;

public class JSONUtil {

	public static String toJSONString(String callback,Object object){
		if(StringUtils.isNotEmpty(callback)){
			return callback + "(" + JSONObject.toJSONString(object) + ")";
		}else{
			return JSONObject.toJSONString(object);
		}
	}
	
}
