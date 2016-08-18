package com.simple.admin.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.ruanwei.tool.SmsClient;
import com.ruanwei.tool.SmsClientAccessTool;
import com.simple.admin.util.AjaxWebUtil;
import com.simple.admin.util.LoginUserUtil;
import com.simple.admin.util.ProductTokenUtil;
import com.simple.common.config.EnvPropertiesConfiger;
import com.simple.constant.Constant;
import com.simple.model.Expressage;
import com.simple.model.Order;
import com.simple.model.OrderForm;
import com.simple.model.ResponseInfo;
import com.simple.model.ResponseStatus;
import com.simple.model.User;
import com.simple.service.OrderService;
import com.simple.service.UserService;
import com.simple.weixin.auth.OAuthAccessToken;
import com.simple.weixin.auth.WeiXinAuth;
import com.simple.weixin.pay.WeiXinPay;
import com.simple.weixin.pay.WeiXinPayConfig;
import com.simple.weixin.pay.WeiXinPrePayResult;

@Controller
@RequestMapping("/order")
public class OrderController {

	@Autowired
	OrderService orderService;
	@Autowired
	UserService userService;
	
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
		order.setProductToken(ProductTokenUtil.getToken(order.getProduct_id(), order.getSeller()));
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
			Order order = orderService.updateOrderSend(code, expressCode, expressNo, expressName);
			SmsClient.sendMsg(order.getSeller(), "您代销的商品"+order.getProduct_name()+"已发货,订单号为["+order.getOrder_no()+"]");
			SmsClient.sendMsg(order.getUser_phone(), "您购买的商品"+order.getProduct_name()+"已发货.请点击查看详情"+EnvPropertiesConfiger.getValue("orderDeatilUrl")+order.getOrder_no());
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
			Order order = orderService.updateReject(code);
			SmsClient.sendMsg(order.getSeller(), "您有一笔退货申请,商品名:"+order.getProduct_name()+",订单号["+order.getOrder_no()+"]请72小时内及时处理.");
			SmsClient.sendMsg(order.getOwner(), "您代理的商品有一笔退货申请,商品名:"+order.getProduct_name()+",订单号["+order.getOrder_no()+"],请协调买卖双方妥善处理.");
			return AjaxWebUtil.sendAjaxResponse(request, response, true, "退货成功", null);
		}catch(Exception e) {
			e.printStackTrace();
			return AjaxWebUtil.sendAjaxResponse(request, response, false, "退货失败:"+e.getLocalizedMessage(), null);
		}
	}
	
	/**
	 * 订单退货成功
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "rejectSuccess",method=RequestMethod.POST)
	@ResponseBody
	public String rejectSuccess(String code,HttpServletRequest request, HttpServletResponse response){
		try {
			Order order = orderService.updateRejectSuccess(code);
			SmsClient.sendMsg(order.getUser_phone(), "您购买的"+order.getProduct_name()+"商品已退货成功,请点击查看详情"+EnvPropertiesConfiger.getValue("orderDeatilUrl")+order.getOrder_no());
			SmsClient.sendMsg(order.getSeller(), "您代销的"+order.getProduct_name()+"商品退货申请已经批准,订单号["+order.getOrder_no()+"],该笔交易无提成.");
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
			Order order = orderService.updateRejectRefuse(code,remark);
			SmsClient.sendMsg(order.getUser_phone(), "您的退货申请被拒绝,请点击查看详情"+EnvPropertiesConfiger.getValue("orderDeatilUrl")+order.getOrder_no());
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
			Order order = orderService.updateCancel(code);
			SmsClient.sendMsg(order.getSeller(), "订单["+order.getOrder_no()+"]已取消.");
			SmsClient.sendMsg(order.getUser_phone(), "订单["+order.getOrder_no()+"]已取消.");
			SmsClient.sendMsg(order.getOwner(), "订单["+order.getOrder_no()+"]已取消.");
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
			if ( null == orderForm.getToken() ) {
				return AjaxWebUtil.sendAjaxResponse(request, response, false, "订单创建失败:无token", null);
			}
			String token = orderForm.getToken();
			String phone = ProductTokenUtil.validToken(orderForm.getProductId(), token);
			if ( null == phone) {
				return AjaxWebUtil.sendAjaxResponse(request, response, false, "订单创建失败:token无效", null);
			}
			User seller = userService.queryByPhone(phone);
			if ( null == seller ) {
				return AjaxWebUtil.sendAjaxResponse(request, response, false, "订单创建失败:代销不存在", null);
			}
			String ordeNo = orderService.addOrder(orderForm,phone);
			return AjaxWebUtil.sendAjaxResponse(request, response, true, "订单创建成功", ordeNo);
		}catch(Exception e) {
			e.printStackTrace();
			return AjaxWebUtil.sendAjaxResponse(request, response, false, "订单创建失败:"+e.getLocalizedMessage(), null);
		}
	}
	
	/**
	 * 跳转到微信授权，然后回跳到微信支付页面
	 * @param code
	 * @param token
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "toPayPage",method=RequestMethod.GET)
	public String toPayPage(String code,String token,HttpServletRequest request, HttpServletResponse response) {
		String url = WeiXinAuth.getAuthUrl(String.format(EnvPropertiesConfiger.getValue("redirectUrl"),code,token), false, "");
		return "redirect:"+url;
	}
	
	/**
	 * TODO 订单支付
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "pay",method=RequestMethod.GET)
	public String pay(String code,String orderCode,String orderToken,HttpServletRequest request, HttpServletResponse response){
		try {
			//查询订单金额，跳转微信支付
			OAuthAccessToken authToken = WeiXinAuth.getOAuthAccessToken(code);
			if ( null == authToken) {
				return AjaxWebUtil.sendAjaxResponse(request, response, false, "订单支付失败：未获取到微信信息，请在微信中打开.", null);
			}
			Order order = orderService.getOrderByCode(orderCode);
			if (order.getOrder_status()!=Constant.ORDER_STATUS_UNPAY) {
				return JSONObject.toJSONString(new ResponseInfo(new ResponseStatus(false,"4","订单已经支付"), ProductTokenUtil.getOrderListToken(order.getUser_phone())));
			}
			return "redirect:"+String.format(EnvPropertiesConfiger.getValue("confirmOrderUrl"), orderCode,orderToken,authToken.getOpenid());
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
	@RequestMapping(value = "payConfig",method=RequestMethod.POST)
	@ResponseBody
	public String payConfig(String openId,String code,HttpServletRequest request, HttpServletResponse response){
		try {
			if (StringUtils.isEmpty(openId)) {
				return AjaxWebUtil.sendAjaxResponse(request, response, false, "openId不能为空", null);
			}
			Order order = orderService.getOrderByCode(code);
			if ( null == order ) {
				return AjaxWebUtil.sendAjaxResponse(request, response, false, "订单号无效", null);
			}
			if (order.getOrder_status()!=Constant.ORDER_STATUS_UNPAY) {
				return JSONObject.toJSONString(new ResponseInfo(new ResponseStatus(false,"4","订单已经支付"), ProductTokenUtil.getOrderListToken(order.getUser_phone())));
			}
			WeiXinPrePayResult wppr = WeiXinPay.pay(request, openId, code, order.getTotal_price());
			WeiXinPayConfig wc = WeiXinPay.getPayConfig(wppr, request);
			return AjaxWebUtil.sendAjaxResponse(request, response, true, "获取支付配置成功", wc);
		}catch(Exception e) {
			e.printStackTrace();
			return AjaxWebUtil.sendAjaxResponse(request, response, false, "获取支付配置成功:"+e.getLocalizedMessage(), null);
		}
	}
	
	/**
	 * 订单支付成功
	 * 更新订单状态为待发货，支付时间，支付微信号
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "paySuccess",method=RequestMethod.POST)
	@ResponseBody
	public String paySuccess(String code,HttpServletRequest request, HttpServletResponse response){
		try {
			//更新订单信息，并返回列表token
			Order order = orderService.updateOrderPaySuccess(code);
			String token = ProductTokenUtil.getOrderListToken(order.getUser_phone());
			SmsClient.sendMsg(order.getUser_phone(), "您购买的商品["+order.getProduct_name()+"]已完成支付，订单号为["+order.getOrder_no()+"].点击查看详情"+EnvPropertiesConfiger.getValue("orderDeatilUrl")+order.getOrder_no());
			SmsClient.sendMsg(order.getOwner(), "有用户购买了您代理的商品["+order.getProduct_name()+"]"+order.getProduct_count()+"个,订单号["+order.getOrder_no()+"].请于24小时内发货,否则交易将自动取消,收入"+order.getAgent_total_charge()+"元.");
			SmsClient.sendMsg(order.getSeller(), "有用户购买了您代销的商品["+order.getProduct_name()+"]"+order.getProduct_count()+"个,订单号["+order.getOrder_no()+"].请及时联系代理["+order.getOwner()+"]发货,收入"+order.getSeller_total_charge()+"元.");
			return AjaxWebUtil.sendAjaxResponse(request, response, true, "订单支付成功", token);
		}catch(Exception e) {
			e.printStackTrace();
			return AjaxWebUtil.sendAjaxResponse(request, response, false, "订单支付失败:"+e.getLocalizedMessage(), null);
		}
	}
	
	/**
	 * 微信支付成功回调地址
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "weixinPaySuccessCallBack",method=RequestMethod.GET)
	@ResponseBody
	public String weixinPaySuccessCallBack(HttpServletRequest request, HttpServletResponse response){
		try {
			//更新订单信息，并返回列表token
			WeiXinPay.paySuccess(request, response);
			return AjaxWebUtil.sendAjaxResponse(request, response, true, "订单支付成功", null);
		}catch(Exception e) {
			e.printStackTrace();
			return AjaxWebUtil.sendAjaxResponse(request, response, false, "订单支付失败:"+e.getLocalizedMessage(), null);
		}
	}
	
	/**
	 * 我的订单
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "myOrders",method=RequestMethod.GET)
	@ResponseBody
	public String myOrders(String token,int pageIndex,int pageSize,HttpServletRequest request, HttpServletResponse response){
		try {
			//更新订单信息，并返回列表token
			String phone = ProductTokenUtil.validOrderListToken(token);
			List<Order> orders =  orderService.queryMyOrders(phone, pageIndex, pageSize);
			return AjaxWebUtil.sendAjaxResponse(request, response, true, "查询订单成功", orders);
		}catch(Exception e) {
			e.printStackTrace();
			return AjaxWebUtil.sendAjaxResponse(request, response, false, "查询订单失败:"+e.getLocalizedMessage(), null);
		}
	}
	
	private static final String KUAIDI_URL = EnvPropertiesConfiger.getValue("kuaidiUrl");
	private static final String KUAIDI_ID = EnvPropertiesConfiger.getValue("kuaidiId");
	private static final String KUAIDI_PARAM ="?id="+KUAIDI_ID+"&com=%s&nu=%s&show=2&muti=1&order=asc";
	/**
	 * 查询物流信息
	 * @param request
	 * @param response
	 * @return
	 */
//	@RequestMapping(value = "orderExpressage",method=RequestMethod.GET)
//	@ResponseBody
//	public String orderExpressage(String code,HttpServletRequest request, HttpServletResponse response){
//		try {
//			//
//			Order order = orderService.getOrderByCode(code);
//			String com = order.getExpressage();
//			String nu = order.getExpressage_no();
//			String result = SmsClientAccessTool.getInstance().doAccessHTTPPost(KUAIDI_URL, String.format(KUAIDI_PARAM, com,nu), null);
//			return AjaxWebUtil.sendAjaxResponse(request, response, true, "查询订单成功", result);
//		}catch(Exception e) {
//			e.printStackTrace();
//			return AjaxWebUtil.sendAjaxResponse(request, response, false, "查询订单失败:"+e.getLocalizedMessage(), null);
//		}
//	}
	/**
	 * 订单物流信息
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "orderExpressage",method=RequestMethod.GET)
	@ResponseBody
	public String logistics(String code,HttpServletRequest request, HttpServletResponse response){
		try {
			Order order = orderService.getOrderByCode(code);
			if (null == order) {
				return AjaxWebUtil.sendAjaxResponse(request, response, false, "订单不存在", null);
			}
			String url = SmsClientAccessTool.getInstance()
			.doAccessHTTPGet("http://www.kuaidi100.com/applyurl?key="+KUAIDI_ID+"&com="+
			order.getExpressage()+"&nu="+order.getExpressage_no(), null);
			return AjaxWebUtil.sendAjaxResponse(request, response, true, "查询成功", url);
		}catch(Exception e) {
			e.printStackTrace();
			return AjaxWebUtil.sendAjaxResponse(request, response, false, "查询失败:"+e.getLocalizedMessage(), null);
		}
	}
}
