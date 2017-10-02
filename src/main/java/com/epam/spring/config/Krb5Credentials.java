package com.epam.spring.config;

import org.apache.commons.lang.StringUtils;

public class Krb5Credentials {
    private String username;
    private String password;
    private String realm;
    private String keytabLocation;

    //For test
    public Krb5Credentials() {
        this(StringUtils.EMPTY);
    }

    public Krb5Credentials(String keytabLocation){
        this(StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, keytabLocation);
    }

    public Krb5Credentials(String username, String password, String realm) {
        this(username, password, realm, StringUtils.EMPTY);
    }

    public Krb5Credentials(String username, String password, String realm, String keytabLocation) {
        this.username = username;
        this.password = password;
        this.realm = realm;
        this.keytabLocation = keytabLocation;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRealm() {
        return realm;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

    public String getKeytabLocation() {
        return keytabLocation;
    }

    public void setKeytabLocation(String keytabLocation) {
        this.keytabLocation = keytabLocation;
    }
}
