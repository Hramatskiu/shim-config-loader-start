package com.epam.spring.service.search;

import com.epam.spring.condition.DownloadableFile;
import com.epam.spring.exception.CommonUtilException;
import com.epam.spring.exception.ServiceException;
import com.epam.spring.search.SearchStrategy;
import com.epam.spring.util.CommonUtilHolder;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SshSearchService {
  public List<DownloadableFile> searchForConfigsLocation( String remoteUrl,
                                                          List<DownloadableFile> searchableServiceNames,
                                                          SearchStrategy searchStrategy ) {
    try {
      return searchStrategy.resolveCommandResult(
        askForClientsConfigs( remoteUrl, searchStrategy.getStrategyCommand() ), searchableServiceNames );
    } catch ( Exception e ) {
      throw new ServiceException( e );
    }
  }

  private String askForClientsConfigs( String host, String command ) throws CommonUtilException {
    return CommonUtilHolder.sshCommonUtilInstance()
      .executeCommand( StringUtils.EMPTY, StringUtils.EMPTY, host, 22, command );
  }
}
