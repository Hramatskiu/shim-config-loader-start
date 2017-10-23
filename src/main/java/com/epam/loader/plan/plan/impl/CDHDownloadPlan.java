package com.epam.loader.plan.plan.impl;

import com.epam.loader.common.service.FileExtractingService;
import com.epam.loader.config.condition.DownloadConfigsCondition;
import com.epam.loader.config.condition.DownloadableFile;
import com.epam.loader.config.condition.constant.DownloadableFileConstants;
import com.epam.loader.plan.function.DownloadFunction;
import com.epam.loader.plan.plan.DownloadPlan;
import com.epam.loader.plan.strategy.SearchStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;

@Component()
@Qualifier( "CDH" )
@Scope( "prototype" )
public class CDHDownloadPlan extends DownloadPlan {
  private static final String HTTP_POSTFIX = ":7180/api/v10/";
  private static final FileExtractingService.ExtractFormats EXTRACT_FORMATS = FileExtractingService.ExtractFormats.ZIP;
  @Autowired
  @Qualifier( "http-download-function" )
  private DownloadFunction downloadFunction;
  @Autowired
  @Qualifier( "cdh-rest-strategy" )
  private SearchStrategy searchStrategy;

  @Override public DownloadConfigsCondition downloadConfigs( String hostName, String destPrefix,
                                                             DownloadConfigsCondition downloadConfigsCondition ) {
    setDownloadFunction( this.downloadFunction );
    setupSearchStrategies( this.searchStrategy );
    return super.downloadConfigs( hostName, destPrefix, downloadConfigsCondition );
  }

  @Override
  protected LoadPathConfig createLoadPathConfig( String hostName, String destPrefix ) {
    return new LoadPathConfig( hostName + HTTP_POSTFIX, destPrefix, EXTRACT_FORMATS );
  }

  protected DownloadConfigsCondition createDownloadConfigsCondition() {
    DownloadConfigsCondition downloadConfigsCondition = new DownloadConfigsCondition();

    downloadConfigsCondition.addConfigFilesToMap( new DownloadableFile( DownloadableFileConstants.ServiceName.HDFS,
      Arrays
        .asList( DownloadableFileConstants.ServiceFileName.HDFS, DownloadableFileConstants.ServiceFileName.CORE ) ) );
    downloadConfigsCondition.addConfigFilesToMap( new DownloadableFile( DownloadableFileConstants.ServiceName.YARN,
      Arrays
        .asList( DownloadableFileConstants.ServiceFileName.YARN, DownloadableFileConstants.ServiceFileName.MAPRED ) ) );
    downloadConfigsCondition.addConfigFilesToMap( new DownloadableFile( DownloadableFileConstants.ServiceName.HBASE,
      Collections.singletonList( DownloadableFileConstants.ServiceFileName.HBASE ) ) );
    downloadConfigsCondition.addConfigFilesToMap( new DownloadableFile( DownloadableFileConstants.ServiceName.HIVE,
      Collections.singletonList( DownloadableFileConstants.ServiceFileName.HIVE ) ) );

    return downloadConfigsCondition;
  }
}
