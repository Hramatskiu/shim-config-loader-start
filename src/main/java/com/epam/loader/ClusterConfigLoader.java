package com.epam.loader;

import com.epam.loader.common.delegating.executor.DelegatingExecutorService;
import com.epam.loader.common.service.ServiceException;
import com.epam.loader.config.credentials.LoadConfigs;
import com.epam.loader.plan.manager.LoadConfigsManager;
import com.epam.spring.config.SpringAppConfig;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;
import java.util.List;
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

  public boolean loadConfigs( LoadConfigs loadConfigs ) {
    LoadConfigsManager loadConfigsManager = applicationContext.getBean( LoadConfigsManager.class );
    try ( DelegatingExecutorService delegatingExecutorService = new DelegatingExecutorService( 2 ) ) {
      List<Boolean> results = Stream.of( CompletableFuture.supplyAsync( () -> {
        logger.info( "Start pressed!" );
        return loadConfigsManager.downloadClientConfigs( loadConfigs.getClusterType(), loadConfigs );
      }, delegatingExecutorService.getExecutorService() ) ).map( CompletableFuture::join )
        .collect( Collectors.toList() );

      results.forEach( logger::info );

      return results.stream().allMatch( result -> result );
    } catch ( IOException | CompletionException | ServiceException ex ) {
      logger.error( ex );
    }

    return false;
  }
}
