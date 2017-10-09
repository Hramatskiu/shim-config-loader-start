package com.epam.spring.config;

import com.epam.spring.manager.LoadConfigsManager;

public class LoadConfigs {
    private HttpCredentials httpCredentials;
    private Krb5Credentials krb5Credentials;
    private SshCredentials sshCredentials;
    private String host;
    private String saveDir;
    private LoadConfigsManager.ClusterType clusterType;

    public LoadConfigs(HttpCredentials httpCredentials, Krb5Credentials krb5Credentials, SshCredentials sshCredentials,
                       String host, String saveDir, LoadConfigsManager.ClusterType clusterType) {
        this.clusterType = clusterType;
        this.saveDir = saveDir;
        this.host = host;
        this.httpCredentials = httpCredentials;
        this.krb5Credentials = krb5Credentials;
        this.sshCredentials = sshCredentials;
    }

    public LoadConfigsManager.ClusterType getClusterType() {
        return clusterType;
    }

    public void setClusterType(LoadConfigsManager.ClusterType clusterType) {
        this.clusterType = clusterType;
    }

    public String getSaveDir() {
        return saveDir;
    }

    public void setSaveDir(String saveDir) {
        this.saveDir = saveDir;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public HttpCredentials getHttpCredentials() {
        return httpCredentials;
    }

    public void setHttpCredentials(HttpCredentials httpCredentials) {
        this.httpCredentials = httpCredentials;
    }

    public Krb5Credentials getKrb5Credentials() {
        return krb5Credentials;
    }

    public void setKrb5Credentials(Krb5Credentials krb5Credentials) {
        this.krb5Credentials = krb5Credentials;
    }

    public SshCredentials getSshCredentials() {
        return sshCredentials;
    }

    public void setSshCredentials(SshCredentials sshCredentials) {
        this.sshCredentials = sshCredentials;
    }
}
