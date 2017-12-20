package com.epam.loader.common.holder;

import com.epam.loader.common.util.ByteCopierUtil;

public class DownloadedFileWrapper {
  private byte[] byteFileContent;
  private String stringFileContent;

  public DownloadedFileWrapper( byte[] byteFileContent ) {
    this.byteFileContent = ByteCopierUtil.addBytesToArray( null, byteFileContent, byteFileContent.length );
  }

  public DownloadedFileWrapper( String stringFileContent ) {
    this.stringFileContent = stringFileContent;
  }

  public void setByteFileContent( byte[] byteFileContent ) {
    this.byteFileContent = ByteCopierUtil.addBytesToArray( null, byteFileContent, byteFileContent.length );
  }

  public void setStringFileContent( String stringFileContent ) {
    this.stringFileContent = stringFileContent;
  }

  public byte[] getByteFileContent() {
    return byteFileContent;
  }

  public String getStringFileContent() {
    return stringFileContent;
  }

  public boolean isEmpty() {
    return isByteContentEmpty() && isStringContentEmpty();
  }

  public boolean isByteContentEmpty() {
    return byteFileContent == null || byteFileContent.length < 1;
  }

  public boolean isStringContentEmpty() {
    return stringFileContent == null || stringFileContent.isEmpty();
  }
}
