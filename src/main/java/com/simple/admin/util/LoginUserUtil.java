package com.simple.admin.util;

import javax.servlet.http.HttpServletRequest;

import com.simple.constant.Constant;
import com.simple.model.User;

public class LoginUserUtil {

	public static User getCurrentUser(HttpServletRequest request) {
		return (User) request.getSession().getAttribute(Constant.CURRENT_USER);
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
