package com.epam.shim.configurator.config;

import com.epam.loader.plan.manager.LoadConfigsManager;
import org.apache.commons.lang.StringUtils;

public class ModifierConfiguration {
  private boolean isSecure;
  private String pathToShim;
  private String pathToTestProperties;
  private String dfsInstallDir;
  private String hosts;
  private LoadConfigsManager.ClusterType clusterType;

  public ModifierConfiguration( String pathToShim, String dfsInstallDir, String pathToTestProperties,
                                boolean isSecure, LoadConfigsManager.ClusterType clusterType, String hosts ) {
    this.dfsInstallDir = dfsInstallDir;
    this.pathToShim = pathToShim;
    this.pathToTestProperties = pathToTestProperties;
    this.isSecure = isSecure;
    this.clusterType = clusterType;
    this.hosts = hosts;
  }

  public ModifierConfiguration( String pathToShim, String dfsInstallDir, String pathToTestProperties,
                                LoadConfigsManager.ClusterType clusterType, String hosts ) {
    this( pathToShim, dfsInstallDir, pathToTestProperties, false, clusterType, hosts );
  }

  public String getPathToTestProperties() {
    return pathToTestProperties;
  }

  public void setPathToTestProperties( String pathToTestProperties ) {
    this.pathToTestProperties = pathToTestProperties;
  }

  public LoadConfigsManager.ClusterType getClusterType() {
    return clusterType;
  }

  public void setClusterType( LoadConfigsManager.ClusterType clusterType ) {
    this.clusterType = clusterType;
  }

  public String getHosts() {
    return hosts;
  }

  public void setHosts( String hosts ) {
    this.hosts = hosts;
  }

  public ModifierConfiguration( String pathToShim, LoadConfigsManager.ClusterType clusterType, String hosts ) {
    this( pathToShim, StringUtils.EMPTY, StringUtils.EMPTY, false, clusterType, hosts );
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
