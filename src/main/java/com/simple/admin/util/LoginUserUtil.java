package com.simple.admin.util;

import javax.servlet.http.HttpServletRequest;

import com.simple.constant.Constant;
import com.simple.model.User;

public class LoginUserUtil {

	public static User getCurrentUser(HttpServletRequest request) {
		return (User) request.getSession().getAttribute(Constant.CURRENT_USER);
//		User user = new User();
//		user.setUserPhone("18600671341");
//		user.setWeChatNo("adfasdfasfd");
//		user.setCategory("鞋、袜子");
//		return user;
	}
	public static void setCurrentUser(HttpServletRequest request,User user) {
		request.getSession().setAttribute(Constant.CURRENT_USER,user);
	}
	public static void removeCurrentUser(HttpServletRequest request) {
		request.getSession().removeAttribute(Constant.CURRENT_USER);
	}
//	public static String getLeaseholderId(HttpServletRequest request) {
//		SysUser user = getCurrentUser(request);
//		//TODO 通过user获取租户ID
//		return "t10001";
//	}
}
