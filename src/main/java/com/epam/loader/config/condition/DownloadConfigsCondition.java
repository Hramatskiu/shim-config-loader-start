package com.epam.loader.config.condition;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DownloadConfigsCondition {
  private Map<DownloadableFile, Boolean> downloadedConfigsMap = new HashMap<>();

  public void addConfigFilesToMap( DownloadableFile downloadableFile ) {
    if ( !downloadableFile.getServiceName().isEmpty() /*&&
                //putIfAbsent => add equals to DownloadableFile || allow override
                downloadedConfigsMap.entrySet().stream().filter(entry -> entry.getKey().getServiceName().equals
                (downloadableFile.getServiceName()))
                        .collect(Collectors.toList()).isEmpty()*/ ) {
      downloadedConfigsMap.put( downloadableFile, false );
    }
  }

  public List<DownloadableFile> getUnloadedConfigsList() {
    return downloadedConfigsMap.entrySet().stream().filter( map -> !map.getValue() ).map( Map.Entry::getKey )
      .collect( Collectors.toList() );
  }

  public void setDownloadCondition( List<Boolean> updatedConditions ) {
    Iterator<Boolean> conditionsIterator = updatedConditions.iterator();
    downloadedConfigsMap.replaceAll( ( k, v ) -> conditionsIterator.hasNext() & !v ? conditionsIterator.next() : v );
  }

  public void setDownloadConditionForFile( DownloadableFile keyFile, Boolean value ) {
    downloadedConfigsMap.put( keyFile, value );
  }
}
