/**
 * 
 */
package com.simple.admin.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.simple.model.ResponseInfo;
import com.simple.model.ResponseStatus;

/**
 * @author hesq1
 * @date 2016年2月18日
 * @desc 
 */
public class AjaxWebUtil{
	
	
	public static void sendAjaxResponse(ServletRequest request, ServletResponse response, Object data,int status){
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		httpServletResponse.setStatus(status);
		sendAjaxResponse(request, response, data);
	}
	
	public static void sendAjaxResponse(ServletRequest request, ServletResponse response, Object data){
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		httpServletResponse.setCharacterEncoding("UTF-8");
		httpServletResponse.setHeader("Content-Type", "application/json;charset=UTF-8");
		PrintWriter out = null;
		try {
			out = httpServletResponse.getWriter();
			String dataStr = JSON.toJSON(data).toString();
			out.println(dataStr);
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if(null != out){
				out.flush();
				out.close();
			}
		}
	}
	
	public static String sendAjaxResponse(ServletRequest request, ServletResponse response,boolean successed,String msg,Object o) {
		//sendAjaxResponse(request,response,new ResponseInfo(new ResponseStatus(successed,msg), o));
		return JSONObject.toJSONString(new ResponseInfo(new ResponseStatus(successed,msg), o));
	}
	
	public static boolean isAjaxRequest(ServletRequest request){
		return "XMLHttpRequest".equalsIgnoreCase(((HttpServletRequest)request).getHeader("X-Requested-With"))?true:false;
	}
	
	public static String getUri(String url){
//		String pattern = "^((http[s]?|ftp):\\/)?\\/?([^:\\/\\s]+)((\\/\\w+)*\\/)([\\w\\-\\.]+[^#?\\s]+)(.*)?(#[\\w\\-]+)?$";
//		String pattern = "^(.*:)\\/\\/([A-Za-z0-9\\-\\.]+)(:[0-9]+)?(.*)$";
		String pattern = "^((http[s]?|ftp):\\/)?\\/?([^:\\/\\s]+)(:([^\\/]*))?((\\/\\w+)*\\/)([\\w\\-\\.]+[^#?\\s]+)(\\?([^#]*))?(#(.*))?$";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(url);
	      if (m.find( )) {
	         String path = m.group(6);
	         String name = m.group(8);
	         return path + name;
	      } else {
	         System.out.println("NO MATCH");
	      }
	      return null;
	}
	
	public static void main(String[] args) {
		System.out.println(getUri(""));
	}
	
	public static String getRequestStream(HttpServletRequest req) {  
        StringBuilder sb = new StringBuilder();  
        try(BufferedReader reader = req.getReader();) {  
                 char[]buff = new char[1024];  
                 int len;  
                 while((len = reader.read(buff)) != -1) {  
                          sb.append(buff,0, len);  
                 }  
        }catch (IOException e) {  
                 e.printStackTrace();  
        }  
        return sb.toString();  
	} 
	
	public static String getRequestParameter(HttpServletRequest req,String name) {
		return getRequestParameter(req,name,false);
	}
	
	public static String getRequestParameter(HttpServletRequest req,String name,boolean decoder) {
		String r = req.getParameter(name);
		if ( null != r) {
			if (decoder) {
				try {
					return URLDecoder.decode(r,"utf-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					return r;
				}
			}else {
				return r;
			}
		}
		Map res = req.getParameterMap();
		if ( null != res) {
			for (Iterator<String> it = res.keySet().iterator();it.hasNext();) {
				String key = it.next();
				Object o = res.get(key);
				if ( key.equals(name) ) {
					if (null != o) {
						if (o instanceof String) {
							if (decoder) {
								try {
									return URLDecoder.decode((String) o,"utf-8");
								} catch (UnsupportedEncodingException e) {
									e.printStackTrace();
									return null;
								}
							}
							return (String) o;
						}else if(o instanceof String[]) {
							String[] a = (String[]) o;
							if (decoder) {
								try {
									return URLDecoder.decode(a[0],"utf-8");
								} catch (UnsupportedEncodingException e) {
									e.printStackTrace();
									return null;
								}
							}
							return a[0];
						}
					}
				}
			}
		}
		return null;
	}
	
	public static Object getRequestJsonParamer(HttpServletRequest req,String name) {
		String json = getRequestStream(req);
		System.out.println("json>>>>>"+json);
		try {
			JSONObject o = JSON.parseObject(json);
			return o.get(name);
		}catch(Exception e) {
		}
		return null;
	}
}
