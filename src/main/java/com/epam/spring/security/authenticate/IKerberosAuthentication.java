package com.epam.spring.security.authenticate;

public interface IKerberosAuthentication {
  boolean isKerberosSet();

  void setKerberosAuth( boolean isKerberos );
}
