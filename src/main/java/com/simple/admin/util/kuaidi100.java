package com.simple.admin.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import com.ruanwei.tool.SmsClient;
import com.ruanwei.tool.SmsClientAccessTool;


public class kuaidi100
{
	
	public static void getUrl(String com,String no)
	{
		try
		{
			URL url= new URL("http://www.kuaidi100.com/applyurl?key=a1aadced81e7f03d&com="+com+"&nu="+no);
			URLConnection con=url.openConnection();
			 con.setAllowUserInteraction(false);
			   InputStream urlStream = url.openStream();
			   String type = con.guessContentTypeFromStream(urlStream);
			   String charSet=null;
			   if (type == null)
			    type = con.getContentType();

			   if (type == null || type.trim().length() == 0 || type.trim().indexOf("text/html") < 0)
			    return ;

			   if(type.indexOf("charset=") > 0)
			    charSet = type.substring(type.indexOf("charset=") + 8);

			   byte b[] = new byte[10000];
			   int numRead = urlStream.read(b);
			  String content = new String(b, 0, numRead);
			   while (numRead != -1) {
			    numRead = urlStream.read(b);
			    if (numRead != -1) {
			     //String newContent = new String(b, 0, numRead);
			     String newContent = new String(b, 0, numRead, charSet);
			     content += newContent;
			    }
			   }
			   System.out.println("content:" + content);
			   urlStream.close();
		} catch (MalformedURLException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		String result = SmsClientAccessTool.getInstance().doAccessHTTPGet("http://www.kuaidi100.com/applyurl?key=a1aadced81e7f03d&com=shunfeng&nu=950265732230", null);
		System.out.println(result);
	}
	
}
