package com.epam.spring.plan.impl;

import com.epam.spring.condition.DownloadConfigsCondition;
import com.epam.spring.condition.DownloadableFile;
import com.epam.spring.constant.DownloadableFileConstants;
import com.epam.spring.function.DownloadFunction;
import com.epam.spring.plan.DownloadPlan;
import com.epam.spring.search.SearchStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;

@Component( "mapr-plan" )
public class MAPRDownloadPlan extends DownloadPlan {
  protected MAPRDownloadPlan( @Autowired @Qualifier( "ssh-download-function" ) DownloadFunction downloadFunction,
                              @Autowired @Qualifier( "MAPR" ) SearchStrategy searchStrategy ) {
    super( downloadFunction, searchStrategy );
  }

  @Override
  protected LoadPathConfig createLoadPathConfig( String hostName, String destPrefix ) {
    return new LoadPathConfig( hostName, destPrefix, null );
  }

  @Override
  protected DownloadConfigsCondition createDownloadConfigsCondition() {
    DownloadConfigsCondition downloadConfigsCondition = new DownloadConfigsCondition();

    downloadConfigsCondition.addConfigFilesToMap( new DownloadableFile( DownloadableFileConstants.ServiceName.HDFS,
      Arrays.asList( DownloadableFileConstants.ServiceFileName.HDFS, DownloadableFileConstants.ServiceFileName.CORE ) ) );
    downloadConfigsCondition.addConfigFilesToMap( new DownloadableFile( DownloadableFileConstants.ServiceName.YARN,
      Collections.singletonList( DownloadableFileConstants.ServiceFileName.YARN ) ) );
    downloadConfigsCondition.addConfigFilesToMap( new DownloadableFile( DownloadableFileConstants.ServiceName.MAPREDUCE2,
      Collections.singletonList( DownloadableFileConstants.ServiceFileName.MAPRED ) ) );
    downloadConfigsCondition.addConfigFilesToMap( new DownloadableFile( DownloadableFileConstants.ServiceName.HBASE,
      Collections.singletonList( DownloadableFileConstants.ServiceFileName.HBASE ) ) );
    downloadConfigsCondition.addConfigFilesToMap( new DownloadableFile( DownloadableFileConstants.ServiceName.HIVE,
      Collections.singletonList( DownloadableFileConstants.ServiceFileName.HIVE ) ) );

    return downloadConfigsCondition;
  }
}
