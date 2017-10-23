package com.epam.loader;

import com.epam.loader.common.service.ServiceException;
import com.epam.loader.config.condition.DownloadConfigsCondition;
import com.epam.loader.config.credentials.LoadConfigs;
import com.epam.loader.plan.manager.LoadConfigsManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletionException;

@Component
public class ClusterConfigLoader {
  //  private ApplicationContext applicationContext;
  private static Logger logger = Logger.getLogger( ClusterConfigLoader.class );
  @Autowired
  private LoadConfigsManager loadConfigsManager;

  public ClusterConfigLoader() {
    System.out.println( "haha" );
  }

  public void init() {
    //    applicationContext = new AnnotationConfigApplicationContext( SpringAppConfig.class );
  }

  public boolean loadConfigs( LoadConfigs loadConfigs ) {
    String[] hosts = loadConfigs.getHost().split( "," );
    int hostArrayIndex = 0;
    boolean downloadResult = false;
    DownloadConfigsCondition downloadConfigsCondition = null;

    while ( !downloadResult && hostArrayIndex < hosts.length ) {
      loadConfigs.setHost( hosts[ hostArrayIndex++ ].trim() );
      //      LoadConfigsManager loadConfigsManager = applicationContext.getBean( LoadConfigsManager.class );
      try {
        logger.info( "Start pressed!" );

        downloadConfigsCondition = loadConfigsManager
          .downloadClientConfigs( loadConfigs.getClusterType(), loadConfigs, downloadConfigsCondition );

        downloadResult =
          downloadConfigsCondition != null && downloadConfigsCondition.getUnloadedConfigsList().isEmpty();
      } catch ( CompletionException | ServiceException ex ) {
        logger.error( ex );
      }
    }

    return downloadResult;
  }
}
