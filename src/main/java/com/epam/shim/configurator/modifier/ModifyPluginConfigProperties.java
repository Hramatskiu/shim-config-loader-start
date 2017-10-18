package com.epam.shim.configurator.modifier;

import com.epam.loader.common.util.CommonUtilException;
import com.epam.loader.common.util.CommonUtilHolder;
import com.epam.loader.config.credentials.EmrCredentials;
import com.epam.loader.config.credentials.SshCredentials;
import com.epam.loader.plan.manager.LoadConfigsManager;
import com.epam.shim.configurator.config.MaprSecureIdConfiguration;
import com.epam.shim.configurator.config.ModifierConfiguration;
import com.epam.shim.configurator.util.LocalProccessCommandExecutor;
import com.epam.shim.configurator.util.PropertyHandler;
import com.epam.shim.configurator.xml.XmlPropertyHandler;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ModifyPluginConfigProperties {

  final static Logger logger = Logger.getLogger( ModifyPluginConfigProperties.class );

  public void modifyPluginProperties( ModifierConfiguration modifierConfiguration, EmrCredentials emrCredentials ) {
    File f = new File( modifierConfiguration.getPathToShim() );
    String shimFolder = f.getName();
    File hadoopConfigurationsFolder = new File( f.getParent() );
    String pluginPropertiesFile = hadoopConfigurationsFolder.getParent() + File.separator + "plugin.properties";
    String configPropertiesFile = modifierConfiguration.getPathToShim() + File.separator + "config.properties";

    if ( modifierConfiguration.getDfsInstallDir() != null && !"".equals( modifierConfiguration.getDfsInstallDir() ) ) {
      PropertyHandler.setProperty( pluginPropertiesFile, "pmr.kettle.dfs.install.dir",
        "/opt/pentaho/mapreduce_" + modifierConfiguration.getDfsInstallDir() );
    }
    PropertyHandler.setProperty( pluginPropertiesFile, "active.hadoop.configuration", shimFolder );

    if ( !modifierConfiguration.getClusterType().equals( LoadConfigsManager.ClusterType.EMR ) ) {
      if ( modifierConfiguration.getClusterType().equals( LoadConfigsManager.ClusterType.MAPR ) ) {
        addMaprClasspath( configPropertiesFile, modifierConfiguration.getPathToShim() );
        if ( modifierConfiguration.isSecure() || modifierConfiguration.getHosts().split( "," )[ 0 ].trim()
          .contains( "sn" )
          || modifierConfiguration.getHosts().split( "," )[ 0 ].trim().contains( "secn" ) ) {
          setMaprSecureConfig( configPropertiesFile );
          addMaprSecureIdToCoreSiteFile( modifierConfiguration.getHosts().split( "," )[ 0 ].trim(),
            modifierConfiguration.getPathToShim() + File.separator + "core-site.xml" );
        }
      } else {
        PropertyHandler.setProperty( configPropertiesFile, "pentaho.oozie.proxy.user", "devuser" );
        setCdhAndHdpSecurity( configPropertiesFile, modifierConfiguration.isSecure() );
      }
    } else {
      setEmrSecureProperties( modifierConfiguration.getPathToShim(),
        emrCredentials.getSecretKey(), emrCredentials.getAccessKey() );
      setEmrFsImpl( modifierConfiguration.getPathToShim() );
    }
  }

  private void addMaprClasspath( String configPropertiesFile, String pathToShim ) {
    if ( System.getProperty( "os.name" ).startsWith( "Windows" ) ) {
      String hadoopClasspath = LocalProccessCommandExecutor
        .executeCommand( "cmd /c %MAPR_HOME%\\hadoop\\hadoop-2.7.0\\bin\\hadoop.cmd classpath" );
      String maprHome = LocalProccessCommandExecutor
        .executeCommand( "cmd /c echo %MAPR_HOME%" );
      hadoopClasspath += ";" + maprHome + "\\lib" + ";" + pathToShim;
      String modifiedHadoopClasspath =
        Arrays.stream( hadoopClasspath.split( ";" ) )
          .map( line -> "file:///" + line )
          .map( line -> line.replace( "\\", "/" ) )
          .map( line -> line.replace( "*", "" ) )
          .collect( Collectors.joining( "," ) );
      PropertyHandler.setProperty( configPropertiesFile, "windows.classpath", modifiedHadoopClasspath );
    } else if ( System.getProperty( "os.name" ).startsWith( "Linux" ) ) {
      String hadoopClasspath = LocalProccessCommandExecutor
        .executeCommand( "/opt/mapr/hadoop/hadoop-2.7.0/bin/hadoop classpath" );
      String maprHome = LocalProccessCommandExecutor
        .executeCommand( "echo /opt/mapr" );
      hadoopClasspath += "," + maprHome + "/lib" + "," + pathToShim;
      String modifiedHadoopClasspath =
        Arrays.stream( hadoopClasspath.split( "," ) ).filter( line -> !line.isEmpty() )
          .map( line -> line.replace( "*", "" ) ).collect( Collectors.joining( "," ) );
      PropertyHandler.setProperty( configPropertiesFile, "linux.classpath", modifiedHadoopClasspath );
    }
  }

  private void setMaprSecureConfig( String configPropertiesFile ) {
    PropertyHandler.setProperty( configPropertiesFile,
      "authentication.kerberos.principal", "mapr@PENTAHOQA.COM" );
    PropertyHandler.setProperty( configPropertiesFile,
      "authentication.kerberos.password", "password" );
    PropertyHandler.setProperty( configPropertiesFile,
      "authentication.superuser.provider", "mapr-kerberos" );
    PropertyHandler.setProperty( configPropertiesFile,
      "authentication.kerberos.id", "mapr-kerberos" );
  }

  private void addMaprSecureIdToCoreSiteFile( String host, String pathToCoreSiteFile ) {
    MaprSecureIdConfiguration maprSecureIdConfiguration = findMaprSecureConfiguration( host );
    XmlPropertyHandler
      .addPropertyToFile( pathToCoreSiteFile, "hadoop.spoofed.user.uid", maprSecureIdConfiguration.getUid() );
    XmlPropertyHandler
      .addPropertyToFile( pathToCoreSiteFile, "hadoop.spoofed.user.gid", maprSecureIdConfiguration.getGid() );
    XmlPropertyHandler
      .addPropertyToFile( pathToCoreSiteFile, "hadoop.spoofed.user.username", maprSecureIdConfiguration.getName() );
    logger.info( "Set mapr secure uid to core-site.xml" );
  }

  private MaprSecureIdConfiguration findMaprSecureConfiguration( String host ) {
    MaprSecureIdConfiguration maprSecureIdConfiguration = new MaprSecureIdConfiguration();
    try {
      String idString = CommonUtilHolder.sshCommonUtilInstance().executeCommand( new SshCredentials(), host, 22,
        "id" );
      if ( idString != null ) {
        String[] ids = idString.split( " " );
        for ( String id : ids ) {
          if ( id.contains( "uid" ) ) {
            maprSecureIdConfiguration.setUid( ( id.split( "=" )[ 1 ] ).split( "\\(" )[ 0 ] );
          }

          if ( id.contains( "gid" ) ) {
            maprSecureIdConfiguration.setGid( ( id.split( "=" )[ 1 ] ).split( "\\(" )[ 0 ] );
          }

          if ( id.contains( "groups" ) ) {
            maprSecureIdConfiguration.setName( ( id.split( "=" )[ 1 ] ).split( "\\(" )[ 1 ].replaceAll( "\\).*", "" ) );
          }
        }
      }
    } catch ( CommonUtilException e ) {
      e.printStackTrace();
    }

    return maprSecureIdConfiguration;
  }

  private void setCdhAndHdpSecurity( String configPropertiesFile, boolean isSecure ) {
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

  private void setEmrSecureProperties( String pathToShim, String secretKey, String accessKey ) {
    XmlPropertyHandler
      .addPropertyToFile( pathToShim + File.separator + "core-site.xml", "fs.s3.awsAccessKeyId", accessKey );
    XmlPropertyHandler
      .addPropertyToFile( pathToShim + File.separator + "core-site.xml", "fs.s3.awsSecretAccessKey", secretKey );
    XmlPropertyHandler
      .addPropertyToFile( pathToShim + File.separator + "core-site.xml", "fs.s3n.awsAccessKeyId", accessKey );
    XmlPropertyHandler
      .addPropertyToFile( pathToShim + File.separator + "core-site.xml", "fs.s3n.awsSecretAccessKey", secretKey );
  }

  private void setEmrFsImpl( String pathToShim ) {
    XmlPropertyHandler.modifyPropertyInFile( pathToShim + File.separator + "core-site.xml",
      "fs.s3.impl", "org.apache.hadoop.fs.s3.S3FileSystem" );
    XmlPropertyHandler.modifyPropertyInFile( pathToShim + File.separator + "core-site.xml",
      "fs.s3n.impl", "org.apache.hadoop.fs.s3native.NativeS3FileSystem" );
  }
}