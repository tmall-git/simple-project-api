package com.simple.admin.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.simple.admin.constant.Constant;
import com.simple.admin.util.AjaxWebUtil;
import com.simple.model.Order;
import com.simple.model.User;
import com.simple.service.OrderService;

@Controller
@RequestMapping("/order")
public class OrderController {

	OrderService orderService;
	
	@RequestMapping("orderList")
	public String getOrderList(HttpServletRequest request, HttpServletResponse response){
		String pageIndex = AjaxWebUtil.getRequestParameter(request,"pageIndex");
		String pageSize = AjaxWebUtil.getRequestParameter(request,"pageSize");
		String prmOrderStatus = AjaxWebUtil.getRequestParameter(request,"orderStatus");
		User user = (User)request.getSession().getAttribute(Constant.CURRENT_USER);
		String userPhone = user.getUserPhone();
		Integer orderStatus = StringUtils.isEmpty(prmOrderStatus) ? null : Integer.valueOf(prmOrderStatus);
		List<Order> orderLists = orderService.getOrdersLists(userPhone, orderStatus, Integer.parseInt(pageIndex), Integer.parseInt(pageSize));
		if(orderLists == null || orderLists.isEmpty()){
			return AjaxWebUtil.sendAjaxResponse(request, response, false, "该用户没有订单", null);
		}
		return AjaxWebUtil.sendAjaxResponse(request, response, true, "订单列表查询成功", orderLists);
	}
	
	@RequestMapping("orderDetail")
	public String getOrderDetail(HttpServletRequest request, HttpServletResponse response){
		String prmId = AjaxWebUtil.getRequestParameter(request,"id");
		Order order = orderService.getOrdersById(Integer.parseInt(prmId));
		if(order == null){
			return AjaxWebUtil.sendAjaxResponse(request, response, false, "该用户没有订单", null);
		}
		return AjaxWebUtil.sendAjaxResponse(request, response, true, "订单列表查询成功", order);
	}
	
	@RequestMapping("sendProduct")
	public String sendProduct(HttpServletRequest request, HttpServletResponse response){
		String prmOrderId = AjaxWebUtil.getRequestParameter(request,"id");
		boolean isSend = orderService.sendProduct(Integer.parseInt(prmOrderId));
		if(isSend){
			return AjaxWebUtil.sendAjaxResponse(request, response, true, "发货成功", null);
		}
		return AjaxWebUtil.sendAjaxResponse(request, response, false, "发货失败", null);
	}
	
	@RequestMapping("returnProduct")
	public String returnProduct(HttpServletRequest request, HttpServletResponse response){
		String prmOrderId = AjaxWebUtil.getRequestParameter(request,"id");
		String prmReturnStatus = AjaxWebUtil.getRequestParameter(request,"status");
		String prmReturnRemark = AjaxWebUtil.getRequestParameter(request,"remark");
		boolean isReturn = orderService.returnProduct(
						Integer.parseInt(prmOrderId), Integer.parseInt(prmReturnStatus), prmReturnRemark);
		if(isReturn){
			return AjaxWebUtil.sendAjaxResponse(request, response, true, "成功", null);
		}
		return AjaxWebUtil.sendAjaxResponse(request, response, false, "失败", null);
	}
}
