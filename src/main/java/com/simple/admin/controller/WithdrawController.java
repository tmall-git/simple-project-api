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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.ruanwei.tool.SmsClient;
import com.ruanwei.tool.SmsResult;
import com.simple.admin.util.AjaxWebUtil;
import com.simple.admin.util.LoginUserUtil;
import com.simple.common.config.EnvPropertiesConfiger;
import com.simple.common.util.DoubleUtil;
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

	private static Map<String, String> cacheValidateCode = new HashMap<String, String>();
	
	@Autowired
	private WithdrawService withdrawService;
	
	@Autowired
	private BaseService baseService;
	
	@RequestMapping(value = "togetcash",method=RequestMethod.GET)
	@ResponseBody
	public String account(HttpServletRequest request, HttpServletResponse response){
		User user = LoginUserUtil.getCurrentUser(request);
		List<Bank> bankList = baseService.getBankList();
		Map result = new HashMap();
		result.put("bankList", bankList);
		result.put("amount", DoubleUtil.formatPrice(user.getBlance()));
		return AjaxWebUtil.sendAjaxResponse(request, response, true, "查找成功", result);
	}
	
	@RequestMapping(value = "getcash",method=RequestMethod.POST)
	@ResponseBody
	public String doAccount(HttpServletRequest request, HttpServletResponse response){
		try {
			String bankCode = AjaxWebUtil.getRequestParameter(request,"bankCode");
			String prmReanlName = AjaxWebUtil.getRequestParameter(request,"realName");
			//支行
			String prmBankAccount = AjaxWebUtil.getRequestParameter(request,"bankAccount");
			//银行帐号
			String prmBankAccountNo = AjaxWebUtil.getRequestParameter(request,"bankAccountNo");
			User user = LoginUserUtil.getCurrentUser(request);
			Account account = new Account();
			account.setId(PrimaryKeyUtil.getUUID());
			account.setBankCode(bankCode);
			account.setRealName(prmReanlName);
			account.setBankAccount(prmBankAccount);
			account.setBankAccountNo(prmBankAccountNo);
			account.setApplyPhone(user.getUserPhone());
			account.setStatus(Constant.CASH_STATUS_COMMIT);
			account.setCashAmount(DoubleUtil.formatPrice(user.getBlance()));
			account.setCashTime(new Timestamp(new Date().getTime()));
			account.setWeiChatNo(user.getWeChatNo());
			withdrawService.addAccount(account);
			return AjaxWebUtil.sendAjaxResponse(request, response, true, "提现申请成功", account.getId());
		}catch(Exception e) {
			e.printStackTrace();
			return AjaxWebUtil.sendAjaxResponse(request, response, false, "提现申请失败："+e.getLocalizedMessage(), null);
		}
		
	}
	
	@RequestMapping(value="validateCode",method=RequestMethod.GET)
	@ResponseBody
	public String getValidateCode(String phone,HttpServletRequest request, HttpServletResponse response){
		try {
			String validatorCode = getValidateCode();
			cacheValidateCode.put(phone,validatorCode );
			SmsResult sr = SmsClient.sendMsg(phone, "取现验证码:"+validatorCode);
			if (sr.isSuccess()) {
				return  AjaxWebUtil.sendAjaxResponse(request, response, true,"获取验证码成功", sr.getMsg());
			}else {
				return  AjaxWebUtil.sendAjaxResponse(request, response, false,"获取验证码失败:"+validatorCode, sr.getMsg());
			}
			
		}
		catch (Exception e){
			return  AjaxWebUtil.sendAjaxResponse(request, response, false,"获取验证码失败:"+e.getLocalizedMessage(), null);
		}
	}
	
	private String getValidateCode(){
		int[] chars = {0,1,2,3,4,5,6,7,8,9};
		String a = new String();
		for (int i = 0; i < 6; i++) {
			a += chars[(int)(Math.random()*10)];
		}
		return a;
	}
	
	@RequestMapping(value = "tovalidate",method=RequestMethod.POST)
	@ResponseBody
	public String tovalidate(String id,HttpServletRequest request, HttpServletResponse response){
		try {
			Account account = withdrawService.queryById(id);
			if ( null == account ) {
				AjaxWebUtil.sendAjaxResponse(request, response, false, "提现记录不存在", account);
			}
			return AjaxWebUtil.sendAjaxResponse(request, response, true, "查询成功", account);
		}catch(Exception e) {
			e.printStackTrace();
			return AjaxWebUtil.sendAjaxResponse(request, response, false, "查询失败："+e.getLocalizedMessage(), null);
		}
		
	}
	
	@RequestMapping(value = "validate",method=RequestMethod.POST)
	@ResponseBody
	public String allowAccount(String id,String phone,String validateCode,HttpServletRequest request, HttpServletResponse response){
		try {
			if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(validateCode)) {
				return AjaxWebUtil.sendAjaxResponse(request, response, false,"请输入手机号，并且填写验证码", null);
			}
			if(!validateCode.equals(cacheValidateCode.get(phone))){
				return AjaxWebUtil.sendAjaxResponse(request, response, false,"验证码错误", null);
			}
			User user = LoginUserUtil.getCurrentUser(request);
			Account account = withdrawService.queryById(id);
			Bank bank = baseService.queryBank(account.getBankCode());
			if ( null != bank ) {
				account.setBankName(bank.getName());
			}
			account.setUserPhone(phone);
			withdrawService.updateAccountPhone(account);
			SmsClient.sendAdminMsg("提现申请:[申请人："+user.getUserPhone()+"(微信号："+user.getWeChatNo()+"),申请金额："+user.getBlance()+"].");
			return AjaxWebUtil.sendAjaxResponse(request, response, true, "提现申请成功", null);
		}catch(Exception e) {
			e.printStackTrace();
			return AjaxWebUtil.sendAjaxResponse(request, response, false, "提现申请失败："+e.getLocalizedMessage(), null);
		}
	}
	
	@RequestMapping(value = "finishcash",method=RequestMethod.POST)
	@ResponseBody
	public String finishcash(String id,HttpServletRequest request, HttpServletResponse response){
		try {
			Account a = withdrawService.queryById(id);
			if (null == a) {
				return AjaxWebUtil.sendAjaxResponse(request, response, false, "记录不存在", null);
			}
			if (a.getStatus()!=Constant.CASH_STATUS_COMMIT) {
				return AjaxWebUtil.sendAjaxResponse(request, response, false, "该状态不允许提现", null);
			}
			withdrawService.updateAccountFinised(a);
			SmsClient.sendMsg(a.getApplyPhone(), "提现金额:"+a.getCashAmount()+"成功.");
			return AjaxWebUtil.sendAjaxResponse(request, response, true, "提现成功", null);
		}catch(Exception e) {
			e.printStackTrace();
			return AjaxWebUtil.sendAjaxResponse(request, response, false, "提现失败："+e.getLocalizedMessage(), null);
		}
	}
	
	@RequestMapping(value = "accountList",method=RequestMethod.GET)
	@ResponseBody
	public String accountList(int pageIndex,int pageSize,HttpServletRequest request, HttpServletResponse response){
		try {
			User user = LoginUserUtil.getCurrentUser(request);
			List<Account> a = withdrawService.queryList(pageIndex, pageSize, user.getUserPhone());
			return AjaxWebUtil.sendAjaxResponse(request, response, true, "查询提现成功", a);
		}catch(Exception e) {
			e.printStackTrace();
			return AjaxWebUtil.sendAjaxResponse(request, response, false, "查询提现失败："+e.getLocalizedMessage(), null);
		}
	}
	
	@RequestMapping(value = "allAccountList",method=RequestMethod.GET)
	@ResponseBody
	public String accountList(int pageIndex,int pageSize,int status,HttpServletRequest request, HttpServletResponse response){
		try {
			List<Account> a = withdrawService.queryAllList(pageIndex, pageSize, null,status);
			return AjaxWebUtil.sendAjaxResponse(request, response, true, "查询提现成功", a);
		}catch(Exception e) {
			e.printStackTrace();
			return AjaxWebUtil.sendAjaxResponse(request, response, false, "查询提现失败："+e.getLocalizedMessage(), null);
		}
	}
}
