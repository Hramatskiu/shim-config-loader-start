package com.epam.loader.plan.strategy.impl;

import com.epam.loader.config.condition.DownloadableFile;
import com.epam.loader.config.condition.constant.DownloadableFileConstants;
import com.epam.spring.exception.StrategyException;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

public class MaprDefaultSearchStrategyTest {
  @Test(expected = StrategyException.class)
  public void resolveCommandResultWhenCommandResultIsEmptyStringShouldRaiseStrategyException() throws Exception {
    MaprDefaultSearchStrategy maprDefaultSearchStrategy = new MaprDefaultSearchStrategy();

    maprDefaultSearchStrategy.tryToResolveCommandResult( StringUtils.EMPTY, Collections.emptyList() );
  }

  @Test
  public void resolveCommandResultForHDFSWhenCommandResultNotEmptyShouldModifyDownloadableFileList() throws Exception {
    MaprDefaultSearchStrategy maprDefaultSearchStrategy = new MaprDefaultSearchStrategy();

    List<DownloadableFile> actualDownloadableFiles = maprDefaultSearchStrategy.resolveCommandResult( "Hadoop 2.7.0-mapr-1607\n/opt/mapr",
      Collections.singletonList( new DownloadableFile( DownloadableFileConstants.ServiceName.HDFS,
        Collections.singletonList( DownloadableFileConstants.ServiceFileName.HDFS ) ) ) );

    Assert.assertEquals( "/opt/mapr/hadoop/hadoop-2.7.0/etc/hadoop/", actualDownloadableFiles.get( 0 ).getDownloadPath() );
  }

  @Test
  public void resolveCommandResultForHBASEWhenCommandResultNotEmptyShouldModifyDownloadableFileList() throws Exception {
    MaprDefaultSearchStrategy maprDefaultSearchStrategy = new MaprDefaultSearchStrategy();

    List<DownloadableFile> actualDownloadableFiles = maprDefaultSearchStrategy.resolveCommandResult( "hbase-1.1.1\n/opt/mapr",
      Collections.singletonList( new DownloadableFile( DownloadableFileConstants.ServiceName.HBASE,
        Collections.singletonList( DownloadableFileConstants.ServiceFileName.HBASE ) ) ) );

    Assert.assertEquals( "/opt/mapr/hbase/hbase-1.1.1/conf/", actualDownloadableFiles.get( 0 ).getDownloadPath() );
  }

  @Test
  public void resolveCommandResultForHIVEWhenCommandResultNotEmptyShouldModifyDownloadableFileList() throws Exception {
    MaprDefaultSearchStrategy maprDefaultSearchStrategy = new MaprDefaultSearchStrategy();

    List<DownloadableFile> actualDownloadableFiles = maprDefaultSearchStrategy.resolveCommandResult( "hive-1.2\n/opt/mapr",
      Collections.singletonList( new DownloadableFile( DownloadableFileConstants.ServiceName.HIVE,
        Collections.singletonList( DownloadableFileConstants.ServiceFileName.HIVE ) ) ) );

    Assert.assertEquals( "/opt/mapr/hive/hive-1.2/conf/", actualDownloadableFiles.get( 0 ).getDownloadPath() );
  }

  @Test
  public void extractHadoopVersionFromCommandResultWhenCommandResultNotContainsMaprPrefixShouldReturnEmptyString() {
    MaprDefaultSearchStrategy maprDefaultSearchStrategy = new MaprDefaultSearchStrategy();

    Assert.assertTrue( maprDefaultSearchStrategy.extractHadoopVersionFromCommandResult( "test" ).isEmpty() );
  }

  @Test
  public void extractHadoopVersionFromCommandResultWhenCommandResultContainsMaprPrefixShouldReturnHadoopVersion() {
    MaprDefaultSearchStrategy maprDefaultSearchStrategy = new MaprDefaultSearchStrategy();

    Assert.assertEquals( "2.7.0", maprDefaultSearchStrategy.extractHadoopVersionFromCommandResult( "Hadoop 2.7.0-mapr-1607" ) );
  }

  @Test
  public void extractHiveHomeFromCommandResultWhenCommandResultNotContainsHivePrefixShouldReturnEmptyString() {
    MaprDefaultSearchStrategy maprDefaultSearchStrategy = new MaprDefaultSearchStrategy();

    Assert.assertTrue( maprDefaultSearchStrategy.extractHiveHomeDirFromCommandResult( "test" ).isEmpty() );
  }

  @Test
  public void extractHiveHomeFromCommandResultWhenCommandResultContainsHivePrefixShouldReturnHadoopVersion() {
    MaprDefaultSearchStrategy maprDefaultSearchStrategy = new MaprDefaultSearchStrategy();

    Assert.assertEquals( "hive-1.2", maprDefaultSearchStrategy.extractHiveHomeDirFromCommandResult( "hive-1.2" ) );
  }

  @Test
  public void extractHbaseHomeFromCommandResultWhenCommandResultNotContainsHbasePrefixShouldReturnEmptyString() {
    MaprDefaultSearchStrategy maprDefaultSearchStrategy = new MaprDefaultSearchStrategy();

    Assert.assertTrue( maprDefaultSearchStrategy.extractHbaseHomeDirFromCommandResult( "test" ).isEmpty() );
  }

  @Test
  public void extractHbaseHomeFromCommandResultWhenCommandResultContainsHbasePrefixShouldReturnHadoopVersion() {
    MaprDefaultSearchStrategy maprDefaultSearchStrategy = new MaprDefaultSearchStrategy();

    Assert.assertEquals( "hbase-1.1.1", maprDefaultSearchStrategy.extractHbaseHomeDirFromCommandResult( "hbase-1.1.1" ) );
  }
}