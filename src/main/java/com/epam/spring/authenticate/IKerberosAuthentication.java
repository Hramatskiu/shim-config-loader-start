package com.epam.spring.authenticate;

public interface IKerberosAuthentication {
  boolean isKerberosSet();

  void setKerberosAuth( boolean isKerberos );
}
