package com.epam.spring.plan.impl;

import com.epam.spring.condition.DownloadConfigsCondition;
import com.epam.spring.condition.DownloadableFile;
import com.epam.spring.constant.DownloadableFileConstants;
import com.epam.spring.function.DownloadFunction;
import com.epam.spring.plan.DownloadPlan;
import com.epam.spring.search.SearchStrategy;
import com.epam.spring.service.FileExtractingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;

@Component( "HDP" )
public class HDPDownloadPlan extends DownloadPlan {
  private static final String HTTP_POSTFIX = ":8080/api/v1/";
  private static final FileExtractingService.ExtractFormats EXTRACT_FORMATS = FileExtractingService.ExtractFormats.TAR;

  public HDPDownloadPlan( @Autowired @Qualifier( "http-download-function" ) DownloadFunction downloadFunction,
                          @Autowired @Qualifier( "hdp-rest-strategy" ) SearchStrategy searchStrategy ) {
    super( downloadFunction, searchStrategy );
  }

  protected LoadPathConfig createLoadPathConfig( String hostName, String destPrefix ) {
    return new LoadPathConfig( hostName + HTTP_POSTFIX, destPrefix, EXTRACT_FORMATS );
  }

  protected DownloadConfigsCondition createDownloadConfigsCondition() {
    DownloadConfigsCondition downloadConfigsCondition = new DownloadConfigsCondition();

    downloadConfigsCondition.addConfigFilesToMap( new DownloadableFile( DownloadableFileConstants.ServiceName.HDFS,
      Arrays
        .asList( DownloadableFileConstants.ServiceFileName.HDFS, DownloadableFileConstants.ServiceFileName.CORE ) ) );
    downloadConfigsCondition.addConfigFilesToMap( new DownloadableFile( DownloadableFileConstants.ServiceName.YARN,
      Collections.singletonList( DownloadableFileConstants.ServiceFileName.YARN ) ) );
    downloadConfigsCondition.addConfigFilesToMap( new DownloadableFile( DownloadableFileConstants.ServiceName.HBASE,
      Collections.singletonList( DownloadableFileConstants.ServiceFileName.HBASE ) ) );
    downloadConfigsCondition.addConfigFilesToMap( new DownloadableFile( DownloadableFileConstants.ServiceName.HIVE,
      Collections.singletonList( DownloadableFileConstants.ServiceFileName.HIVE ) ) );
    downloadConfigsCondition
      .addConfigFilesToMap( new DownloadableFile( DownloadableFileConstants.ServiceName.MAPREDUCE2,
        Collections.singletonList( DownloadableFileConstants.ServiceFileName.MAPRED ) ) );

    return downloadConfigsCondition;
  }
}
