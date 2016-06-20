package com.simple.admin.intercepter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class LenovoInterceptor implements HandlerInterceptor{

	private static final Log LOGGER = LogFactory.getLog(LenovoInterceptor.class);

	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object object, Exception exception)
			throws Exception {
		
	}

	public void postHandle(HttpServletRequest request, HttpServletResponse response,
			Object object, ModelAndView handle) throws Exception {
	}

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object handler) throws Exception {
		
//		HandlerMethod handlerMethod = (HandlerMethod) handler; 
//		boolean notCareLogin = handlerMethod.getMethod().isAnnotationPresent(NotCareLogin.class);
//		if (notCareLogin) {
//			return true;
//		}
		
		return true;
	}
	
	public String getRemortIP(HttpServletRequest request) {
		if (request.getHeader("x-forwarded-for") == null) {
			return request.getRemoteAddr();
		}
		return request.getHeader("x-forwarded-for");
	}
}
