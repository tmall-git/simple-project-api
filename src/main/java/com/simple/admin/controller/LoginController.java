package com.simple.admin.controller;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.simple.admin.util.AjaxWebUtil;
import com.simple.admin.util.LoginUserUtil;
import com.simple.admin.util.MD5Util;
import com.simple.model.User;
import com.simple.service.UserService;

@Controller
@RequestMapping("")
public class LoginController {

	@Autowired
	private UserService userService;
	
	@RequestMapping(value = "doLogin",method=RequestMethod.POST)
	@ResponseBody
	public String doLogin(HttpServletRequest request, HttpServletResponse response){
		String password = AjaxWebUtil.getRequestParameter(request,"password");
		String userPhone = AjaxWebUtil.getRequestParameter(request,"userPhone");
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userPhone", userPhone);
		User user = null;
		try {
			user = userService.selectOne("user.selectOne", params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(user == null){
			return AjaxWebUtil.sendAjaxResponse(request, response, false,"用户不存在，请注册", null);
		}
		String dbPwd = user.getPassword(); 
		if(!dbPwd.equals(getMD5Password(password, user))){
			return AjaxWebUtil.sendAjaxResponse(request, response, false,"密码错误", null);
		}
		LoginUserUtil.setCurrentUser(request, user);
		return AjaxWebUtil.sendAjaxResponse(request, response, true,"登陆成功", null);
	}

	private String getMD5Password(String password, User user) {
		return MD5Util.MD5Encode(password, user.getSalt());
	}
	
	@RequestMapping(value = "logout",method=RequestMethod.GET)
	@ResponseBody
	public String logout(HttpServletRequest request, HttpServletResponse response){
		LoginUserUtil.removeCurrentUser(request);
		return AjaxWebUtil.sendAjaxResponse(request, response, true,"登出成功", null);
	}
	
	@RequestMapping(value = "loginpage",method=RequestMethod.GET)
	public String loginpage(HttpServletRequest request, HttpServletResponse response) {
		request.setAttribute("rootpath", request.getRequestURI().replace(request.getContextPath(), ""));
		return "login";
	}
	
	public static void main(String[] args) {
		LoginController lc = new LoginController();
		User user = new User();
		user.setSalt("b65d024c-5d35-4207-b1ec-64ccb2abd32f");
		String md5 = lc.getMD5Password("123456", user);
		System.out.println(md5);
	}
}
