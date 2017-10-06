package com.epam.kerberos;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;

public class KerberosConfiguration extends Configuration {
  private AppConfigurationEntry[] appConfigurationEntries;

  public KerberosConfiguration( AppConfigurationEntry[] appConfigurationEntries ) {
    this.appConfigurationEntries = appConfigurationEntries;
  }

  @Override
  public AppConfigurationEntry[] getAppConfigurationEntry( String name ) {
    return appConfigurationEntries;
  }
}
