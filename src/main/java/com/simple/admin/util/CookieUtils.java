package com.simple.admin.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieUtils {

	/**
	 * 设置 cookie
	 * @param response
	 * @param name    cookie 名字
	 * @param value   cookie 值
	 * @param maxAge  cookie 生命周期  以秒为单位
	 */
	public static void addCookie(HttpServletResponse response, String name, String value, int maxAge){
		Cookie cookie = new Cookie(name,value);
		cookie.setPath("/");
		if(maxAge > 0)
		{
			cookie.setMaxAge(maxAge);
		}
		response.addCookie(cookie);
	}
	
	/**
	 * 根据名字获取 Cookie
	 * @param request
	 * @param name cookie名字
	 * @return
	 */
	public static Cookie getCookieByName(HttpServletRequest request, String name){
		Map<String, Cookie> cookieMap = readCookieMap(request);
		if(cookieMap.containsKey(name)){
			Cookie cookie = cookieMap.get(name);
			return cookie;
		}else{
			return null;
		}
	}
	
	/**
	 * 将cookie封装到Map里面
	 * @param request
	 * @return
	 */
	private static Map<String, Cookie> readCookieMap(HttpServletRequest request){
		Map<String, Cookie> cookieMap = new HashMap<String, Cookie>();
		Cookie[] cookies = request.getCookies();
		if(null != cookies){
			for(Cookie cookie : cookies){
				cookieMap.put(cookie.getName(), cookie);
			}
		}
		return cookieMap;
	}

}
