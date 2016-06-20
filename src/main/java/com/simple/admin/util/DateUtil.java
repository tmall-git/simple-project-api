package com.simple.admin.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	public static String date2String(Date date) {
		return new SimpleDateFormat("yyyy-MM-dd").format(date);
	}
}
