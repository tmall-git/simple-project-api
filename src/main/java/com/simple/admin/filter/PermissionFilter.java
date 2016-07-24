package com.simple.admin.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple.admin.util.AjaxWebUtil;
import com.simple.admin.util.LoginUserUtil;
import com.simple.admin.util.PatternUtils;
import com.simple.common.config.EnvPropertiesConfiger;
import com.simple.model.ResponseInfo;
import com.simple.model.ResponseStatus;
import com.simple.model.User;

public class PermissionFilter
  implements Filter
{
  private Logger logger = LoggerFactory.getLogger(PermissionFilter.class);

  public List<String> notIncludes = new ArrayList<String>();

  public void destroy()
  {
  }

  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
    throws IOException, ServletException
  {
    HttpServletRequest request = (HttpServletRequest)servletRequest;
    HttpServletResponse response = (HttpServletResponse)servletResponse;

    String currURI = request.getRequestURI();

    if (isExclude(currURI)) {
      this.logger.info("requesturl skip");
      chain.doFilter(servletRequest, servletResponse);
    }
    else {
      //判断用户是否已经登录，已经登录的跳转，没有登录跳转到登录页面
      User user = LoginUserUtil.getCurrentUser(request);
      if ( null == user ) {
    	  redirectLogin(request,response);
    	  //chain.doFilter(servletRequest, servletResponse); 
      }else {
    	  chain.doFilter(servletRequest, servletResponse); 
      }
    }
  }

  public void init(FilterConfig filterConfig)
    throws ServletException
  {
    this.logger.info("AdminClientFilter.init");
    initProperties();
  }

  private boolean isExclude(String currURI)
  {
    boolean rs = false;
    PatternUtils sp;
    if ((StringUtils.isNotBlank(currURI)) && (this.notIncludes.size() > 0)) {
      sp = new PatternUtils(currURI);
      for (String uriStr : this.notIncludes) {
        PatternUtils up = new PatternUtils(uriStr);
        if (up.implies(sp)) {
          rs = true;
          break;
        }
      }
    }
    return rs;
  }

  private void initProperties()
  {
	String euri = EnvPropertiesConfiger.getValue("permission.exclusive.uri");
    if (StringUtils.isNotBlank(euri)) {
      String[] uris = euri.split(",");
      for (String str : uris) {
        if (StringUtils.isNotBlank(str)) {
          this.notIncludes.add(str);
        }
      }
    }
    this.logger.info("initProperties EXCLUSIVE_URI:{} ", euri);
  }

  private void redirectLogin(HttpServletRequest request, HttpServletResponse response) {
//	  try {
//		response.sendRedirect(EnvPropertiesConfiger.getValue("redirectpage"));
//	} catch (IOException e) {
//		e.printStackTrace();
//	}
	if (!AjaxWebUtil.isAjaxRequest(request)) {
		try {
			response.sendRedirect(EnvPropertiesConfiger.getValue("redirectpage"));
		} catch (IOException e) {
			this.logger.error(e.getMessage(), e);
		}
	} else {
		AjaxWebUtil.sendAjaxResponse(request, response,new ResponseInfo(new ResponseStatus(false,ResponseStatus.REDIRECT,"请重新登录"), null));
	}
  }
}