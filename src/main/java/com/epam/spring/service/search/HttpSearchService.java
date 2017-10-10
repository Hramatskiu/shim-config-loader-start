package com.epam.spring.service.search;

import com.epam.spring.condition.DownloadableFile;
import com.epam.spring.search.SearchStrategy;
import com.epam.spring.util.CommonUtilHolder;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HttpSearchService {
  private static final String HTTP_PREFIX = "http://";

  public List<DownloadableFile> searchForConfigsLocation( String remoteUrl,
                                                          List<DownloadableFile> searchableServiceNames,
                                                          SearchStrategy searchStrategy ) throws Exception {
    List<DownloadableFile> files = searchStrategy.resolveCommandResult(
      askForClientsConfigs( HTTP_PREFIX + remoteUrl + searchStrategy.getStrategyCommand() ), searchableServiceNames );
    files.forEach( service -> service.setDownloadPath( HTTP_PREFIX + remoteUrl + service.getDownloadPath() ) );

    return files;
  }

  private String askForClientsConfigs( String uri ) throws Exception {
    return new String( IOUtils.toByteArray( CommonUtilHolder.httpCommonUtilInstance().createHttpClient()
      .execute( CommonUtilHolder.httpCommonUtilInstance().createHttpUriRequest( uri ) )
      .getEntity().getContent() ) );
  }
}
