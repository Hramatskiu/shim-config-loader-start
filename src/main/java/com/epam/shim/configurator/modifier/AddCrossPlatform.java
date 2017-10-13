package com.epam.shim.configurator.modifier;

import com.epam.shim.configurator.xml.XmlPropertyHandler;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;

public class AddCrossPlatform {

  private final static Logger logger = Logger.getLogger( AddCrossPlatform.class );

  public void addCrossPlatform( String pathToMapredSiteXML ) {

    try {
      DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
      Document doc = docBuilder.parse( pathToMapredSiteXML );

      Node configuration = doc.getElementsByTagName( "configuration" ).item( 0 );
      XmlPropertyHandler xmlPropertyHandler = new XmlPropertyHandler();
      configuration
        .appendChild( xmlPropertyHandler.createProperty( doc, "mapreduce.app-submission.cross-platform", "true" ) );

      // write the content into xml file
      Transformer transformer = TransformerFactory.newInstance().newTransformer();
      transformer.setOutputProperty( OutputKeys.INDENT, "yes" );
      DOMSource source = new DOMSource( doc );
      StreamResult result = new StreamResult( new File( pathToMapredSiteXML ) );
      transformer.transform( source, result );

      logger.info( "cross-platform added" );

    } catch ( ParserConfigurationException | TransformerException | IOException | SAXException pce ) {
      pce.printStackTrace();
    }
  }

}
