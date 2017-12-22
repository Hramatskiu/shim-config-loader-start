package com.epam.shim.configurator.cluster;

import com.epam.loader.plan.manager.LoadConfigsManager;
import com.epam.shim.configurator.config.ModifierConfiguration;
import com.epam.shim.configurator.util.NamedClusterPropertyExtractingUtil;
import com.epam.spring.security.BaseSecurityContextHandler;
import org.apache.log4j.Logger;
import org.pentaho.di.core.namedcluster.NamedClusterManager;
import org.pentaho.di.core.namedcluster.model.NamedCluster;
import org.pentaho.metastore.api.exceptions.MetaStoreException;
import org.pentaho.metastore.stores.xml.XmlMetaStore;

import java.io.File;

public class NamedClusterCreator extends BaseSecurityContextHandler {
  private static final Logger logger = Logger.getLogger( NamedClusterCreator.class );

  public static void createNamedCluster( ModifierConfiguration modifierConfiguration, String namedClusterName ) {
    NamedClusterManager namedClusterManager = NamedClusterManager.getInstance();
    try {
      logger.info( "Start setup named cluster with name - " + namedClusterName );
      NamedCluster namedCluster = new NamedCluster();
      setupNamedCluster( namedCluster, modifierConfiguration, namedClusterName );
      namedClusterManager
        .create( namedCluster, new XmlMetaStore( System.getProperty( "user.home" ) + File.separator + ".pentaho" ) );
    } catch ( MetaStoreException e ) {
      logger.error( e.getMessage() );
    }
  }

  private static void setupNamedCluster( NamedCluster namedCluster, ModifierConfiguration modifierConfiguration,
                                         String namedClusterName ) {
    namedCluster.setName( namedClusterName != null && !namedClusterName.isEmpty()
      ? namedClusterName : modifierConfiguration.getHosts().split( "," )[ 0 ] );

    if ( !modifierConfiguration.getClusterType().equals( LoadConfigsManager.ClusterType.MAPR ) ) {
      if ( !modifierConfiguration.isSecure() ) {
        setSecureParams( namedCluster );
        setJobTrackerServer( namedCluster, modifierConfiguration.getPathToShim() + File.separator );
      } else {
        setSecureJobTrackerServer( namedCluster, modifierConfiguration.getPathToShim() + File.separator );
      }
      setHdfsServerProtoPortUrl( namedCluster, modifierConfiguration.getPathToShim() + File.separator );
    } else {
      namedCluster.setMapr( true );
    }

    setOozie( namedCluster, modifierConfiguration.getHosts() );
    setZookeeper( namedCluster, modifierConfiguration.getPathToShim() + File.separator );
  }

  private static void setSecureParams( NamedCluster namedCluster ) {
    namedCluster.setHdfsUsername( getCredentialsFromSecurityContext().getSshCredentials().getUsername() );
    namedCluster.setHdfsPassword( getCredentialsFromSecurityContext().getSshCredentials().getPassword() );

    logger.info( "Set security params - " + namedCluster.getHdfsUsername() + ":" + namedCluster.getHdfsPassword() );
  }

  private static void setZookeeper( NamedCluster namedCluster, String pathToShim ) {
    NamedClusterProperty zookeeperProperty = NamedClusterPropertyExtractingUtil.extractZookeeper( pathToShim );

    namedCluster.setZooKeeperHost( zookeeperProperty.getHost() );
    namedCluster.setZooKeeperPort( zookeeperProperty.getPort() );
    logger.info( "Set zookeeper - " + namedCluster.getZooKeeperHost() + ":" + namedCluster.getZooKeeperPort() );
  }

  private static void setSecureJobTrackerServer( NamedCluster namedCluster, String pathToShim ) {
    NamedClusterProperty jobTrackerProperties =
      NamedClusterPropertyExtractingUtil.extractHdfsServerProtoPortUrl( pathToShim );

    namedCluster.setJobTrackerHost( jobTrackerProperties.getHost() );
    namedCluster.setJobTrackerPort( jobTrackerProperties.getPort() );
    logger.info( "Set job tracker - " + namedCluster.getJobTrackerHost() + ":" + namedCluster.getJobTrackerPort() );
  }

  private static void setJobTrackerServer( NamedCluster namedCluster, String pathToShim ) {
    NamedClusterProperty jobTrackerProperties =
      NamedClusterPropertyExtractingUtil.extractJobTrackerServer( pathToShim );

    namedCluster.setJobTrackerHost( jobTrackerProperties.getHost() );
    namedCluster.setJobTrackerPort( jobTrackerProperties.getPort() );
    logger.info( "Set job tracker - " + namedCluster.getJobTrackerHost() + ":" + namedCluster.getJobTrackerPort() );
  }

  private static void setHdfsServerProtoPortUrl( NamedCluster namedCluster, String pathToShim ) {
    NamedClusterProperty hdfsProperty = NamedClusterPropertyExtractingUtil.extractHdfsServerProtoPortUrl( pathToShim );

    namedCluster.setHdfsHost( hdfsProperty.getHost() );
    namedCluster.setHdfsPort( hdfsProperty.getPort() );

    logger.info( "Set hdfs - " + namedCluster.getHdfsHost() + ":" + namedCluster.getHdfsPort() );
  }

  @SuppressWarnings( "unchecked" )
  private static void setOozie( NamedCluster namedCluster, String hosts ) {
    String oozieUrl = NamedClusterPropertyExtractingUtil.extractOozieUrl( hosts );

    if ( !oozieUrl.isEmpty() ) {
      namedCluster.setOozieUrl( oozieUrl );
      logger.info( "Set oozie - " + namedCluster.getOozieUrl() );
    } else {
      logger.error( "Can't find oozie url!" );
    }
  }
}
