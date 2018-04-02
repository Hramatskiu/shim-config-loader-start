package com.epam.shim.configurator.util;

import com.epam.loader.plan.manager.LoadConfigsManager;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.pentaho.di.core.Const;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

public class CopyDriversUtil {
  final static Logger logger = Logger.getLogger( CopyDriversUtil.class );

  private static String rootUtilityFolder;

  public static String createDestConfigPath( String destFolder ) {
    try {
      createConfigPropertiesFile( getPathToSaveConfigs( destFolder ) );
      return getPathToSaveConfigs( destFolder );
    } catch ( IOException e ) {
      logger.error( "Can't create/get dest folder - " + Const.getUserHomeDirectory() + ".pentaho" + File.separator
        + "metastore" + File.separator + "pentaho" + File.separator + "NamedCluster" + File.separator + "Configs"
        + File.separator + destFolder, e );
    }

    return StringUtils.EMPTY;
  }

  public static void copyAllDrivers( String pathToShim, LoadConfigsManager.ClusterType clusterType ) {
    if ( clusterType.equals( LoadConfigsManager.ClusterType.CDH ) ) {
      copyImpalaSimbaDriver( pathToShim );
    }
    copyMySqlDriver( pathToShim );
    if ( clusterType.equals( LoadConfigsManager.ClusterType.HDP ) ) {
      copySparkSqlDriver( pathToShim );
    }
  }

  //Copy impala simba driver to appropriate place
  public static void copyImpalaSimbaDriver( String pathToShim ) {
    try {
      copyDriverFileToShimLib( "ImpalaJDBC41.jar", pathToShim );
      //copyDriverFileToShimLib( "SimbaApacheSparkJDBCDriver.lic", pathToShim );
      logger.info( "ImpalaSimbaDriver copy successful to "
        + Paths.get( pathToShim + File.separator + "lib"
        + File.separator + "ImpalaJDBC41.jar" ) );
    } catch ( NoSuchElementException nsee ) {
      logger.warn( "Impala Simba Driver was not found in ShimConfig folder" );
    } catch ( FileAlreadyExistsException ee ) {
      logger.info( "Impala Simba driver already exists in the shim folder" );
    } catch ( IOException e ) {
      logger.error( "IOexception while copying impala simba driver" + e );
    } catch ( Exception e ) {
      logger.error( e );
    }
  }

  //Copy mysql driver to appropriate place
  public static void copyMySqlDriver( String pathToShimLocation ) {
    try {
      Path mysqlDriverPath = findFileInThisUtilityFolder( "mysql-connector-java-.+?.jar" );
      if ( Files.exists( mysqlDriverPath ) ) {
        Path pathToShim = Paths.get( pathToShimLocation );
        if ( pathToShim.getParent().getParent().getParent().getParent().getFileName().toString().
          equalsIgnoreCase( "data-integration" ) ) {
          try {
            Files.copy( mysqlDriverPath, Paths.get( pathToShim.getParent().getParent().getParent().getParent()
              + File.separator + "lib" + File.separator + mysqlDriverPath.getFileName() ) );
            logger.info( "MySQL Driver copy successful to " + Paths.get( pathToShim.
              getParent().getParent().getParent().getParent()
              + File.separator + "lib" + File.separator + mysqlDriverPath.getFileName() ) );
          } catch ( NoSuchElementException nse ) {
            logger.warn( "MySQL Driver was not found in ShimConfig folder." );
          } catch ( FileAlreadyExistsException ee ) {
            logger.info( "MySQL driver already exists in the destination folder" );
          } catch ( IOException e ) {
            logger.error( "IOexception while copying MySQL driver" + e );
          } catch ( Exception e ) {
            logger.error( e );
          }
        }
        if ( pathToShim.getParent().getParent().getParent().getParent().getParent().getFileName().toString().
          equalsIgnoreCase( "design-tools" ) ) {
          Files.copy( mysqlDriverPath,
            Paths.get( pathToShim.getParent().getParent().getParent().getParent().getParent().getParent()
              + File.separator + "server" + File.separator + "pentaho-server" + File.separator + "tomcat"
              + File.separator + "lib" + File.separator + mysqlDriverPath.getFileName() ) );
          logger.info( "MySQL Driver copy successful to " + Paths
            .get( pathToShim.getParent().getParent().getParent().getParent().getParent().getParent()
              + File.separator + "server" + File.separator + "pentaho-server" + File.separator + "tomcat"
              + File.separator + "lib" + File.separator + mysqlDriverPath.getFileName() ) );
        }
        if ( pathToShim.getParent().getParent().getParent().getParent().getFileName().toString().
          equalsIgnoreCase( "kettle" ) ) {
          Files.copy( mysqlDriverPath, Paths.get( pathToShim.getParent().getParent().getParent().getParent().getParent()
            .getParent().getParent() + File.separator + "tomcat" + File.separator + "lib"
            + File.separator + mysqlDriverPath.getFileName() ) );
          logger.info( "MySQL Driver copy successful to "
            + Paths.get( pathToShim.getParent().getParent().getParent().getParent().getParent()
            .getParent().getParent() + File.separator + "tomcat" + File.separator + "lib"
            + File.separator + mysqlDriverPath.getFileName() ) );
        }
      }
    } catch ( NoSuchElementException nse ) {
      logger.warn( "MySQL Driver was not found in ShimConfig folder." );
    } catch ( FileAlreadyExistsException ee ) {
      logger.info( "MySQL driver already exists in the destination folder" );
    } catch ( IOException e ) {
      logger.error( "IOexception while copying MySQL driver" + e );
    } catch ( Exception e ) {
      logger.error( e );
    }
  }

  // copy sparkSQL driver to appropriate place
  public static void copySparkSqlDriver( String pathToShim ) {
    String[] filesToCopy = { "ql.jar", "SparkJDBC41.jar", "TCLIServiceClient.jar" };

    try {
      for ( String f : filesToCopy ) {
        copyDriverFileToShimLib( f, pathToShim );
      }
      logger.info( "SparkSQL Driver copy successful to "
        + Paths.get( pathToShim + File.separator ) + File.pathSeparator + "lib" + File.pathSeparator );
    } catch ( NoSuchElementException nse ) {
      logger.warn( "SparkSQL Driver was not found in ShimConfig folder" );
    } catch ( FileAlreadyExistsException ee ) {
      logger.info( "SparkSQL driver already exists in the shim folder" );
    } catch ( IOException e ) {
      logger.error( "IOexception while copying SparkSQL driver" + e );
    } catch ( Exception e ) {
      logger.error( e );
    }
  }

  public static void copyLicensesForSpoon( String pathToshim ) {
    try {
      if ( Paths.get( pathToshim ).getParent().getParent().getParent().getParent().getFileName().toString()
        .equals( "data-integration" ) ) {
        Path installedLicensesPath = findFileInThisUtilityFolder( ".installedLicenses.xml" );
        if ( Files.exists( installedLicensesPath ) ) {
          Files.copy( installedLicensesPath, Paths.get( pathToshim ).getParent().getParent().getParent().getParent() );
        }
      }
    } catch ( NoSuchElementException nse ) {
      logger.warn( ".installedLicenses.xml file was not found" );
    } catch ( FileAlreadyExistsException faee ) {
      logger.info( ".installedLicenses.xml already exists in the destination folder" );
    } catch ( Exception use ) {
      logger.error( use );
    }
  }

  private static String createConfigPropertiesFile( String path ) throws IOException {
    if ( !Files.exists( Paths.get( path + File.separator + "config.properties" ) ) ) {
      Files.createFile( Paths.get( path + File.separator + "config.properties" ) );
    }

    return path + File.separator + "config.properties";
  }

  private static String getPathToSaveConfigs( String destFolder ) throws IOException {
    Path pathToSave = Paths.get( Const.getUserHomeDirectory() + File.separator + ".pentaho" + File.separator
      + "metastore" + File.separator + "pentaho" + File.separator + "NamedCluster" + File.separator + "Configs"
      + File.separator + destFolder );

    if ( !Files.exists( pathToSave ) ) {
      Files.createDirectory( pathToSave );
    }

    return pathToSave.toAbsolutePath().toString();
  }

  private static void copyDriverFileToShimLib( String driverFile, String pathToShim ) throws Exception {
    Path driverPath = findFileInThisUtilityFolder( driverFile );
    if ( Files.exists( driverPath ) ) {
      Files.copy( driverPath, Paths.get( pathToShim + File.separator + "lib"
        + File.separator + driverPath.getFileName() ) );
    }
  }

  private static Path findFileInThisUtilityFolder( String regex )
    throws Exception {
    return Files.find( Paths.get( getRootUtilityFolder() ), 3, ( p, bfa ) -> bfa.isRegularFile()
      && p.getFileName().toString().matches( regex ) ).findFirst().get();
  }

  public static String getRootUtilityFolder() {
    if ( rootUtilityFolder == null ) {
      synchronized ( CopyDriversUtil.class ) {
        if ( rootUtilityFolder == null ) {
          try {
            rootUtilityFolder =
              Paths.get( CopyDriversUtil.class.getProtectionDomain().getCodeSource().getLocation().toURI() )
                .getParent().toAbsolutePath().normalize().toString();
          } catch ( Exception e ) {
            logger.error( "this should never happen... " + e );
          }
        }
      }
    }
    return rootUtilityFolder;
  }
}
