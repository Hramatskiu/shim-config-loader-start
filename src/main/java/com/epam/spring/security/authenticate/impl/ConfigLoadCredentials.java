package com.epam.spring.security.authenticate.impl;

import com.epam.loader.config.credentials.SshCredentials;
import com.epam.spring.security.authenticate.BaseCredentialsToken;
import com.epam.spring.security.authenticate.IHttpCredentials;
import com.epam.spring.security.authenticate.IKerberosAuthentication;
import com.epam.spring.security.authenticate.ISshCredentials;
import org.apache.http.client.CredentialsProvider;

import java.util.ArrayList;
import java.util.List;

public class ConfigLoadCredentials extends BaseCredentialsToken implements IHttpCredentials,
  ISshCredentials, IKerberosAuthentication {
  private CredentialsProvider credentialsProvider;
  private SshCredentials sshCredentials;
  private List<String> authShemes;
  private boolean kerberosAuth;

  public ConfigLoadCredentials() {
    credentialsProvider = null;
    sshCredentials = null;
    authShemes = new ArrayList<>();
    kerberosAuth = false;
  }

  @Override
  public CredentialsProvider getCredentialsProvider() {
    return credentialsProvider;
  }

  @Override
  public void setCredentialsProvider( CredentialsProvider credentialsProvider ) {
    this.credentialsProvider = credentialsProvider;
  }

  @Override
  public List<String> getAuthShemes() {
    return authShemes;
  }

  @Override
  public void setAuthShemes( List<String> authShemes ) {
    this.authShemes = authShemes;
  }

  @Override
  public SshCredentials getSshCredentials() {
    return sshCredentials;
  }

  @Override
  public void setSshCredentials( SshCredentials sshCredentials ) {
    this.sshCredentials = sshCredentials;
  }

  @Override public void setKerberosAuth( boolean isKerberos ) {
    this.kerberosAuth = isKerberos;
  }

  @Override public boolean isKerberosSet() {
    return kerberosAuth;
  }
}
