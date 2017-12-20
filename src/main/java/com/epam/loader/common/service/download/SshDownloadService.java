package com.epam.loader.common.service.download;

import com.epam.loader.common.delegating.executor.DelegatingExecutorService;
import com.epam.loader.common.holder.DownloadedFileWrapper;
import com.epam.loader.common.service.ServiceException;
import com.epam.loader.common.util.CheckingParamsUtil;
import com.epam.loader.common.util.CommonUtilException;
import com.epam.loader.common.util.CommonUtilHolder;
import com.epam.loader.common.util.FileCommonUtil;
import com.epam.loader.config.credentials.SshCredentials;
import com.epam.loader.plan.plan.DownloadPlan;
import com.epam.spring.annotation.SecurityAnnotation;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Component
public class SshDownloadService {
  private final Logger logger = Logger.getLogger( SshDownloadService.class );

  @SecurityAnnotation
  public CompletableFuture<Boolean> loadConfigsFromCommand( String command, DownloadPlan.LoadPathConfig loadPathConfig,
                                                            ExecutorService executorService ) {
    return CompletableFuture.supplyAsync( () -> {
      List<DownloadedFileWrapper> answer = new ArrayList<>(
        askForClientsConfigs( loadPathConfig.getLoadedFiles(), loadPathConfig.getCompositeHost(), command ) );

      logger.info( "Download configs for " + loadPathConfig.getLoadedFiles() + " from host - " + loadPathConfig.getCompositeHost() );

      return checkAnswer( answer, loadPathConfig.getLoadedFiles() ) && saveClientConfigs( answer, loadPathConfig );
    }, executorService );
  }

  DownloadedFileWrapper askForClientsConfigs( String host, String command ) {
    try {
      return CheckingParamsUtil.checkParamsWithNullAndEmpty( host, command )
        ? CommonUtilHolder.sshCommonUtilInstance().downloadViaSftpAsFileWrapper( new SshCredentials(), host, 22, command )
        : new DownloadedFileWrapper( StringUtils.EMPTY );
    } catch ( CommonUtilException e ) {
      throw new ServiceException( e );
    }
  }

  private List<DownloadedFileWrapper> askForClientsConfigs( List<String> loadedFiles, String host, String command ) {
    return loadedFiles.size() == 1
      ? Collections.singletonList( askForClientsConfigs( host, command + loadedFiles.get( 0 ) ) )
      : askForClientsConfigsInParallel( loadedFiles, host, command );
  }

  private List<DownloadedFileWrapper> askForClientsConfigsInParallel( List<String> loadedFiles, String host, String command ) {
    try ( DelegatingExecutorService delegatingExecutorService = new DelegatingExecutorService( loadedFiles.size() ) ) {
      List<CompletableFuture<DownloadedFileWrapper>> loadedFileTasks = loadedFiles.stream()
        .map( file -> createAskForClientsConfigsTask( host, command + file,
          delegatingExecutorService.getExecutorService() ) )
        .collect( Collectors.toList() );

      return loadedFileTasks.stream().map( CompletableFuture::join ).collect( Collectors.toList() );
    } catch ( IOException ex ) {
      throw new ServiceException( ex );
    }
  }

  private CompletableFuture<DownloadedFileWrapper> createAskForClientsConfigsTask( String host, String command,
                                                                    ExecutorService executorService ) {
    return CompletableFuture.supplyAsync( () -> askForClientsConfigs( host, command ), executorService );
  }

  private boolean checkAnswer( List<DownloadedFileWrapper> answer, List<String> loaddedFileNames ) {
    return !answer.isEmpty() && answer.size() == loaddedFileNames.size() && answer.stream()
      .noneMatch( DownloadedFileWrapper::isEmpty );
  }

  private boolean saveClientConfigs( List<DownloadedFileWrapper> configString, DownloadPlan.LoadPathConfig loadPathConfig ) {
    return configString.size() == 1 ? saveClientConfigs( configString.get( 0 ),
      loadPathConfig.getDestPrefix() + File.separator + loadPathConfig.getLoadedFiles().get( 0 ) )
      : saveClientConfigsInParallel( configString, loadPathConfig );
  }

  //Think about
  private boolean saveClientConfigs( DownloadedFileWrapper configString, String destPath ) {
    try {
      if ( !configString.isByteContentEmpty() ) {
        FileCommonUtil.writeByteArrayToFile( destPath, configString.getByteFileContent() );
      } else if ( !configString.isStringContentEmpty() ) {
        FileCommonUtil.writeStringToFile( destPath, configString.getStringFileContent() );
      } else {
        return false;
      }

      return true;
    } catch ( CommonUtilException e ) {
      throw new ServiceException( e );
    }
  }

  //Think about
  private boolean saveClientConfigsInParallel( List<DownloadedFileWrapper> configString, DownloadPlan.LoadPathConfig loadPathConfig ) {
    try ( DelegatingExecutorService delegatingExecutorService = new DelegatingExecutorService( configString.size() ) ) {
      List<CompletableFuture<Boolean>> saveConfigsTasksList = new ArrayList<>();

      Iterator<DownloadedFileWrapper> iterator = configString.iterator();
      loadPathConfig.getLoadedFiles().forEach( file -> {
        if ( iterator.hasNext() ) {
          saveConfigsTasksList.add(
            createSaveConfigsTask( iterator.next(), loadPathConfig.getDestPrefix() + File.separator + file,
              delegatingExecutorService.getExecutorService() ) );
        }
      } );

      return saveConfigsTasksList.stream().allMatch( CompletableFuture::join );
    } catch ( IOException ex ) {
      throw new ServiceException( ex );
    }

  }

  private CompletableFuture<Boolean> createSaveConfigsTask( DownloadedFileWrapper configString, String destPath,
                                                            ExecutorService executorService ) {
    return CompletableFuture.supplyAsync( () -> saveClientConfigs( configString, destPath ), executorService );
  }
}
