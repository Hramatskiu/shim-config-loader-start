package com.epam.loader.plan.plan.impl;

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
@Qualifier( "MAPR" )
@Scope( "prototype" )
public class MAPRDownloadPlan extends DownloadPlan {
  @Autowired
  @Qualifier( "ssh-download-function" )
  private DownloadFunction downloadFunction;
  @Autowired
  @Qualifier( "mapr-default-strategy" )
  private SearchStrategy searchStrategy;
  @Autowired
  @Qualifier( "hadoop-classpath-strategy" )
  private SearchStrategy searchStrategy1;

  @Override public DownloadConfigsCondition downloadConfigs( String hostName, String destPrefix,
                                                             DownloadConfigsCondition downloadConfigsCondition ) {
    setDownloadFunction( this.downloadFunction );
    setupSearchStrategies( this.searchStrategy );
    return super.downloadConfigs( hostName, destPrefix, downloadConfigsCondition );
  }

  @Override
  protected LoadPathConfig createLoadPathConfig( String hostName, String destPrefix ) {
    return new LoadPathConfig( hostName, destPrefix, null );
  }

  @Override
  protected DownloadConfigsCondition createDownloadConfigsCondition() {
    DownloadConfigsCondition downloadConfigsCondition = new DownloadConfigsCondition();

    downloadConfigsCondition.addConfigFilesToMap( new DownloadableFile( DownloadableFileConstants.ServiceName.HDFS,
      Arrays
        .asList( DownloadableFileConstants.ServiceFileName.HDFS, DownloadableFileConstants.ServiceFileName.CORE ) ) );
    downloadConfigsCondition.addConfigFilesToMap( new DownloadableFile( DownloadableFileConstants.ServiceName.YARN,
      Collections.singletonList( DownloadableFileConstants.ServiceFileName.YARN ) ) );
    downloadConfigsCondition
      .addConfigFilesToMap( new DownloadableFile( DownloadableFileConstants.ServiceName.MAPREDUCE2,
        Collections.singletonList( DownloadableFileConstants.ServiceFileName.MAPRED ) ) );
    downloadConfigsCondition.addConfigFilesToMap( new DownloadableFile( DownloadableFileConstants.ServiceName.HBASE,
      Collections.singletonList( DownloadableFileConstants.ServiceFileName.HBASE ) ) );
    downloadConfigsCondition.addConfigFilesToMap( new DownloadableFile( DownloadableFileConstants.ServiceName.HIVE,
      Collections.singletonList( DownloadableFileConstants.ServiceFileName.HIVE ) ) );
    downloadConfigsCondition
      .addConfigFilesToMap( new DownloadableFile( DownloadableFileConstants.ServiceName.SSL_TRUSTSTORE,
        Collections.singletonList( DownloadableFileConstants.ServiceFileName.SSL_TRUSTSTORE ) ) );

    return downloadConfigsCondition;
  }
}
