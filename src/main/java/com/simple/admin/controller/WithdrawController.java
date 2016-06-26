package com.simple.admin.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.simple.admin.util.AjaxWebUtil;
import com.simple.admin.util.LoginUserUtil;
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
	
	@RequestMapping("account")
	@ResponseBody
	public String account(HttpServletRequest request, HttpServletResponse response){
		User user = LoginUserUtil.getCurrentUser(request);
		List<Bank> bankList = baseService.getBankList();
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("account", user.getBalance());
		data.put("bankList", bankList);
		return AjaxWebUtil.sendAjaxResponse(request, response, true, "查找成功", JSON.toJSONString(data));
	}
	
	@RequestMapping("doAccount")
	@ResponseBody
	public String doAccount(HttpServletRequest request, HttpServletResponse response){
		String prmBankId = AjaxWebUtil.getRequestParameter(request,"bankId");
		String prmReanlName = AjaxWebUtil.getRequestParameter(request,"realName");
		String prmBankAccount = AjaxWebUtil.getRequestParameter(request,"bankAccount");
		String prmBankAccountNo = AjaxWebUtil.getRequestParameter(request,"bankAccountNo");
		Account account = new Account();
		account.setBankId(Integer.parseInt(prmBankId));
		account.setRealName(prmReanlName);
		account.setBankAccount(prmBankAccount);
		account.setBankAccountNo(Integer.parseInt(prmBankAccountNo));
		boolean isAccount = withdrawService.addAccount(account);
		if(isAccount){
			return AjaxWebUtil.sendAjaxResponse(request, response, true, "提现成功", null);
		}
		return AjaxWebUtil.sendAjaxResponse(request, response, false, "提现失败", null);
	}
}
