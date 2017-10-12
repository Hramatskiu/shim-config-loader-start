package com.epam.spring;

import com.epam.spring.config.LoadConfigs;
import com.epam.spring.config.SpringAppConfig;
import com.epam.spring.exception.ServiceException;
import com.epam.spring.executor.DelegatingExecutorService;
import com.epam.spring.manager.LoadConfigsManager;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClusterConfigLoader {
  private ApplicationContext applicationContext;
  private static Logger logger = Logger.getLogger( ClusterConfigLoader.class );

  public void init() {
    applicationContext = new AnnotationConfigApplicationContext( SpringAppConfig.class );
  }

  public void loadConfigs( LoadConfigs loadConfigs ) {
    Date date = new Date();
    long start = date.getTime();
    logger.info( date.getTime() );
    LoadConfigsManager loadConfigsManager = applicationContext.getBean( LoadConfigsManager.class );
    try ( DelegatingExecutorService delegatingExecutorService = new DelegatingExecutorService( 2 ) ) {
      Stream.of( CompletableFuture.supplyAsync( () -> {
        logger.info( "Start pressed at - " + new Date() );
        return loadConfigsManager.downloadClientConfigs( loadConfigs.getClusterType(), loadConfigs );
      }, delegatingExecutorService.getExecutorService() ) ).map( CompletableFuture::join )
        .collect( Collectors.toList() ).forEach( logger::info );
    } catch ( IOException | CompletionException | ServiceException ex ) {
      logger.error( ex.getMessage() );
    }

    date = new Date();
    logger.info( start - date.getTime() );
  }
}
