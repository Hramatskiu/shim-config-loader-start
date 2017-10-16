package com.epam.shim.configurator.modifier;

import com.epam.loader.plan.manager.LoadConfigsManager;
import com.epam.shim.configurator.config.ModifierConfiguration;
import com.epam.shim.configurator.util.LocalProccessCommandExecutor;
import com.epam.shim.configurator.util.PropertyHandler;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ModifyPluginConfigProperties {

  final static Logger logger = Logger.getLogger( ModifyPluginConfigProperties.class );

  public void modifyPluginProperties( ModifierConfiguration modifierConfiguration ) {

    // Determine shim folder
    File f = new File( modifierConfiguration.getPathToShim() );
    String shimFolder = f.getName();
    File hadoopConfigurationsFolder = new File( f.getParent() );
    String pluginPropertiesFile = hadoopConfigurationsFolder.getParent() + File.separator + "plugin.properties";
    String configPropertiesFile = modifierConfiguration.getPathToShim() + "\\config.properties";

    PropertyHandler.setProperty( pluginPropertiesFile, "active.hadoop.configuration", shimFolder );

    PropertyHandler.setProperty( configPropertiesFile, "pentaho.oozie.proxy.user", "devuser" );

    if ( modifierConfiguration.getClusterType().equals( LoadConfigsManager.ClusterType.MAPR ) ) {
      addMaprClasspath( configPropertiesFile );
    }

    if ( modifierConfiguration.isSecure() ) {
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

    // modifying /opt/pentaho/mapreduce in plugin.properties file
    if ( modifierConfiguration.getDfsInstallDir() != null && !"".equals( modifierConfiguration.getDfsInstallDir() ) ) {
      PropertyHandler.setProperty( pluginPropertiesFile, "pmr.kettle.dfs.install.dir",
        "/opt/pentaho/mapreduce_" + modifierConfiguration.getDfsInstallDir() );
    }
  }

  private void addMaprClasspath( String configPropertiesFile ) {
    if ( System.getProperty( "os.name" ).startsWith( "Windows" ) ) {
      String lines = LocalProccessCommandExecutor
        .executeCommand( "cmd /c %MAPR_HOME%\\hadoop\\hadoop-2.7.0\\bin\\hadoop.cmd classpath" );
      String modifiedLines =
        Arrays.stream( lines.split( ";" ) ).map( line -> "file:///" + line ).map( line -> line.replace( "\\", "/" ) )
          .map( line -> line.replace( "*", "" ) ).collect( Collectors.joining( "," ) );
      PropertyHandler.setProperty( configPropertiesFile, "windows.classpath", modifiedLines );
    }
  }
}