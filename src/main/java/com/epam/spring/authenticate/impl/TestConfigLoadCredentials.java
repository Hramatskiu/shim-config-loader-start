package com.epam.spring.authenticate.impl;

import com.epam.spring.authenticate.BaseCredentialsToken;
import com.epam.spring.authenticate.IHttpCredentials;
import com.epam.spring.authenticate.ISshCredentials;
import com.epam.spring.config.SshCredentials;
import org.apache.http.client.CredentialsProvider;

import java.util.ArrayList;
import java.util.List;

public class TestConfigLoadCredentials extends BaseCredentialsToken implements IHttpCredentials, ISshCredentials {
  private CredentialsProvider credentialsProvider;
  private SshCredentials sshCredentials;
  private List<String> authShemes;

  public TestConfigLoadCredentials() {
    credentialsProvider = null;
    sshCredentials = null;
    authShemes = new ArrayList<>();
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
}
