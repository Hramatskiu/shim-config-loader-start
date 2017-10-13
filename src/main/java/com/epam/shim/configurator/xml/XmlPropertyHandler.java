package com.epam.shim.configurator.xml;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class XmlPropertyHandler {

  private final static Logger logger = Logger.getLogger( XmlPropertyHandler.class );

  public static String readXmlPropertyValue( String pathToFile, String property ) {
    // Read *-site.xml file and return property value, return null if property was not found

    try {
      NodeList list = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse( pathToFile )
        .getElementsByTagName( "property" );
      for ( int i = 0; i < list.getLength(); i++ ) {
        Node node = list.item( i );

        if ( property.equals( node.getChildNodes().item( 1 ).getFirstChild().getNodeValue() ) ) {
          return node.getChildNodes().item( 3 ).getFirstChild().getNodeValue();
        }
      }

    } catch ( ParserConfigurationException | IOException | SAXException pce ) {
      logger.error( pce );
    }

    return null;
  }

  public Node createProperty( Document doc, String name, String value ) {
    Element property = doc.createElement( "property" );
    property.appendChild( createPropertyElements( doc, property, "name", name ) );
    property.appendChild( createPropertyElements( doc, property, "value", value ) );
    return property;
  }

  // utility method to create text node
  private Node createPropertyElements( Document doc, Element element, String name, String value ) {
    Element node = doc.createElement( name );
    node.appendChild( doc.createTextNode( value ) );
    return node;
  }

}
