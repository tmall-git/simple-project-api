package com.simple.admin.controller;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.ruanwei.tool.SmsClient;
import com.simple.admin.util.AjaxWebUtil;
import com.simple.admin.util.LoginUserUtil;
import com.simple.common.util.PrimaryKeyUtil;
import com.simple.constant.Constant;
import com.simple.model.Account;
import com.simple.model.Bank;
import com.simple.model.User;
import com.simple.service.BaseService;
import com.simple.service.WithdrawService;

@Controller
@RequestMapping("account")
public class WithdrawController {

	@Autowired
	private WithdrawService withdrawService;
	
	@Autowired
	private BaseService baseService;
	
	@RequestMapping(value = "account",method=RequestMethod.GET)
	@ResponseBody
	public String account(HttpServletRequest request, HttpServletResponse response){
		User user = LoginUserUtil.getCurrentUser(request);
		List<Bank> bankList = baseService.getBankList();
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("account", user.getBalance());
		data.put("bankList", bankList);
		return AjaxWebUtil.sendAjaxResponse(request, response, true, "查找成功", JSON.toJSONString(data));
	}
	
	@RequestMapping(value = "doAccount",method=RequestMethod.POST)
	@ResponseBody
	public String doAccount(HttpServletRequest request, HttpServletResponse response){
		try {
			String bankCode = AjaxWebUtil.getRequestParameter(request,"bankCode");
			String prmReanlName = AjaxWebUtil.getRequestParameter(request,"realName");
			String prmBankAccount = AjaxWebUtil.getRequestParameter(request,"bankAccount");
			String prmBankAccountNo = AjaxWebUtil.getRequestParameter(request,"bankAccountNo");
			String bankPhone = AjaxWebUtil.getRequestParameter(request,"phone");
			User user = LoginUserUtil.getCurrentUser(request);
			Account account = new Account();
			account.setId(PrimaryKeyUtil.getUUID());
			account.setBankCode(bankCode);
			account.setRealName(prmReanlName);
			account.setBankAccount(prmBankAccount);
			account.setBankAccountNo(prmBankAccountNo);
			account.setUserPhone(user.getUserPhone());
			account.setBankPhone(bankPhone);
			account.setCashTime(new Timestamp(new Date().getTime()));
			account.setCashAmount(user.getBalance());
			account.setWeiChatNo(user.getWeChatNo());
			account.setStatus(Constant.CASH_STATUS_COMMIT);
			withdrawService.addAccount(account);
			SmsClient.sendAdminMsg("提现申请:[申请人："+user.getUserPhone()+"(微信号："+user.getWeChatNo()+"),申请金额："+user.getBalance()+"].");
			return AjaxWebUtil.sendAjaxResponse(request, response, true, "提现成功", null);
		}catch(Exception e) {
			e.printStackTrace();
			return AjaxWebUtil.sendAjaxResponse(request, response, false, "提现失败："+e.getLocalizedMessage(), null);
		}
		
	}
	
	@RequestMapping(value = "allowAccount",method=RequestMethod.POST)
	@ResponseBody
	public String allowAccount(String id,String remark,HttpServletRequest request, HttpServletResponse response){
		try {
			Account a = withdrawService.updateAccountFinised(id, remark);
			SmsClient.sendMsg(a.getUserPhone(), "提现:"+a.getCashAmount()+"成功.");
			return AjaxWebUtil.sendAjaxResponse(request, response, true, "提现成功", null);
		}catch(Exception e) {
			e.printStackTrace();
			return AjaxWebUtil.sendAjaxResponse(request, response, false, "提现失败："+e.getLocalizedMessage(), null);
		}
		
	}
	
	@RequestMapping(value = "cancelAccount",method=RequestMethod.POST)
	@ResponseBody
	public String cancelAccount(String id,String remark,HttpServletRequest request, HttpServletResponse response){
		try {
			Account a = withdrawService.updateAccountCancel(id, remark);
			SmsClient.sendMsg(a.getUserPhone(), "提现取消:"+a.getCashAmount()+"成功.");
			return AjaxWebUtil.sendAjaxResponse(request, response, true, "提现成功", null);
		}catch(Exception e) {
			e.printStackTrace();
			return AjaxWebUtil.sendAjaxResponse(request, response, false, "提现失败："+e.getLocalizedMessage(), null);
		}
		
	}
}
