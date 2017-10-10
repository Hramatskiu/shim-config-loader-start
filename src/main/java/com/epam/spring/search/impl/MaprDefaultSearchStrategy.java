package com.epam.spring.search.impl;

import com.epam.spring.condition.DownloadableFile;
import com.epam.spring.constant.DownloadableFileConstants;
import com.epam.spring.search.SearchStrategy;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Component( "mapr-default-strategy" )
public class MaprDefaultSearchStrategy implements SearchStrategy {
  @Override
  public String getStrategyCommand() {
    return "hadoop version; ls $MAPR_HOME/hbase; ls $MAPR_HOME/hive";
  }

  @Override
  public List<DownloadableFile> resolveCommandResult( String commandResult,
                                                      List<DownloadableFile> searchableServiceNames ) {
    String hadoopVersion = extractHadoopVersionFromCommandResult( commandResult );
    searchableServiceNames.forEach( service -> {
      if ( DownloadableFileConstants.ServiceName.HIVE.equals( service.getServiceName() ) ) {
        service.setDownloadPath(
          "$MAPR_HOME/" + service.getServiceName() + "/" + extractHiveHomeDirFromCommandResult( commandResult )
            + "/conf/" );
      } else if ( DownloadableFileConstants.ServiceName.HBASE.equals( service.getServiceName() ) ) {
        service.setDownloadPath(
          "$MAPR_HOME/" + service.getServiceName() + "/" + extractHbaseHomeDirFromCommandResult( commandResult )
            + "/conf/" );
      } else {
        service.setDownloadPath( "$MAPR_HOME/hadoop/hadoop-" + hadoopVersion + "/etc/hadoop/" );
      }
    } );

    return searchableServiceNames;
  }

  private String extractHadoopVersionFromCommandResult( String commandResult ) {
    return Arrays.stream( Stream.of( commandResult.split( " " ) )
      .filter( splitCommand -> splitCommand.contains( "mapr" ) ).findFirst().orElse( StringUtils.EMPTY ).split( "-" ) )
      .findFirst().orElse( StringUtils.EMPTY );
  }

  private String extractHiveHomeDirFromCommandResult( String commandResult ) {
    return Stream.of( commandResult.split( "[ \n]" ) )
      .filter( splitCommand -> splitCommand.contains( "hive-" ) ).findFirst().orElse( StringUtils.EMPTY );
  }

  private String extractHbaseHomeDirFromCommandResult( String commandResult ) {
    return Stream.of( commandResult.split( "[ \n]" ) )
      .filter( splitCommand -> splitCommand.contains( "hbase-" ) ).findFirst().orElse( StringUtils.EMPTY );
  }
}
