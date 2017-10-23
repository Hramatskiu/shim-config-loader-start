package com.epam.shim.configurator;

import com.epam.loader.common.util.CommonUtilException;
import com.epam.loader.common.util.CommonUtilHolder;
import com.epam.loader.common.util.FileCommonUtil;
import com.epam.loader.config.credentials.SshCredentials;
import com.epam.shim.configurator.util.CopyDriversUtil;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.NoSuchElementException;

public class Krb5Configurator {
  private static final Logger logger = Logger.getLogger( Krb5Configurator.class );
  private static String rootUtilityFolder;

  public static void upgradeJavaSecurity() {
    copyFiles( "local_policy.jar" );
    copyFiles( "US_export_policy.jar" );
  }

  public static void copyKrb5ConfToJavaSecurity( String host ) {

  }

  public static void downloadKrb5FromCluster( String host, SshCredentials sshCredentials ) {
    try {
      logger.info( "Start download configs from cluster!" );
      FileCommonUtil.writeStringToFile( findJavaPath() + File.separator + "krb5.conf",
        CommonUtilHolder.sshCommonUtilInstance().downloadViaSftp( sshCredentials, host, 22, "/etc/krb5.conf" ) );
    } catch ( CommonUtilException e ) {
      e.printStackTrace();
    }
  }

  private static void copyFiles( String fileName ) {
    try {
      Path mysqlDriverPath = findFileInThisUtilityFolder( fileName );
      if ( Files.exists( mysqlDriverPath ) ) {
        Path javaLibSecurityPath = Paths.get( findJavaPath() );
        if ( javaLibSecurityPath.toFile().exists() ) {
          Files.copy( mysqlDriverPath, Paths.get( javaLibSecurityPath.toAbsolutePath().toString()
            + mysqlDriverPath.getFileName() ) );
          logger.info( "Local policy added "
            + Paths.get( javaLibSecurityPath.toAbsolutePath().toString() + mysqlDriverPath.getFileName() ) );
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

  private static String findJavaPath() {
    return System.getProperty( "java.home" ) + File.separator + "lib" + File.separator
      + "security" + File.separator;
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
