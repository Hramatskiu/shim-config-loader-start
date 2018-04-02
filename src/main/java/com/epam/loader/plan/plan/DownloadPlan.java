package com.epam.loader.plan.plan;

import com.epam.loader.common.service.FileExtractingService;
import com.epam.loader.common.service.ServiceException;
import com.epam.loader.config.condition.DownloadConfigsCondition;
import com.epam.loader.plan.function.DownloadFunction;
import com.epam.loader.plan.strategy.SearchStrategy;
import org.apache.log4j.Logger;
import org.springframework.security.core.AuthenticationException;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.CompletionException;

public abstract class DownloadPlan {
  private List<DownloadFunction> downloadFunction;
  protected Deque<SearchStrategy> searchStrategies;
  private final Logger logger = Logger.getLogger( DownloadPlan.class );

  protected DownloadPlan( DownloadFunction downloadFunction, SearchStrategy... searchStrategies ) {
    this.downloadFunction = Collections.singletonList( downloadFunction );
    setupSearchStrategies( searchStrategies );
  }

  protected DownloadPlan( List<DownloadFunction> downloadFunction, SearchStrategy... searchStrategies ) {
    this.downloadFunction = new ArrayList<>( downloadFunction );
    setupSearchStrategies( searchStrategies );
  }

  public DownloadConfigsCondition downloadConfigs( String hostName, String destPrefix,
                                                   DownloadConfigsCondition downloadConfigsCondition ) {
    if ( downloadConfigsCondition == null ) {
      downloadConfigsCondition = createDownloadConfigsCondition();
    }

    while ( !downloadConfigsCondition.getUnloadedConfigsList().isEmpty() && !searchStrategies.isEmpty() ) {
      logger.info( "Start loading!" );
      try {
        SearchStrategy searchStrategy = searchStrategies.pop();
        DownloadFunction function = downloadFunction.stream()
          .filter( dFunction -> dFunction.isSsh() == searchStrategy.useSsh() )
          .findFirst().orElse( null );

        if ( function != null ) {
          function
            .downloadConfigs( downloadConfigsCondition, searchStrategy, createLoadPathConfig( hostName, destPrefix ) );
        }
      } catch ( CompletionException | ServiceException | AuthenticationException ex ) {
        logger.error( ex.getMessage(), ex );
      }
    }

    return downloadConfigsCondition;
  }

  protected abstract LoadPathConfig createLoadPathConfig( String hostName, String destPrefix );

  protected abstract DownloadConfigsCondition createDownloadConfigsCondition();

  private void setupSearchStrategies( SearchStrategy[] searchStrategies ) {
    this.searchStrategies = new ArrayDeque<>();
    Arrays.stream( searchStrategies ).forEach( this.searchStrategies::push );
  }

  public static class LoadPathConfig {
    private String compositeHost;
    private String destPrefix;
    private List<String> loadedFiles;
    private FileExtractingService.ExtractFormats extractFormat;

    public LoadPathConfig( String compositeHost, String destPrefix,
                           FileExtractingService.ExtractFormats extractFormat ) {
      this.compositeHost = compositeHost;
      this.destPrefix = destPrefix;
      this.extractFormat = extractFormat;
      this.loadedFiles = new ArrayList<>();
    }

    public String getCompositeHost() {
      return compositeHost;
    }

    public void setCompositeHost( String compositeHost ) {
      this.compositeHost = compositeHost;
    }

    public String getDestPrefix() {
      return destPrefix;
    }

    public void setDestPrefix( String destPrefix ) {
      this.destPrefix = destPrefix;
    }

    public List<String> getLoadedFiles() {
      return loadedFiles;
    }

    public void setLoadedFiles( List<String> loadedFiles ) {
      this.loadedFiles = loadedFiles;
    }

    public FileExtractingService.ExtractFormats getExtractFormat() {
      return extractFormat;
    }

    public void setExtractFormat( FileExtractingService.ExtractFormats extractFormat ) {
      this.extractFormat = extractFormat;
    }
  }
}
