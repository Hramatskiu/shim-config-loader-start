package com.epam.shim.configurator.plugin;

import com.epam.loader.config.condition.constant.DownloadableFileConstants;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BIServerPlugin {
  final static Logger logger = Logger.getLogger( BIServerPlugin.class );
  private String sourcePath;

  public BIServerPlugin( String sourcePath ) {
    this.sourcePath = sourcePath;
  }

  public boolean canUseBiServerPlugin( String shimName ) {
    return Files.exists( Paths.get( createDownloadPath( shimName ) ) );
  }

  public String createDownloadPath( String shimName ) {
    logger.info( "Using BiServerPlugin! Creating download path to shim - " + shimName );
    return isSeparateSpoonInstall() ? createSeparateSpoonPath() + File.separator + shimName
      : createSpoonShimPath() + File.separator + shimName;
  }

  public void copyFilesToOtherProducts( String shimName ) {
    if ( !isSeparateSpoonInstall() ) {
      String pathToShim = createDownloadPath( shimName );

      createListOfOutputDirs().forEach( outputDir -> {
        logger.info( "Using BiServerPlugin! Copy config files to " + outputDir );
        createListOfFilesToCopy().forEach( fileToCopy -> copyFile( pathToShim, outputDir + File.separator + shimName, fileToCopy ) );
        copyFile( pathToShim, outputDir + File.separator + shimName, "config.properties" );
        copyFile( new File( pathToShim ).getParentFile().getParentFile().getAbsolutePath(),
          new File( outputDir ).getParentFile().getAbsolutePath(), "plugin.properties" );
      } );
    }
  }

  public List<String> getAvailableShims() {
    List<String> availableShimList = new ArrayList<>(  );
    File[] childFiles = new File( isSeparateSpoonInstall()
      ? createSeparateSpoonPath() : createSpoonShimPath() ).listFiles();

    if ( childFiles != null ) {
      Arrays.stream( childFiles ).filter( File::isDirectory )
        .map( File::getName ).forEach( availableShimList::add );
    } else {
      logger.error( "Verify you path to pentaho root folder!" );
    }

    return availableShimList;
  }

  public List<String> createListOfOutputDirs(  ) {
    List<String> outputDirs = new ArrayList<>(  );

    if ( !isSeparateSpoonInstall() ) {
      outputDirs.add( createServerShimPath() );
      outputDirs.add( createMetadataEditorShimPath() );
      outputDirs.add( createReportDesignerShimPath() );
    }

    return outputDirs;
  }

  private void copyFile( String source, String dest, String fileName ) {
    try {
      if ( Files.exists( Paths.get( source + File.separator + fileName ) ) ) {
        Files.copy( Paths.get( source + File.separator + fileName ),
          Paths.get( dest + File.separator + fileName ), StandardCopyOption.REPLACE_EXISTING );
      }
    } catch ( IOException ex ) {
      logger.error( ex );
    }
  }

  private boolean isSeparateSpoonInstall() {
    return Files.exists( Paths.get( createSeparateSpoonPath() ) );
  }

  private String createSeparateSpoonPath() {
    return sourcePath + File.separator + "data-integration" + File.separator + "plugins" + File.separator
      + "pentaho-big-data-plugin" + File.separator + "hadoop-configurations";
  }

  private String createServerShimPath() {
    return sourcePath + File.separator + "server" + File.separator + "pentaho-server" + File.separator
      + "pentaho-solutions" + File.separator + "system" + File.separator + "kettle" + File.separator + "plugins"
      + File.separator + "pentaho-big-data-plugin" + File.separator + "hadoop-configurations";
  }

  private String createSpoonShimPath() {
    return sourcePath + File.separator + "design-tools" + File.separator + "data-integration"
      + File.separator + "plugins" + File.separator + "pentaho-big-data-plugin" + File.separator + "hadoop-configurations";
  }

  private String createReportDesignerShimPath() {
    return sourcePath + File.separator + "design-tools" + File.separator + "report-designer" + File.separator + "plugins"
      + File.separator + "pentaho-big-data-plugin" + File.separator + "hadoop-configurations";
  }

  private String createMetadataEditorShimPath() {
    return sourcePath + File.separator + "design-tools" + File.separator + "metadata-editor" + File.separator + "plugins"
      + File.separator + "pentaho-big-data-plugin" + File.separator + "hadoop-configurations";
  }

  private List<String> createListOfFilesToCopy() {
    List<String> filesToCopy = new ArrayList<>(  );

    filesToCopy.add( DownloadableFileConstants.ServiceFileName.HDFS );
    filesToCopy.add( DownloadableFileConstants.ServiceFileName.HBASE );
    filesToCopy.add( DownloadableFileConstants.ServiceFileName.HIVE );
    filesToCopy.add( DownloadableFileConstants.ServiceFileName.CORE );
    filesToCopy.add( DownloadableFileConstants.ServiceFileName.MAPRED );
    filesToCopy.add( DownloadableFileConstants.ServiceFileName.YARN );

    return filesToCopy;
  }
}
