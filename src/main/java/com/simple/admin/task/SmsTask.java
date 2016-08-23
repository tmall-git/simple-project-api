package com.simple.admin.task;

import org.springframework.stereotype.Component;

import com.ruanwei.tool.SmsClient;
import com.ruanwei.tool.SmsResult;

@Component
public class SmsTask {

	public void overage() {
		SmsResult sr = SmsClient.getOverage();
		if (!sr.isSuccess()) {
			SmsClient.sendAdminMsg("短信查询余额接口调用失败：请查询系统日志");
		}else {
			try {
				int count = Integer.parseInt((String)sr.getData());
				if (count<=500) {
					SmsClient.sendAdminMsg("短信余额小于500,请尽快充值");
				}
			}catch(Exception e) {
				SmsClient.sendAdminMsg("短信查询余额接口调用失败：未查询到余额条数");
			}
		}
	}
	
}
