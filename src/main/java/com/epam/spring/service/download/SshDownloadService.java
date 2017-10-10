package com.epam.spring.service.download;

import com.epam.spring.annotation.SecurityAnnotation;
import com.epam.spring.exception.CommonUtilException;
import com.epam.spring.exception.ServiceException;
import com.epam.spring.plan.DownloadPlan;
import com.epam.spring.util.CommonUtilHolder;
import com.epam.spring.util.FileCommonUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Component
public class SshDownloadService {
  @SecurityAnnotation
  public CompletableFuture<Boolean> loadConfigsFromCommand( String command, DownloadPlan.LoadPathConfig loadPathConfig,
                                                            ExecutorService executorService ) {
    return CompletableFuture.supplyAsync( () -> {
      List<String> answer = new ArrayList<>(
        askForClientsConfigs( loadPathConfig.getLoadedFiles(), loadPathConfig.getCompositeHost(), command ) );

      return checkAnswer( answer, loadPathConfig.getLoadedFiles() ) && saveClientConfigs( answer, loadPathConfig );
    }, executorService );
  }

  private String askForClientsConfigs( String host, String command ) {
    try {
      return CommonUtilHolder.sshCommonUtilInstance()
        .executeCommand( StringUtils.EMPTY, StringUtils.EMPTY, host, 22, command );
    } catch ( CommonUtilException e ) {
      throw new ServiceException( e );
    }
  }

  private List<String> askForClientsConfigs( List<String> loadedFiles, String host, String command ) {
    return loadedFiles.size() == 1
      ? Collections.singletonList( askForClientsConfigs( host, "cat " + command + loadedFiles.get( 0 ) ) )
      : askForClientsConfigsInParallel( loadedFiles, host, command );
  }

  private List<String> askForClientsConfigsInParallel( List<String> loadedFiles, String host, String command ) {
    ExecutorService executorService = Executors.newFixedThreadPool( loadedFiles.size() );

    List<CompletableFuture<String>> loadedFileTasks = loadedFiles.stream()
      .map( file -> createAskForClientsConfigsTask( host, "cat " + command + file, executorService ) )
      .collect( Collectors.toList() );

    List<String> answers = loadedFileTasks.stream().map( CompletableFuture::join ).collect( Collectors.toList() );
    executorService.shutdown();

    return answers;
  }

  private CompletableFuture<String> createAskForClientsConfigsTask( String host, String command,
                                                                    ExecutorService executorService ) {
    return CompletableFuture.supplyAsync( () -> askForClientsConfigs( host, command ), executorService );
  }

  private boolean checkAnswer( List<String> answer, List<String> loaddedFileNames ) {
    return !answer.isEmpty() && answer.size() == loaddedFileNames.size() && answer.stream().noneMatch( String::isEmpty );
  }

  private boolean saveClientConfigs( List<String> configString, DownloadPlan.LoadPathConfig loadPathConfig ) {
    return configString.size() == 1 ? saveClientConfigs( configString.get( 0 ),
      loadPathConfig.getDestPrefix() + "\\" + loadPathConfig.getLoadedFiles().get( 0 ) )
      : saveClientConfigsInParallel( configString, loadPathConfig );
  }

  //Think about
  private boolean saveClientConfigs( String configString, String destPath ) {
    try {
      FileCommonUtil.writeStringToFile( destPath, configString );

      return true;
    } catch ( CommonUtilException e ) {
      //logging
      e.printStackTrace();
    }

    return false;
  }

  private boolean saveClientConfigsInParallel( List<String> configString, DownloadPlan.LoadPathConfig loadPathConfig ) {
    List<CompletableFuture<Boolean>> saveConfigsTasksList = new ArrayList<>();
    ExecutorService executorService = Executors.newFixedThreadPool( configString.size() );

    Iterator<String> iterator = configString.iterator();
    loadPathConfig.getLoadedFiles().forEach( file -> {
      if ( iterator.hasNext() ) {
        saveConfigsTasksList.add(
          createSaveConfigsTask( iterator.next(), loadPathConfig.getDestPrefix() + "\\" + file, executorService ) );
      }
    } );

    boolean result = saveConfigsTasksList.stream().allMatch( CompletableFuture::join );

    executorService.shutdown();

    return result;
  }

  private CompletableFuture<Boolean> createSaveConfigsTask( String configString, String destPath,
                                                            ExecutorService executorService ) {
    return CompletableFuture.supplyAsync( () -> saveClientConfigs( configString, destPath ), executorService );
  }
}
