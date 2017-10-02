package com.epam.spring.authenticate.impl;

import com.epam.spring.authenticate.BaseCredentialsToken;
import com.epam.spring.config.HttpCredentials;
import com.epam.spring.config.Krb5Credentials;
import com.epam.spring.config.SshCredentials;
import org.springframework.security.core.Authentication;

public class BaseConfigLoadAuthentication extends BaseCredentialsToken {
    private HttpCredentials httpCredentials;
    private Krb5Credentials krb5Credentials;
    private SshCredentials sshCredentials;

    public BaseConfigLoadAuthentication(HttpCredentials httpCredentials, Krb5Credentials krb5Credentials, SshCredentials sshCredentials) {
        this.httpCredentials = httpCredentials;
        this.krb5Credentials = krb5Credentials;
        this.sshCredentials = sshCredentials;
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
