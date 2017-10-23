package com.epam.shim.configurator.modifier;

import com.epam.loader.common.util.CommonUtilException;
import com.epam.loader.common.util.CommonUtilHolder;
import com.epam.loader.config.credentials.SshCredentials;
import com.epam.loader.plan.manager.LoadConfigsManager;
import com.epam.shim.configurator.cluster.NamedClusterProperty;
import com.epam.shim.configurator.config.ModifierConfiguration;
import com.epam.shim.configurator.util.NamedClusterPropertyExtractingUtil;
import com.epam.shim.configurator.util.PropertyHandler;
import com.epam.shim.configurator.xml.XmlPropertyHandler;
import com.epam.spring.security.BaseSecurityContextHandler;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class ModifyTestProperties extends BaseSecurityContextHandler {

  private final static Logger logger = Logger.getLogger( ModifyTestProperties.class );

  public static void modifyAllTestProperties( ModifierConfiguration modifierConfiguration ) throws IOException {
    setSecuredValue( modifierConfiguration.getPathToTestProperties() + File.separator,
      modifierConfiguration.isSecure() );
    setShimActive( modifierConfiguration.getPathToTestProperties() + File.separator,
      modifierConfiguration.getPathToShim() + File.separator );
    setSshSeverUserPassword( modifierConfiguration.getPathToTestProperties() + File.separator,
      modifierConfiguration.getHosts() );
    if ( modifierConfiguration.getClusterType().equals( LoadConfigsManager.ClusterType.CDH )
      || modifierConfiguration.getClusterType().equals( LoadConfigsManager.ClusterType.HDP ) ) {
      setHdfsServerProtoPortUrl( modifierConfiguration.getPathToTestProperties() + File.separator,
        modifierConfiguration.getPathToShim() + File.separator );
      setJobTrackerServer( modifierConfiguration.getPathToTestProperties() + File.separator,
        modifierConfiguration.getPathToShim() + File.separator );
      setHiveHost( modifierConfiguration.getPathToTestProperties() + File.separator,
        modifierConfiguration.getHosts(), modifierConfiguration.getClusterType(),
        modifierConfiguration.isSecure(), modifierConfiguration.getPathToShim() + File.separator );
      setZookeeper( modifierConfiguration.getPathToTestProperties() + File.separator,
        modifierConfiguration.getPathToShim() + File.separator );
      setOozie( modifierConfiguration.getPathToTestProperties() + File.separator, modifierConfiguration.getHosts() );
      setSpark( modifierConfiguration.getPathToTestProperties() + File.separator,
        modifierConfiguration.getHosts().split( "," )[ 0 ].trim(),
        modifierConfiguration.getClusterType() );
      setHdpVersion( modifierConfiguration.getPathToShim(), modifierConfiguration.getClusterType(),
        modifierConfiguration.getHosts().split( "," )[ 0 ].trim() );
      setTextSplitter( modifierConfiguration.getPathToTestProperties() + File.separator,
        getShimVersion( modifierConfiguration.getPathToShim(),
          modifierConfiguration.getClusterType().toString().toLowerCase() ), modifierConfiguration.getClusterType() );
      setSqoopSecureLibjarPath( modifierConfiguration.getPathToTestProperties() + File.separator,
        modifierConfiguration.getPathToShim() + File.separator, modifierConfiguration.isSecure() );
      setHiveWarehouseDir( modifierConfiguration.getPathToTestProperties() + File.separator,
        modifierConfiguration.getPathToShim() + File.separator );
    }
  }


  // set secured value
  private static void setSecuredValue( String pathToTestProperties, boolean isSecure ) {
    if ( isSecure ) {
      PropertyHandler.setProperty( pathToTestProperties, "secure_cluster", "true" );
    } else {
      PropertyHandler.setProperty( pathToTestProperties, "secure_cluster", "false" );
    }
  }

  //set shim_active
  private static void setShimActive( String pathToTestProperties, String pathToShim ) {
    String[] shimFoldersTree = pathToShim.replace( File.separator, ":" ).split( ":" );
    PropertyHandler.setProperty( pathToTestProperties, "shim_active", shimFoldersTree[ shimFoldersTree.length - 1 ] );
  }

  // set sshServer, sshUser, sshPassword
  private static void setSshSeverUserPassword( String pathToTestProperties, String hosts ) {
    PropertyHandler.setProperty( pathToTestProperties, "sshServer", hosts.split( "," )[ 0 ].trim() );
    PropertyHandler.setProperty( pathToTestProperties, "sshUser",
      getCredentialsFromSecurityContext().getSshCredentials().getUsername() );
    PropertyHandler.setProperty( pathToTestProperties, "sshPassword",
      getCredentialsFromSecurityContext().getSshCredentials().getPassword() );
  }

  // set hdfsServer, hdfsProto, hdfsPort, hdfsUrl values
  private static void setHdfsServerProtoPortUrl( String pathToTestProperties, String pathToShim ) {
    String defaultFS = XmlPropertyHandler.readXmlPropertyValue( pathToShim + "core-site.xml",
      "fs.defaultFS" );
    String[] defaultFsSplitForHdfsProto = defaultFS.split( "://" );
    String[] defaultFsSplitForHdfsPort = defaultFsSplitForHdfsProto[ 1 ].split( ":" );
    PropertyHandler.setProperty( pathToTestProperties, "hdfsProto", defaultFsSplitForHdfsProto[ 0 ] );
    PropertyHandler.setProperty( pathToTestProperties, "hdfsServer", defaultFsSplitForHdfsPort[ 0 ] );
    if ( defaultFsSplitForHdfsPort.length == 1 ) {
      PropertyHandler.setProperty( pathToTestProperties, "hdfsPort", "" );
      PropertyHandler.setProperty( pathToTestProperties, "hdfsUrl", "${hdfsProto}://${hdfsServer}" );
    } else {
      PropertyHandler.setProperty( pathToTestProperties, "hdfsPort", defaultFsSplitForHdfsPort[ 1 ] );
      PropertyHandler.setProperty( pathToTestProperties, "hdfsUrl", "${hdfsProto}://${hdfsServer}:${hdfsPort}" );
    }
  }

  //add jobTrackerServer / jobTrackerPort
  //for hdp we take it from yarn.resourcemanager.address property
  private static void setJobTrackerServer( String pathToTestProperties, String pathToShim ) {
    NamedClusterProperty jobTrackerProperties =
      NamedClusterPropertyExtractingUtil.extractJobTrackerServer( pathToShim );
    PropertyHandler.setProperty( pathToTestProperties, "jobTrackerServer", jobTrackerProperties.getHost() );
    PropertyHandler.setProperty( pathToTestProperties, "jobTrackerPort", jobTrackerProperties.getPort() );
  }

  // determine hive host and set all values for it
  //TODO: Refactor this and other methods - need to create interface(abstract class?) for different hadoop vendors
  private static void setHiveHost( String pathToTestProperties, String hosts,
                                   LoadConfigsManager.ClusterType clusterType,
                                   boolean isSecure, String pathToShim ) {
    String hiveServerNode = "";
    try {
      for ( String node : hosts.split( "," ) ) {
        if ( CommonUtilHolder.sshCommonUtilInstance().executeCommand( new SshCredentials(), node, 22,
          "ps aux | grep HiveServer2" ).contains( "org.apache.hive.service.server.HiveServer2" ) ) {
          hiveServerNode = node;
        }
      }
      if ( !hiveServerNode.isEmpty() ) {
        PropertyHandler.setProperty( pathToTestProperties, "hive2_hostname", hiveServerNode );
        //If vendor is cdh - adding Impala properties, same as for hive
        if ( clusterType.equals( LoadConfigsManager.ClusterType.CDH ) ) {
          PropertyHandler.setProperty( pathToTestProperties, "impala_hostname", hiveServerNode );
        }
      } else {
        logger.error( "Hive node was not determined!!!" );
      }
      //if secured - add hive principal
      if ( isSecure ) {
        if ( clusterType.equals( LoadConfigsManager.ClusterType.CDH ) ) {

          String[] hivePrincipalTemp1 = XmlPropertyHandler.readXmlPropertyValue( pathToShim + "hive-site.xml",
            "hive.metastore.kerberos.principal" ).split( "/" );

          String[] hivePrincipalTemp2 = hivePrincipalTemp1[ 1 ].split( "@" );
          String hivePrincipal = hivePrincipalTemp1[ 0 ] + "/" + hiveServerNode + "@" + hivePrincipalTemp2[
            1 ];
          String impalaKrbServiceName = "";
          try {
            String fullImpalaConfig =
              new String( IOUtils.toByteArray( CommonUtilHolder.httpCommonUtilInstance().createHttpClient()
                .execute( CommonUtilHolder.httpCommonUtilInstance()
                  .createHttpUriRequest( "http://" + hosts.split( "," )[ 0 ].trim()
                    + ":7180/api/v10/clusters/cluster/services/impala/config?view=FULL" ) )
                .getEntity().getContent() ) );
            JSONObject obj = new JSONObject( fullImpalaConfig );
            JSONArray arr = obj.getJSONArray( "items" );
            for ( int i = 0; i < arr.length(); i++ ) {
              if ( arr.getJSONObject( i ).getString( "name" ).equalsIgnoreCase(
                "kerberos_princ_name" )
                ) {
                JSONObject obj2 = arr.getJSONObject( i );
                impalaKrbServiceName = obj2.getString( "default" );
                break;
              }
            }
          } catch ( JSONException | IOException e ) {
            logger.error( "JSON exception: " + e );
          }

          PropertyHandler.setProperty( pathToTestProperties, "hive2_option", "principal" );
          PropertyHandler.setProperty( pathToTestProperties, "hive2_principal", hivePrincipal );
          PropertyHandler.setProperty( pathToTestProperties, "impala_KrbRealm", hivePrincipalTemp2[ 1 ] );
          PropertyHandler.setProperty( pathToTestProperties, "impala_KrbHostFQDN", hiveServerNode );
          PropertyHandler.setProperty( pathToTestProperties, "impala_KrbServiceName",
            impalaKrbServiceName );
        } else {
          String[] hivePrincipalTemp1 = XmlPropertyHandler.readXmlPropertyValue( pathToShim + "hive-site.xml",
            "hive.metastore.kerberos.principal" ).split( "/" );
          String[] hivePrincipalTemp2 = hivePrincipalTemp1[ 1 ].split( "@" );
          String hivePrincipal = hivePrincipalTemp1[ 0 ] + "/" + hiveServerNode + "@" + hivePrincipalTemp2[
            1 ];

          PropertyHandler.setProperty( pathToTestProperties, "hive2_option", "principal" );
          PropertyHandler.setProperty( pathToTestProperties, "hive2_principal", hivePrincipal );

        }
      } else {
        PropertyHandler.setProperty( pathToTestProperties, "hive2_option", "" );
        PropertyHandler.setProperty( pathToTestProperties, "hive2_principal", "" );
        PropertyHandler.setProperty( pathToTestProperties, "impala_KrbRealm", "" );
        PropertyHandler.setProperty( pathToTestProperties, "impala_KrbHostFQDN", "" );
        PropertyHandler.setProperty( pathToTestProperties, "impala_KrbServiceName", "" );
      }
    } catch ( CommonUtilException e ) {
      logger.error( e.getMessage() );
    }
  }

  // add zookeeper host and port
  //for hdp it can be taken from "hadoop.registry.zk.quorum" property
  private static void setZookeeper( String pathToTestProperties, String pathToShim ) {
    NamedClusterProperty zookeeperProperty = NamedClusterPropertyExtractingUtil.extractZookeeper( pathToShim );

    // actual adding zookeeper host and port
    PropertyHandler.setProperty( pathToTestProperties, "zookeeper_host", zookeeperProperty.getHost() );
    PropertyHandler.setProperty( pathToTestProperties, "zookeeper_port", zookeeperProperty.getPort() );
  }

  // Adding Oozie oozie_server
  private static void setOozie( String pathToTestProperties, String hosts ) {
    String oozieHost = NamedClusterPropertyExtractingUtil.extractOozieHost( hosts );
    PropertyHandler.setProperty( pathToTestProperties, "oozie_server", oozieHost );
  }

  //find and set spark-assembly jar
  private static void setSpark( String pathToTestProperties, String host, LoadConfigsManager.ClusterType clusterType ) {
    try {
      String[] findSparkAssembly =
        CommonUtilHolder.sshCommonUtilInstance().executeCommand( new SshCredentials(), host, 22,
          "find / -name 'spark-assembly*'" ).split( "\\r|\\n" );
      String localSparkAssemblyPath = Arrays.stream( findSparkAssembly )
        .filter( singleFindSparkAssembly -> singleFindSparkAssembly.contains( "spark-assembly-" ) )
        .findFirst().orElse( StringUtils.EMPTY );
      // copy spark-assembly jar to hdfs and set spark_yarn_jar property
      CommonUtilHolder.sshCommonUtilInstance().executeCommand( new SshCredentials(), host, 22,
        ( "hadoop fs -copyFromLocal " + localSparkAssemblyPath + " /opt/pentaho" ) );
      File f = new File( localSparkAssemblyPath );
      String sparkAssemblyName = f.getName();
      PropertyHandler
        .setProperty( pathToTestProperties, "spark_yarn_jar", "${hdfsUrl}/opt/pentaho/"
          + sparkAssemblyName );
      // if it is hdp cluster - 2 more properties are needed
      if ( clusterType.equals( LoadConfigsManager.ClusterType.HDP ) ) {
        String hdpVersion = CommonUtilHolder.sshCommonUtilInstance().executeCommand(
          new SshCredentials(), host, 22,
          "hdp-select versions" ).replaceAll( "\\r|\\n", "" );
        PropertyHandler
          .setProperty( pathToTestProperties, "spark_driver_extraJavaOptions", "-Dhdp.version="
            + hdpVersion );
        PropertyHandler
          .setProperty( pathToTestProperties, "spark_yarn_am_extraJavaOptions", "-Dhdp.version="
            + hdpVersion );
      } else {
        PropertyHandler
          .setProperty( pathToTestProperties, "spark_driver_extraJavaOptions", "" );
        PropertyHandler
          .setProperty( pathToTestProperties, "spark_yarn_am_extraJavaOptions", "" );
      }
    } catch ( CommonUtilException e ) {
      logger.error( e.getMessage() );
    }

  }

  // TODO: determine if this is really needed
  private static String setHdpVersion( String pathToShim, LoadConfigsManager.ClusterType clusterType, String host ) {
    String configPropertiesFile = pathToShim + File.separator + "config.properties";
    String hdpVersion = StringUtils.EMPTY;
    if ( clusterType.equals( LoadConfigsManager.ClusterType.HDP ) ) {
      try {
        hdpVersion = CommonUtilHolder.sshCommonUtilInstance().executeCommand(
          new SshCredentials(), host, 22,
          "hdp-select versions" ).replaceAll( "\\r|\\n", "" );
        PropertyHandler.setProperty( configPropertiesFile, "java.system.hdp.version", hdpVersion );
      } catch ( CommonUtilException e ) {
        logger.error( e.getMessage() );
      }
    }

    return hdpVersion;
  }

  //modifying allow_text_splitter value
  private static void setTextSplitter( String pathToTestProperties, int hdpVersion,
                                       LoadConfigsManager.ClusterType clusterType ) {
    if ( clusterType.equals( LoadConfigsManager.ClusterType.HDP )
      && hdpVersion > 24 ) {
      PropertyHandler
        .setProperty( pathToTestProperties, "allow_text_splitter", "org.apache.sqoop.splitter.allow_text_splitter" );
      PropertyHandler.setProperty( pathToTestProperties, "allow_text_splitter_value", "true" );
    } else {
      PropertyHandler.setProperty( pathToTestProperties, "allow_text_splitter", "" );
      PropertyHandler.setProperty( pathToTestProperties, "allow_text_splitter_value", "" );
    }
  }

  private static int getShimVersion( String pathToShim, String shimName ) {
    String[] shimFoldersTree = pathToShim.replace( File.separator, ":" ).split( ":" );
    return Integer.valueOf( shimFoldersTree[ shimFoldersTree.length - 1 ].replace( shimName, "" ) );
  }

  //set sqoop_secure_libjar_path
  private static void setSqoopSecureLibjarPath( String pathToTestProperties, String pathToShim, boolean isSecure )
    throws IOException {
    if ( isSecure ) {
      try {
        // todo: add try/catch to check if this file exists
        String filename = Files.find( Paths.get( pathToShim + File.separator + "lib" ), 1,
          ( p, bfa ) -> bfa.isRegularFile() && p.getFileName().toString()
            .matches( "pentaho-hadoop-shims-.+?-security-.+?\\.jar" ) ).findFirst()
          .get().toAbsolutePath().normalize().toUri().toString();

        PropertyHandler.setProperty( pathToTestProperties, "sqoop_secure_libjar_path", filename );
      } catch ( FileNotFoundException fnfe ) {
        logger.error( fnfe );
      }
    } else {
      PropertyHandler.setProperty( pathToTestProperties, "sqoop_secure_libjar_path", "" );
    }
  }

  private static void setHiveWarehouseDir( String pathToTestProperties, String pathToShim ) {
    String hiveWarehouse = XmlPropertyHandler.readXmlPropertyValue( pathToShim + "hive-site.xml",
      "hive.metastore.warehouse.dir" );
    PropertyHandler.setProperty( pathToTestProperties, "hive_warehouse", hiveWarehouse );
  }

}
