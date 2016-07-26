package com.simple.admin.task;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.simple.common.util.DateUtil;
import com.simple.constant.Constant;
import com.simple.model.Order;
import com.simple.service.OrderService;

@Component
public class OrderTask {

	@Autowired
	OrderService orderService;
	
	/**
	 * 取消订单  1天未付款
	 */
	public void cancelUnPayOrder() {
		String end = DateUtil.date2AllString(DateUtil.getNewDateByHours(new Date(), -24));
		List<Order> orders = orderService.queryListByStatus(null, null, Constant.ORDER_STATUS_UNPAY, null, end, 1, 100);
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
	 * 取消订单  2天未发货取消订单, 
	 */
	public void cancelToSendOrder() {
		String end = DateUtil.date2AllString(DateUtil.getNewDateByDays(new Date(), -2));
		List<Order> orders = orderService.queryListByStatus(null, null, Constant.ORDER_STATUS_TOSEND, null, end, 1, 100);
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
	 * 完成订单  7天已发货订单
	 */	
	public void finishOrder() {
		String end = DateUtil.date2AllString(DateUtil.getNewDateByDays(new Date(), -7));
		List<Order> orders = orderService.querySendList(null, null, null, end, 1, 100);
		if ( null != orders && orders.size() > 0 ) {
			for ( int i = 0 ; i < orders.size() ; i ++) {
				try {
					orderService.updateFinished(orders.get(i));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	
}
