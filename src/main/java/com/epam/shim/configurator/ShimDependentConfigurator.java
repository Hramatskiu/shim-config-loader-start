package com.epam.shim.configurator;

import com.epam.loader.common.util.CheckingParamsUtil;
import com.epam.loader.config.credentials.EmrCredentials;
import com.epam.shim.configurator.cluster.NamedClusterCreator;
import com.epam.shim.configurator.config.ModifierConfiguration;
import com.epam.shim.configurator.modifier.ModifyPluginConfigProperties;
import com.epam.shim.configurator.modifier.ModifyTestProperties;
import com.epam.shim.configurator.xml.XmlPropertyHandler;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

public class ShimDependentConfigurator {
  private final static Logger logger = Logger.getLogger( ShimDependentConfigurator.class );

  public static void configureShimProperties( ModifierConfiguration modifierConfiguration,
                                              EmrCredentials emrCredentials, String namedClusterName ) {
    if ( System.getProperty( "os.name" ).startsWith( "Windows" ) ) {
      XmlPropertyHandler.addPropertyToFile( modifierConfiguration.getPathToShim() + File.separator + "mapred-site.xml",
        "mapreduce.app-submission.cross-platform", "true" );
      logger.info( "cross-platform added" );
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
        logger.error( e.getMessage() );
      }
    }

    NamedClusterCreator.createNamedCluster( modifierConfiguration, namedClusterName );
  }
}
