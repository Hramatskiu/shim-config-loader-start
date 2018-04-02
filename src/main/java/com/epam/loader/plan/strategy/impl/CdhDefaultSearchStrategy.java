package com.epam.loader.plan.strategy.impl;

import com.epam.loader.common.util.CheckingParamsUtil;
import com.epam.loader.config.condition.DownloadableFile;
import com.epam.loader.config.condition.constant.DownloadableFileConstants;
import com.epam.loader.plan.strategy.SearchStrategy;
import com.epam.loader.plan.strategy.StrategyException;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component( "cdh-default-strategy" )
public class CdhDefaultSearchStrategy implements SearchStrategy {
  private static final String HADOOP_DEFAULT_HOME = "/etc/hadoop/conf/";

  @Override public String getStrategyCommand( List<DownloadableFile> searchableServiceNames ) {
    return StringUtils.EMPTY;
  }

  @Override public List<DownloadableFile> resolveCommandResult( String commandResult,
                                                                List<DownloadableFile> searchableServiceNames )
    throws StrategyException {
    searchableServiceNames.forEach(
      downloadableFile -> downloadableFile.setDownloadPath( chooseDownloadPath( downloadableFile.getServiceName() ) ) );

    return searchableServiceNames;
  }

  @Override public void validateCommandResult( String commandResult ) throws StrategyException {
    if ( !CheckingParamsUtil.checkParamsWithNull( commandResult ) ) {
      throw new StrategyException( "Invalid command result." );
    }
  }

  @Override public boolean useSsh() {
    return true;
  }

  private String chooseDownloadPath( String serviceName ) {
    switch ( serviceName ) {
      case DownloadableFileConstants.ServiceName.HDFS:
      case DownloadableFileConstants.ServiceName.YARN:
      case DownloadableFileConstants.ServiceName.MAPREDUCE2:
        return HADOOP_DEFAULT_HOME;
      default:
        return "/etc/" + serviceName.toLowerCase() + "/conf/";

    }
  }
}
