package com.simple.admin.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.simple.admin.util.AjaxWebUtil;
import com.simple.admin.util.MD5Util;
import com.simple.constant.Constant;
import com.simple.model.User;
import com.simple.service.UserService;

@Controller
@RequestMapping(value = "/user")
public class UserController {
	
	private static final Logger log = LoggerFactory.getLogger(UserController.class);

	private Map<String, String> cacheValidatorCode = new HashMap<String, String>();
	
	@Autowired
	UserService userService;
	
	@RequestMapping(value = "register",method=RequestMethod.POST)
	@ResponseBody
	public String register(HttpServletRequest request, HttpServletResponse response) {
		try {
			String password = AjaxWebUtil.getRequestParameter(request,"password");
			String validateCode = AjaxWebUtil.getRequestParameter(request,"validateCode");
			String userPhone = AjaxWebUtil.getRequestParameter(request,"userPhone");
			String wechatNo = AjaxWebUtil.getRequestParameter(request,"wechatNo");
			if(password == null){
				return AjaxWebUtil.sendAjaxResponse(request, response, false,"密码不能为空", null);
			}
			if(!validateCode.equals(cacheValidatorCode.get(userPhone))){
				return AjaxWebUtil.sendAjaxResponse(request, response, false,"验证码错误", null);
			}
			String salt = UUID.randomUUID().toString();
			String mPassword = MD5Util.MD5Encode(password, salt);
			User u = new User();
			u.setSalt(salt);
			u.setUserPhone(userPhone);
			u.setWeChatNo(wechatNo);
			u.setPassword(mPassword);
			u.setLoginName(userPhone);
			userService.insert(u);
			request.getSession().setAttribute(Constant.CURRENT_USER,u);
			return AjaxWebUtil.sendAjaxResponse(request, response, true,"注册成功", null);
		}catch(Exception e) {
			log.error("注册失败",e);
			e.printStackTrace();
			return AjaxWebUtil.sendAjaxResponse(request, response, false,"注册失败", null);
		}
	}
	
	@RequestMapping(value="validateCode",method=RequestMethod.GET)
	@ResponseBody
	public String getValidateCode(String phone,HttpServletRequest request, HttpServletResponse response){
		try {
			String validatorCode = getValidateCode();
			cacheValidatorCode.put(phone,validatorCode );
			return  AjaxWebUtil.sendAjaxResponse(request, response, true,"获取验证码成功", validatorCode);
		}
		catch (Exception e){
			return  AjaxWebUtil.sendAjaxResponse(request, response, false,"获取验证码失败", null);
		}
	}
	
//	@RequestMapping(value = "modifyPwd",method=RequestMethod.POST)
//	@ResponseBody
//	@SuppressWarnings("unchecked")
//	public String modifyPwd(HttpServletRequest request, HttpServletResponse response) {
//		try {
//			String data = request.getParameter("data");
//			Map<String, Object> params = (Map<String, Object>) JSON.parse(data);
//			User user = userService.getById("user.selectOne", Integer.parseInt(params.get("id").toString()));
//			if(params.get("oldPwd") == null){
//				return JSON.toJSONString(new ResultData(false,"旧密码不能为空"));
//			}
//			if(params.get("newPwd") == null){
//				return JSON.toJSONString(new ResultData(false,"新密码不能为空"));
//			}
//			String oldPwd = params.get("oldPwd").toString();
//			String newPwd = params.get("newPwd").toString();
//			if(!oldPwd.equals(newPwd)){
//				return JSON.toJSONString(new ResultData(false,"新密码与旧密码不匹配"));
//			}
//			String salt = UUID.randomUUID().toString();
//			String mPassword = MD5Util.MD5Encode(newPwd, salt);
//			user.setPassword(mPassword);
//			user.setSalt(salt);
//			Map<String, Object> beanMap = BeanUtils.describe(user);
//			beanMap.putAll(params);
//			int id = userService.update("user.modify",beanMap);
//			if(id != 1){
//				return JSON.toJSONString(new ResultData(false,"更新失败"));
//			}
//			BeanUtils.copyProperties(user, beanMap);
//			request.getSession().setAttribute(Constant.CURRENT_USER,user);
//			return JSON.toJSONString(new ResultData(true, "更新成功"));
//		}catch(Exception e) {
//			log.error(e.getMessage(),e);
//			return JSON.toJSONString(new ResultData(true, e.getMessage()));
//		}
//	}
//	
//	@RequestMapping(value = "modifyUser",method=RequestMethod.POST)
//	@ResponseBody
//	@SuppressWarnings("unchecked")
//	public String modifyUser(HttpServletRequest request, HttpServletResponse response) {
//		try {
//			String data = request.getParameter("data");
//			Map<String, Object> params = (Map<String, Object>) JSON.parse(data);
//			User user = userService.getById("user.selectOne", Integer.parseInt(params.get("id").toString()));
//			Map<String, Object> beanMap = BeanUtils.describe(user);
//			beanMap.putAll(params);
//			int id = userService.update("user.modify",beanMap);
//			if(id != 1){
//				throw new Exception("更新失败");
//			}
//			BeanUtils.copyProperties(user, beanMap);
//			request.getSession().setAttribute(Constant.CURRENT_USER,user);
//			return JSON.toJSONString(new ResultData(true, "更新成功"));
//		}catch(Exception e) {
//			log.error(e.getMessage(),e);
//			return JSON.toJSONString(new ResultData(true, e.getMessage()));
//		}
//	}
//	
//	
//	private ResultData checkUserUnique(String statement, Map<String, Object> params) {
//		ResultData resp = null;
//		User user = userService.selectOne(statement, params);
//		if(user != null){
//			if(StringUtils.isNotEmpty(user.getLoginName())){
//				resp = new ResultData(false, "用户名已存在");
//			}else if(user.getUserPhone() != 0 ){
//				resp = new ResultData(false, "用户手机已存在");
//			}
//			return resp;
//		}
//		return null;
//	}
//	
	private String getValidateCode(){
		int[] chars = {0,1,2,3,4,5,6,7,8,9};
		String a = new String();
		for (int i = 0; i < 6; i++) {
			a += chars[(int)(Math.random()*10)];
		}
		return a;
	}
	public static void main(String[] args) {
		int[] chars = {0,1,2,3,4,5,6,7,8,9};
		long sysTime = System.currentTimeMillis();
		System.out.println();
		String a = new String();
		for (int i = 0; i < 6; i++) {
			a += chars[(int)(Math.random()*10)];
		}
		System.out.println(System.currentTimeMillis() - sysTime);
		System.out.println(a);
	}
}
