package com.epam.shim.configurator.profile;

import com.epam.loader.config.credentials.EmrCredentials;
import com.epam.loader.config.credentials.HttpCredentials;
import com.epam.loader.config.credentials.Krb5Credentials;
import com.epam.loader.config.credentials.SshCredentials;
import com.epam.loader.plan.manager.LoadConfigsManager;

public class Profile {
  private Krb5Credentials krb5Credentials;
  private SshCredentials sshCredentials;
  private HttpCredentials httpCredentials;
  private LoadConfigsManager.ClusterType clusterType;
  private String pathToShim;
  private String dfsInstallDir;
  private String hosts;
  private String name;
  private EmrCredentials emrCredentials;
  private String pathToTestProperties;
  private String namedClusterName;

  public EmrCredentials getEmrCredentials() {
    return emrCredentials;
  }

  public void setEmrCredentials( EmrCredentials emrCredentials ) {
    this.emrCredentials = emrCredentials;
  }

  public Profile( Krb5Credentials krb5Credentials, SshCredentials sshCredentials,
                  HttpCredentials httpCredentials,
                  LoadConfigsManager.ClusterType clusterType, String pathToShim, String dfsInstallDir,
                  String hosts, String name, EmrCredentials emrCredentials, String pathToTestProperties,
                  String namedClusterName ) {
    this.krb5Credentials = krb5Credentials;
    this.sshCredentials = sshCredentials;
    this.httpCredentials = httpCredentials;
    this.clusterType = clusterType;
    this.pathToShim = pathToShim;
    this.dfsInstallDir = dfsInstallDir;
    this.hosts = hosts;
    this.name = name;
    this.emrCredentials = emrCredentials;
    this.pathToTestProperties = pathToTestProperties;
    this.namedClusterName = namedClusterName;
  }

  public String getPathToTestProperties() {
    return pathToTestProperties;
  }

  public void setPathToTestProperties( String pathToTestProperties ) {
    this.pathToTestProperties = pathToTestProperties;
  }

  public String getName() {
    return name;
  }

  public void setName( String name ) {
    this.name = name;
  }

  public Krb5Credentials getKrb5Credentials() {
    return krb5Credentials;
  }

  public void setKrb5Credentials( Krb5Credentials krb5Credentials ) {
    this.krb5Credentials = krb5Credentials;
  }

  public SshCredentials getSshCredentials() {
    return sshCredentials;
  }

  public void setSshCredentials( SshCredentials sshCredentials ) {
    this.sshCredentials = sshCredentials;
  }

  public HttpCredentials getHttpCredentials() {
    return httpCredentials;
  }

  public void setHttpCredentials( HttpCredentials httpCredentials ) {
    this.httpCredentials = httpCredentials;
  }

  public LoadConfigsManager.ClusterType getClusterType() {
    return clusterType;
  }

  public void setClusterType( LoadConfigsManager.ClusterType clusterType ) {
    this.clusterType = clusterType;
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

  public String getHosts() {
    return hosts;
  }

  public void setHosts( String hosts ) {
    this.hosts = hosts;
  }

  public String getNamedClusterName() {
    return namedClusterName;
  }

  public void setNamedClusterName( String namedClusterName ) {
    this.namedClusterName = namedClusterName;
  }
}
