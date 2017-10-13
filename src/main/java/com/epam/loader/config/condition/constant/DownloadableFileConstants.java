package com.epam.loader.config.condition.constant;

public class DownloadableFileConstants {
  public static class ServiceName {
    public static final String HDFS = "hdfs";
    public static final String HIVE = "hive";
    public static final String HBASE = "hbase";
    public static final String YARN = "yarn";
    public static final String MAPREDUCE2 = "mapreduce2";
    public static final String EMR = "emr";
  }

  public static class ServiceFileName {
    public static final String HDFS = "hdfs-site.xml";
    public static final String CORE = "core-site.xml";
    public static final String HIVE = "hive-site.xml";
    public static final String HBASE = "hbase-site.xml";
    public static final String YARN = "yarn-site.xml";
    public static final String MAPRED = "mapred-site.xml";
    public static final String EMRFS = "emrfs-site.xml";
  }
}
