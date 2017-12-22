package com.epam.shim.configurator.modifier;

import com.epam.loader.common.util.CommonUtilException;
import com.epam.loader.common.util.CommonUtilHolder;
import com.epam.loader.config.credentials.SshCredentials;
import com.epam.shim.configurator.util.LocalProccessCommandExecutor;
import com.epam.shim.configurator.xml.XmlPropertyHandler;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class MaprConfigurator {
  final static Logger logger = Logger.getLogger( MaprConfigurator.class );

  public void configureMaprClient( boolean isSecure, String hosts, String pathToMapredFile ) {
    logger.info( "Configure local mapr client!" );
    String[] hostsArray = hosts.split( "," );
    String configureCommand = getCliCommand() + getConfigureCommand() + getClusterName( hostsArray[0] )
      + getNodeSetupCommand( isSecure )
      + Arrays.stream( hostsArray ).map( this::createCLDBString ).collect( Collectors.joining( ", " ) )
      + " -RM " + getMaprRMNode( hosts ) + " -HS " + extractMaprHSNode( hosts, pathToMapredFile );

    LocalProccessCommandExecutor.executeCommand( configureCommand );
  }

  public String createCLDBString( String host ) {
    return host + ":7222";
  }

  private String extractMaprHSNode( String hosts, String pathToMapredFile ) {
    String hsNode = getMaprHSNode( hosts );
    return hsNode.isEmpty() ? extractMaprHSNode( pathToMapredFile ) : hsNode;
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

  private String getMaprHSNode( String hosts ) {
    for ( String node : hosts.split( "," ) ) {
      try {
        if ( CommonUtilHolder.sshCommonUtilInstance().executeCommand( new SshCredentials(), node, 22,
          "ps aux | grep HistoryServer" )
          .contains( "org.apache.hadoop.mapreduce.v2.hs.JobHistoryServer" ) ) {
          return node;
        }
      } catch ( CommonUtilException e ) {
        logger.error( e.getMessage() );
      }
    }

    return StringUtils.EMPTY;
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

  private String getClusterName( String host ) {
    try {
      String dashboardInfo = new String( IOUtils.toByteArray( CommonUtilHolder.httpCommonUtilInstance().createHttpClient()
        .execute( CommonUtilHolder.httpCommonUtilInstance()
          .createHttpUriRequest( createMaprRestUriDashboardInfo( host ) ) )
        .getEntity().getContent() ) );
      JSONObject obj = new JSONObject( dashboardInfo );
      return obj.getJSONArray( "data" ).getJSONObject( 0 ).getJSONObject( "cluster" ).getString( "name" );
    } catch ( IOException | CommonUtilException | JSONException e ) {
      logger.warn( "Can't connect to mapr rest api to get cluster name. Verify rest api credentials. As name will use - " + host );
      return host;
    }
  }

  private String createMaprRestUriDashboardInfo( String host ) {
    return "https://" + host + ":8443/rest/dashboard/info";
  }
}
