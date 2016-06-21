package com.simple.admin.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {

	private static String byteArrayToHexString(byte b[]) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++)
			resultSb.append(byteToHexString(b[i]));

		return resultSb.toString();
	}

	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0)
			n += 256;
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}

	public static String MD5Encode(String origin){
		return MD5Util.MD5Encode(origin, null);
	}
	
	/** 加密规则： 
	 * 按照被加密字符串和盐值字符串拼接的字符串做加密
	 * @param origin 被加密字符串
	 * @param charsetname 加密盐值
	 * @return
	 */
	public static String MD5Encode(String origin, String charsetname) {
		try {
			return charsetname == null?
					byteArrayToHexString(MessageDigest.getInstance("MD5").digest(origin.getBytes()))
					: byteArrayToHexString(MessageDigest.getInstance("MD5").digest((origin + charsetname).getBytes()));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static final String hexDigits[] = { "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
	
	public static void main(String[] args) {
		String a = MD5Util.MD5Encode("admin","123");
		System.out.println(a);
		String a0 = MD5Util.MD5Encode("admin","123");
		System.out.println(a0);
	}

}