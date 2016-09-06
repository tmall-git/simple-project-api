package com.simple.admin.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.simple.admin.util.AjaxWebUtil;
import com.simple.admin.util.LoginUserUtil;
import com.simple.admin.util.MD5Util;
import com.simple.common.config.EnvPropertiesConfiger;
import com.simple.constant.Constant;
import com.simple.model.User;
import com.simple.service.UserService;
import com.simple.weixin.auth.OAuthAccessToken;
import com.simple.weixin.auth.OAuthUserInfo;
import com.simple.weixin.auth.WeiXinAuth;

@Controller
@RequestMapping("")
public class LoginController {

	@Autowired
	private UserService userService;
	
	
	/**
	 * 跳转到微信授权，然后回跳到微信支付页面
	 * @param code
	 * @param token
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "toAuthPage",method=RequestMethod.GET)
	public String toAuthPage(String userPhone,String password,int type,HttpServletRequest request, HttpServletResponse response) {
		String url = WeiXinAuth.getAuthUrl(EnvPropertiesConfiger.getValue("redirectLoginUrl")+"?userPhone="+userPhone+"&password="+password+"&type="+type, true, "");
		return "redirect:"+url;
	}
	
	@RequestMapping(value = "doLogin",method=RequestMethod.GET)
	public String doLogin(String code,HttpServletRequest request, HttpServletResponse response){
		try {
			OAuthAccessToken authToken = WeiXinAuth.getOAuthAccessToken(code);
			if ( null == authToken ) {
				AjaxWebUtil.sendAjaxResponse(request, response, "订单授权失败：未获取到token.");
				return null;
			}
			OAuthUserInfo oi = WeiXinAuth.getOAuthUserInfo(authToken.getAccess_token(), authToken.getOpenid());
			if ( null == oi) {
				AjaxWebUtil.sendAjaxResponse(request, response, "订单授权失败：未获取到用户信息.");
				return null;
			}
			String password = AjaxWebUtil.getRequestParameter(request,"password");
			String userPhone = AjaxWebUtil.getRequestParameter(request,"userPhone");
			
			User user = userService.queryByPhone(userPhone,false);
			if(user == null){
				AjaxWebUtil.sendAjaxResponse(request, response, "用户不存在，请注册");
				return null;
			}
			if(user.getStatus() != Constant.USER_STATUS_VALID){
				AjaxWebUtil.sendAjaxResponse(request, response, "该账户已被封号，不能登录");
				return  null;
			}
			String dbPwd = user.getPassword(); 
			if(!dbPwd.equals(getMD5Password(password, user))){
				AjaxWebUtil.sendAjaxResponse(request, response, "密码错误");
				return null;
			}
			
			String headimgUrl = oi.getHeadimgurl();
			String nickname = oi.getNickname();
			if (!StringUtils.isEmpty(headimgUrl)) {
				user.setHeadimg(headimgUrl);
			}
			if (!StringUtils.isEmpty(nickname)) {
				user.setUserNick(nickname);
				user.setUserName(nickname);
			}
			if (StringUtils.isEmpty(headimgUrl) && StringUtils.isEmpty(nickname)) {
			}else {
				userService.update(user);
			}
			LoginUserUtil.setCurrentUser(request, user);
			//return AjaxWebUtil.sendAjaxResponse(request, response, true,"登陆成功", null);
			String type = AjaxWebUtil.getRequestParameter(request,"type");
			if ("1".equals(type)) {
				return "redirect:"+EnvPropertiesConfiger.getValue("dlloginPageUrl");
			}else {
				return "redirect:"+EnvPropertiesConfiger.getValue("dxloginPageUrl");
			}
		}catch(Exception e) {
			AjaxWebUtil.sendAjaxResponse(request, response, "登录失败:"+e.getLocalizedMessage());
			return null;
		}
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
