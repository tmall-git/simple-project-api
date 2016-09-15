package com.simple.admin.task;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ruanwei.tool.SmsClient;
import com.simple.admin.util.ProductTokenUtil;
import com.simple.common.config.EnvPropertiesConfiger;
import com.simple.common.util.DateUtil;
import com.simple.constant.Constant;
import com.simple.model.Order;
import com.simple.service.OrderService;

@Component
public class OrderTask {

	@Autowired
	OrderService orderService;
	
	/**
	 * 取消订单  15分钟未付款
	 */
	public void cancelUnPayOrder() {
		String end = DateUtil.date2AllString(DateUtil.getNewDateByMinutes(new Date(), -15));
		List<Order> orders = orderService.queryListByStatus(null, null, Constant.ORDER_STATUS_UNPAY, null, end, 1, 100,false);
		if ( null != orders && orders.size() > 0 ) {
			for ( int i = 0 ; i < orders.size() ; i ++) {
				try {
					orderService.updateCancel(orders.get(i));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		
	}
	
	/**
	 * 取消订单  1天未发货取消订单, 
	 */
	public void cancelToSendOrder() {
		String end = DateUtil.date2AllString(DateUtil.getNewDateByDays(new Date(), -1));
		List<Order> orders = orderService.queryListByStatus(null, null, Constant.ORDER_STATUS_TOSEND, null, end, 1, 100,true);
		if ( null != orders && orders.size() > 0 ) {
			for ( int i = 0 ; i < orders.size() ; i ++) {
				try {
					Order order = orders.get(i);
					orderService.updateCancel(order);
					SmsClient.sendMsg(order.getUser_phone(), "您购买的"+order.getProduct_name()+"商品因卖家未发货,交易自动取消,请点击查看详情"+EnvPropertiesConfiger.getValue("orderDeatilUrl")+ProductTokenUtil.getOrderListToken(order.getUser_phone()));
					SmsClient.sendMsg(order.getOwner(), "您有一笔订单因延期未发货被取消交易,商品名："+order.getProduct_name()+",订单号："+order.getOrder_no());
					SmsClient.sendMsg(order.getSeller(), "您有一笔订单因为卖家延期未发货被取消交易,商品名："+order.getProduct_name()+",订单号："+order.getOrder_no());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	/**
	 * 完成订单  
	 * 7天已发货订单,拒绝退货订单
	 */	
	public void finishOrder() {
		String end = DateUtil.date2AllString(DateUtil.getNewDateByDays(new Date(), -7));
		List<Order> orders = orderService.queryToFinishList(null, null, null, end, 1, 100);
		if ( null != orders && orders.size() > 0 ) {
			for ( int i = 0 ; i < orders.size() ; i ++) {
				try {
					Order order = orders.get(i);
					orderService.updateFinished(order);
					SmsClient.sendMsg(order.getOwner(), "您有一笔已完成的订单"+order.getOrder_no()+",收入为："+order.getAgent_total_charge()+",可即日提现");
					SmsClient.sendMsg(order.getSeller(), "您有一笔已完成的订单"+order.getOrder_no()+",提成为："+order.getSeller_total_charge()+",可即日提现");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	
}
