package com.epam.loader.config.credentials;

import org.apache.commons.lang.StringUtils;

public class SshCredentials {
  private String username;
  private String password;
  private String identityPath; //path to pem file

  //For test
  public SshCredentials() {
    this( StringUtils.EMPTY, StringUtils.EMPTY );
  }

  public SshCredentials( String username, String password ) {
    this( username, password, StringUtils.EMPTY );
  }

  public SshCredentials( String username, String password, String identityPath ) {
    this.username = username;
    this.password = password;
    this.identityPath = identityPath;
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

  public String getIdentityPath() {
    return identityPath;
  }

  public void setIdentityPath( String identityPath ) {
    this.identityPath = identityPath;
  }
}
