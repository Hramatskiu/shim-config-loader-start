package com.epam.spring.condition;

import org.apache.commons.lang.StringUtils;

import java.util.List;

public class DownloadableFile {
  private String serviceName;
  private List<String> files;
  private String downloadPath;

  public DownloadableFile( String serviceName, List<String> files ) {
    this.files = files;
    this.serviceName = serviceName;
    this.downloadPath = StringUtils.EMPTY;
  }

  public String getServiceName() {
    return serviceName;
  }

  public void setServiceName( String serviceName ) {
    this.serviceName = serviceName;
  }

  public List<String> getFiles() {
    return files;
  }

  public void setFiles( List<String> files ) {
    this.files = files;
  }

  public String getDownloadPath() {
    return downloadPath;
  }

  public void setDownloadPath( String downloadPath ) {
    this.downloadPath = downloadPath;
  }

  @Override
  public boolean equals( Object obj ) {
    if ( this == obj ) {
      return true;
    }

    if ( obj != null && obj instanceof DownloadableFile ) {
      DownloadableFile other = (DownloadableFile) obj;

      return serviceName != null && serviceName.equals( other.getServiceName() );
    }

    return false;
  }
}
