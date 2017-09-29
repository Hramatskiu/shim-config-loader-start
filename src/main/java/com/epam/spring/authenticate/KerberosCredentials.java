package com.epam.spring.authenticate;

import javax.security.auth.Subject;

public interface KerberosCredentials {
    Subject getKrb5Subject();
    void setKrbSubject(Subject krb5subject);
}
