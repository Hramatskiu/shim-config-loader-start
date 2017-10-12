package com.epam.spring.service.search;

import com.epam.spring.condition.DownloadableFile;
import com.epam.spring.exception.CommonUtilException;
import com.epam.spring.exception.ServiceException;
import com.epam.spring.exception.StrategyException;
import com.epam.spring.executor.DelegatingExecutorService;
import com.epam.spring.search.SearchStrategy;
import com.epam.spring.util.CheckingParamsUtil;
import com.epam.spring.util.CommonUtilHolder;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Component
public class SshSearchService {
  private static final int SSH_DEFAULT_PORT = 22;
  private Logger logger = Logger.getLogger( SshSearchService.class );

  public List<DownloadableFile> searchForConfigsLocation( String remoteUrl,
                                                          List<DownloadableFile> searchableServiceNames,
                                                          SearchStrategy searchStrategy ) {
    try {
      return searchStrategy.tryToResolveCommandResult(
        askForClientsConfigsInParallel( remoteUrl, searchStrategy.getStrategyCommand( searchableServiceNames ) ),
        searchableServiceNames );
    } catch ( StrategyException e ) {
      throw new ServiceException( e );
    }
  }

  String askForClientsConfigs( String host, String command ) throws CommonUtilException {
    return CheckingParamsUtil.checkParamsWithNullAndEmpty( host, command ) ? CommonUtilHolder.sshCommonUtilInstance()
      .executeCommand( StringUtils.EMPTY, StringUtils.EMPTY, host, SSH_DEFAULT_PORT, command, StringUtils.EMPTY )
      : StringUtils.EMPTY;
  }

  @SuppressWarnings( "unchecked" ) private String askForClientsConfigsInParallel( String host, String command ) {
    try ( DelegatingExecutorService delegatingExecutorService = new DelegatingExecutorService( 4 ) ) {
      List<CompletableFuture<String>> taskList = Arrays.stream( command.split( ";" ) )
        .map( singleCommand -> createTaskAskForClientsConfigs( host, singleCommand,
          delegatingExecutorService.getExecutorService() ) )
        .collect( Collectors.toList() );

      return taskList.stream().map( CompletableFuture::join ).collect( Collectors.joining( "\n" ) );
    } catch ( IOException ex ) {
      throw new ServiceException( ex );
    }
  }

  private CompletableFuture<String> createTaskAskForClientsConfigs( String host, String command,
                                                                    ExecutorService executorService ) {
    return CompletableFuture.supplyAsync( () -> {
      try {
        logger.info( "Start search for " + command );
        String result = askForClientsConfigs( host, command );
        logger.info( "Finish search for " + command );

        return result;
      } catch ( CommonUtilException e ) {
        throw new ServiceException( e );
      }
    }, executorService );
  }
}
