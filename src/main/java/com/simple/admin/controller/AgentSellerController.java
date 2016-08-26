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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.simple.admin.util.AjaxWebUtil;
import com.simple.admin.util.LoginUserUtil;
import com.simple.constant.Constant;
import com.simple.model.AgentSeller;
import com.simple.model.Order;
import com.simple.model.Product;
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
	 * 代销------》加入代理
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "join",method=RequestMethod.POST)
	@ResponseBody
	public String ownerlist(String owner,HttpServletRequest request, HttpServletResponse response) {
		try {
			if (StringUtils.isEmpty(owner)) {
				return AjaxWebUtil.sendAjaxResponse(request, response, false,"绑定失败:owner为空", null);
			}
			User seller = LoginUserUtil.getCurrentUser(request);
			return this.createAgentSeller(owner, seller.getUserPhone(),seller.getWeChatNo(), request, response);
		}catch(Exception e) {
			log.error("绑定失败",e);
			e.printStackTrace();
			return AjaxWebUtil.sendAjaxResponse(request, response, false,"绑定失败:"+e.getLocalizedMessage(), null);
		}
	}
	
	private String createAgentSeller(String owner,String seller,String sellerWeiChat,HttpServletRequest request, HttpServletResponse response) {
		int count = agentSellerService.queryCountByPhone(owner, seller);
		if (count > 0 ) {
			return AjaxWebUtil.sendAjaxResponse(request, response, true,"绑定成功", null);
		}
		User user = userService.queryByPhone(owner,false);
		if (null == user ) {
			return AjaxWebUtil.sendAjaxResponse(request, response, false,"绑定失败：该代理不存在", null);
		}
		if (user.getStatus() != Constant.USER_STATUS_VALID ) {
			return AjaxWebUtil.sendAjaxResponse(request, response, false,"绑定失败：该代理已被封号", null);
		}
		AgentSeller as = new AgentSeller();
		as.setSellerPhone(seller);
		//as.setSellerName(sellerWeiChat);
		as.setAgentPhone(owner);
		as.setChargePercent(user.getChargePrecent());
		//as.setAgentName(user.getWeChatNo());
		agentSellerService.add(as);
		return AjaxWebUtil.sendAjaxResponse(request, response, true,"绑定成功", null);
	}
	
	/**
	 * 用户订单------》加入代销
	 * @param code:订单编号
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "userJoin",method=RequestMethod.POST)
	@ResponseBody
	public String userJoin(String code,HttpServletRequest request, HttpServletResponse response) {
		try {
			Order order = orderService.getOrderByCode(code);
			int productId = order.getProduct_id();
			Product product = productService.getById(productId, false);
			return AjaxWebUtil.sendAjaxResponse(request, response, true,"设置成功:", product.getOwner());
		}catch(Exception e) {
			log.error("绑定失败",e);
			e.printStackTrace();
			return AjaxWebUtil.sendAjaxResponse(request, response, false,"绑定失败:"+e.getLocalizedMessage(), null);
		}
	}
	
	
	/**
	 * 代销------》取消加入代理
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
	 * 代理------》取消代销
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "cancelSeller",method=RequestMethod.POST)
	@ResponseBody
	public String cancelSeller(String seller,HttpServletRequest request, HttpServletResponse response) {
		try {
			User owner = LoginUserUtil.getCurrentUser(request);
			List<AgentSeller> ass = agentSellerService.queryListByPhone(owner.getUserPhone(), seller, 1, 1);
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
	 * 代理------》设置代销
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
	 * 代理------>设置是否允许代销
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
			owner.setAllowSell(allow);
			LoginUserUtil.setCurrentUser(request, owner);
			return AjaxWebUtil.sendAjaxResponse(request, response, true,"设置成功", null);
		}catch(Exception e) {
			log.error("设置失败",e);
			e.printStackTrace();
			return AjaxWebUtil.sendAjaxResponse(request, response, false,"设置失败:"+e.getLocalizedMessage(), null);
		}
	}
	
	/**
	 * 代理------》统一设置代销提成比
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
			owner.setChargePrecent(percent);
			LoginUserUtil.setCurrentUser(request, owner);
			return AjaxWebUtil.sendAjaxResponse(request, response, true,"设置成功", null);
		}catch(Exception e) {
			log.error("设置失败",e);
			e.printStackTrace();
			return AjaxWebUtil.sendAjaxResponse(request, response, false,"设置失败:"+e.getLocalizedMessage(), null);
		}
	}
	
	/**
	 * 代理------》查询代销提成比
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "agentSellerPercent",method=RequestMethod.GET)
	@ResponseBody
	public String agentSellerPercent(String seller,HttpServletRequest request, HttpServletResponse response) {
		try {
			User owner = LoginUserUtil.getCurrentUser(request);
			List<AgentSeller> ass = agentSellerService.queryListByPhone(owner.getUserPhone(), seller, 1, 1);
			double precent = 0.00;
			if ( null != ass && ass.size() > 0) {
				precent = ass.get(0).getChargePercent();
			}
			return AjaxWebUtil.sendAjaxResponse(request, response, true,"查询成功", precent);
		}catch(Exception e) {
			log.error("查询失败",e);
			e.printStackTrace();
			return AjaxWebUtil.sendAjaxResponse(request, response, false,"查询失败:"+e.getLocalizedMessage(), null);
		}
	}
	
	/**
	 * 代理------》设置单个代销提成比
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "agentSellerPercentSet",method=RequestMethod.POST)
	@ResponseBody
	public String agentSellerPercentSet(String seller,double percent,HttpServletRequest request, HttpServletResponse response) {
		try {
			User owner = LoginUserUtil.getCurrentUser(request);
			agentSellerService.updatePercent(owner.getUserPhone(), seller, percent);
			return AjaxWebUtil.sendAjaxResponse(request, response, true,"设置成功", null);
		}catch(Exception e) {
			log.error("设置失败",e);
			e.printStackTrace();
			return AjaxWebUtil.sendAjaxResponse(request, response, false,"设置失败:"+e.getLocalizedMessage(), null);
		}
	}
	
}
