package com.epam.loader.common.service.search;

import com.epam.loader.common.delegating.executor.DelegatingExecutorService;
import com.epam.loader.common.service.ServiceException;
import com.epam.loader.common.util.CheckingParamsUtil;
import com.epam.loader.common.util.CommonUtilException;
import com.epam.loader.common.util.CommonUtilHolder;
import com.epam.loader.config.condition.DownloadableFile;
import com.epam.loader.config.credentials.SshCredentials;
import com.epam.loader.plan.strategy.SearchStrategy;
import com.epam.loader.plan.strategy.StrategyException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Component
public class SshSearchService {
  private static final int SSH_DEFAULT_PORT = 22;
  private Logger logger = Logger.getLogger( SshSearchService.class );
  private static final String LINUX_COMMAND_SEPARATOR = ";";

  public List<DownloadableFile> searchForConfigsLocation( String remoteUrl,
                                                          List<DownloadableFile> searchableServiceNames,
                                                          SearchStrategy searchStrategy ) {
    try {
      return searchStrategy.tryToResolveCommandResult(
        askForClientsConfigLocationInParallel( remoteUrl, searchStrategy.getStrategyCommand( searchableServiceNames ) ),
        searchableServiceNames );
    } catch ( StrategyException | CompletionException e ) {
      throw new ServiceException( e );
    }
  }

  String askForClientsConfigLocation( String host, String command ) throws CommonUtilException {
    return CheckingParamsUtil.checkParamsWithNullAndEmpty( host, command )
      ? CommonUtilHolder.sshCommonUtilInstance().executeCommand( new SshCredentials(), host, SSH_DEFAULT_PORT, command )
      : StringUtils.EMPTY;
  }

  @SuppressWarnings( "unchecked" ) private String askForClientsConfigLocationInParallel( String host, String command ) {
    String[] singleCommandsArray = command.split( LINUX_COMMAND_SEPARATOR );
    try ( DelegatingExecutorService delegatingExecutorService = new DelegatingExecutorService(
      singleCommandsArray.length ) ) {
      List<CompletableFuture<String>> taskList = Arrays.stream( singleCommandsArray )
        .map( singleCommand -> createTaskAskForClientsConfigLocation( host, singleCommand,
          delegatingExecutorService.getExecutorService() ) )
        .collect( Collectors.toList() );

      return taskList.stream().map( CompletableFuture::join ).collect( Collectors.joining( "\n" ) );
    } catch ( IOException ex ) {
      throw new ServiceException( ex );
    }
  }

  private CompletableFuture<String> createTaskAskForClientsConfigLocation( String host, String command,
                                                                           ExecutorService executorService ) {
    return CompletableFuture.supplyAsync( () -> {
      try {
        logger.info( "Start search for " + command + " on host - " + host );
        String result = askForClientsConfigLocation( host, command );
        logger.info( "Finish search for " + command + " on host - " + host );

        return result;
      } catch ( CommonUtilException e ) {
        throw new ServiceException( e );
      }
    }, executorService );
  }
}
