package com.epam.shim.configurator.cluster;

public class NamedClusterProperty {
  private String host;
  private String port;

  public NamedClusterProperty( String host, String port ) {
    this.host = host;
    this.port = port;
  }

  public String getHost() {
    return host;
  }

  public void setHost( String host ) {
    this.host = host;
  }

  public String getPort() {
    return port;
  }

  public void setPort( String port ) {
    this.port = port;
  }
}
