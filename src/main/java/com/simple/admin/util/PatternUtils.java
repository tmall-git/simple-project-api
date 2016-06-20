package com.simple.admin.util;

import java.io.PrintStream;

public class PatternUtils
{
  public static AntPathMatcher apm = new AntPathMatcher();
  private String url;

  PatternUtils()
  {
  }

  public PatternUtils(String url)
  {
    this.url = url;
  }

  public boolean implies(PatternUtils p) {
    if (!(p instanceof PatternUtils)) {
      return false;
    }

    PatternUtils sp = p;
    if ((null != this.url) && (null != sp.url)) {
      if (!this.url.startsWith("/")) this.url = ("/" + this.url);
      return apm.match(this.url, sp.url);
    }
    return false;
  }

  public static void main(String[] args)
  {
    PatternUtils up = new PatternUtils("/aa/*/dad/*.do");
    PatternUtils sp = new PatternUtils("/aa/aad/dasd/getHomePage.do");
    System.out.println(up.implies(sp));
  }
}