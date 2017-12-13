package com.epam.shim.configurator;

import com.epam.loader.common.util.CommonUtilException;
import com.epam.loader.common.util.CommonUtilHolder;
import com.epam.loader.common.util.FileCommonUtil;
import com.epam.loader.config.credentials.SshCredentials;
import com.epam.shim.configurator.util.CopyDriversUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class Krb5Configurator {
  private static final Logger logger = Logger.getLogger( Krb5Configurator.class );
  private static String rootUtilityFolder;

  public static void upgradeJavaSecurity() {
    copyFile( "local_policy.jar" );
    copyFile( "US_export_policy.jar" );
  }

  public static void copyKrb5ConfToJavaSecurity( String host ) {

  }

  public static String getKrb5LocalPath() {
    return
      Files.exists( Paths.get( getRootUtilityFolder() + File.separator + "kerberos" + File.separator + "krb5.conf" ) )
        ? getRootUtilityFolder() + File.separator + "kerberos" + File.separator + "krb5.conf"
        : Files.exists( Paths.get( findPentahoJavaPath() + File.separator + "krb5.conf" ) )
        ? findPentahoJavaPath() + File.separator + "krb5.conf"
        : Files.exists( Paths.get( findJavaSecurityPath() ) )
        ? findJavaSecurityPath() : StringUtils.EMPTY;
  }

  public static void downloadKrb5FromCluster( String host, SshCredentials sshCredentials ) {
    logger.info( "Start download krb5 from cluster!" );
    String rootUtilFolder = getRootUtilityFolder() + File.separator + "kerberos" + File.separator + "krb5.conf";

    try {
      FileCommonUtil.writeStringToFile( rootUtilFolder,
        CommonUtilHolder.sshCommonUtilInstance().downloadViaSftp( sshCredentials, host, 22, "/etc/krb5.conf" ) );

      logger.info( "Successfully copied to + " + rootUtilFolder );

      copyToPentahoJavaHome( rootUtilFolder );
      copyToJavaSecurityHome( rootUtilFolder );
    } catch ( CommonUtilException ex ) {
      logger.warn( "Can't save krb5.conf file. Don't worry, check permissions for that path." + ex.getMessage() );
      loadToPentahoJavaHome( host, sshCredentials );
      loadToJavaHome( host, sshCredentials );
    }
  }


  private static void copyFile( String fileName ) {
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
      logger.error( "IOException while copying MySQL driver" + e );
    } catch ( Exception e ) {
      logger.error( e );
    }
  }

  private static String findJavaPath() {
    String javaHome = System.getenv( "JAVA_HOME" );
    if ( javaHome != null ) {
      javaHome = Paths.get( javaHome + File.separator + "jre" ).toFile().exists()
        ? Paths.get( javaHome + File.separator + "jre" + File.separator + "lib" + File.separator + "security" )
        .toAbsolutePath().toString()
        : Paths.get( javaHome + File.separator + "lib" + File.separator + "security" ).toAbsolutePath().toString();
    }

    return javaHome != null ? javaHome : System.getProperty( "java.home" ) + File.separator + "lib" + File.separator
      + "security" + File.separator;
  }

  private static void copyToPentahoJavaHome( String source ) {
    String pentahoJavaHome = findPentahoJavaPath();
    if ( pentahoJavaHome != null && Files.exists( Paths.get( source ) ) ) {
      try {
        logger.info( "Start copy krb5 to PENTAHO_JAVA_HOME!" );
        FileCommonUtil
          .writeStringToFile( pentahoJavaHome + File.separator + "krb5.conf", Files.lines( Paths.get( source ) )
            .collect( Collectors.joining( "\n" ) ) );
      } catch ( CommonUtilException | IOException e ) {
        logger.error( e.getMessage() );
      }
    }
  }

  private static void copyToJavaSecurityHome( String source ) {
    String javaSecurityLibraryPath;
    if ( System.getProperty( "os.name" ).startsWith( "Windows" ) ) {
      javaSecurityLibraryPath = findJavaPath() + File.separator + "krb5.conf";
    } else {
      javaSecurityLibraryPath = "/etc/krb5.conf";
    }

    if ( Files.exists( Paths.get( source ) ) ) {
      try {
        logger.info( "Start copy krb5 to JAVA_HOME!" );
        FileCommonUtil
          .writeStringToFile( javaSecurityLibraryPath, Files.lines( Paths.get( source ) )
            .collect( Collectors.joining( "\n" ) ) );
      } catch ( CommonUtilException | IOException e ) {
        logger.error( e.getMessage() );
      }
    }
  }

  private static void loadToPentahoJavaHome( String host, SshCredentials sshCredentials ) {
    String pentahoJavaHome = findPentahoJavaPath();
    if ( pentahoJavaHome != null ) {
      try {
        logger.info( "Start load krb5 to PENTAHO_JAVA_HOME!" );
        FileCommonUtil.writeStringToFile( findPentahoJavaPath() + File.separator + "krb5.conf",
          CommonUtilHolder.sshCommonUtilInstance().downloadViaSftp( sshCredentials, host, 22, "/etc/krb5.conf" ) );
      } catch ( CommonUtilException e ) {
        logger.error( e.getMessage() );
      }
    }
  }

  private static void loadToJavaHome( String host, SshCredentials sshCredentials ) {
    String javaSecurityLibraryPath = findJavaSecurityPath();

    if ( javaSecurityLibraryPath != null ) {
      try {
        logger.info( "Start load krb5 to JAVA_HOME!" );
        FileCommonUtil.writeStringToFile( javaSecurityLibraryPath,
          CommonUtilHolder.sshCommonUtilInstance().downloadViaSftp( sshCredentials, host, 22, "/etc/krb5.conf" ) );
      } catch ( CommonUtilException e ) {
        logger.error( e.getMessage() );
      }
    }
  }

  private static String findJavaSecurityPath() {
    String javaSecurityLibraryPath;
    if ( System.getProperty( "os.name" ).startsWith( "Windows" ) ) {
      javaSecurityLibraryPath = findJavaPath() + File.separator + "krb5.conf";
    } else {
      javaSecurityLibraryPath = "/etc/krb5.conf";
    }

    return javaSecurityLibraryPath;
  }

  private static String findPentahoJavaPath() {
    String javaHome = System.getenv( "PENTAHO_JAVA_HOME" );
    if ( javaHome != null ) {
      javaHome = Paths.get( javaHome + File.separator + "jre" ).toFile().exists()
        ? Paths.get( javaHome + File.separator + "jre" + File.separator + "lib" + File.separator + "security" )
        .toAbsolutePath().toString()
        : Paths.get( javaHome + File.separator + "lib" + File.separator + "security" ).toAbsolutePath().toString();
    }

    return javaHome;
  }

  private static Path findFileInThisUtilityFolder( String regex )
    throws Exception {
    return Files.find( Paths.get( getRootUtilityFolder() ), 3, ( p, bfa ) -> bfa.isRegularFile()
      && p.getFileName().toString().matches( regex ) ).findFirst().get();
  }

  private static String getRootUtilityFolder() {
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
