package com.epam.loader.plan.strategy.impl;

import com.epam.loader.config.condition.DownloadableFile;
import com.epam.loader.config.condition.constant.DownloadableFileConstants;
import com.epam.loader.plan.strategy.StrategyException;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

public class CdhSearchStrategyTest {
  @Test
  public void extractClusterNameFromCommandResultWhenCommandResultIsEmptyShouldReturnEmptyString() throws Exception {
    CdhSearchStrategy cdhSearchStrategy = new CdhSearchStrategy();

    Assert.assertTrue( cdhSearchStrategy.extractClusterNameFromCommandResult( StringUtils.EMPTY ).isEmpty() );
  }

  @Test(expected = StrategyException.class)
  public void extractClusterNameFromCommandResultWhenCommandResultIsNotJsonStringShouldRaiseStrategyException() throws Exception {
    CdhSearchStrategy cdhSearchStrategy = new CdhSearchStrategy();

    Assert.assertTrue( cdhSearchStrategy.extractClusterNameFromCommandResult( "no json" ).isEmpty() );
  }

  @Test(expected = StrategyException.class)
  public void extractClusterNameFromCommandResultWhenCommandResultIsNotValidJsonStringShouldRaiseStrategyException() throws Exception {
    CdhSearchStrategy cdhSearchStrategy = new CdhSearchStrategy();

    Assert.assertTrue( cdhSearchStrategy.extractClusterNameFromCommandResult( "{no: json}" ).isEmpty() );
  }

  @Test
  public void extractClusterNameFromCommandResultWhenCommandResultIsValidJsonStringShouldReturnClusterName() throws Exception {
    CdhSearchStrategy cdhSearchStrategy = new CdhSearchStrategy();

    Assert.assertEquals( "cluster", cdhSearchStrategy.extractClusterNameFromCommandResult( "{items: [{name: cluster}]}" ) );
  }

  @Test
  public void resolveCommandResultForHDFSWhenCommandResultNotEmptyShouldModifyDownloadableFileList() throws Exception {
    CdhSearchStrategy cdhSearchStrategy = new CdhSearchStrategy();

    List<DownloadableFile> actualDownloadableFiles = cdhSearchStrategy.resolveCommandResult( "{items: [{name: cluster}]}",
      Collections.singletonList( new DownloadableFile( DownloadableFileConstants.ServiceName.HDFS,
        Collections.singletonList( DownloadableFileConstants.ServiceFileName.HDFS ) ) ) );

    Assert.assertEquals( "clusters/cluster/services/hdfs/clientConfig", actualDownloadableFiles.get( 0 ).getDownloadPath() );
  }

  @Test
  public void resolveCommandResultWhenCommandResultNotValidClusterNameShouldReturnEmptyList() throws Exception {
    CdhSearchStrategy cdhSearchStrategy = new CdhSearchStrategy();

    List<DownloadableFile> actualDownloadableFiles = cdhSearchStrategy.resolveCommandResult( "{items: [{name: \"\"}]}",
      Collections.singletonList( new DownloadableFile( DownloadableFileConstants.ServiceName.HDFS,
        Collections.singletonList( DownloadableFileConstants.ServiceFileName.HDFS ) ) ) );

    Assert.assertTrue( actualDownloadableFiles.isEmpty() );
  }
}