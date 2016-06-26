package com.simple.admin.constant;

public class Constant {

	/*session用户**/
	public static final String CURRENT_USER = "_sys_current_user";
	
	/*产品状态**/
	public static final int PRODUCT_STATUS_CREATE = 1;
	public static final int PRODUCT_STATUS_ONLINE  =2;
	public static final int PRODUCT_STATUS_OFFLINE = 3;
	public static final int PRODUCT_STATUS_DELETE = 4;
	
	/*订单状态**/
	public static final int ORDER_STATUS_TOSEND = 1;
	public static final int ORDER_STATUS_SEND = 2;
	public static final int ORDER_STATUS_FINISHED = 3;
	public static final int ORDER_STATUS_CANCEL = 4;
	
	public static final int ORDER_CHANGE_STATUS_NO = 0;
	public static final int ORDER_CHANGE_STATUS_YES = 1;
	
	public static final int ORDER_REJECT_STATUS_NO = 0;
	public static final int ORDER_REJECT_STATUS_YES = 1;

	public static final int ORDER_PAY_STATUS_UNPAY = 0;
	public static final int ORDER_PAY_STATUS_PAY = 1;
	public static final int ORDER_PAY_STATUS_FAIL = 2;
	
}
