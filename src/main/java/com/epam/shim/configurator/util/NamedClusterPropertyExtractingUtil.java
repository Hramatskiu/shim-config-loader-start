package com.epam.shim.configurator.util;

import com.epam.loader.common.util.CheckingParamsUtil;
import com.epam.loader.common.util.CommonUtilException;
import com.epam.loader.common.util.CommonUtilHolder;
import com.epam.loader.config.credentials.SshCredentials;
import com.epam.shim.configurator.cluster.NamedClusterProperty;
import com.epam.shim.configurator.xml.XmlPropertyHandler;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class NamedClusterPropertyExtractingUtil {
  public static NamedClusterProperty extractZookeeper( String pathToShim ) {
    String zkQuorum = "";
    String zkQuorumRes = "";
    String zkPort = "";

    if ( XmlPropertyHandler.readXmlPropertyValue( pathToShim + "yarn-site.xml",
      "hadoop.registry.zk.quorum" ) != null ) {
      zkQuorum = XmlPropertyHandler.readXmlPropertyValue( pathToShim + "yarn-site.xml",
        "hadoop.registry.zk.quorum" );
      //for cdh it can be taken from "yarn.resourcemanager.zk-address" property
    } else if ( XmlPropertyHandler.readXmlPropertyValue( pathToShim + "yarn-site.xml",
      "yarn.resourcemanager.zk-address" ) != null ) {
      zkQuorum = XmlPropertyHandler.readXmlPropertyValue( pathToShim + "yarn-site.xml",
        "yarn.resourcemanager.zk-address" );
    }
    // parsing zookeeper address if we have it
    if ( CheckingParamsUtil.checkParamsWithNullAndEmpty( zkQuorum ) ) {
      String[] zkQuorumTemp1 = zkQuorum.split( "[,]" );
      ArrayList<String> zkQuorumArrayRes = new ArrayList<>();
      for ( String aZkQuorumTemp1 : zkQuorumTemp1 ) {
        String[] zTemp = aZkQuorumTemp1.split( ":" );
        zkQuorumArrayRes.add( zTemp[ 0 ] );
        zkPort = zTemp[ 1 ];
      }
      zkQuorumRes = String.join( ",", zkQuorumArrayRes );
    }

    if ( !CheckingParamsUtil.checkParamsWithNullAndEmpty( zkQuorum )
      && XmlPropertyHandler.readXmlPropertyValue( pathToShim
      + "hbase-site.xml", "hbase.zookeeper.quorum" ) != null ) {
      zkQuorumRes = XmlPropertyHandler.readXmlPropertyValue( pathToShim + "hbase-site.xml",
        "hbase.zookeeper.quorum" );
      zkPort = XmlPropertyHandler.readXmlPropertyValue( pathToShim + "hbase-site.xml",
        "hbase.zookeeper.property.clientPort" );
    }

    return new NamedClusterProperty( zkQuorumRes, zkPort );
  }

  public static NamedClusterProperty extractJobTrackerServer( String pathToShim ) {
    String[] rmAddress = null;
    if ( XmlPropertyHandler.readXmlPropertyValue( pathToShim + "yarn-site.xml",
      "yarn.resourcemanager.address" ) != null ) {
      rmAddress = XmlPropertyHandler.readXmlPropertyValue( pathToShim + "yarn-site.xml",
        "yarn.resourcemanager.address" ).split( ":" );
    } else if ( XmlPropertyHandler.readXmlPropertyValue( pathToShim + "yarn-site.xml",
      "yarn.resourcemanager.ha.rm-ids" ) != null ) {
      //for cdh we take it from yarn.resourcemanager.address.someAlias , aliases can be found in yarn.resourcemanager
      // .ha.rm-ids
      String[] rmAlias = XmlPropertyHandler.readXmlPropertyValue( pathToShim + "yarn-site.xml",
        "yarn.resourcemanager.ha.rm-ids" ).split( "[,]" );
      rmAddress = XmlPropertyHandler.readXmlPropertyValue( pathToShim + "yarn-site.xml",
        "yarn.resourcemanager.address" + "." + rmAlias[ 0 ] ).split( ":" );
    }

    return rmAddress != null && rmAddress.length > 1 ? new NamedClusterProperty( rmAddress[ 0 ], rmAddress[ 1 ] ) :
      new NamedClusterProperty(
        StringUtils.EMPTY, StringUtils.EMPTY );
  }

  public static NamedClusterProperty extractHdfsServerProtoPortUrl( String pathToShim ) {
    String defaultFS = XmlPropertyHandler.readXmlPropertyValue( pathToShim + "core-site.xml",
      "fs.defaultFS" );
    String hdfsPort = StringUtils.EMPTY;
    String hdfsHost = StringUtils.EMPTY;
    if ( CheckingParamsUtil.checkParamsWithNullAndEmpty( defaultFS ) ) {
      String[] defaultFsSplitForHdfsProto = defaultFS.split( "://" );
      String[] defaultFsSplitForHdfsPort = defaultFsSplitForHdfsProto[ 1 ].split( ":" );
      hdfsHost = defaultFsSplitForHdfsPort[ 0 ];
      if ( defaultFsSplitForHdfsPort.length > 1 ) {
        hdfsPort = defaultFsSplitForHdfsPort[ 1 ];
      }
    }

    return new NamedClusterProperty( hdfsHost, hdfsPort );
  }

  @SuppressWarnings( "unchecked" )
  public static String extractOozieUrl( String hosts ) {
    return Arrays.stream( hosts.split( "," ) ).map( host -> CompletableFuture.supplyAsync( () -> {
      try {
        String oozieResult =
          CommonUtilHolder.sshCommonUtilInstance().executeCommand( new SshCredentials(), host.trim(), 22,
            "ps aux | grep oozie" );
        List<String> oozieBaseUrl;
        if ( oozieResult != null && !( oozieBaseUrl = Arrays.stream( oozieResult.split( " " ) )
          .filter(
            splitted -> splitted.contains( "-Doozie.http.port=" ) || splitted.contains( "-Doozie.http.hostname=" ) )
          .collect( Collectors.toList() ) ).isEmpty() ) {
          String ooziePort;
          String oozieHost;

          if ( oozieBaseUrl.get( 0 ).contains( "-Doozie.http.port=" ) ) {
            ooziePort = oozieBaseUrl.get( 0 ).split( "=" )[ 1 ];
            oozieHost = oozieBaseUrl.get( 1 ).split( "=" )[ 1 ];
          } else {
            ooziePort = oozieBaseUrl.get( 1 ).split( "=" )[ 1 ];
            oozieHost = oozieBaseUrl.get( 0 ).split( "=" )[ 1 ];
          }

          return "http://" + oozieHost + ":" + ooziePort + "/oozie";
        }
      } catch ( CommonUtilException e ) {
        e.printStackTrace();
      }
      return StringUtils.EMPTY;
    } ) ).map( CompletableFuture::join ).filter( result -> !result.isEmpty() ).findFirst().orElse( StringUtils.EMPTY );
  }

  @SuppressWarnings( "unchecked" )
  public static String extractOozieHost( String hosts ) {
    return Arrays.stream( hosts.split( "," ) ).map( host -> CompletableFuture.supplyAsync( () -> {
      try {
        String oozieResult =
          CommonUtilHolder.sshCommonUtilInstance().executeCommand( new SshCredentials(), host.trim(), 22,
            "ps aux | grep oozie" );
        String oozieBaseUrl;
        if ( oozieResult != null && !( oozieBaseUrl =
          Arrays.stream( oozieResult.split( " " ) ).filter( splitted -> splitted.contains( "-Doozie.http.hostname=" ) )
            .collect( Collectors.joining( "" ) ) ).isEmpty() ) {
          return host;
        }
      } catch ( CommonUtilException e ) {
        e.printStackTrace();
      }
      return StringUtils.EMPTY;
    } ) ).map( CompletableFuture::join ).filter( result -> !result.isEmpty() ).findFirst().orElse( StringUtils.EMPTY );
  }
}
