package com.epam.shim.configurator;

import com.epam.loader.common.util.CheckingParamsUtil;
import com.epam.shim.configurator.config.ModifierConfiguration;
import com.epam.shim.configurator.modifier.AddCrossPlatform;
import com.epam.shim.configurator.modifier.ModifyPluginConfigProperties;
import com.epam.shim.configurator.modifier.ModifyTestProperties;
import com.epam.shim.configurator.util.CopyDriversUtil;
import com.epam.shim.configurator.xml.XmlPropertyHandler;

import java.io.IOException;

public class ShimDependentConfigurator {
  public static void configureShimProperties( ModifierConfiguration modifierConfiguration ) {
    AddCrossPlatform addCrossPlatform = new AddCrossPlatform();
    addCrossPlatform.addCrossPlatform( modifierConfiguration.getPathToShim() + "\\mapred-site.xml" );

    String secured = XmlPropertyHandler.readXmlPropertyValue( modifierConfiguration.getPathToShim() + "\\core-site.xml",
      "hadoop.security.authorization" );
    if ( secured != null && secured.equalsIgnoreCase( "true" ) ) {
      modifierConfiguration.setSecure( true );
    } else {
      modifierConfiguration.setSecure( false );
    }

    ModifyPluginConfigProperties modifyPluginConfigProperties = new ModifyPluginConfigProperties();
    modifyPluginConfigProperties.modifyPluginProperties( modifierConfiguration );

    if ( CheckingParamsUtil.checkParamsWithNullAndEmpty( modifierConfiguration.getPathToTestProperties() ) ) {
      try {
        ModifyTestProperties.modifyAllTestProperties( modifierConfiguration );
      } catch ( IOException e ) {
        e.printStackTrace();
      }
    }

    //CopyDriversUtil.copyAllDrivers( modifierConfiguration.getPathToShim() );
  }
}
