package com.simple.admin.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.simple.admin.constant.Constant;
import com.simple.admin.util.AjaxWebUtil;
import com.simple.admin.util.LoginUserUtil;
import com.simple.model.AgentHome;
import com.simple.model.AgentSeller;
import com.simple.model.SellerJoinHeadVO;
import com.simple.model.SellerMainVO;
import com.simple.model.User;
import com.simple.model.UserSellCount;
import com.simple.service.AgentSellerService;
import com.simple.service.OrderService;
import com.simple.service.ProductService;
import com.simple.service.UserService;

@Controller
@RequestMapping(value = "/home")
public class HomeController {
	
	private static final Logger log = LoggerFactory.getLogger(HomeController.class);

	@Autowired
	UserService userService;
	@Autowired
	OrderService orderService;
	@Autowired
	ProductService productService;
	@Autowired
	AgentSellerService agentSellerService;
	
	@RequestMapping(value = "sellCount",method=RequestMethod.GET)
	@ResponseBody
	public String register(HttpServletRequest request, HttpServletResponse response) {
		try {
			String phone = LoginUserUtil.getCurrentUser(request).getUserPhone();
			double charge = orderService.queryTotalCharge(phone);
			double total = orderService.queryTotalPrice(phone);
			return AjaxWebUtil.sendAjaxResponse(request, response, true,"查询成功", new UserSellCount(total,charge));
		}catch(Exception e) {
			log.error("查询失败",e);
			e.printStackTrace();
			return AjaxWebUtil.sendAjaxResponse(request, response, false,"查询失败", null);
		}
	}
	
	@RequestMapping(value = "agent",method=RequestMethod.GET)
	@ResponseBody
	public String modifyPwd(HttpServletRequest request, HttpServletResponse response) {
		try {
			String phone = LoginUserUtil.getCurrentUser(request).getUserPhone();
			int daifahuo = orderService.queryCountByStatus(phone,Constant.ORDER_STATUS_TOSEND, -1, -1, Constant.ORDER_PAY_STATUS_PAY);
			int tuihuozhong = orderService.queryCountByStatus(phone,-1, -1, Constant.ORDER_REJECT_STATUS_YES, -1);
			int sellproduct = productService.queryProductCount(Constant.PRODUCT_STATUS_ONLINE, phone);
			int nostock = productService.queryNoStockCount(phone);
			int totalSellers = agentSellerService.queryCountByAgent(phone);
			double chargepercent  = 0d;
			List<AgentSeller> ass =  agentSellerService.queryByAgent(phone);
			if ( null != ass && ass.size() > 0 ) {
				chargepercent = ass.get(0).getChargePercent();
			}
			return AjaxWebUtil.sendAjaxResponse(request, response, true,"查询成功", 
					new AgentHome(daifahuo, tuihuozhong, sellproduct, nostock, totalSellers, chargepercent));
		}catch(Exception e) {
			log.error(e.getMessage(),e);
			return AjaxWebUtil.sendAjaxResponse(request, response, false,"查询失败", e.getMessage());
		}
	}
	
	@RequestMapping(value = "sellerMain",method=RequestMethod.GET)
	@ResponseBody
	public String sellerMain(HttpServletRequest request, HttpServletResponse response) {
		try {
			User user = LoginUserUtil.getCurrentUser(request);
			Map<String, Object> smVO = userService.toSellerMain(user);
			String result = AjaxWebUtil.sendAjaxResponse(request, response, true, "查询成功", smVO);
			log.debug(result);
			return result;
		}catch(Exception e) {
			log.error(e.getMessage(),e);
			return AjaxWebUtil.sendAjaxResponse(request, response, false, "查询失败", e.getMessage());
		}
	}
	
	@RequestMapping(value = "sellerJoinList",method=RequestMethod.GET)
	@ResponseBody
	public String sellerJoinList(HttpServletRequest request, HttpServletResponse response) {
		try {
			User user = LoginUserUtil.getCurrentUser(request);
			List<Map<String, Object>> lists = agentSellerService.getSellerJoinList(user.getUserPhone());
			String result = AjaxWebUtil.sendAjaxResponse(request, response, true, "查询成功", lists);
			log.debug(result);
			return result;
		}catch(Exception e) {
			log.error(e.getMessage(),e);
			return AjaxWebUtil.sendAjaxResponse(request, response, false, "查询失败", e.getMessage());
		}
	}
	
	
	
	@RequestMapping(value = "modifyUser",method=RequestMethod.POST)
	@ResponseBody
	public String modifyUser(HttpServletRequest request, HttpServletResponse response) {
		try {
			String userPhone = AjaxWebUtil.getRequestParameter(request,"userPhone");
			String prmWechatNo = AjaxWebUtil.getRequestParameter(request,"wechatNo");
			String prmUserNick = AjaxWebUtil.getRequestParameter(request,"userNick");
			String prmCategory = AjaxWebUtil.getRequestParameter(request,"category");
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("userPhone", userPhone);
			User user = userService.selectOne("user.selectOne", params);
			user.setWeChatNo(prmWechatNo);
			user.setUserNick(prmUserNick);
			if(StringUtils.isNotEmpty(prmCategory)){
				user.setCategory(prmCategory);
			}
			userService.update(user);
			LoginUserUtil.setCurrentUser(request, user);
			return AjaxWebUtil.sendAjaxResponse(request, response, true,"成功", null);
		}catch(Exception e) {
			log.error(e.getMessage(),e);
			return AjaxWebUtil.sendAjaxResponse(request, response, false,"失败", null);
		}
	}
	
	private boolean checkUserUnique(String statement, Map<String, Object> params) {
		User user = userService.selectOne(statement, params);
		if(user != null){
			if(StringUtils.isNotEmpty(user.getUserPhone())){
				return true;
			}
		}
		return false;
	}
	
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
