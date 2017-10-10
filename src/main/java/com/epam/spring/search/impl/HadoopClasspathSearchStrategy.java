package com.epam.spring.search.impl;

import com.epam.spring.condition.DownloadableFile;
import com.epam.spring.constant.DownloadableFileConstants;
import com.epam.spring.exception.StrategyException;
import com.epam.spring.search.SearchStrategy;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component( "hadoop-classpath-strategy" )
public class HadoopClasspathSearchStrategy implements SearchStrategy {
  @Override public String getStrategyCommand() {
    return "hadoop classpath; yarn classpath; hbase classpath; mapred classpath";
  }

  @Override public List<DownloadableFile> resolveCommandResult( String commandResult,
                                                                List<DownloadableFile> searchableServiceNames )
    throws StrategyException {
    List<String> confDirs = extractConfDirs( commandResult );
    searchableServiceNames.forEach( downloadableFile -> downloadableFile.setDownloadPath( confDirs.get( getServiceListIndex( downloadableFile.getServiceName() ) ) + "/" ) );

    return searchableServiceNames;
  }

  private List<String> extractConfDirs( String commandResult ) {
    return Arrays.stream( commandResult.split( "\n" ) ).map( this::extractConfDir ).collect( Collectors.toList() );
  }

  private String extractConfDir( String classpathString ) {
    return Arrays.stream( classpathString.split( ":" ) ).findFirst().orElse( StringUtils.EMPTY );
  }

  private int getServiceListIndex( String serviceName ) {
    switch ( serviceName ) {
      case DownloadableFileConstants.ServiceName.HDFS: return 0;
      case DownloadableFileConstants.ServiceName.YARN: return 1;
      case DownloadableFileConstants.ServiceName.HBASE: return 2;
      case DownloadableFileConstants.ServiceName.MAPREDUCE2: return 3;
      default:
        return 0;
    }
  }
}
