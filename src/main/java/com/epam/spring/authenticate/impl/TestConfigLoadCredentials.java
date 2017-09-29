package com.epam.spring.authenticate.impl;

import com.epam.spring.authenticate.BaseCredentialsToken;
import com.epam.spring.authenticate.HttpCredentials;
import com.epam.spring.authenticate.KerberosCredentials;
import com.epam.spring.authenticate.SshCredentials;
import org.apache.http.impl.client.CloseableHttpClient;

import javax.security.auth.Subject;

public class TestConfigLoadCredentials extends BaseCredentialsToken implements HttpCredentials, KerberosCredentials, SshCredentials {
    private CloseableHttpClient closeableHttpClient;
    private Subject krb5Subject;

    public TestConfigLoadCredentials() {
        closeableHttpClient = null;
        krb5Subject = null;
    }

    @Override
    public void setHttpClient(CloseableHttpClient httpClient) {
        closeableHttpClient = httpClient;
    }

    @Override
    public void setKrbSubject(Subject krb5subject) {
        this.krb5Subject = krb5subject;
    }

    @Override
    public CloseableHttpClient getHttpClient() {
        return closeableHttpClient;
    }

    @Override
    public Subject getKrb5Subject() {
        return krb5Subject;
    }

    @Override
    public void getSshSession() {

    }
}
