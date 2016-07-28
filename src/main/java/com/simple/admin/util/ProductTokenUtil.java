package com.simple.admin.util;

import com.simple.common.util.Base64;

public class ProductTokenUtil {

	public static String getToken(int id,String phone) {
		return Base64.getBase64("P"+id+"S"+phone);
	}
	
	/**
	 * 校验token规则，如果合规，返回解析出来的phone
	 * @param id
	 * @param token
	 * @return
	 */
	public static String validToken(int id,String token) {
		String tokenstring = Base64.getFromBase64(token);
		if (null == tokenstring || tokenstring.length()<=1) {
			return null;
		}
		String pid = tokenstring.substring(1,tokenstring.indexOf("S"));
		if ( null == pid || pid.length() ==0 ) {
			return null;
		}
		
		boolean pidValid = false;
		try {
			int _pid = Integer.parseInt(pid);
			if (id == _pid) {
				pidValid = true;
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		if (!pidValid) {
			return null;
		}
		try {
			return tokenstring.substring(tokenstring.indexOf("S")+1);
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String getOrderListToken(String phone) {
		return Base64.getBase64("OL"+phone);
	}
	
	public static String validOrderListToken(String token) {
		String tokenstring = Base64.getFromBase64(token);
		if (null == tokenstring || tokenstring.length()<=2) {
			return null;
		}
		return tokenstring.substring(2);
	}
	
	public static void main(String[] args) {
		String token = getToken(1,"18600671341");
		System.out.println(token);
		System.out.println(validToken(1,token));
		String oltoken = getOrderListToken("18600671341");
		System.out.println(oltoken+">>>>"+validOrderListToken("T0wxODYwMDY3MTM0WA"));
	}
	
}
