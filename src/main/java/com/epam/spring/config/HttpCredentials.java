package com.epam.spring.config;

import org.apache.commons.lang.StringUtils;

public class HttpCredentials {
  private String username;
  private String password;

  //For test
  public HttpCredentials() {
    this( StringUtils.EMPTY, StringUtils.EMPTY );
  }

  public HttpCredentials( String username, String password ) {
    this.username = username;
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername( String username ) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword( String password ) {
    this.password = password;
  }
}
