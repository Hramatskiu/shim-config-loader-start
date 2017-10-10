package com.epam.spring.service.search;

import com.epam.spring.condition.DownloadableFile;
import com.epam.spring.search.SearchStrategy;
import com.epam.spring.util.SshCommonUtil;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SshSearchService {
  public List<DownloadableFile> searchForConfigsLocation( String remoteUrl,
                                                          List<DownloadableFile> searchableServiceNames,
                                                          SearchStrategy searchStrategy ) throws Exception {
    return searchStrategy.resolveCommandResult(
      askForClientsConfigs( remoteUrl, 22, searchStrategy.getStrategyCommand() ), searchableServiceNames );
  }

  private String askForClientsConfigs( String host, int port, String command ) throws Exception {
    return SshCommonUtil.executeCommand( "mapr", "password", host, port, command );
  }
}
