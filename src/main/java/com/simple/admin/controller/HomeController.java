package com.simple.admin.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.simple.model.AgentSellerMain;
import com.simple.model.SellerListVO;
import com.simple.model.SellerMain;
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
	
	/**
	 * 代理销售额
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "agentSellCount",method=RequestMethod.GET)
	@ResponseBody
	public String agentSellCount(HttpServletRequest request, HttpServletResponse response) {
		try {
			String phone = LoginUserUtil.getCurrentUser(request).getUserPhone();
			Double charge = orderService.queryAgentTotalCharge(phone);
			Double total = orderService.queryAgentTotalPrice(phone);
			User user = userService.queryByPhone(phone);
			return AjaxWebUtil.sendAjaxResponse(request, response, true,"查询成功", new UserSellCount(total,charge,user.getBalance()));
		}catch(Exception e) {
			log.error("查询失败",e);
			e.printStackTrace();
			return AjaxWebUtil.sendAjaxResponse(request, response, false,"查询失败", null);
		}
	}
	
	/**
	 * 代销销售额
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "sellerSellCount",method=RequestMethod.GET)
	@ResponseBody
	public String sellerSellCount(HttpServletRequest request, HttpServletResponse response) {
		try {
			String phone = LoginUserUtil.getCurrentUser(request).getUserPhone();
			Double charge = orderService.querySellerTotalCharge(null,phone);
			Double total = orderService.querySellerTotalPrice(null,phone);
			User user = userService.queryByPhone(phone);
			return AjaxWebUtil.sendAjaxResponse(request, response, true,"查询成功", new UserSellCount(total,charge,user.getBalance()));
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
			User user = LoginUserUtil.getCurrentUser(request);
			Integer daifahuo = orderService.queryCountByStatus(user.getUserPhone(),null,Constant.ORDER_STATUS_TOSEND, -1, -1, Constant.ORDER_PAY_STATUS_PAY);
			Integer tuihuozhong = orderService.queryCountByStatus(user.getUserPhone(),null,-1, -1, Constant.ORDER_REJECT_STATUS_YES, -1);
			Integer sellproduct = productService.queryProductCount(Constant.PRODUCT_STATUS_ONLINE, user.getUserPhone());
			Integer nostock = productService.queryNoStockCount(user.getUserPhone());
			Integer totalSellers = agentSellerService.queryCountByPhone(user.getUserPhone(),null);
			Double chargepercent  = 0d;
			List<AgentSeller> ass =  agentSellerService.queryByAgent(user.getUserPhone());
			if ( null != ass && ass.size() > 0 ) {
				chargepercent = ass.get(0).getChargePercent();
			}
			return AjaxWebUtil.sendAjaxResponse(request, response, true,"查询成功", 
					new AgentHome(daifahuo, tuihuozhong, sellproduct, nostock, totalSellers, chargepercent,user.getCategory()));
		}catch(Exception e) {
			log.error(e.getMessage(),e);
			return AjaxWebUtil.sendAjaxResponse(request, response, false,"查询失败", e.getMessage());
		}
	}
	
	@RequestMapping(value = "sellerMain",method=RequestMethod.GET)
	@ResponseBody
	public String sellerMainList(int pageIndex,int pageSize,HttpServletRequest request, HttpServletResponse response) {
		try {
			User user = LoginUserUtil.getCurrentUser(request);
			//待发货
			int daifahuo = orderService.queryCountByStatus(null,user.getUserPhone(),Constant.ORDER_STATUS_TOSEND, -1, -1, Constant.ORDER_PAY_STATUS_PAY);
			//退货中
			int tuihuozhong = orderService.queryCountByStatus(null,user.getUserPhone(),-1, -1, Constant.ORDER_REJECT_STATUS_YES, -1);
			
			//String prmPageIndex = AjaxWebUtil.getRequestParameter(request,"pageIndex");
			//String prmPageSize = AjaxWebUtil.getRequestParameter(request,"pageSize");
			//Integer pageIndex = Integer.parseInt(prmPageIndex) <= 0 ? 1 : StringUtils.isEmpty(prmPageIndex) ? 1 : Integer.parseInt(prmPageIndex);
			//Integer pageSize = StringUtils.isEmpty(prmPageSize) ? 5 : Integer.parseInt(prmPageSize);
			//代销店铺总数
			int count = agentSellerService.queryCountByPhone(null, user.getUserPhone());
			List<AgentSeller> asllers = agentSellerService.queryListByPhone(null, user.getUserPhone(),pageIndex,pageSize);
			SellerMain sm = new SellerMain();
			if ( null != asllers && asllers.size() > 0 ) {
				List<SellerListVO> pageList = new ArrayList<SellerListVO>();
				for (int i = 0 ; i < asllers.size() ; i ++)  {
					AgentSeller as = asllers.get(i);
					if (!as.isAllow()) {
						continue;
					}
					Double charge = orderService.querySellerTotalPrice(as.getAgentPhone(),as.getSellerPhone());
					Integer orderCount = orderService.queryCountByStatus(as.getAgentPhone(),as.getSellerPhone(),-1, -1, -1, Constant.ORDER_PAY_STATUS_PAY);
					Integer productCount = orderService.queryProductCount(as.getAgentPhone(),as.getSellerPhone(),-1, -1, -1, Constant.ORDER_PAY_STATUS_PAY);
					SellerListVO sv = new SellerListVO();
					sv.setDealCount(orderCount);
					sv.setProductCount(productCount);
					sv.setSellerAmount(charge);
					sv.setUserPhone(as.getAgentPhone());
					sv.setWechatName(as.getAgentName());
					pageList.add(sv);
				}
				sm.setShops(pageList);
			}
			sm.setDaifahuo(daifahuo);
			sm.setTuihuozhong(tuihuozhong);
			sm.setShopCount(count);
			return AjaxWebUtil.sendAjaxResponse(request, response, true, "查询成功", sm);
		}catch(Exception e) {
			log.error(e.getMessage(),e);
			return AjaxWebUtil.sendAjaxResponse(request, response, false, "查询失败", e.getMessage());
		}
	}
	
	@RequestMapping(value = "agentSeller",method=RequestMethod.GET)
	@ResponseBody
	public String agentSeller(int pageIndex,int pageSize,HttpServletRequest request, HttpServletResponse response) {
		try {
			User user = LoginUserUtil.getCurrentUser(request);
			List<AgentSeller> asllers = agentSellerService.queryListByPhone(user.getUserPhone(),null, pageIndex,pageSize);
			List<AgentSellerMain> pageList = null;
			if ( null != asllers && asllers.size() > 0 ) {
				pageList = new ArrayList<AgentSellerMain>();
				for (int i = 0 ; i < asllers.size() ; i ++)  {
					AgentSeller as = asllers.get(i);
					Double charge = orderService.querySellerTotalPrice(as.getAgentPhone(),as.getSellerPhone());
					Integer orderCount = orderService.queryCountByStatus(as.getAgentPhone(),as.getSellerPhone(),-1, -1, -1, Constant.ORDER_PAY_STATUS_PAY);
					AgentSellerMain asm = new AgentSellerMain();
					asm.setOrderCount(orderCount==null?0:orderCount);
					asm.setTotalSell(charge==null?0d:charge);
					asm.setWatchCount(as.getWatchCount());
					pageList.add(asm);
				}
			}
			return AjaxWebUtil.sendAjaxResponse(request, response, true, "查询成功", pageList);
		}catch(Exception e) {
			log.error(e.getMessage(),e);
			return AjaxWebUtil.sendAjaxResponse(request, response, false, "查询失败", e.getMessage());
		}
	}
	
}
