package com.epam.shim.configurator.config;

import org.apache.commons.lang.StringUtils;

public class MaprSecureIdConfiguration {
  private String uid;
  private String gid;
  private String name;

  public MaprSecureIdConfiguration() {
    this.uid = StringUtils.EMPTY;
    this.gid = StringUtils.EMPTY;
    this.name = StringUtils.EMPTY;
  }

  public MaprSecureIdConfiguration( String uid, String gid, String name ) {
    this.uid = uid;
    this.gid = gid;
    this.name = name;
  }

  public String getUid() {
    return uid;
  }

  public void setUid( String uid ) {
    this.uid = uid;
  }

  public String getGid() {
    return gid;
  }

  public void setGid( String gid ) {
    this.gid = gid;
  }

  public String getName() {
    return name;
  }

  public void setName( String name ) {
    this.name = name;
  }
}
