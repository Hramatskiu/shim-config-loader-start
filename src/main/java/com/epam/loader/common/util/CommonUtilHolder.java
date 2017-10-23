package com.epam.loader.common.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommonUtilHolder {
  private static HttpCommonUtil httpCommonUtil;
  private static SshCommonUtil sshCommonUtil;

  //  @Autowired
  //  public CommonUtilHolder( HttpCommonUtil httpCommonUtil, SshCommonUtil sshCommonUtil ) {
  //    CommonUtilHolder.httpCommonUtil = httpCommonUtil;
  //    CommonUtilHolder.sshCommonUtil = sshCommonUtil;
  //  }

  @Autowired
  public void setSshCommonUtil( SshCommonUtil sshCommonUtil ) {
    CommonUtilHolder.sshCommonUtil = sshCommonUtil;
  }

  @Autowired
  public void setHttpCommonUtil( HttpCommonUtil httpCommonUtil ) {
    CommonUtilHolder.httpCommonUtil = httpCommonUtil;
  }

  public static HttpCommonUtil httpCommonUtilInstance() {
    return httpCommonUtil;
  }

  public static SshCommonUtil sshCommonUtilInstance() {
    return sshCommonUtil;
  }
}
