package com.simple.admin.util;

import java.util.Random;

/**
 * String工具类
 * @author furunxin
 *
 */
public class StringUtils {
	
	/**
	 * 
	 * @param prefix 前缀
	 * @param count 随机数个数(不能大于interval的长度)
	 * @param interval 随机数取值范围<0~9 a~z A~Z>
	 * @return String
	 */
	public static String generateRandom(String prefix ,String interval, int count)
	{
		StringBuffer sb = new StringBuffer();
		String random = null;
		if( count > 0) 
		{
			Random rd = new Random();
			for( int i=0;i<count;i++ )
			{
	            int num = rd.nextInt(interval.length());
	            sb.append(interval.charAt(num));
	            interval = interval.replace((interval.charAt(num)+""), "");
	        }
			random = prefix+sb.toString();
		}
		return random;
	}
}
