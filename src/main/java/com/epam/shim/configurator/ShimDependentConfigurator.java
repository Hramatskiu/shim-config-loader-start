package com.epam.shim.configurator;

import com.epam.loader.common.util.CheckingParamsUtil;
import com.epam.loader.config.credentials.EmrCredentials;
import com.epam.shim.configurator.cluster.NamedClusterCreator;
import com.epam.shim.configurator.config.ModifierConfiguration;
import com.epam.shim.configurator.modifier.AddCrossPlatform;
import com.epam.shim.configurator.modifier.ModifyPluginConfigProperties;
import com.epam.shim.configurator.modifier.ModifyTestProperties;
import com.epam.shim.configurator.util.CopyDriversUtil;
import com.epam.shim.configurator.xml.XmlPropertyHandler;

import java.io.File;
import java.io.IOException;

public class ShimDependentConfigurator {
  public static void configureShimProperties( ModifierConfiguration modifierConfiguration,
                                              EmrCredentials emrCredentials ) {
    if ( System.getProperty( "os.name" ).startsWith( "Windows" ) ) {
      AddCrossPlatform addCrossPlatform = new AddCrossPlatform();
      addCrossPlatform.addCrossPlatform( modifierConfiguration.getPathToShim() + File.separator + "mapred-site.xml" );
    }

    String secured =
      XmlPropertyHandler.readXmlPropertyValue( modifierConfiguration.getPathToShim() + File.separator + "core-site.xml",
        "hadoop.security.authorization" );
    if ( secured != null && secured.equalsIgnoreCase( "true" ) ) {
      modifierConfiguration.setSecure( true );
    } else {
      modifierConfiguration.setSecure( false );
    }

    ModifyPluginConfigProperties modifyPluginConfigProperties = new ModifyPluginConfigProperties();
    modifyPluginConfigProperties.modifyPluginProperties( modifierConfiguration, emrCredentials );

    if ( CheckingParamsUtil.checkParamsWithNullAndEmpty( modifierConfiguration.getPathToTestProperties() ) ) {
      try {
        ModifyTestProperties.modifyAllTestProperties( modifierConfiguration );
      } catch ( IOException e ) {
        e.printStackTrace();
      }
    }

    NamedClusterCreator.createNamedCluster( modifierConfiguration );
    CopyDriversUtil.copyAllDrivers( modifierConfiguration.getPathToShim(), modifierConfiguration.getClusterType() );
  }
}
