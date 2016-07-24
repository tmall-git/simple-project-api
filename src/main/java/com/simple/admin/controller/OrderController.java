package com.simple.admin.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.simple.admin.util.AjaxWebUtil;
import com.simple.admin.util.LoginUserUtil;
import com.simple.constant.Constant;
import com.simple.model.Expressage;
import com.simple.model.Order;
import com.simple.model.OrderForm;
import com.simple.model.User;
import com.simple.service.OrderService;

@Controller
@RequestMapping("/order")
public class OrderController {

	@Autowired
	OrderService orderService;
	
	/**
	 * 代理------》待发货
	 * @param pageIndex
	 * @param pageSize
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "toSendList",method=RequestMethod.GET)
	@ResponseBody
	public String toSendList(int pageIndex,int pageSize,HttpServletRequest request, HttpServletResponse response){
		try {
			//String pageIndex = AjaxWebUtil.getRequestParameter(request,"pageIndex");
			//String pageSize = AjaxWebUtil.getRequestParameter(request,"pageSize");
			User user = LoginUserUtil.getCurrentUser(request);
			List<Order> orderList = orderService.queryListByStatus(user.getUserPhone(), null, Constant.ORDER_STATUS_TOSEND, null, null, pageIndex, pageSize);
			return AjaxWebUtil.sendAjaxResponse(request, response, true, "查询成功", orderList);
		}catch(Exception e) {
			return AjaxWebUtil.sendAjaxResponse(request, response, false, "查询失败："+e.getLocalizedMessage(), null);
		}
	
	}
	
	/**
	 * 代理------》已发货
	 * @param pageIndex
	 * @param pageSize
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "sendList",method=RequestMethod.GET)
	@ResponseBody
	public String sendList(int pageIndex,int pageSize,HttpServletRequest request, HttpServletResponse response){
		try {
			User user = LoginUserUtil.getCurrentUser(request);
			List<Order> orderList = orderService.queryListByStatus(user.getUserPhone(), null, Constant.ORDER_STATUS_SEND, null, null, pageIndex, pageSize);
			return AjaxWebUtil.sendAjaxResponse(request, response, true, "查询成功", orderList);
		}catch(Exception e) {
			return AjaxWebUtil.sendAjaxResponse(request, response, false, "查询失败："+e.getLocalizedMessage(), null);
		}
	}
	
	/**
	 * 代理------》已发货
	 * @param pageIndex
	 * @param pageSize
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "finishedList",method=RequestMethod.GET)
	@ResponseBody
	public String finishedList(int pageIndex,int pageSize,HttpServletRequest request, HttpServletResponse response){
		try {
			User user = LoginUserUtil.getCurrentUser(request);
			List<Order> orderList = orderService.queryListByStatus(user.getUserPhone(), null, Constant.ORDER_STATUS_FINISHED, null, null, pageIndex, pageSize);
			return AjaxWebUtil.sendAjaxResponse(request, response, true, "查询成功", orderList);
		}catch(Exception e) {
			return AjaxWebUtil.sendAjaxResponse(request, response, false, "查询失败："+e.getLocalizedMessage(), null);
		}
	}
	
	/**
	 * 代理------》待处理
	 * 1.待发货
	 * 2.已完成,退货中，或者换货中
	 * @param pageIndex
	 * @param pageSize
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "todoList",method=RequestMethod.GET)
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
	
	
	/**
	 * 代销------》待发货
	 * @param pageIndex
	 * @param pageSize
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "sellertoSendList",method=RequestMethod.GET)
	@ResponseBody
	public String sellertoSendList(int pageIndex,int pageSize,HttpServletRequest request, HttpServletResponse response){
		try {
			//String pageIndex = AjaxWebUtil.getRequestParameter(request,"pageIndex");
			//String pageSize = AjaxWebUtil.getRequestParameter(request,"pageSize");
			User user = LoginUserUtil.getCurrentUser(request);
			List<Order> orderList = orderService.queryListByStatus(null,user.getUserPhone(),  Constant.ORDER_STATUS_TOSEND, null, null, pageIndex, pageSize);
			return AjaxWebUtil.sendAjaxResponse(request, response, true, "查询成功", orderList);
		}catch(Exception e) {
			return AjaxWebUtil.sendAjaxResponse(request, response, false, "查询失败："+e.getLocalizedMessage(), null);
		}
	
	}
	
	/**
	 * 代销------》已发货
	 * @param pageIndex
	 * @param pageSize
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "sellerSendList",method=RequestMethod.GET)
	@ResponseBody
	public String sellerSendList(int pageIndex,int pageSize,HttpServletRequest request, HttpServletResponse response){
		try {
			User user = LoginUserUtil.getCurrentUser(request);
			List<Order> orderList = orderService.queryListByStatus(null, user.getUserPhone(), Constant.ORDER_STATUS_SEND, null, null, pageIndex, pageSize);
			return AjaxWebUtil.sendAjaxResponse(request, response, true, "查询成功", orderList);
		}catch(Exception e) {
			return AjaxWebUtil.sendAjaxResponse(request, response, false, "查询失败："+e.getLocalizedMessage(), null);
		}
	}
	
	/**
	 * 代销------》已完成
	 * @param pageIndex
	 * @param pageSize
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "sellerFinishedList",method=RequestMethod.GET)
	@ResponseBody
	public String sellerFinishedList(int pageIndex,int pageSize,HttpServletRequest request, HttpServletResponse response){
		try {
			User user = LoginUserUtil.getCurrentUser(request);
			List<Order> orderList = orderService.queryListByStatus(null,user.getUserPhone(),  Constant.ORDER_STATUS_FINISHED, null, null, pageIndex, pageSize);
			return AjaxWebUtil.sendAjaxResponse(request, response, true, "查询成功", orderList);
		}catch(Exception e) {
			return AjaxWebUtil.sendAjaxResponse(request, response, false, "查询失败："+e.getLocalizedMessage(), null);
		}
	}
	
	/**
	 * 代销------》待处理
	 * @param pageIndex
	 * @param pageSize
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "sellerTodoList",method=RequestMethod.GET)
	@ResponseBody
	public String sellerTodoList(int pageIndex,int pageSize,HttpServletRequest request, HttpServletResponse response){
		try {
			User user = LoginUserUtil.getCurrentUser(request);
			List<Order> orderList = orderService.queryToDoList(null,user.getUserPhone(),  null, null, pageIndex, pageSize);
			return AjaxWebUtil.sendAjaxResponse(request, response, true, "查询成功", orderList);
		}catch(Exception e) {
			return AjaxWebUtil.sendAjaxResponse(request, response, false, "查询失败："+e.getLocalizedMessage(), null);
		}
	}
	
	/**
	 * 快递列表
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "expressage",method=RequestMethod.GET)
	@ResponseBody
	public String expressage(HttpServletRequest request, HttpServletResponse response){
		List<Expressage> expressage = orderService.queryExpressage();
		return AjaxWebUtil.sendAjaxResponse(request, response, true, "快递列表查询成功", expressage);
	}
	
	/**
	 * 订单详情
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "orderDetail",method=RequestMethod.GET)
	@ResponseBody
	public String getOrderDetail(String code,HttpServletRequest request, HttpServletResponse response){
		//String prmId = AjaxWebUtil.getRequestParameter(request,"id");
		Order order = orderService.getOrderByCode(code);
		if(order == null){
			return AjaxWebUtil.sendAjaxResponse(request, response, false, "订单不存在!", null);
		}
		return AjaxWebUtil.sendAjaxResponse(request, response, true, "订单列表查询成功", order);
	}
	
	/**
	 * 订单发货
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "send",method=RequestMethod.POST)
	@ResponseBody
	public String send(String code,String expressCode,String expressNo,String expressName, HttpServletRequest request, HttpServletResponse response){
		try {
			orderService.updateOrderSend(code, expressCode, expressNo, expressName);
			return AjaxWebUtil.sendAjaxResponse(request, response, true, "发货成功", null);
		}catch(Exception e) {
			e.printStackTrace();
			return AjaxWebUtil.sendAjaxResponse(request, response, false, "发货失败:"+e.getLocalizedMessage(), null);
		}
	}
	
	/**
	 * 订单退货
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "reject",method=RequestMethod.POST)
	@ResponseBody
	public String reject(String code,HttpServletRequest request, HttpServletResponse response){
		try {
			orderService.updateReject(code);
			return AjaxWebUtil.sendAjaxResponse(request, response, true, "退货成功", null);
		}catch(Exception e) {
			e.printStackTrace();
			return AjaxWebUtil.sendAjaxResponse(request, response, false, "退货失败:"+e.getLocalizedMessage(), null);
		}
	}
	
	/**
	 * 订单拒绝退货
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "rejectRefuse",method=RequestMethod.POST)
	@ResponseBody
	public String rejectRefuse(String code,String remark,HttpServletRequest request, HttpServletResponse response){
		try {
			orderService.updateRejectRefuse(code,remark);
			return AjaxWebUtil.sendAjaxResponse(request, response, true, "退货成功", null);
		}catch(Exception e) {
			e.printStackTrace();
			return AjaxWebUtil.sendAjaxResponse(request, response, false, "退货失败:"+e.getLocalizedMessage(), null);
		}
	}
	
	/**
	 * 订单取消
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "cancel",method=RequestMethod.POST)
	@ResponseBody
	public String cancel(String code,HttpServletRequest request, HttpServletResponse response){
		try {
			orderService.updateCancel(code);
			return AjaxWebUtil.sendAjaxResponse(request, response, true, "取消成功", null);
		}catch(Exception e) {
			e.printStackTrace();
			return AjaxWebUtil.sendAjaxResponse(request, response, false, "取消失败:"+e.getLocalizedMessage(), null);
		}
	}
	
	/**
	 * 创建订单
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "addOrder",method=RequestMethod.POST)
	@ResponseBody
	public String addOrder(OrderForm orderForm,HttpServletRequest request, HttpServletResponse response){
		try {
			String ordeNo = orderService.addOrder(orderForm);
			return AjaxWebUtil.sendAjaxResponse(request, response, true, "订单创建成功", ordeNo);
		}catch(Exception e) {
			e.printStackTrace();
			return AjaxWebUtil.sendAjaxResponse(request, response, false, "订单创建失败:"+e.getLocalizedMessage(), null);
		}
	}
	
	
	/**
	 * TODO 订单支付
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "pay",method=RequestMethod.POST)
	@ResponseBody
	public String pay(String code,HttpServletRequest request, HttpServletResponse response){
		try {
			return AjaxWebUtil.sendAjaxResponse(request, response, true, "订单创建成功", null);
		}catch(Exception e) {
			e.printStackTrace();
			return AjaxWebUtil.sendAjaxResponse(request, response, false, "订单创建失败:"+e.getLocalizedMessage(), null);
		}
	}
	
}
