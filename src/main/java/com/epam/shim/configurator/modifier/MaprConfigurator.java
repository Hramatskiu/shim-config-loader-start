package com.epam.shim.configurator.modifier;

import com.epam.loader.common.util.CommonUtilException;
import com.epam.loader.common.util.CommonUtilHolder;
import com.epam.loader.config.credentials.SshCredentials;
import com.epam.shim.configurator.util.LocalProccessCommandExecutor;
import com.epam.shim.configurator.xml.XmlPropertyHandler;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.stream.Collectors;

public class MaprConfigurator {
  final static Logger logger = Logger.getLogger( MaprConfigurator.class );

  public void configureMaprClient( boolean isSecure, String hosts, String pathToMapredFile ) {
    logger.info( "Configure local mapr client!" );
    String[] hostsArray = hosts.split( "," );
    String configureCommand = getCliCommand() + getConfigureCommand() + hostsArray[ 0 ]
      + getNodeSetupCommand( isSecure )
      + Arrays.stream( hostsArray ).map( this::createCLDBString ).collect( Collectors.joining( ", " ) )
      + " -RM " + getMaprRMNode( hosts ) + " -HS " + extractMaprHSNode( pathToMapredFile );

    LocalProccessCommandExecutor.executeCommand( configureCommand );
  }

  public String createCLDBString( String host ) {
    return host + ":7222";
  }

  private String extractMaprHSNode( String pathToMapredFile ) {
    return XmlPropertyHandler.readXmlPropertyValue( pathToMapredFile, "mapreduce.jobhistory.address" )
      .split( ":" )[ 0 ];
  }

  private String getCliCommand() {
    return System.getProperty( "os.name" ).startsWith( "Windows" ) ? "cmd /c " : StringUtils.EMPTY;
  }

  private String getNodeSetupCommand( boolean isSecure ) {
    return isSecure ? " -c -secure -C " : " -c -C ";
  }

  private String getConfigureCommand() {
    return System.getProperty( "os.name" ).startsWith( "Windows" ) ? "%MAPR_HOME%\\server\\configure.bat -N "
      : "/opt/mapr/server/configure.sh -N ";
  }

  private String getMaprRMNode( String hosts ) {
    for ( String node : hosts.split( "," ) ) {
      try {
        if ( CommonUtilHolder.sshCommonUtilInstance().executeCommand( new SshCredentials(), node, 22,
          "ps aux | grep ResourceManager" )
          .contains( "org.apache.hadoop.yarn.server.resourcemanager.ResourceManager" ) ) {
          return node;
        }
      } catch ( CommonUtilException e ) {
        logger.error( e.getMessage() );
      }
    }

    return StringUtils.EMPTY;
  }
}
