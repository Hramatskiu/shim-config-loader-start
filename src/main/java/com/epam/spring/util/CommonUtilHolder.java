package com.epam.spring.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommonUtilHolder {
  private static HttpCommonUtil httpCommonUtil;

  @Autowired
  public CommonUtilHolder( HttpCommonUtil httpCommonUtil ) {
    CommonUtilHolder.httpCommonUtil = httpCommonUtil;
  }

  public static HttpCommonUtil httpCommonUtilInstance() {
    return httpCommonUtil;
  }
}
