package com.simple.admin.task;

import org.springframework.stereotype.Component;
import com.simple.weixin.auth.WeiXinAuth;
@Component
public class WeixinTask {

	public void initGlobalAccessToken() {
		WeiXinAuth.installAcessToken();
	}
	
	public void initJsTicket() {
		WeiXinAuth.installJsTicket();
	}
}
