package com.epam.loader.plan.strategy.impl;

import com.epam.loader.config.condition.DownloadableFile;
import com.epam.loader.config.condition.constant.DownloadableFileConstants;
import com.epam.loader.plan.strategy.SearchStrategy;
import com.epam.spring.exception.StrategyException;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component( "mapr-default-strategy" )
public class MaprDefaultSearchStrategy implements SearchStrategy {
  @Override
  public String getStrategyCommand( List<DownloadableFile> searchableServiceNames ) {
    return createStrategyCommand( searchableServiceNames );
  }

  @Override
  public List<DownloadableFile> resolveCommandResult( String commandResult,
                                                      List<DownloadableFile> searchableServiceNames )
    throws StrategyException {
    logger.info( "Mapr start resolve for - " );
    String hadoopVersion = extractHadoopVersionFromCommandResult( commandResult );
    String maprHome = extractMaprHome( commandResult );

    searchableServiceNames.forEach( service -> {
      if ( DownloadableFileConstants.ServiceName.HIVE.equals( service.getServiceName() ) ) {
        service.setDownloadPath(
          maprHome + "/" + service.getServiceName() + "/" + extractHiveHomeDirFromCommandResult( commandResult )
            + "/conf/" );
      } else if ( DownloadableFileConstants.ServiceName.HBASE.equals( service.getServiceName() ) ) {
        service.setDownloadPath(
          maprHome + "/" + service.getServiceName() + "/" + extractHbaseHomeDirFromCommandResult( commandResult )
            + "/conf/" );
      } else {
        service.setDownloadPath( maprHome + "/hadoop/hadoop-" + hadoopVersion + "/etc/hadoop/" );
      }
    } );

    return searchableServiceNames;
  }

  String extractHadoopVersionFromCommandResult( String commandResult ) {
    return Arrays.stream( Stream.of( commandResult.split( " " ) )
      .filter( splitCommand -> splitCommand.contains( "mapr" ) ).findFirst().orElse( StringUtils.EMPTY ).split( "-" ) )
      .findFirst().orElse( StringUtils.EMPTY );
  }

  String extractHiveHomeDirFromCommandResult( String commandResult ) {
    return Stream.of( commandResult.split( "[ \n]" ) )
      .filter( splitCommand -> splitCommand.contains( "hive-" ) ).findFirst().orElse( StringUtils.EMPTY );
  }

  String extractHbaseHomeDirFromCommandResult( String commandResult ) {
    return Stream.of( commandResult.split( "[ \n]" ) )
      .filter( splitCommand -> splitCommand.contains( "hbase-" ) ).findFirst().orElse( StringUtils.EMPTY );
  }

  private String extractMaprHome( String commandResult ) {
    String[] parsedCommand = commandResult.split( "\n" );

    return parsedCommand[ parsedCommand.length - 1 ];
  }

  private String createStrategyCommand( List<DownloadableFile> searchableServiceNames ) {
    return searchableServiceNames.stream().map( DownloadableFile::getServiceName ).map( this::getSshCommandForService )
      .collect( Collectors.joining( "; " ) ) + "; echo $MAPR_HOME";
  }

  private String getSshCommandForService( String serviceName ) {
    switch ( serviceName ) {
      case DownloadableFileConstants.ServiceName.HDFS:
      case DownloadableFileConstants.ServiceName.YARN:
      case DownloadableFileConstants.ServiceName.MAPREDUCE2:
        return "hadoop version";
      case DownloadableFileConstants.ServiceName.HBASE:
        return "ls $MAPR_HOME/hbase";
      case DownloadableFileConstants.ServiceName.HIVE:
        return "ls $MAPR_HOME/hive";
      default:
        return StringUtils.EMPTY;
    }
  }
}
