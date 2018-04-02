package com.epam.shim.configurator.modifier.impl;

import com.epam.loader.common.util.CheckingParamsUtil;
import com.epam.loader.common.util.CommonUtilException;
import com.epam.loader.common.util.CommonUtilHolder;
import com.epam.loader.config.credentials.SshCredentials;
import com.epam.shim.configurator.config.MaprSecureIdConfiguration;
import com.epam.shim.configurator.config.ModifierConfiguration;
import com.epam.shim.configurator.modifier.IShimModifier;
import com.epam.shim.configurator.modifier.MaprConfigurator;
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

public class MapRModifier implements IShimModifier {
  private final static Logger logger = Logger.getLogger( MapRModifier.class );

  @Override public void modifyShim( ModifierConfiguration modifierConfiguration, String configPropertiesFile ) {
    addMaprClasspath( configPropertiesFile, modifierConfiguration.getPathToShim() );
    addMaprSecureIdToCoreSiteFile( modifierConfiguration.getHosts().split( "," )[ 0 ].trim(),
      modifierConfiguration.getPathToShim() + File.separator + "core-site.xml" );
    setMapRMapreduceMemoryLimits( modifierConfiguration.getPathToShim() );
    addHBaseMappingsPropertyToCoreSiteFile( modifierConfiguration.getPathToShim(), "*:/hbase" );
    if ( modifierConfiguration.isSecure() || modifierConfiguration.getHosts().split( "," )[ 0 ].trim()
      .contains( "sn" )
      || modifierConfiguration.getHosts().split( "," )[ 0 ].trim().contains( "secn" ) ) {
      setMaprSecureConfig( configPropertiesFile );
    }

    if ( modifierConfiguration.isConfigureMapr() ) {
      copyMaprConfigFilesToMaprClient( modifierConfiguration.getPathToShim() );
      configureMaprClient(
        modifierConfiguration.isSecure() || modifierConfiguration.getHosts().split( "," )[ 0 ].trim()
          .contains( "sn" ) || modifierConfiguration.getHosts().split( "," )[ 0 ].trim().contains( "secn" ),
        modifierConfiguration.getHosts(),
        modifierConfiguration.getPathToShim() + File.separator + "mapred-site.xml" );
    }
  }

  private void copyMaprConfigFilesToMaprClient( String pathToShim ) {
    String maprHome = getMaprHome();
    if ( CheckingParamsUtil.checkParamsWithNullAndEmpty( maprHome ) ) {
      copyFile( pathToShim, "mapred-site.xml", getMaprHadoopConfHome( maprHome ), false );
      copyFile( pathToShim, "yarn-site.xml", getMaprHadoopConfHome( maprHome ), false );
      copyFile( pathToShim, "hdfs-site.xml", getMaprHadoopConfHome( maprHome ), true );
      copyFile( pathToShim, "core-site.xml", getMaprHadoopConfHome( maprHome ), false );
      copyFile( pathToShim, "ssl_truststore", maprHome + File.separator + "conf", true );
    } else {
      logger.warn( "MAPR_HOME not set. See - http://doc.mapr.com/display/MapR/Setting+Up+the+Client ." );
    }
  }

  private void copyYarnSiteFileToMapRClientAfterConfigureRun( String pathToShim ) {
    String maprHome = getMaprHome();
    if ( CheckingParamsUtil.checkParamsWithNullAndEmpty( maprHome ) ) {
      try {
        copyFile( pathToShim, "yarn-site.xml", getMaprHadoopConfHome( maprHome ), true );

        Files.deleteIfExists( Paths.get( pathToShim + File.separator + "mapred-site.xml" ) );
        Files.deleteIfExists( Paths.get( pathToShim + File.separator + "yarn-site.xml" ) );
      } catch ( IOException e ) {
        logger.error( e );
      }
    } else {
      logger.warn( "MAPR_HOME not set. See - http://doc.mapr.com/display/MapR/Setting+Up+the+Client ." );
    }
  }

  private void copyFile( String sourceDirectory, String fileName, String destDirectory, boolean deleteFromSource ) {
    try {
      Files.copy( Paths.get( sourceDirectory + File.separator + fileName ),
        Paths.get( destDirectory + File.separator + fileName ),
        StandardCopyOption.REPLACE_EXISTING );
      if ( deleteFromSource ) {
        Files.deleteIfExists( Paths.get( sourceDirectory + File.separator + fileName ) );
      }
    } catch ( IOException e ) {
      logger.error( e );
    }
  }

  private void addMaprClasspath( String configPropertiesFile, String pathToShim ) {
    if ( System.getProperty( "os.name" ).startsWith( "Windows" ) ) {
      String maprHome = getMaprHome();
      if ( CheckingParamsUtil.checkParamsWithNullAndEmpty( maprHome )
        && Files.exists( Paths.get(
        maprHome + File.separator + "hadoop" + File.separator + findMaprHadoopHome( maprHome )
          + "\\bin\\hadoop.cmd" ) ) ) {
        String hadoopClasspath = LocalProccessCommandExecutor
          .executeCommand(
            "cmd /c %MAPR_HOME%\\hadoop\\" + findMaprHadoopHome( maprHome ) + "\\bin\\hadoop.cmd classpath" );

        hadoopClasspath += ";" + maprHome + "\\lib" + ";" + pathToShim;
        String modifiedHadoopClasspath =
          Arrays.stream( hadoopClasspath.split( ";" ) )
            .map( line -> "file:///" + line )
            .map( line -> line.replace( "\\", "/" ) )
            .map( line -> line.replace( "*", "" ) )
            .collect( Collectors.joining( "," ) );
        PropertyHandler.setProperty( configPropertiesFile, "windows.classpath", modifiedHadoopClasspath );
      } else {
        logger.warn( "MAPR_HOME not set. See - http://doc.mapr.com/display/MapR/Setting+Up+the+Client ." );
      }
    } else if ( System.getProperty( "os.name" ).startsWith( "Linux" ) ) {
      String maprHome = "/opt/mapr";
      if ( Files.exists( Paths.get( "/opt/mapr/hadoop/" + findMaprHadoopHome( maprHome ) + "/bin/hadoop" ) ) ) {
        String hadoopClasspath = LocalProccessCommandExecutor
          .executeCommand( "/opt/mapr/hadoop/" + findMaprHadoopHome( maprHome ) + "/bin/hadoop classpath" );
        hadoopClasspath += "," + maprHome + "/lib" + "," + pathToShim;
        String modifiedHadoopClasspath =
          Arrays.stream( hadoopClasspath.replaceAll( ":", "," ).split( "," ) ).filter( line -> !line.isEmpty() )
            .map( line -> line.replace( "*", "" ) ).collect( Collectors.joining( "," ) );
        PropertyHandler.setProperty( configPropertiesFile, "linux.classpath", modifiedHadoopClasspath );
      }
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
    return Files.exists( Paths.get( maprHome + File.separator + "hadoop" ) )
      ? Arrays.stream( new File( maprHome + File.separator + "hadoop" )
      .listFiles( File::isDirectory ) ).map( File::getName )
      .filter( file -> !file.contains( "hadoop-0" ) && file.matches( "hadoop-\\d.*" ) ).findFirst()
      .orElse( "hadoop-2.7.0" )
      : StringUtils.EMPTY;
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

  private void addHBaseMappingsPropertyToCoreSiteFile( String pathToShim, String value ) {
    if ( isHBaseMappingsPropertyNecessary( pathToShim ) ) {
      XmlPropertyHandler.addPropertyToFile( pathToShim + File.separator + "core-site.xml", "hbase.table.namespace.mappings", value );
    }
  }

  private boolean isHBaseMappingsPropertyNecessary( String pathToShim ) {
    String shimName = extractShimNameFromPath( pathToShim );

    return shimName.contains( "mapr" ) && isMajorVersionEqualOrAbove( shimName, 6 );
  }

  private String extractShimNameFromPath( String pathToShim ) {
    String[] shimFoldersTree = pathToShim.replace( File.separator, ":" ).split( ":" );

    return shimFoldersTree[ shimFoldersTree.length - 1 ];
  }

  private boolean isMajorVersionEqualOrAbove( String shimName, int version ) {
    return shimName.replaceAll( "\\D", "" ).charAt( 0 ) > version - 1;
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

  private void setMapRMapreduceMemoryLimits( String pathToShim ) {
    if ( XmlPropertyHandler.readXmlPropertyValue( pathToShim + File.separator + "mapred-site.xml", "mapreduce.map.memory.mb" )
      != null ) {
      XmlPropertyHandler
        .modifyPropertyInFile( pathToShim + File.separator + "mapred-site.xml", "mapreduce.map.memory.mb", "4096" );
    } else {
      XmlPropertyHandler
        .addPropertyToFile( pathToShim + File.separator + "mapred-site.xml", "mapreduce.map.memory.mb", "4096" );
    }
    if ( XmlPropertyHandler.readXmlPropertyValue( pathToShim + File.separator + "mapred-site.xml", "mapreduce.reduce.memory.mb" )
      != null ) {
      XmlPropertyHandler
        .modifyPropertyInFile( pathToShim + File.separator + "mapred-site.xml", "mapreduce.reduce.memory.mb", "4096" );
    } else {
      XmlPropertyHandler
        .addPropertyToFile( pathToShim + File.separator + "mapred-site.xml", "mapreduce.reduce.memory.mb", "4096" );
    }
  }
}
