package com.epam.shim.configurator.modifier;

import com.epam.loader.common.util.CheckingParamsUtil;
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
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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

        configureMaprClient(
          modifierConfiguration.isSecure() || modifierConfiguration.getHosts().split( "," )[ 0 ].trim()
            .contains( "sn" ) || modifierConfiguration.getHosts().split( "," )[ 0 ].trim().contains( "secn" ),
          modifierConfiguration.getHosts(),
          modifierConfiguration.getPathToShim() + File.separator + "mapred-site.xml" );
        copyMaprConfigFilesToMaprClient( modifierConfiguration.getPathToShim() );
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

  private void copyMaprConfigFilesToMaprClient( String pathToShim ) {
    String maprHome = getMaprHome();
    if ( CheckingParamsUtil.checkParamsWithNullAndEmpty( maprHome ) ) {
      try {
        Files.copy( Paths.get( pathToShim + File.separator + "mapred-site.xml" ),
          Paths.get( getMaprHadoopConfHome( maprHome ) + File.separator + "mapred-site.xml" ),
          StandardCopyOption.REPLACE_EXISTING );
        Files.copy( Paths.get( pathToShim + File.separator + "yarn-site.xml" ),
          Paths.get( getMaprHadoopConfHome( maprHome ) + File.separator + "yarn-site.xml" ),
          StandardCopyOption.REPLACE_EXISTING );
        Files.copy( Paths.get( pathToShim + File.separator + "core-site.xml" ),
          Paths.get( getMaprHadoopConfHome( maprHome ) + File.separator + "core-site.xml" ),
          StandardCopyOption.REPLACE_EXISTING );
        Files.copy( Paths.get( pathToShim + File.separator + "hdfs-site.xml" ),
          Paths.get( getMaprHadoopConfHome( maprHome ) + File.separator + "hdfs-site.xml" ),
          StandardCopyOption.REPLACE_EXISTING );
        Files.copy( Paths.get( pathToShim + File.separator + "ssl_truststore" ),
          Paths.get( maprHome + File.separator + "conf" + File.separator + "ssl_truststore" ),
          StandardCopyOption.REPLACE_EXISTING );
      } catch ( IOException e ) {
        logger.error( e );
      }
    } else {
      logger.warn( "MAPR_HOME not set. See - http://doc.mapr.com/display/MapR/Setting+Up+the+Client ." );
    }
  }

  private void addMaprClasspath( String configPropertiesFile, String pathToShim ) {
    if ( System.getProperty( "os.name" ).startsWith( "Windows" ) ) {
      String maprHome = getMaprHome();
      if ( CheckingParamsUtil.checkParamsWithNullAndEmpty( maprHome ) ) {
        String hadoopClasspath = LocalProccessCommandExecutor
          .executeCommand(
            "cmd /c %MAPR_HOME%\\hadoop\\" + findMaprHadoopHome( maprHome ) + "\\bin\\hadoop.cmd classpath" );

        hadoopClasspath += ";" + maprHome + "\\lib" + ";" + pathToShim;
        String modifiedHadoopClasspath =
          Arrays.stream( hadoopClasspath.split( ";" ) )
            .map( line -> "file:///" + line )
            .map( line -> line.replace( "\\", "/" ) )
            .map( line -> line.replace( "*", "" ) )
            .map( line -> line.replace( "file:///C", "file:///c" ) )
            .collect( Collectors.joining( "," ) );
        PropertyHandler.setProperty( configPropertiesFile, "windows.classpath", modifiedHadoopClasspath );
      } else {
        logger.warn( "MAPR_HOME not set. See - http://doc.mapr.com/display/MapR/Setting+Up+the+Client ." );
      }
    } else if ( System.getProperty( "os.name" ).startsWith( "Linux" ) ) {
      String maprHome = "/opt/mapr";
      String hadoopClasspath = LocalProccessCommandExecutor
        .executeCommand( "/opt/mapr/hadoop/" + findMaprHadoopHome( maprHome ) + "/bin/hadoop classpath" );
      hadoopClasspath += "," + maprHome + "/lib" + "," + pathToShim;
      String modifiedHadoopClasspath =
        Arrays.stream( hadoopClasspath.split( "," ) ).filter( line -> !line.isEmpty() )
          .map( line -> line.replace( "*", "" ) ).collect( Collectors.joining( "," ) );
      PropertyHandler.setProperty( configPropertiesFile, "linux.classpath", modifiedHadoopClasspath );
    }
  }

  private String getMaprHome() {
    return System.getProperty( "os.name" ).startsWith( "Windows" )
      ? System.getenv( "MAPR_HOME" ) != null
      ? System.getenv( "MAPR_HOME" ) : StringUtils.EMPTY
      : "/opt/mapr";
  }

  private String getMaprHadoopConfHome( String maprHome ) {
    return maprHome + File.separator + "hadoop"
      + File.separator + findMaprHadoopHome( maprHome )
      + File.separator + "etc"
      + File.separator + "hadoop" + File.separator;
  }

  @SuppressWarnings( "ConstantConditions" )
  private String findMaprHadoopHome( String maprHome ) {
    return Arrays.stream( new File( maprHome + File.separator + "hadoop" )
      .listFiles( File::isDirectory ) ).map( File::getName )
      .filter( file -> !file.contains( "hadoop-0" ) && file.matches( "hadoop-\\d.*" ) ).findFirst()
      .orElse( "hadoop-2.7.0" );
  }

  private void configureMaprClient( boolean isSecure, String hosts, String pathToMapredFile ) {
    MaprConfigurator maprConfigurator = new MaprConfigurator();
    maprConfigurator.configureMaprClient( isSecure, hosts, pathToMapredFile );
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
    XmlPropertyHandler
      .addPropertyToFile( pathToShim + File.separator + "core-site.xml", "fs.s3a.access.key", accessKey );
    XmlPropertyHandler
      .addPropertyToFile( pathToShim + File.separator + "core-site.xml", "fs.s3a.secret.key", secretKey );
  }

  private void setEmrFsImpl( String pathToShim ) {
    XmlPropertyHandler.modifyPropertyInFile( pathToShim + File.separator + "core-site.xml",
      "fs.s3.impl", "org.apache.hadoop.fs.s3.S3FileSystem" );
    XmlPropertyHandler.modifyPropertyInFile( pathToShim + File.separator + "core-site.xml",
      "fs.s3n.impl", "org.apache.hadoop.fs.s3native.NativeS3FileSystem" );
    XmlPropertyHandler.modifyPropertyInFile( pathToShim + File.separator + "core-site.xml",
      "fs.s3a.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem" );
  }
}