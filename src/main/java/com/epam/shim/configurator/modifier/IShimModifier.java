package com.epam.shim.configurator.modifier;

import com.epam.shim.configurator.config.ModifierConfiguration;
import com.epam.shim.configurator.util.PropertyHandler;

public interface IShimModifier {
  default void modifyShim( ModifierConfiguration modifierConfiguration, String configPropertiesFile ) {
    PropertyHandler.setProperty( configPropertiesFile, "pentaho.oozie.proxy.user", "devuser" );
    setupSecurity( configPropertiesFile, modifierConfiguration.isSecure() );
  }

  default void setupSecurity( String configPropertiesFile, boolean isSecure ) {
    if ( isSecure ) {
      //determine if shim is using impersonation and modify it accordingly
      if ( PropertyHandler.getPropertyFromFile( configPropertiesFile,
        "pentaho.authentication.default.mapping.impersonation.type" ) == null ) {
        PropertyHandler.setProperty( configPropertiesFile, "authentication.superuser.provider", "kerberos" );
        PropertyHandler.setProperty( configPropertiesFile, "authentication.kerberos.id", "kerberos" );
        PropertyHandler
          .setProperty( configPropertiesFile, "authentication.kerberos.principal", "devuser@PENTAHOQA.COM" );
        PropertyHandler.setProperty( configPropertiesFile, "authentication.kerberos.password", "password" );
      } else {
        PropertyHandler.setProperty( configPropertiesFile,
          "pentaho.authentication.default.kerberos.principal", "devuser@PENTAHOQA.COM" );
        PropertyHandler.setProperty( configPropertiesFile,
          "pentaho.authentication.default.kerberos.password", "password" );
        PropertyHandler.setProperty( configPropertiesFile,
          "pentaho.authentication.default.mapping.impersonation.type", "simple" );
        PropertyHandler.setProperty( configPropertiesFile,
          "pentaho.authentication.default.mapping.server.credentials.kerberos.principal", "hive@PENTAHOQA.COM" );
        PropertyHandler.setProperty( configPropertiesFile,
          "pentaho.authentication.default.mapping.server.credentials.kerberos.password", "password" );
      }
    } else {
      //determine if shim is using impersonation and modify it accordingly
      if ( PropertyHandler.getPropertyFromFile( configPropertiesFile,
        "pentaho.authentication.default.mapping.impersonation.type" ) == null ) {
        PropertyHandler.setProperty( configPropertiesFile, "authentication.superuser.provider", "NO_AUTH" );
      } else {
        PropertyHandler.setProperty( configPropertiesFile,
          "pentaho.authentication.default.mapping.impersonation.type", "disabled" );
        PropertyHandler.setProperty( configPropertiesFile,
          "pentaho.authentication.default.kerberos.password", "" );
      }
    }
  }
}
