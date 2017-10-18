package com.epam.loader.config.credentials;

public class EmrCredentials {
  private String accessKey;
  private String secretKey;

  public EmrCredentials( String accessKey, String secretKey ) {
    this.accessKey = accessKey;
    this.secretKey = secretKey;
  }

  public String getAccessKey() {
    return accessKey;
  }

  public void setAccessKey( String accessKey ) {
    this.accessKey = accessKey;
  }

  public String getSecretKey() {
    return secretKey;
  }

  public void setSecretKey( String secretKey ) {
    this.secretKey = secretKey;
  }
}
