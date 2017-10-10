package com.epam.spring.authenticate;

import org.apache.http.client.CredentialsProvider;

import java.util.List;

public interface IHttpCredentials {
  CredentialsProvider getCredentialsProvider();

  void setCredentialsProvider( CredentialsProvider credentialsProvider );

  List<String> getAuthShemes();

  void setAuthShemes( List<String> authShemes );
}
