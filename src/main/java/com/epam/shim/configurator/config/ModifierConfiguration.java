package com.epam.shim.configurator.config;

import org.apache.commons.lang.StringUtils;

public class ModifierConfiguration {
  private boolean isSecure;
  private String pathToShim;
  private String pathToTestProperties;
  private String dfsInstallDir;

  public ModifierConfiguration( String pathToShim, String dfsInstallDir, String pathToTestProperties,
                                boolean isSecure ) {
    this.dfsInstallDir = dfsInstallDir;
    this.pathToShim = pathToShim;
    this.pathToTestProperties = pathToTestProperties;
    this.isSecure = isSecure;
  }

  public ModifierConfiguration( String pathToShim, String dfsInstallDir, String pathToTestProperties ) {
    this( pathToShim, dfsInstallDir, pathToTestProperties, false );
  }

  public String getPathToTestProperties() {
    return pathToTestProperties;
  }

  public void setPathToTestProperties( String pathToTestProperties ) {
    this.pathToTestProperties = pathToTestProperties;
  }

  public ModifierConfiguration( String pathToShim ) {
    this( pathToShim, StringUtils.EMPTY, StringUtils.EMPTY, false );
  }

  public boolean isSecure() {
    return isSecure;
  }

  public void setSecure( boolean secure ) {
    isSecure = secure;
  }

  public String getPathToShim() {
    return pathToShim;
  }

  public void setPathToShim( String pathToShim ) {
    this.pathToShim = pathToShim;
  }

  public String getDfsInstallDir() {
    return dfsInstallDir;
  }

  public void setDfsInstallDir( String dfsInstallDir ) {
    this.dfsInstallDir = dfsInstallDir;
  }
}
