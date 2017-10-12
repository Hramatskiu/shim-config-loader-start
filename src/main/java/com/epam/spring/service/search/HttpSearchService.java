package com.epam.spring.service.search;

import com.epam.spring.condition.DownloadableFile;
import com.epam.spring.exception.CommonUtilException;
import com.epam.spring.exception.ServiceException;
import com.epam.spring.exception.StrategyException;
import com.epam.spring.search.SearchStrategy;
import com.epam.spring.util.CheckingParamsUtil;
import com.epam.spring.util.CommonUtilHolder;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class HttpSearchService {
  static final String HTTP_PREFIX = "http://";

  public List<DownloadableFile> searchForConfigsLocation( String remoteUrl,
                                                          List<DownloadableFile> searchableServiceNames,
                                                          SearchStrategy searchStrategy ) {
    try {
      List<DownloadableFile> files = searchStrategy.tryToResolveCommandResult(
        askForClientsConfigs( HTTP_PREFIX + remoteUrl + searchStrategy.getStrategyCommand( searchableServiceNames ) ),
        searchableServiceNames );
      files.forEach( service -> service.setDownloadPath( HTTP_PREFIX + remoteUrl + service.getDownloadPath() ) );

      return files;
    } catch ( StrategyException e ) {
      throw new ServiceException( e );
    }
  }

  String askForClientsConfigs( String uri ) {
    try {
      return CheckingParamsUtil.checkParamsWithNullAndEmpty( uri )
        ? new String( IOUtils.toByteArray( CommonUtilHolder.httpCommonUtilInstance().createHttpClient()
          .execute( CommonUtilHolder.httpCommonUtilInstance().createHttpUriRequest( uri ) )
          .getEntity().getContent() ) ) : StringUtils.EMPTY;
    } catch ( IOException | CommonUtilException e ) {
      throw new ServiceException( e );
    }
  }
}
