package com.epam.kerberos;

import com.sun.security.auth.module.Krb5LoginModule;
import org.apache.hadoop.security.UserGroupInformation;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import java.util.HashMap;
import java.util.Map;

public class KerberosUtil {
  public static LoginContext getLoginContextFromUsernamePassword( String principal, String password )
    throws LoginException {
    return new LoginContext( "app_name", createSubject(), callbacks -> {
      for ( Callback callback : callbacks ) {
        if ( callback instanceof NameCallback ) {
          ( (NameCallback) callback ).setName( principal );
        } else if ( callback instanceof PasswordCallback ) {
          ( (PasswordCallback) callback ).setPassword( password.toCharArray() );
        } else {
          throw new UnsupportedCallbackException( callback );
        }
      }
    }, new KerberosConfiguration( createAppConfigurationEntry(
      createBaseKerberosUserLoginOptions( "principal", principal ) ) ) );
  }

  private static Subject createSubject() {
    return new Subject();
  }

  private static AppConfigurationEntry[] createAppConfigurationEntry( Map<String, String> options ) {
    return new AppConfigurationEntry[] {
      new AppConfigurationEntry( Krb5LoginModule.class.getName(),
        AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, options ),
      new AppConfigurationEntry( UserGroupInformation.HadoopLoginModule.class.getName(),
        AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, options )
    };
  }

  private static Map<String, String> createBaseKerberosUserLoginOptions( String optKey, String optValue ) {
    Map<String, String> options = new HashMap<>( createBaseKerberosUserLoginOptions() );
    options.put( optKey, optValue );

    return options;
  }

  private static Map<String, String> createBaseKerberosUserLoginOptions() {
    Map<String, String> options = new HashMap<>( createBaseLoginConfigMap() );

    options.put( "useTicketCache", Boolean.TRUE.toString() );
    options.put( "renewTGT", Boolean.TRUE.toString() );

    return options;
  }


  private static Map<String, String> createBaseLoginConfigMap() {
    Map<String, String> configBaseMap = new HashMap<>();

    configBaseMap.put( "debug", Boolean.FALSE.toString() );

    return configBaseMap;
  }
}
