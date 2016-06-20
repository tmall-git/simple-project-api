package com.simple.admin.util;

import org.apache.log4j.Logger;

/**
 * @description HttpClient 的工具类
 * @author qinhc123
 * @2015下午6:03:09
 */
public class HttpClientUtil {

	private static Logger logger = Logger.getLogger(HttpClientUtil.class);

	/**
	 * @description 发送httpClient post 请求，json 格式返回
	 * @author qinhc
	 * @2015下午6:03:09
	 * @param url
	 * @param body
	 * @return
	 * @throws Exception
	 */
//	public static String executeHttpPost(String url, String body)
//			throws Exception {
//		DefaultHttpClient httpClient = new DefaultHttpClient();
//		HttpPost method = new HttpPost(url);
//		if(null != body && !"".equals(body)){
//			StringEntity entity = new StringEntity(body, "utf-8");// 解决中文乱码问题
//			entity.setContentEncoding("UTF-8");
//			entity.setContentType("application/json");
//			method.setEntity(entity);
//		}
//		String resData = "";
//		// 请求超时
//		httpClient.getParams().setParameter(
//				CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
//		// 读取超时
//		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
//				20000);
//		try {
//			HttpResponse result = httpClient.execute(method);
//			// 请求结束，返回结果
//			resData = EntityUtils.toString(result.getEntity());
//			logger.info("订单消息返回的数据：" + resData);
//		} catch (Exception e) {
//			logger.error("订单消息发送请求出错：" + e.getMessage());
//		} finally {
//			method.releaseConnection();
//		}
//		return resData;
//	}
	
	/**
	 * @description
	 * @author qinhc
	 * @2015下午6:00:41
	 * @param url
	 * @param body
	 * @param type 请求的类型
	 * @return
	 * @throws Exception
	 */
//	public static String executeHttpPostType(String url, String body,String type)
//			throws Exception {
//		DefaultHttpClient httpClient = new DefaultHttpClient();
//		HttpPost method = new HttpPost(url);
//		StringEntity entity = new StringEntity(body, "utf-8");// 解决中文乱码问题
//		entity.setContentEncoding("UTF-8");
//		entity.setContentType(type);
//		method.setEntity(entity);
//		String resData = "";
//		// 请求超时
//		httpClient.getParams().setParameter(
//				CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
//		// 读取超时
//		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
//				20000);
//		try {
//			HttpResponse result = httpClient.execute(method);
//			// 请求结束，返回结果
//			resData = EntityUtils.toString(result.getEntity());
//			logger.info("订单消息返回的数据：" + resData);
//		} catch (Exception e) {
//			logger.error("订单消息发送请求出错：" + e.getMessage());
//		} finally {
//			method.releaseConnection();
//		}
//		return resData;
//	}
}
