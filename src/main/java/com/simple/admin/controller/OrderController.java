package com.simple.admin.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.simple.admin.constant.Constant;
import com.simple.admin.util.AjaxWebUtil;
import com.simple.admin.util.LoginUserUtil;
import com.simple.model.Order;
import com.simple.model.PageResult;
import com.simple.model.User;
import com.simple.service.OrderService;

@Controller
@RequestMapping("/order")
public class OrderController {

	@Autowired
	OrderService orderService;
	
	/**
	 * 待发货
	 * @param pageIndex
	 * @param pageSize
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("toSendList")
	@ResponseBody
	public String toSendList(int pageIndex,int pageSize,HttpServletRequest request, HttpServletResponse response){
		try {
			//String pageIndex = AjaxWebUtil.getRequestParameter(request,"pageIndex");
			//String pageSize = AjaxWebUtil.getRequestParameter(request,"pageSize");
			User user = LoginUserUtil.getCurrentUser(request);
			List<Order> orderList = orderService.queryListByStatus(user.getUserPhone(), null, Constant.ORDER_STATUS_TOSEND, -1, -1, Constant.ORDER_PAY_STATUS_PAY, null, null, pageIndex, pageSize);
			return AjaxWebUtil.sendAjaxResponse(request, response, true, "查询成功", orderList);
		}catch(Exception e) {
			return AjaxWebUtil.sendAjaxResponse(request, response, false, "查询失败："+e.getLocalizedMessage(), null);
		}
	
	}
	
	/**
	 * 已发货
	 * @param pageIndex
	 * @param pageSize
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("sendList")
	@ResponseBody
	public String sendList(int pageIndex,int pageSize,HttpServletRequest request, HttpServletResponse response){
		try {
			User user = LoginUserUtil.getCurrentUser(request);
			List<Order> orderList = orderService.queryListByStatus(user.getUserPhone(), null, Constant.ORDER_STATUS_SEND, -1, -1, Constant.ORDER_PAY_STATUS_PAY, null, null, pageIndex, pageSize);
			return AjaxWebUtil.sendAjaxResponse(request, response, true, "查询成功", orderList);
		}catch(Exception e) {
			return AjaxWebUtil.sendAjaxResponse(request, response, false, "查询失败："+e.getLocalizedMessage(), null);
		}
	}
	
	/**
	 * 已发货
	 * @param pageIndex
	 * @param pageSize
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("finishedList")
	@ResponseBody
	public String finishedList(int pageIndex,int pageSize,HttpServletRequest request, HttpServletResponse response){
		try {
			User user = LoginUserUtil.getCurrentUser(request);
			List<Order> orderList = orderService.queryListByStatus(user.getUserPhone(), null, Constant.ORDER_STATUS_FINISHED, -1, -1, Constant.ORDER_PAY_STATUS_PAY, null, null, pageIndex, pageSize);
			return AjaxWebUtil.sendAjaxResponse(request, response, true, "查询成功", orderList);
		}catch(Exception e) {
			return AjaxWebUtil.sendAjaxResponse(request, response, false, "查询失败："+e.getLocalizedMessage(), null);
		}
	}
	
	/**
	 * 待处理
	 * @param pageIndex
	 * @param pageSize
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("todoList")
	@ResponseBody
	public String todoList(int pageIndex,int pageSize,HttpServletRequest request, HttpServletResponse response){
		try {
			User user = LoginUserUtil.getCurrentUser(request);
			List<Order> orderList = orderService.queryToDoList(user.getUserPhone(), null, null, null, pageIndex, pageSize);
			return AjaxWebUtil.sendAjaxResponse(request, response, true, "查询成功", orderList);
		}catch(Exception e) {
			return AjaxWebUtil.sendAjaxResponse(request, response, false, "查询失败："+e.getLocalizedMessage(), null);
		}
	}
	
	@RequestMapping("orderDetail")
	@ResponseBody
	public String getOrderDetail(HttpServletRequest request, HttpServletResponse response){
		String prmId = AjaxWebUtil.getRequestParameter(request,"id");
		Order order = orderService.getOrdersById(Integer.parseInt(prmId));
		if(order == null){
			return AjaxWebUtil.sendAjaxResponse(request, response, false, "该用户没有订单", null);
		}
		return AjaxWebUtil.sendAjaxResponse(request, response, true, "订单列表查询成功", order);
	}
	
	@RequestMapping("sendProduct")
	@ResponseBody
	public String sendProduct(HttpServletRequest request, HttpServletResponse response){
		String prmOrderId = AjaxWebUtil.getRequestParameter(request,"id");
		boolean isSend = orderService.updateProductToSend(Integer.parseInt(prmOrderId));
		if(isSend){
			return AjaxWebUtil.sendAjaxResponse(request, response, true, "发货成功", null);
		}
		return AjaxWebUtil.sendAjaxResponse(request, response, false, "发货失败", null);
	}
	
	@RequestMapping("returnProduct")
	@ResponseBody
	public String returnProduct(HttpServletRequest request, HttpServletResponse response){
		String prmOrderId = AjaxWebUtil.getRequestParameter(request,"id");
		String prmReturnStatus = AjaxWebUtil.getRequestParameter(request,"status");
		String prmReturnRemark = AjaxWebUtil.getRequestParameter(request,"remark");
		boolean isReturn = orderService.updateReturnProduct(
						Integer.parseInt(prmOrderId), Integer.parseInt(prmReturnStatus), prmReturnRemark);
		if(isReturn){
			return AjaxWebUtil.sendAjaxResponse(request, response, true, "成功", null);
		}
		return AjaxWebUtil.sendAjaxResponse(request, response, false, "失败", null);
	}
}
