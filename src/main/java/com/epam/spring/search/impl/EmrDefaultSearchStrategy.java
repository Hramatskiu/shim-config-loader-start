package com.epam.spring.search.impl;

import com.epam.spring.condition.DownloadableFile;
import com.epam.spring.constant.DownloadableFileConstants;
import com.epam.spring.exception.StrategyException;
import com.epam.spring.search.SearchStrategy;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component( "emr-default-strategy" )
public class EmrDefaultSearchStrategy implements SearchStrategy {
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

  private String chooseDownloadPath( String serviceName ) {
    switch ( serviceName ) {
      case DownloadableFileConstants.ServiceName.HDFS:
      case DownloadableFileConstants.ServiceName.YARN:
      case DownloadableFileConstants.ServiceName.MAPREDUCE2:
        return "/etc/hadoop/conf/";
      case DownloadableFileConstants.ServiceName.EMR:
        return "/usr/share/aws/emr/emrfs/conf/";
      default:
        return "/etc/" + serviceName.toLowerCase() + "/conf/";

    }
  }
}
