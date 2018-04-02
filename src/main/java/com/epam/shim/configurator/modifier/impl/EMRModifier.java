package com.epam.shim.configurator.modifier.impl;

import com.epam.loader.config.credentials.EmrCredentials;
import com.epam.shim.configurator.config.ModifierConfiguration;
import com.epam.shim.configurator.modifier.IShimModifier;
import com.epam.shim.configurator.xml.XmlPropertyHandler;

import java.io.File;

public class EMRModifier implements IShimModifier {
  private EmrCredentials emrCredentials;

  public EMRModifier( EmrCredentials emrCredentials ) {
    this.emrCredentials = emrCredentials;
  }

  @Override public void modifyShim( ModifierConfiguration modifierConfiguration, String configPropertiesFile ) {
    setEmrSecureProperties( modifierConfiguration.getPathToShim(),
      emrCredentials.getSecretKey(), emrCredentials.getAccessKey() );
    setEmrFsImpl( modifierConfiguration.getPathToShim() );
    removeEmrCodecs( modifierConfiguration.getPathToShim() );
  }

  private void setEmrSecureProperties( String pathToShim, String secretKey, String accessKey ) {
    XmlPropertyHandler
      .addOrModifyIfExistsProperty( pathToShim + File.separator + "core-site.xml", "fs.s3.awsAccessKeyId", accessKey );
    XmlPropertyHandler
      .addOrModifyIfExistsProperty( pathToShim + File.separator + "core-site.xml", "fs.s3.awsSecretAccessKey", secretKey );
    XmlPropertyHandler
      .addOrModifyIfExistsProperty( pathToShim + File.separator + "core-site.xml", "fs.s3n.awsAccessKeyId", accessKey );
    XmlPropertyHandler
      .addOrModifyIfExistsProperty( pathToShim + File.separator + "core-site.xml", "fs.s3n.awsSecretAccessKey", secretKey );
    XmlPropertyHandler
      .addOrModifyIfExistsProperty( pathToShim + File.separator + "core-site.xml", "fs.s3a.access.key", accessKey );
    XmlPropertyHandler
      .addOrModifyIfExistsProperty( pathToShim + File.separator + "core-site.xml", "fs.s3a.secret.key", secretKey );
  }

  private void setEmrFsImpl( String pathToShim ) {
    XmlPropertyHandler.addOrModifyIfExistsProperty( pathToShim + File.separator + "core-site.xml",
      "fs.s3.impl", "org.apache.hadoop.fs.s3.S3FileSystem" );
    XmlPropertyHandler.addOrModifyIfExistsProperty( pathToShim + File.separator + "core-site.xml",
      "fs.s3n.impl", "org.apache.hadoop.fs.s3native.NativeS3FileSystem" );
    XmlPropertyHandler.addOrModifyIfExistsProperty( pathToShim + File.separator + "core-site.xml",
      "fs.s3a.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem" );
  }

  private void removeEmrCodecs( String pathToShim ) {
    XmlPropertyHandler.deletePropertyInFile( pathToShim + File.separator + "core-site.xml", "io.compression.codecs" );
    XmlPropertyHandler
      .deletePropertyInFile( pathToShim + File.separator + "core-site.xml", "io.compression.codec.lzo.class" );
  }
}
