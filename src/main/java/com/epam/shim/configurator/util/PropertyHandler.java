package com.epam.shim.configurator.util;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PropertyHandler {

  private final static Logger logger = Logger.getLogger( PropertyHandler.class );

  public static String getPropertyFromFile( String pathToFile, String property ) {
    // Read property file and return property value, return null if property was not found

    try ( InputStream inputStream = new FileInputStream( pathToFile ) ) {
      PropertiesConfiguration prop = new PropertiesConfiguration();
      prop.load( inputStream );
      Object resProperty = prop.getProperty( property );
      if ( resProperty == null ) {
        return null;
      }
      return castPropertyToString( resProperty );

    } catch ( IOException | ConfigurationException ex ) {
      logger.error( "Exception: " + ex );
    }

    return null;
  }

  public static void setProperty( String file, String property, String value ) {
    try ( InputStream inputStream = new FileInputStream( file ) ) {
      PropertiesConfiguration prop = new PropertiesConfiguration();
      prop.load( inputStream );
      prop.setProperty( property, value );

      try ( OutputStream outputStream = new FileOutputStream( file ) ) {
        prop.save( outputStream, null );
        logger.info( "property \"" + property + "\" is set to value \"" + value + "\" in file \"" + file + "\"" );
      } catch ( ConfigurationException ex ) {
        logger.error( ex );
      }
    } catch ( IOException | ConfigurationException ex ) {
      logger.error( "IOException: " + ex );
    }
  }

  public static String getPropertyFromByteSource( byte[] source, String property ) {
    // Read property file and return property value, return null if property was not found

    try ( InputStream inputStream = new ByteArrayInputStream( source ) ) {
      PropertiesConfiguration prop = new PropertiesConfiguration();
      prop.load( inputStream );
      Object resProperty = prop.getProperty( property );
      if ( resProperty == null ) {
        return null;
      }

      return castPropertyToString( resProperty );

    } catch ( IOException | ConfigurationException ex ) {
      logger.error( "Exception: " + ex );
    }

    return null;
  }

  private static String castPropertyToString( Object property ) {
    return property instanceof List ? makeStringFromListProperty( property ) : property.toString();
  }

  private static String makeStringFromListProperty( Object property ) {
    String stringProperty = property.toString();

    return Arrays.stream( stringProperty.substring( 1, stringProperty.length() - 1 ).split( "," ) )
      .map( String::trim ).collect( Collectors.joining( "," ) );
  }
}
