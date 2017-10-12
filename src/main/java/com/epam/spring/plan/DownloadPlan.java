package com.epam.spring.plan;

import com.epam.spring.condition.DownloadConfigsCondition;
import com.epam.spring.function.DownloadFunction;
import com.epam.spring.search.SearchStrategy;
import com.epam.spring.service.FileExtractingService;
import org.apache.log4j.Logger;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Deque;
import java.util.List;

public abstract class DownloadPlan {
  private DownloadFunction downloadFunction;
  private Deque<SearchStrategy> searchStrategies;
  private final Logger logger = Logger.getLogger( DownloadPlan.class );

  protected DownloadPlan( DownloadFunction downloadFunction, SearchStrategy... searchStrategies ) {
    this.downloadFunction = downloadFunction;
    setupSearchStrategies( searchStrategies );
  }

  public boolean downloadConfigs( String hostName, String destPrefix ) {
    DownloadConfigsCondition downloadConfigsCondition = createDownloadConfigsCondition();
    while ( !downloadConfigsCondition.getUnloadedConfigsList().isEmpty() && !searchStrategies.isEmpty() ) {
      logger.info( "Start loading at " + new Date() );
      downloadFunction
        .downloadConfigs( downloadConfigsCondition, searchStrategies.pop(),
          createLoadPathConfig( hostName, destPrefix ) );
    }

    return downloadConfigsCondition.getUnloadedConfigsList().isEmpty();
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
