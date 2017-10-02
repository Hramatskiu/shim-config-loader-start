package com.epam.spring.authenticate.impl;

import com.epam.spring.authenticate.BaseCredentialsToken;
import com.epam.spring.authenticate.IHttpCredentials;
import com.epam.spring.authenticate.IKerberosCredentials;
import com.epam.spring.authenticate.ISshCredentials;
import com.epam.spring.config.SshCredentials;
import org.apache.http.client.CredentialsProvider;

import javax.security.auth.Subject;
import java.util.ArrayList;
import java.util.List;

public class TestConfigLoadCredentials extends BaseCredentialsToken implements IHttpCredentials, IKerberosCredentials, ISshCredentials {
    private CredentialsProvider credentialsProvider;
    private Subject krb5Subject;
    private SshCredentials sshCredentials;
    private List<String> authShemes;

    public TestConfigLoadCredentials() {
        credentialsProvider = null;
        krb5Subject = null;
        sshCredentials = null;
        authShemes = new ArrayList<>();
    }

    @Override
    public CredentialsProvider getCredentialsProvider() {
        return credentialsProvider;
    }

    @Override
    public void setCredentialsProvider(CredentialsProvider credentialsProvider) {
        this.credentialsProvider = credentialsProvider;
    }

    @Override
    public void setKrbSubject(Subject krb5subject) {
        this.krb5Subject = krb5subject;
    }

    @Override
    public Subject getKrb5Subject() {
        return krb5Subject;
    }

    @Override
    public List<String> getAuthShemes() {
        return authShemes;
    }

    @Override
    public void setAuthShemes(List<String> authShemes) {
        this.authShemes = authShemes;
    }

    @Override
    public SshCredentials getSshSession() {
        return sshCredentials;
    }

    @Override
    public void setSshCredentials(SshCredentials sshCredentials) {
        this.sshCredentials = sshCredentials;
    }
}
