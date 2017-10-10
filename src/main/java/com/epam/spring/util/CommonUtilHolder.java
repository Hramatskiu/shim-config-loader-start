package com.epam.spring.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommonUtilHolder {
  private static HttpCommonUtil httpCommonUtil;
  private static SshCommonUtil sshCommonUtil;

  @Autowired
  public CommonUtilHolder( HttpCommonUtil httpCommonUtil, SshCommonUtil sshCommonUtil ) {
    CommonUtilHolder.httpCommonUtil = httpCommonUtil;
    CommonUtilHolder.sshCommonUtil = sshCommonUtil;
  }

  public static HttpCommonUtil httpCommonUtilInstance() {
    return httpCommonUtil;
  }

  public static SshCommonUtil sshCommonUtilInstance() {
    return sshCommonUtil;
  }
}
