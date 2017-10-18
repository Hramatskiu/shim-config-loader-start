package com.epam.shim.configurator.modifier;

import com.epam.shim.configurator.xml.XmlPropertyHandler;
import org.apache.log4j.Logger;

public class AddCrossPlatform {

  private final static Logger logger = Logger.getLogger( AddCrossPlatform.class );

  public void addCrossPlatform( String pathToMapredSiteXML ) {
    XmlPropertyHandler.addPropertyToFile( pathToMapredSiteXML, "mapreduce.app-submission.cross-platform", "true" );

    logger.info( "cross-platform added" );
  }

}
