package com.simple.admin.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.simple.admin.util.LoginUserUtil;
import com.simple.model.AgentSeller;
import com.simple.model.User;
import com.simple.service.AgentSellerService;
import com.simple.service.OrderService;
import com.simple.service.ProductService;
import com.simple.service.UserService;

@Controller
@RequestMapping(value = "/agentSeller")
public class AgentSellerController {
	
	private static final Logger log = LoggerFactory.getLogger(AgentSellerController.class);

	@Autowired
	UserService userService;
	@Autowired
	OrderService orderService;
	@Autowired
	ProductService productService;
	@Autowired
	AgentSellerService agentSellerService;
	
	/**
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "join",method=RequestMethod.POST)
	@ResponseBody
	public String ownerlist(String owner,HttpServletRequest request, HttpServletResponse response) {
		try {
			User seller = LoginUserUtil.getCurrentUser(request);
			int count = agentSellerService.queryCountByPhone(owner, seller.getUserPhone());
			if (count > 0 ) {
				return AjaxWebUtil.sendAjaxResponse(request, response, true,"绑定成功", null);
			}
			User user = userService.queryByPhone(owner);
			AgentSeller as = new AgentSeller();
			as.setSellerPhone(seller.getUserPhone());
			as.setSellerName(seller.getWeChatNo());
			as.setAgentPhone(owner);
			as.setChargePercent(user.getChargePrecent());
			as.setAgentName(user.getWeChatNo());
			agentSellerService.add(as);
			return AjaxWebUtil.sendAjaxResponse(request, response, true,"绑定成功", null);
		}catch(Exception e) {
			log.error("绑定失败",e);
			e.printStackTrace();
			return AjaxWebUtil.sendAjaxResponse(request, response, false,"绑定失败:"+e.getLocalizedMessage(), null);
		}
	}
	
	
	/**
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "cancel",method=RequestMethod.POST)
	@ResponseBody
	public String cancel(String owner,HttpServletRequest request, HttpServletResponse response) {
		try {
			User seller = LoginUserUtil.getCurrentUser(request);
			List<AgentSeller> ass = agentSellerService.queryListByPhone(owner, seller.getUserPhone(), 1, 1);
			if ( null != ass && ass.size() > 0 ) {
				AgentSeller as = ass.get(0);
				agentSellerService.delete(as);
			}
			return AjaxWebUtil.sendAjaxResponse(request, response, true,"取消成功", null);
		}catch(Exception e) {
			log.error("取消失败",e);
			e.printStackTrace();
			return AjaxWebUtil.sendAjaxResponse(request, response, false,"取消失败:"+e.getLocalizedMessage(), null);
		}
	}
	
	/**
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "agentSetPage",method=RequestMethod.GET)
	@ResponseBody
	public String agentSetPage(HttpServletRequest request, HttpServletResponse response) {
		try {
			User owner = LoginUserUtil.getCurrentUser(request);
			Integer totalSellers = agentSellerService.queryCountByPhone(owner.getUserPhone(),null);
			Map result = new HashMap();
			result.put("sellers", totalSellers == null ? 0 : totalSellers);
			result.put("percent", owner.getChargePrecent());
			result.put("allowSell", owner.getAllowSell());
			return AjaxWebUtil.sendAjaxResponse(request, response, true,"取消成功", result);
		}catch(Exception e) {
			log.error("取消失败",e);
			e.printStackTrace();
			return AjaxWebUtil.sendAjaxResponse(request, response, false,"取消失败:"+e.getLocalizedMessage(), null);
		}
	}
	
	/**
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "agentAllowed",method=RequestMethod.POST)
	@ResponseBody
	public String agentSetPage(int allow,HttpServletRequest request, HttpServletResponse response) {
		try {
			User owner = LoginUserUtil.getCurrentUser(request);
			boolean isallow = false;
			if (allow == 1 ) {
				isallow = true;
			}
			agentSellerService.updateAllow(owner.getUserPhone(), null, isallow);
			return AjaxWebUtil.sendAjaxResponse(request, response, true,"设置成功", null);
		}catch(Exception e) {
			log.error("设置失败",e);
			e.printStackTrace();
			return AjaxWebUtil.sendAjaxResponse(request, response, false,"设置失败:"+e.getLocalizedMessage(), null);
		}
	}
	
	/**
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "agentPercent",method=RequestMethod.POST)
	@ResponseBody
	public String agentPercent(double percent,HttpServletRequest request, HttpServletResponse response) {
		try {
			User owner = LoginUserUtil.getCurrentUser(request);
			agentSellerService.updatePercent(owner.getUserPhone(), percent);
			return AjaxWebUtil.sendAjaxResponse(request, response, true,"设置成功", null);
		}catch(Exception e) {
			log.error("设置失败",e);
			e.printStackTrace();
			return AjaxWebUtil.sendAjaxResponse(request, response, false,"设置失败:"+e.getLocalizedMessage(), null);
		}
	}
	
	
	
}
