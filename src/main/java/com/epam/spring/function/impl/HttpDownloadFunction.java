package com.epam.spring.function.impl;

import com.epam.spring.condition.DownloadConfigsCondition;
import com.epam.spring.exception.ServiceException;
import com.epam.spring.executor.DelegatingExecutorService;
import com.epam.spring.function.DownloadFunction;
import com.epam.spring.plan.DownloadPlan;
import com.epam.spring.search.SearchStrategy;
import com.epam.spring.service.download.HttpDownloadService;
import com.epam.spring.service.search.HttpSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component( "http-download-function" )
public class HttpDownloadFunction extends DownloadFunction {
  @Autowired
  private HttpDownloadService downloadService;
  @Autowired
  private HttpSearchService httpSearchService;

  public void downloadConfigs( DownloadConfigsCondition downloadConfigsCondition, SearchStrategy searchStrategy,
                               DownloadPlan.LoadPathConfig loadPathConfig ) {
    try ( DelegatingExecutorService delegatingExecutorService = new DelegatingExecutorService(
      downloadConfigsCondition.getUnloadedConfigsList().size() ) ) {
      List<CompletableFuture<Boolean>> taskList =
        httpSearchService.searchForConfigsLocation( loadPathConfig.getCompositeHost(),
          downloadConfigsCondition.getUnloadedConfigsList(), searchStrategy ).stream()
          .map( file -> {
            DownloadPlan.LoadPathConfig copiedLoadPathConfig = copyLoadPathConfig( loadPathConfig );
            copiedLoadPathConfig.setLoadedFiles( file.getFiles() );

            return downloadService.loadConfigsFromUri( file.getDownloadPath(), copiedLoadPathConfig,
              delegatingExecutorService.getExecutorService() );
          } ).collect( Collectors.toList() );

      downloadConfigsCondition.setDownloadCondition(
        taskList.stream().map( CompletableFuture::join )
          .collect( Collectors.toList() ) );
    } catch ( IOException ex ) {
      throw new ServiceException( ex );
    }
  }

  private DownloadPlan.LoadPathConfig copyLoadPathConfig( DownloadPlan.LoadPathConfig loadPathConfig ) {
    return new DownloadPlan.LoadPathConfig( loadPathConfig.getCompositeHost(),
      loadPathConfig.getDestPrefix(), loadPathConfig.getExtractFormat() );
  }

}
